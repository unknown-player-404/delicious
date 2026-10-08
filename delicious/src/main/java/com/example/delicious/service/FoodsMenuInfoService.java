package com.example.delicious.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.FoodsMenuInfo;
import com.example.delicious.mapper.FoodsMenuInfoMapper;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodsMenuInfoService {
    @Autowired
    private FoodsMenuInfoMapper foodsMenuInfoMapper;

    /**
     * 增加菜单
     */
    public FoodsMenuInfo add(FoodsMenuInfo info) {
        foodsMenuInfoMapper.insert(info);
        return info;
    }

    /**
     * 删除菜单
     */
    public void delete(Long id) {
        foodsMenuInfoMapper.deleteById(id);
    }

    /**
     * 修改菜单
     */
    public void update(FoodsMenuInfo info) {
        foodsMenuInfoMapper.updateById(info);
    }

    /**
     * 按id查询
     */
    public FoodsMenuInfo findById(Long id) {
        return foodsMenuInfoMapper.selectById(id);
    }

    /**
     * 查询列表
     */
    public List<FoodsMenuInfo> findAll() {
        return foodsMenuInfoMapper.selectList(null);
    }

    /**
     * 分页查询
     */
    public Page<FoodsMenuInfo> findPage(String name, Long classifyId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)&& !"null".equals(name)) {
            wrapper.like(FoodsMenuInfo::getName, name);
        }
        if (classifyId != null) {
            wrapper.eq(FoodsMenuInfo::getClassifyId, classifyId);
        }
        return foodsMenuInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 按User信息来分页查询
     */
    public Page<FoodsMenuInfo> findPageByUser(String name, String userName, Integer level,
                                              Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(FoodsMenuInfo::getName, name);
        }
        if (StringUtils.isNotBlank(userName)) {
            wrapper.eq(FoodsMenuInfo::getUserName, userName);
        }
        if (level != null) {
            wrapper.eq(FoodsMenuInfo::getLevel, level);
        }
        return foodsMenuInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

}
