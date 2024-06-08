package com.shop.common.aspect;


import com.shop.common.annotation.AutoFill;
import com.shop.common.constant.AutoFillConstant;
import com.shop.common.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.shop.serve.service.*.*(..)) && @annotation(com.shop.common.annotation.AutoFill)")
    public void autoFillPointCut() {
    }


    /**
     * 前置通知中进行公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {

        log.info("进行公共字段自动填充");

        //获取到当前被拦截的方法上的数据库操作类型
        //反射处理
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();//准备赋值的数据

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
            try {
                //? 这里强绑定了实体类Notice的更新方法, 仅为演示, 因为没有设置日志模块, 并不存在许多要更新的字段.
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_PUBLISH_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_UPDATE_TIME, LocalDateTime.class);
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
