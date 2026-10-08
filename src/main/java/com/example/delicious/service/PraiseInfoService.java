package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.PraiseInfo;

import java.util.List;
import java.util.Map;

public interface PraiseInfoService extends IService<PraiseInfo> {
    PraiseInfo add(PraiseInfo info);

    void delete(Long id);

    void update(PraiseInfo info);

    PraiseInfo findById(Long id);

    PraiseInfo findByName(String name);

    List<PraiseInfo> findAll();

    Page<PraiseInfo> findPage(String name, Integer pageNum, Integer pageSize);

    Boolean check(Long foodsId, Long userId);

    Long getCount(Long foodsId);

    /** 批量获取点赞数，列表页一次查一批，避免 N 次请求 */
    Map<Long, Long> getCounts(List<Long> foodsIds);
}
