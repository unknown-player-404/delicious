package com.example.delicious.common.exception;


import com.example.delicious.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 自定义异常
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
        log.error("服务器内部错误", e);
        return Result.error("500","服务器内部错误"+e.getMessage());
    }
}
