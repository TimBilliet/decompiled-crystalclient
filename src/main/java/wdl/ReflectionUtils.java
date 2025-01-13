package wdl;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Field stealField(Class<?> typeOfClass, Class<?> typeOfField) {
        Field[] fields = typeOfClass.getDeclaredFields();
        for (Field f : fields) {
            if (f.getType().equals(typeOfField))
                try {
                    f.setAccessible(true);
                    return f;
                } catch (Exception e) {
                    throw new RuntimeException("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !", e);
                }
        }
        throw new RuntimeException("WorldDownloader: Couldn't steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !");
    }

    public static <T> T stealAndGetField(Object object, Class<T> typeOfField) {
        Class<?> typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class) object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }
        try {
            Field f = stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f.get(object));
        } catch (Exception e) {
            throw new RuntimeException("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", e);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfField, Object value) {
        Class<?> typeOfObject;
        if (object instanceof Class) {
            typeOfObject = (Class) object;
            object = null;
        } else {
            typeOfObject = object.getClass();
        }
        try {
            Field f = stealField(typeOfObject, typeOfField);
            f.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", e);
        }
    }

    public static <T> T stealAndGetField(Object object, Class<?> typeOfObject, Class<T> typeOfField) {
        try {
            Field f = stealField(typeOfObject, typeOfField);
            return typeOfField.cast(f.get(object));
        } catch (Exception e) {
            throw new RuntimeException("WorldDownloader: Couldn't get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !", e);
        }
    }

    public static void stealAndSetField(Object object, Class<?> typeOfObject, Class<?> typeOfField, Object value) {
        try {
            Field f = stealField(typeOfObject, typeOfField);
            f.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("WorldDownloader: Couldn't set Field of type \"" + typeOfField + "\" from object \"" + object + "\" to " + value + "!", e);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\ReflectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */