package com.shop.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.shop.common.context.EmployeeHolder;
import com.shop.common.exception.AccountNotFoundException;
import com.shop.common.exception.NotLoginException;
import com.shop.pojo.dto.EmployeeLocalDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shop.common.constant.MessageConstants.ACCOUNT_NOT_FOUND;
import static com.shop.common.constant.MessageConstants.USER_NOT_LOGIN;
import static com.shop.common.constant.RedisConstants.LOGIN_USER_KEY_ADMIN;
import static com.shop.common.constant.RedisConstants.LOGIN_USER_TTL_ADMIN;


/**
 * 管理端刷新token拦截器
 *
 * @author SK
 * @date 2024/06/06
 */
@Slf4j
public class AdminRefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public AdminRefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("authorization");// 获取请求头中的token
        if (StrUtil.isBlank(token)) throw new NotLoginException(USER_NOT_LOGIN);

        if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
            token = token.substring(7);
        }


        String key = LOGIN_USER_KEY_ADMIN + token;     //基于TOKEN获取redis中的管理员
        Map<Object, Object> employeeMap = stringRedisTemplate.opsForHash().entries(key);
        log.info("操作管理员信息: " + employeeMap);

        if (employeeMap.isEmpty()) throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);

        EmployeeLocalDTO employeeLocalDTO = BeanUtil.fillBeanWithMap(employeeMap, new EmployeeLocalDTO(), false);
        EmployeeHolder.saveEmployee(employeeLocalDTO);

        stringRedisTemplate.expire(key, LOGIN_USER_TTL_ADMIN, TimeUnit.MINUTES);      // 一次用户操作, 能够刷新token有效期

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        EmployeeHolder.removeEmployee(); // 移除管理员
    }
}
