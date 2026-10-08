package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.ClassifyInfo;
import com.example.delicious.mapper.ClassifyInfoMapper;
import com.example.delicious.service.ClassifyInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassifyInfoServiceImpl extends ServiceImpl<ClassifyInfoMapper, ClassifyInfo> implements ClassifyInfoService {
    @Resource
    private ClassifyInfoMapper classifyInfoMapper;

    @Override
    public ClassifyInfo add(ClassifyInfo info) {
        Long count = classifyInfoMapper.selectCount(
                new LambdaQueryWrapper<ClassifyInfo>().eq(ClassifyInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "菜品分类已存在");
        }
        classifyInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        classifyInfoMapper.deleteById(id);
    }

    @Override
    public void update(ClassifyInfo info) {
        classifyInfoMapper.updateById(info);
    }

    @Override
    public ClassifyInfo findById(Long id) {
        return classifyInfoMapper.selectById(id);
    }

    @Override
    public List<ClassifyInfo> findAll() {
        return classifyInfoMapper.selectList(null);
    }

    @Override
    public ClassifyInfo findByName(String name) {
        LambdaQueryWrapper<ClassifyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(ClassifyInfo::getName, name);
        }
        return classifyInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<ClassifyInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ClassifyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(ClassifyInfo::getName, name);
        }
        return classifyInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
