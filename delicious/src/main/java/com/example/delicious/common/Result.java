package com.example.delicious.common;

import lombok.Data;

@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    /**
     * 带参
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 无参
     */
    public static <T> Result<T> success() {
        return Result.success(null);
    }

    /**
     * 失败
     */
    public static <T> Result<T> error(String code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

}
