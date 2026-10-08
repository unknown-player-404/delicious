package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.CollectInfo;
import com.example.delicious.mapper.CollectInfoMapper;
import com.example.delicious.service.CollectInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectInfoServiceImpl extends ServiceImpl<CollectInfoMapper, CollectInfo> implements CollectInfoService {
    @Resource
    private CollectInfoMapper collectInfoMapper;

    @Override
    public CollectInfo add(CollectInfo info) {
        if (info.getFoodsId() == null || info.getUserId() == null) {
            throw new CustomException("400", "参数错误");
        }
        Long count = collectInfoMapper.selectCount(
                new LambdaQueryWrapper<CollectInfo>()
                        .eq(CollectInfo::getFoodsId, info.getFoodsId())
                        .eq(CollectInfo::getUserId, info.getUserId())
        );
        if (count > 0) {
            throw new CustomException("400", "已收藏");
        }
        collectInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        collectInfoMapper.deleteById(id);
    }

    @Override
    public void update(CollectInfo info) {
        collectInfoMapper.updateById(info);
    }

    @Override
    public CollectInfo findById(Long id) {
        return collectInfoMapper.selectById(id);
    }

    @Override
    public List<CollectInfo> findAll() {
        return collectInfoMapper.selectList(null);
    }

    @Override
    public CollectInfo findByName(String name) {
        LambdaQueryWrapper<CollectInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(CollectInfo::getName, name);
        }
        return collectInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<CollectInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CollectInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(CollectInfo::getName, name);
        }
        return collectInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean check(Long foodsId, Long userId) {
        return collectInfoMapper.selectCount(
                new LambdaQueryWrapper<CollectInfo>()
                        .eq(CollectInfo::getFoodsId, foodsId)
                        .eq(CollectInfo::getUserId, userId)
        ) > 0;
    }
}
