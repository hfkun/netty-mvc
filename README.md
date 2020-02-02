

## 基于Netty的MVC框架

#### 基本原理

类似springboot，在运用启动的时候回去扫描启动类所在包下面的控制器注解，
使用netty通讯，接收http请求，根据请求地址，通过反射得到结果返回给客户端

#### 依赖

jkd1.8，fastjson，snakeyaml



#### 使用

* 依赖：

```xml
<dependency>    
  <groupId>com.nettymvc</groupId>
  <artifactId>netty-mvc</artifactId>
  <version>1.0</version>
</dependency>
```

* 配置类：application.yml (系统默认会读取classpath下的application.yml)

``` properties
server:
  port: 8089 # 端口配置，可选，默认8080
```

* 启动类：

```java
public class App {
    public static void main(String[] args) {
        NettyMvcApplication.run(App.class, args);
    }
}
```

* Controller层代码：

```java
import com.nettymvc.annotation.RequestBody;
import com.nettymvc.annotation.RequestMapping;
import com.nettymvc.annotation.RequestParam;
import com.nettymvc.annotation.RestController;
import io.netty.handler.codec.http.HttpRequest;
import net.test.domain.Bill;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/find")
    public String find(@RequestParam("id") String id,
                       HttpRequest req){
        System.out.println(req.uri());
        return "find, netty-mvc, id:"+id;
    }

    @RequestMapping(value = "/update", method = "POST")
    public String update(@RequestParam("id") String id,
                         @RequestParam("type") Integer type,
                         @RequestBody Bill bill){
        return "update, netty-mvc, id:"+bill.getId() + ", type:"+type;
    }
}
```

> 注意：
>
> * 方法参数如果是简单类型（包含String），需要注解**@RequestParam** ，因为jdk在编译时默认并不会把参数名带上，必须指定，才能自动注入
>
> * 方法参数如果是复杂对象（Bill），其注解 **@RequestBody**可以省略
>
> * 如果需要使用**HttpRequest**，可导入**io.netty.handler.codec.http.HttpRequest**，Response没有提供


#### 不足的地方
>目前的实体在自动注入时，没有考虑List Set等集合对象









