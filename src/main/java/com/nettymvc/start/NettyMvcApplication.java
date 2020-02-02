package com.nettymvc.start;

import com.nettymvc.cache.ApplicationYaml;
import com.nettymvc.http.HttpServer;
import com.nettymvc.util.RequestMappingResolver;

import java.util.HashMap;

public class NettyMvcApplication {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

    public static void run(Class clazz, String[] args){
        try{
            RequestMappingResolver requestMappingResolver = new RequestMappingResolver(clazz.getPackage().getName());
            requestMappingResolver.resolve();

            HashMap configMap = ApplicationYaml.getConfigMap();
            if(configMap.get("server") != null){
                Integer p = (Integer) ((HashMap)configMap.get("server")).get("port");
                if(p != null){
                    PORT = p;
                }
            }

            HttpServer.start(SSL, PORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
