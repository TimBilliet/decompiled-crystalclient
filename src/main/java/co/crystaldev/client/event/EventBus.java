package co.crystaldev.client.event;

import co.crystaldev.client.event.data.AbstractEventData;
import co.crystaldev.client.event.data.LambdaEventData;
import co.crystaldev.client.event.data.ReflectiveEventData;
import co.crystaldev.client.util.type.GlueList;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<? extends Event>, List<AbstractEventData>> REGISTRY_MAP = new ConcurrentHashMap<>();

    public static void register(Object o) {
        for (Method method : o.getClass().getMethods()) {
            if (!isMethodBad(method))
                register(method, o);
        }
        if (o instanceof IRegistrable)
            ((IRegistrable) o).registerEvents();
    }

    public static <T extends Event> void register(Object o, Class<T> clazz, byte priority, Consumer<T> consumer) {
        LambdaEventData methodData = new LambdaEventData(o, (Consumer<Event>) consumer, priority);
        (REGISTRY_MAP.computeIfAbsent(clazz, c -> new GlueList())).add(methodData);
        sortListValue(clazz);
    }

    public static <T extends Event> void register(Object o, Class<T> clazz, Consumer<T> consumer) {
        register(o, clazz, (byte) 2, consumer);
    }

    public static void register(Object o, Class<? extends Event> clazz) {
        for (Method method : o.getClass().getMethods()) {
            if (!isMethodBad(method, clazz))
                register(method, o);
        }
    }

    private static void register(Method method, Object o) {
        synchronized (REGISTRY_MAP) {
            Class<?> clazz = method.getParameterTypes()[0];
            ReflectiveEventData methodData = new ReflectiveEventData(o, method, ((SubscribeEvent) method.<SubscribeEvent>getAnnotation(SubscribeEvent.class)).priority());
            if (!methodData.target.isAccessible())
                methodData.target.setAccessible(true);
            (REGISTRY_MAP.computeIfAbsent((Class<? extends Event>) clazz, c -> new GlueList())).add(methodData);
            sortListValue((Class) clazz);
        }
    }

    public static void unregister(Object o) {
        for (Iterator<List<AbstractEventData>> iterator = REGISTRY_MAP.values().iterator(); iterator.hasNext(); ) {
            List<AbstractEventData> flexibleArray = iterator.next();
            flexibleArray.removeIf(methodData -> methodData.source.equals(o));
        }
        cleanMap(true);
    }

    public static void unregister(Object o, Class<? extends Event> clazz) {
        List<AbstractEventData> dataList = REGISTRY_MAP.get(clazz);
        if (dataList != null) {
            dataList.removeIf(methodData -> methodData.source.equals(o));
            cleanMap(true);
        }
    }

    public static void cleanMap(boolean b) {
        Iterator<Map.Entry<Class<? extends Event>, List<AbstractEventData>>> iterator = REGISTRY_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!b || ((List) ((Map.Entry) iterator.next()).getValue()).isEmpty())
                iterator.remove();
        }
    }

    public static void removeEntry(Class<? extends Event> clazz) {
        Iterator<Map.Entry<Class<? extends Event>, List<AbstractEventData>>> iterator = REGISTRY_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            if (((Class) ((Map.Entry) iterator.next()).getKey()).equals(clazz)) {
                iterator.remove();
                break;
            }
        }
    }

    private static void sortListValue(Class<? extends Event> clazz) {
        GlueList<AbstractEventData> glueList = new GlueList();
        for (byte b : Event.Priority.PRIORITIES) {
            for (AbstractEventData methodData : REGISTRY_MAP.get(clazz)) {
                if (methodData.priority == b)
                    glueList.add(methodData);
            }
        }
        REGISTRY_MAP.put(clazz, glueList);
    }

    private static boolean isMethodBad(Method method) {
        return ((method.getParameterTypes()).length != 1 || !method.isAnnotationPresent((Class) SubscribeEvent.class));
    }

    private static boolean isMethodBad(Method method, Class<? extends Event> clazz) {
        return (isMethodBad(method) || method.getParameterTypes()[0].equals(clazz));
    }

    public static List<AbstractEventData> get(Class<? extends Event> clazz) {
        return REGISTRY_MAP.get(clazz);
    }

    public static void shutdown() {
        REGISTRY_MAP.clear();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\EventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */