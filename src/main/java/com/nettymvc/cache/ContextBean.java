package com.nettymvc.cache;

import java.util.HashMap;
import java.util.Map;

public class ContextBean {
    private static Map<String, Object> contextBean = new HashMap<>();

    public static Object getBean(String beanName){
        return contextBean.get(beanName);
    }
    public static Object registerBean(String beanName, Object obj){
        contextBean.put(beanName, obj);
        return obj;
    }
}
