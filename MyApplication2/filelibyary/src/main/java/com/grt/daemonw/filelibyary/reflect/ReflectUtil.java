package com.grt.daemonw.filelibyary.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Object getField(Object obj, String fieldName) {
        Object fieldVal = null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldVal = field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldVal;
    }


    public static Object getStaticField(Object obj, String fieldName) {
        Object fieldVal = null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldVal = field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldVal;
    }


    public static Method getMethod(Class<?> clazz, String methodName, boolean isPublic) {
        Method method = null;
        try {
            method = isPublic ? clazz.getMethod(methodName) : clazz.getDeclaredMethod(methodName);
            if (!isPublic) {
                method.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Method getMethod(Object obj, String methodName, boolean isPublic) {
        Method method = null;
        try {
            method = isPublic ? obj.getClass().getMethod(methodName) : obj.getClass().getDeclaredMethod(methodName);
            if (!isPublic) {
                method.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
        Object result = null;
        try {
            result = method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static Object call(Object obj, String methodName, boolean isPublic, boolean isStatic, Object... args) {
        Object result = null;
        try {
            Method method = isPublic ? obj.getClass().getMethod(methodName) : obj.getClass().getDeclaredMethod(methodName);
            if (!isPublic) {
                method.setAccessible(true);
            }
            result = isStatic ? method.invoke(null, args) : method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
