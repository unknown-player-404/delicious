package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.PraiseInfo;

import java.util.List;

public interface PraiseInfoService extends IService<PraiseInfo> {
    PraiseInfo add(PraiseInfo info);

    void delete(Long id);

    void update(PraiseInfo info);

    PraiseInfo findById(Long id);

    PraiseInfo findByName(String name);

    List<PraiseInfo> findAll();

    Page<PraiseInfo> findPage(String name, Integer pageNum, Integer pageSize);

    Boolean check(Long foodsId, Long userId);
}
