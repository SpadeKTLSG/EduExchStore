package com.shop.guest.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.shop.common.websocket.WebSocketServer;
import com.shop.pojo.Result;
import com.shop.pojo.dto.OrderAllDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端客服模块
 *
 * @author SK
 * @date 2024/06/07
 */
@Slf4j
@Tag(name = "Customer", description = "客服")
@RequestMapping("/customer/guest") //customer为客户端和服务端的统一前缀
@RestController
public class CustomerController {

    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 针对交易请求客服接入
     */
    @PostMapping("/service")
    @Operation(summary = "客户端请求客服接入")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单信息", required = true))
    public Result serviceAskHelp(@RequestBody OrderAllDTO orderAllDTO, Session session) {

        //将订单信息转换为Map, 直接发送给客服端请求接入
        //注意, 使用Postman测试时无法发送WebSocket请求, 需要使用前端页面模式建立连接
        Map<String, Object> orderMap = BeanUtil.beanToMap(orderAllDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        //webSocketServer.onOpen(session, "guest/service"); //仅为测试使用
        webSocketServer.sendToAllClient(JSON.toJSONString(orderMap));
        return Result.success();
    }
    //http://localhost:8086/customer/guest/service


}
