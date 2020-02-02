package net.test.controller;

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
