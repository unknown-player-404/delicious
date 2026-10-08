package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMaterialInfo;
import com.example.delicious.mapper.FoodsMaterialInfoMapper;
import com.example.delicious.service.FoodsMaterialInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodsMaterialInfoServiceImpl extends ServiceImpl<FoodsMaterialInfoMapper, FoodsMaterialInfo> implements FoodsMaterialInfoService {
    @Resource
    private FoodsMaterialInfoMapper foodsMaterialInfoMapper;

    @Override
    public FoodsMaterialInfo add(FoodsMaterialInfo info) {
        Long count = foodsMaterialInfoMapper.selectCount(
                new LambdaQueryWrapper<FoodsMaterialInfo>().eq(FoodsMaterialInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "食材已存在");
        }
        foodsMaterialInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        foodsMaterialInfoMapper.deleteById(id);
    }

    @Override
    public void update(FoodsMaterialInfo info) {
        foodsMaterialInfoMapper.updateById(info);
    }

    @Override
    public FoodsMaterialInfo findById(Long id) {
        return foodsMaterialInfoMapper.selectById(id);
    }

    @Override
    public List<FoodsMaterialInfo> findAll() {
        return foodsMaterialInfoMapper.selectList(null);
    }

    @Override
    public FoodsMaterialInfo findByName(String name) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(FoodsMaterialInfo::getName, name);
        }
        return foodsMaterialInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<FoodsMaterialInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(FoodsMaterialInfo::getName, name);
        }
        return foodsMaterialInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
