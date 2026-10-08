package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.CollectInfo;

import java.util.List;

public interface CollectInfoService extends IService<CollectInfo> {
    CollectInfo add(CollectInfo info);

    void delete(Long id);

    void update(CollectInfo info);

    CollectInfo findById(Long id);

    CollectInfo findByName(String name);

    List<CollectInfo> findAll();

    Page<CollectInfo> findPage(String name, Integer pageNum, Integer pageSize);

    boolean check(Long foodsId, Long userId);
}
