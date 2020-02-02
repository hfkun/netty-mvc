package com.nettymvc.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制器集合requestMappingMap
 * path - {
 *     bean
 *     method
 *     httpMethod
 * }
 */
public class RequestMappingMap {
    private static Map<String, Map<String, Object>> requestMappingMap = new HashMap<>();

    public static Map<String, Object> getRequestMapping(String path){
        return requestMappingMap.get(path);
    }

    public static Object addRequestMapping(String path, Map<String, Object> obj){
        requestMappingMap.put(path, obj);
        return obj;
    }
}
