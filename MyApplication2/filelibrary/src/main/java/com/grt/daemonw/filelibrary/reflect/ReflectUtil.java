package com.grt.daemonw.filelibrary.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Object getPublicField(Object obj, String fieldName) {
        Object fieldVal = null;
        try {
            Field field = obj.getClass().getField(fieldName);
            fieldVal = field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldVal;
    }

    public static Object getPrivateField(Object obj, String fieldName) {
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


    public static Object getPublicStaticField(Object obj, String fieldName) {
        Object fieldVal = null;
        try {
            Field field = obj.getClass().getField(fieldName);
            fieldVal = field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldVal;
    }

    public static Object getPrivateStaticField(Object obj, String fieldName) {
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


    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Method getPublicMethod(Object obj, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Method getPrivateMethod(Class<?> clazz, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName);
            method.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Method getPrivateMethod(Object obj, String methodName, Class<?>... params) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName);
            method.setAccessible(true);
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
}
