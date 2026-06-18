package com.weiqiang.personal_crm_backend.common;

import lombok.Getter;

/**
 * 统一错误码枚举
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    PARAMS_ERROR(40001, "validation failed"),
    UNAUTHORIZED(40101, "unauthorized or invalid token"),
    FORBIDDEN(40301, "forbidden"),
    NOT_FOUND(40401, "not found"),
    CONFLICT(40901, "state conflict"),
    SYSTEM_ERROR(50001, "internal server error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
