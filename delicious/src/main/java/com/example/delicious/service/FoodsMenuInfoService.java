package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.ClassifyInfo;
import com.example.delicious.entity.FoodsMenuInfo;

import java.util.List;

public interface FoodsMenuInfoService extends IService<FoodsMenuInfo> {
    FoodsMenuInfo add(FoodsMenuInfo info);

    void delete(Long id);

    void update(FoodsMenuInfo info);

    FoodsMenuInfo findById(Long id);

    FoodsMenuInfo findByName(String name);

    List<FoodsMenuInfo> findAll();

    Page<FoodsMenuInfo> findPage(String name, Long classifyId, Integer pageNum, Integer pageSize);

}
