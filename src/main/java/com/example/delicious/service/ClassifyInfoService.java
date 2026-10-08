package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.ClassifyInfo;

import java.util.List;

public interface ClassifyInfoService extends IService<ClassifyInfo> {
    ClassifyInfo add(ClassifyInfo info);

    void delete(Long id);

    void update(ClassifyInfo info);

    ClassifyInfo findById(Long id);

    ClassifyInfo findByName(String name);

    List<ClassifyInfo> findAll();

    Page<ClassifyInfo> findPage(String name, Integer pageNum, Integer pageSize);
}
