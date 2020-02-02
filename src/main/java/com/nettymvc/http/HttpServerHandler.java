package com.nettymvc.http;

import com.alibaba.fastjson.JSONObject;
import com.nettymvc.annotation.RequestBody;
import com.nettymvc.annotation.RequestParam;
import com.nettymvc.cache.RequestMappingMap;
import com.nettymvc.util.OrmUtil;
import com.nettymvc.util.UriResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

/**
 * http请求处理类
 * 根据URI获取控制器反射得到结果，返回给前端
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            String httpMethod = req.method().name(); // GET
            String uri = req.uri(); // /test/hello
            if(uri.indexOf("?") > 0){
                uri = uri.substring(0, uri.indexOf("?"));
            }
            if(uri.indexOf("#") > 0){
                uri = uri.substring(0, uri.indexOf("#"));
            }
            System.out.println(httpMethod);
            System.out.println(uri);

            String res = "";
            HttpResponseStatus httpResponseStatus = HttpResponseStatus.OK;
            Map<String, Object> requestMapping = RequestMappingMap.getRequestMapping(uri);
            if(requestMapping == null){
                res = "404, no found, url:"+uri+", method:"+httpMethod;
                httpResponseStatus = HttpResponseStatus.NOT_FOUND;
            }else if(!httpMethod.equals(requestMapping.get("httpMethod"))){
                res = "404, no found, url:"+uri+", method:"+httpMethod;
                httpResponseStatus = HttpResponseStatus.NOT_FOUND;
            }else{
                try {
                    //解析请求参数
                    Map<String, Object> requestParamMap = UriResolver.resolve(req.uri());
                    //post body
                    if(HttpMethod.POST.name().equals(httpMethod)) {
                        ByteBuf postContent = ((FullHttpRequest) msg).content();
                        byte[] postDataByte = new byte[postContent.readableBytes()];
                        postContent.readBytes(postDataByte);
                        Map jsonMap = JSONObject.parseObject(new String(postDataByte));
                        requestParamMap.putAll(jsonMap);
                    }

                    //通过反射对应控制器得到结果
                    Method method = (Method) requestMapping.get("method");
                    // 参数自动注入
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                        RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
                        if(requestParam != null){
                            String paramName = requestParam.value();
                            Object paramValue = requestParamMap.get(paramName);

                            if(paramValue == null){
                                if(requestParam.required()){
                                    throw new RuntimeException("parameter ["+paramName+"] no found");
                                }else{
                                    paramValue = requestParam.defaultValue();
                                }
                            }
                            OrmUtil.setArgs(args, i, parameters[i].getType().getName(), paramValue);
//                            args[i] = paramValue;
                        }else if(requestBody != null){
                            args[i] = OrmUtil.autoSetObject(requestParamMap, parameters[i].getType());
                        }else{ //没有加注解的情况
                            //判断是不是复杂类型
                            //自动注入
                            String typeName = parameters[i].getType().getName();
                            if("io.netty.handler.codec.http.HttpRequest".equals(typeName)){
                                args[i] = req;
                            }else if(OrmUtil.isSimpleType(typeName)){
                                throw new RuntimeException("parameter type["+typeName+"] no assign");
                            }else{
                                args[i] = OrmUtil.autoSetObject(requestParamMap, parameters[i].getType());
                            }
                        }
                    }

                    res = (String) method.invoke(requestMapping.get("bean"), args);
                } catch (Exception e) {
                    e.printStackTrace();
                    res = "500, error: "+e.getMessage();
                    httpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                }
            }

            boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), httpResponseStatus,
                                                                    Unpooled.wrappedBuffer(res.getBytes()));
            response.headers()
                    .set(CONTENT_TYPE, TEXT_PLAIN)
                    .setInt(CONTENT_LENGTH, response.content().readableBytes());

            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                }
            } else {
                // Tell the client we're going to close the connection.
                response.headers().set(CONNECTION, CLOSE);
            }

            ChannelFuture f = ctx.write(response);

            if (!keepAlive) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}