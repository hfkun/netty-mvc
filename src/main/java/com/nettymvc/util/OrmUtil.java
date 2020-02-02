package com.nettymvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * ORM映射工具
 */
public class OrmUtil {
    /**
     * 主动注入对象
     * @param requestParamMap
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static Object autoSetObject(Map<String, Object> requestParamMap, Class clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Field[] declaredFields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();

        Object instance = clazz.newInstance();
        String methodName;
        for (Field declaredField : declaredFields) {
            methodName = "set"+toFirstUpperCase(declaredField.getName());
            for (Method method : methods) {
                if(methodName.equals(method.getName())){
                    invoke(instance, method, declaredField.getType().getName(), requestParamMap.get(declaredField.getName()));
                    break;
                }
            }
        }
        return instance;
    }

    /**
     * 判断是否简单类型
     * @param typeName
     * @return
     */
    public static boolean isSimpleType(String typeName){
        boolean isSimpleType = false;
        if("boolean".equals(typeName) || "char".equals(typeName) || "float".equals(typeName) || "double".equals(typeName) ||
                "byte".equals(typeName) || "short".equals(typeName) || "int".equals(typeName) || "long".equals(typeName) ||
                "java.lang.Boolean".equals(typeName) || "java.lang.Character".equals(typeName) || "java.lang.Float".equals(typeName) || "java.lang.Double".equals(typeName)||
                "java.lang.Byte".equals(typeName) || "java.lang.Short".equals(typeName) || "java.lang.Integer".equals(typeName) || "java.lang.Long".equals(typeName) ||
                "java.lang.String".equals(typeName)){
            isSimpleType = true;
        }
        return isSimpleType;
    }

    private static String toFirstUpperCase(String seq){
        return (char)(seq.charAt(0)-32)+seq.substring(1);
    }
    private static void invoke(Object instance, Method method, String typeName, Object val) throws InvocationTargetException, IllegalAccessException {
        if("boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)){ method.invoke(instance, Boolean.parseBoolean((String) val)); }
        if("char".equals(typeName) || "java.lang.Character".equals(typeName)){ method.invoke(instance, (char) val); }
        if("float".equals(typeName) || "java.lang.Float".equals(typeName)){ method.invoke(instance, Float.parseFloat((String) val)); }
        if("double".equals(typeName) || "java.lang.Double".equals(typeName)){ method.invoke(instance, Double.parseDouble((String) val)); }
        if("byte".equals(typeName) || "java.lang.Byte".equals(typeName)){ method.invoke(instance, Byte.parseByte((String) val)); }
        if("short".equals(typeName) || "java.lang.Short".equals(typeName)){ method.invoke(instance, Short.parseShort((String) val)); }
        if("int".equals(typeName) || "java.lang.Integer".equals(typeName)){ method.invoke(instance, Integer.parseInt((String) val)); }
        if("long".equals(typeName) || "java.lang.Long".equals(typeName)){ method.invoke(instance, Long.parseLong((String) val)); }
        if("java.lang.String".equals(typeName)){ method.invoke(instance, (String)val); }
    }

    /**
     * 设置反射方法的参数
     * @param args
     * @param i
     * @param typeName
     * @param val
     */
    public static void setArgs(Object[] args, int i, String typeName, Object val) {
        if("boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)){ args[i] = Boolean.parseBoolean((String) val); }
        if("char".equals(typeName) || "java.lang.Character".equals(typeName)){ args[i] = (char) val; }
        if("float".equals(typeName) || "java.lang.Float".equals(typeName)){ args[i] = Float.parseFloat((String) val); }
        if("double".equals(typeName) || "java.lang.Double".equals(typeName)){ args[i] = Double.parseDouble((String) val); }
        if("byte".equals(typeName) || "java.lang.Byte".equals(typeName)){ args[i] = Byte.parseByte((String) val); }
        if("short".equals(typeName) || "java.lang.Short".equals(typeName)){ args[i] = Short.parseShort((String) val); }
        if("int".equals(typeName) || "java.lang.Integer".equals(typeName)){ args[i] = Integer.parseInt((String) val); }
        if("long".equals(typeName) || "java.lang.Long".equals(typeName)){ args[i] = Long.parseLong((String) val); }
        if("java.lang.String".equals(typeName)){ args[i] = (String)val; }
    }
}
