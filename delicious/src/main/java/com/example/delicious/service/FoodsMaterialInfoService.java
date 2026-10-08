package com.example.delicious.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.FoodsMaterialInfo;
import com.example.delicious.mapper.FoodsMaterialInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodsMaterialInfoService {
    @Autowired
    private FoodsMaterialInfoMapper foodsMaterialInfoMapper;

    /**
     * 增加食材
     */
    public FoodsMaterialInfo add(FoodsMaterialInfo info) {
        foodsMaterialInfoMapper.insert(info);
        return info;
    }

    /**
     * 删除食材
     */
    public void delete(Long id) {
        foodsMaterialInfoMapper.deleteById(id);
    }

    /**
     * 更新食材
     */
    public void update(FoodsMaterialInfo info) {
        foodsMaterialInfoMapper.updateById(info);
    }

    /**
     * 根据id查询食材
     */
    public FoodsMaterialInfo findById(Long id) {
        return foodsMaterialInfoMapper.selectById(id);
    }

    /**
     * 查询所有食材列表
     */
    public List<FoodsMaterialInfo> findAll() {
        return foodsMaterialInfoMapper.selectList(null);
    }

    /**
     * 分页查询
     */
    public Page<FoodsMaterialInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            wrapper.like(FoodsMaterialInfo::getName, name);
        }
        return foodsMaterialInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 按user信息来分页查询
     */
    public Page<FoodsMaterialInfo> findPageByUser(String name, String userName, Integer level,
                                                  Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)&& !"null".equals(name)) {
            wrapper.like(FoodsMaterialInfo::getName, name);
        }
        if (StringUtils.isNotBlank(userName)) {
            wrapper.eq(FoodsMaterialInfo::getUserName, userName);
        }
        if (level != null) {
            wrapper.eq(FoodsMaterialInfo::getLevel, level);
        }
        return foodsMaterialInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
