package com.example.delicious.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.AdminInfo;

import java.util.List;

public interface AdminInfoService extends IService<AdminInfo> {

    AdminInfo login(String name,String password);

    AdminInfo add(AdminInfo info);

    void delete(Long id);

    void update(AdminInfo info);

    AdminInfo findById(Long id);

    AdminInfo findByName(String name);

    List<AdminInfo> findAll();

    Page<AdminInfo> findPage(String name, Integer pageNum, Integer pageSize);
}
