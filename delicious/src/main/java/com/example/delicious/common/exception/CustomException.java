package com.example.delicious.common.exception;


import lombok.Data;

@Data
public class CustomException extends RuntimeException{
    private String code;

    public CustomException(String code,String message){
        super(message);
        this.code=code;
    }


}
