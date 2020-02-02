package com.nettymvc.util;

import com.nettymvc.annotation.RequestMapping;
import com.nettymvc.cache.ContextBean;
import com.nettymvc.cache.RequestMappingMap;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析加有RequestMapping注解的控制器
 * 放到缓存里
 */
public class RequestMappingResolver {
    // 要扫描解析的包名
    private String basePackage;
    public RequestMappingResolver(String basePackage){
        this.basePackage = basePackage;
    }

    public void resolve() throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        Enumeration<URL> resources = contextClassLoader.getResources(basePackage.replace(".", "/"));
        while (resources.hasMoreElements()) {
            String packagePath = URLDecoder.decode(resources.nextElement().getFile(), "UTF-8");
            getRequestMappingClassMap(basePackage, packagePath);
        }
    }
    // 这里并没有考虑 jar 的加载
    private void getRequestMappingClassMap(String basePackage, String packagePath) throws Exception {
        File dir = new File(packagePath);

        if(!dir.exists() || !dir.isDirectory()){
            return ;
        }

        String fileName;
        for(File f : dir.listFiles()){
            if(f.isDirectory()){
                getRequestMappingClassMap(basePackage+"."+f.getName(), f.getAbsolutePath());
            }else{
                fileName = f.getName();
                if(fileName.endsWith(".class")){
                    //判断有无分布式事物注解
                    String className = fileName.substring(0, fileName.length() - 6);
//                    System.out.println(basePackage+"."+className);
                    resolveRequestMappingAnnotation(basePackage, className);
                }
            }
        }
    }
    // 只取public的方法
    private void resolveRequestMappingAnnotation(String basePackage, String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clazz = Class.forName(basePackage+"."+className);
        String classRequestPath = "";
        Annotation clazzAnnotation = clazz.getAnnotation(RequestMapping.class);
        if(clazzAnnotation!=null){
            classRequestPath = ((RequestMapping)clazzAnnotation).value();
            if(!classRequestPath.startsWith("/")){
                classRequestPath = "/"+classRequestPath;
            }
        }

        Object bean = ContextBean.registerBean(className, clazz.newInstance());
        Annotation methodAnnotation = null;
        String methodRequestPath = "";
        for (Method method : clazz.getMethods()) {
            methodAnnotation = method.getAnnotation(RequestMapping.class);
            if(methodAnnotation != null){
                methodRequestPath = ((RequestMapping)methodAnnotation).value();
                if(!methodRequestPath.startsWith("/")){
                    methodRequestPath = "/"+methodRequestPath;
                }

                Map<String, Object> mapper = new HashMap<>();
                mapper.put("bean", bean);
                mapper.put("method", method);
                mapper.put("httpMethod", ((RequestMapping)methodAnnotation).method());
                RequestMappingMap.addRequestMapping(classRequestPath+methodRequestPath, mapper);
            }
        }
    }
}
