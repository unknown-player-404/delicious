package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.entity.UserInfo;

import java.util.List;

public interface UserInfoService extends IService<UserInfo> {
    UserInfo login(String name,String password);

    UserInfo add(UserInfo info);

    void delete(Long id);

    void update(UserInfo info);

    UserInfo findById(Long id);

    UserInfo findByName(String name);

    List<UserInfo> findAll();

    Page<UserInfo> findPage(String name,Integer pageNum,Integer pageSize);
}
