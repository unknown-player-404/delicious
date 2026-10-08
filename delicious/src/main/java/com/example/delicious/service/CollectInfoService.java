package com.example.delicious.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.CollectInfo;
import com.example.delicious.mapper.CollectInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectInfoService {
    @Autowired
    private CollectInfoMapper collectInfoMapper;
    /**
     * 进行收藏
     */
    public CollectInfo add(CollectInfo info){
        collectInfoMapper.insert(info);
        return info;
    }
    /**
     * 取消收藏
     */
    public void delete(Long id){
        collectInfoMapper.deleteById(id);
    }
    /**
     * 更新收藏数据
     */
    public void update(CollectInfo info){
        collectInfoMapper.updateById(info);
    }
    /**
     * 按id查询
     */
    public CollectInfo findById(Long id){
        return collectInfoMapper.selectById(id);
    }
    /**
     * 查询全部
     */
    public List<CollectInfo> findAll(){
        return collectInfoMapper.selectList(null);
    }
    /**
     * 分页查询
     */
    public Page<CollectInfo> findPage(String name,Long userId,Integer pageNum,Integer pageSize){
        LambdaQueryWrapper<CollectInfo> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&& !"null".equals(name)){
            wrapper.like(CollectInfo::getName,name);
        }
        if(userId!=null){
            wrapper.eq(CollectInfo::getUserId,userId);
        }
        return collectInfoMapper.selectPage(new Page<>(pageNum,pageSize),wrapper);
    }
    /**
     * 查询是否收藏
     */
    public boolean check(Long foodsId,Long userId){
        return collectInfoMapper.selectCount(
                new LambdaQueryWrapper<CollectInfo>()
                        .eq(CollectInfo::getFoodsId,foodsId)
                        .eq(CollectInfo::getUserId,userId)
        )>0;
    }
}
