package com.nettymvc.cache;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 配置文件解析
 * yaml
 * yml
 */
public class ApplicationYaml {
    private static String configFile = "application.yml";
    private static Yaml yaml = new Yaml();
    private static HashMap configMap = new HashMap();

    static {
        InputStream resourceAsStream = ApplicationYaml.class.getClassLoader().getResourceAsStream(configFile);
        if(resourceAsStream != null){
            configMap = yaml.loadAs(resourceAsStream, HashMap.class);
        }
    }

    public static HashMap getConfigMap() {
        return configMap;
    }
}
