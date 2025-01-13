package co.crystaldev.client.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {
    public static class UnableToFindMethodException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToFindMethodException(String[] methodNames, Exception failed) {
            super(failed);
        }
    }

    public static class UnableToFindClassException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToFindClassException(String[] classNames, Exception err) {
            super(err);
        }
    }

    public static class UnableToAccessFieldException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToAccessFieldException(String[] fieldNames, Exception e) {
            super(e);
        }
    }

    public static class UnableToFindFieldException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToFindFieldException(String[] fieldNameList, Exception e) {
            super(e);
        }
    }

    public static Field findField(Class<?> clazz, String... fieldNames) {
        Exception failed = null;
        for (String fieldName : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                failed = e;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex) {
        try {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T) f.get(instance);
        } catch (Exception e) {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames) {
        try {
            return (T) findField(classToAccess, fieldNames).get(instance);
        } catch (Exception e) {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
        try {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            findField(classToAccess, fieldNames).set(instance, value);
        } catch (Exception e) {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static Class<? super Object> getClass(ClassLoader loader, String... classNames) {
        Exception err = null;
        for (String className : classNames) {
            try {
                return (Class) Class.forName(className, false, loader);
            } catch (Exception e) {
                err = e;
            }
        }
        throw new UnableToFindClassException(classNames, err);
    }

    public static <E> Method findMethod(Class<? super E> clazz, String[] methodNames, Class<?>... methodTypes) {
        Exception failed = null;
        for (String methodName : methodNames) {
            try {
                Method m = clazz.getDeclaredMethod(methodName, methodTypes);
                m.setAccessible(true);
                return m;
            } catch (Exception e) {
                failed = e;
            }
        }
        throw new UnableToFindMethodException(methodNames, failed);
    }

    public static Object getFieldValue(Field field, Object obj) {
        return getFieldValue(field, obj, Object.class);
    }

    public static <E> E getFieldValue(Field field, Object obj, Class<? super E> clazz) {
        try {
            return (E) field.get(obj);
        } catch (Exception ex) {
            return null;
        }
    }

    public static <E> E invokeMethod(Method method, Object obj, Object... args) {
        try {
            return (E) method.invoke(obj, args);
        } catch (Exception ex) {
            return null;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\ReflectionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */