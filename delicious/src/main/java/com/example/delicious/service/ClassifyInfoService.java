package com.example.delicious.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.ClassifyInfo;
import com.example.delicious.mapper.ClassifyInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassifyInfoService {
    @Autowired
    private ClassifyInfoMapper classifyInfoMapper;

    /**
     * 增加分类
     */
    public ClassifyInfo add(ClassifyInfo info){
        classifyInfoMapper.insert(info);
        return info;
    }
    /**
     * 删除分类
     */
    public void delete(Long id){
        classifyInfoMapper.deleteById(id);
    }
    /**
     * 更新分类
     */
    public void update(ClassifyInfo info){
        classifyInfoMapper.updateById(info);
    }
    /**
     * 按id查询
     */
    public ClassifyInfo findById(Long id){
        return classifyInfoMapper.selectById(id);
    }
    /**
     * 查询全部分类
     */
    public List<ClassifyInfo> findAll(){
        return classifyInfoMapper.selectList(null);
    }
    /**
     * 分页查询
     */
    public Page<ClassifyInfo> findPage(String name,Integer pageNum,Integer pageSize){
        LambdaQueryWrapper<ClassifyInfo> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&& !"null".equals(name)){
            wrapper.like(ClassifyInfo::getName,name);
        }
        return classifyInfoMapper.selectPage(new Page<>(pageNum,pageSize),wrapper);
    }
}
