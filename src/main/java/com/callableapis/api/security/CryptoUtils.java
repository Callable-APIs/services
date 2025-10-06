package com.callableapis.api.security;

import com.callableapis.api.config.AppConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CryptoUtils {
    private CryptoUtils() {}

    public static String computeApiKeyForIdentity(String oidcIdentity) {
        String salt = AppConfig.getApiKeySalt();
        String input = salt + ":" + oidcIdentity;
        byte[] digest = sha256(input.getBytes(StandardCharsets.UTF_8));
        return toHex(digest);
    }

    private static byte[] sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
