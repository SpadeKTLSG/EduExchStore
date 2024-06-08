package com.shop.admin.controller;


import com.alibaba.fastjson.JSON;
import com.shop.common.websocket.WebSocketServer;
import com.shop.pojo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务端客服
 *
 * @author SK
 * @date 2024/06/07
 */
@Slf4j
@Tag(name = "Customer", description = "客服")
@RequestMapping("/customer/admin")  //customer为客户端和服务端的统一前缀
@RestController
public class CustomerController {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 向客户端推送消息
     */
    @PostMapping("/service")
    @Operation(summary = "服务端向客户端推送消息")
    public Result serviceSendMes(@RequestBody String mes, Session session) {
        //测试使用简单推送模式
        webSocketServer.sendToAllClient(JSON.toJSONString(mes));
        return Result.success();
    }
    //http://localhost:8085/customer/admin/service


}
