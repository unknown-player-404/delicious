package com.example.delicious.entity;

import lombok.Data;


/**
 * 请求体
 */
@Data
public class Account {
    private Long id;
    private String name;
    private String password;
    private String newPassword;
    private String nickName;
    private Integer level;
}
