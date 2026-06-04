package com.tzmall.common.core.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.tzmall.common.core.exception.BizException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类：生成Token、解析Token
 */
public class JwtUtil {

    /**
     * 密钥：用于签名和验证Token（生产环境请替换为自己的安全密钥）
     * 注意：HMAC256算法要求密钥长度至少32位，太短会有安全风险
     */
    private static final String KEY = "tz-mall-secret-key-for-jwt-token-generation";

    /**
     * 接收业务数据，生成Token并返回
     * @param claims 业务数据（如用户ID、用户名、角色）
     * @return 生成的JWT Token
     */
    public static String genToken(Map<String, Object> claims) {
        // 1. 设置过期时间（当前时间 + 12小时）
        long expireTime = System.currentTimeMillis() + 1000 * 60 * 60 * 12;
        // 2. 平铺存储每个 claim（避免嵌套在"claims"键下导致下游解析失败）
        com.auth0.jwt.JWTCreator.Builder builder = JWT.create();
        if (claims != null) {
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder = builder.withClaim(entry.getKey(), (String) value);
                } else if (value instanceof Integer) {
                    builder = builder.withClaim(entry.getKey(), (Integer) value);
                } else if (value instanceof Long) {
                    builder = builder.withClaim(entry.getKey(), (Long) value);
                } else if (value instanceof Boolean) {
                    builder = builder.withClaim(entry.getKey(), (Boolean) value);
                } else if (value instanceof Double) {
                    builder = builder.withClaim(entry.getKey(), (Double) value);
                } else if (value != null) {
                    builder = builder.withClaim(entry.getKey(), value.toString());
                }
            }
        }
        return builder
                .withExpiresAt(new Date(expireTime))
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * 接收Token，验证Token并返回业务数据
     * @param token 前端传来的Token
     * @return 业务数据（如用户ID、用户名、角色）
     * @throws BizException Token无效（过期、篡改、格式错误等）
     */
    public static Map<String, Object> parseToken(String token) {
        try {
            Map<String, Claim> claimMap = JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(token)
                    .getClaims();
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, Claim> entry : claimMap.entrySet()) {
                Claim claim = entry.getValue();
                if (claim == null) {
                    continue;
                }
                if (claim.asString() != null) {
                    result.put(entry.getKey(), claim.asString());
                } else if (claim.asLong() != null) {
                    result.put(entry.getKey(), claim.asLong());
                } else if (claim.asInt() != null) {
                    result.put(entry.getKey(), claim.asInt());
                } else if (claim.asBoolean() != null) {
                    result.put(entry.getKey(), claim.asBoolean());
                } else if (claim.asDouble() != null) {
                    result.put(entry.getKey(), claim.asDouble());
                }
            }
            return result;
        } catch (JWTVerificationException e) {
            throw new BizException(401, "Token无效");
        }
    }
}