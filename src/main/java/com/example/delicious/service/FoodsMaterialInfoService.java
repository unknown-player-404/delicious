package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.FoodsMaterialInfo;

import java.util.List;

public interface FoodsMaterialInfoService extends IService<FoodsMaterialInfo> {
    FoodsMaterialInfo add(FoodsMaterialInfo info);

    void delete(Long id);

    void update(FoodsMaterialInfo info);

    FoodsMaterialInfo findById(Long id);

    FoodsMaterialInfo findByName(String name);

    List<FoodsMaterialInfo> findAll();

    Page<FoodsMaterialInfo> findPage(String name, Integer pageNum, Integer pageSize);
}
