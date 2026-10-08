package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMenuInfo;
import com.example.delicious.mapper.FoodsMenuInfoMapper;
import com.example.delicious.service.FoodsMenuInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodsMenuInfoServiceImpl extends ServiceImpl<FoodsMenuInfoMapper, FoodsMenuInfo> implements FoodsMenuInfoService {
    @Resource
    private FoodsMenuInfoMapper FoodsMenuInfoMapper;

    @Override
    public FoodsMenuInfo add(FoodsMenuInfo info) {
        Long count = FoodsMenuInfoMapper.selectCount(
                new LambdaQueryWrapper<FoodsMenuInfo>().eq(FoodsMenuInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "菜单已存在");
        }
        FoodsMenuInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        FoodsMenuInfoMapper.deleteById(id);
    }

    @Override
    public void update(FoodsMenuInfo info) {
        FoodsMenuInfoMapper.updateById(info);
    }

    @Override
    public FoodsMenuInfo findById(Long id) {
        return FoodsMenuInfoMapper.selectById(id);
    }

    @Override
    public List<FoodsMenuInfo> findAll() {
        return FoodsMenuInfoMapper.selectList(null);
    }

    @Override
    public FoodsMenuInfo findByName(String name) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(FoodsMenuInfo::getName, name);
        }
        return FoodsMenuInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<FoodsMenuInfo> findPage(String name, Long classifyId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(FoodsMenuInfo::getName, name);
        }
        if (classifyId != null) {
            wrapper.eq(FoodsMenuInfo::getClassifyId, classifyId);
        }
        return FoodsMenuInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
