package com.example.delicious.common.exception;

/**
 * 自定义异常
 */
public class CustomException extends RuntimeException {
    private final String code;

    public CustomException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
