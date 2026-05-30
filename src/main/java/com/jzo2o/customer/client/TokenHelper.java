package com.jzo2o.customer.client;

import cn.hutool.jwt.JWT;
import java.nio.charset.StandardCharsets;

public class TokenHelper {

    public static String generateTokenOfAdmin(String appId, String accessKeyId, String accessKeySecret, CurrentUser currentUser) {
        return createJwtToken(appId, accessKeyId, accessKeySecret, currentUser, "admin");
    }

    public static String generateTokenOfUser(String appId, String accessKeyId, String accessKeySecret, CurrentUser currentUser) {
        return createJwtToken(appId, accessKeyId, accessKeySecret, currentUser, "user");
    }

    private static String createJwtToken(String appId, String accessKeyId, String accessKeySecret, CurrentUser currentUser, String roleType) {
        // 使用项目中现有的 hutool 生成 JWT Token
        // 密钥使用配置文件中的 accessKeySecret
        return JWT.create()
                .setPayload("appId", appId)
                .setPayload("accessKeyId", accessKeyId)
                .setPayload("userId", currentUser.getId())
                .setPayload("nickName", currentUser.getNickName())
                .setPayload("userAvatar", currentUser.getUserAvatar())
                .setPayload("roleType", roleType)
                .setPayload("exp", System.currentTimeMillis() + 1000 * 60 * 60 * 24) // 设置1天过期
                .setKey(accessKeySecret.getBytes(StandardCharsets.UTF_8))
                .sign();
    }
}