package com.shop.common.context;

import com.shop.pojo.dto.UserLocalDTO;

/**
 * 用户上下文
 *
 * @author SK
 * @date 2024/06/05
 */
public class UserHolder {

    /**
     * 用户ThreadLocal
     */
    private static final ThreadLocal<UserLocalDTO> userTL = new ThreadLocal<>();

    /**
     * 保存用户
     */
    public static void saveUser(UserLocalDTO user) {
        userTL.set(user);
    }

    /**
     * 获取用户
     */
    public static UserLocalDTO getUser() {
        return userTL.get();
    }

    /**
     * 移除用户
     */
    public static void removeUser() {
        userTL.remove();
    }
}
