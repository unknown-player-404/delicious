package com.example.delicious.common.exception;


import com.example.delicious.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常处理
     */
    @ExceptionHandler(CustomException.class)
    public Result<Void> handlerCustomException(CustomException e){
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handlerException(Exception e){
        e.printStackTrace();
        return Result.error("500","服务器内部错误:"+e.getMessage());
    }
}
