package com.shop.guest;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户端启动类
 *
 * @author SK
 * @date 2024/06/05
 */
@Slf4j
@SpringBootApplication
@EnableScheduling //开启定时任务
@ComponentScan("com.shop")
@MapperScan(basePackages = {"com.shop.serve.mapper"})
public class GuestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuestApplication.class, args);
    }

}
