package com.nettymvc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * uri解析
 */
public class UriResolver {
    /**
     * 解析uri上的请求参数
     * @param uri
     * @return
     */
    public static Map<String, Object> resolve(String uri){
        Map<String, Object> requestParamMap = new HashMap<>();
        if(uri.indexOf("?") > 0){
            String params = uri.substring(uri.indexOf("?")+1);
            if(params.indexOf("#") > 0){
                params = params.substring(0, params.indexOf("#"));
            }

            String[] kvs = params.split("&");
            for (int i = 0; i < kvs.length; i++) {
                String[] kvMap = kvs[i].split("=");
                requestParamMap.put(kvMap[0], kvMap[1]);
            }
        }

        return requestParamMap;
    }
}
