package com.example.delicious.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.PraiseInfo;
import com.example.delicious.mapper.PraiseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PraiseInfoService {
    @Autowired
    private PraiseInfoMapper praiseInfoMapper;
    /**
     * 点赞
     */
    public PraiseInfo add(PraiseInfo info){
        praiseInfoMapper.insert(info);
        return info;
    }
    /**
     * 取消点赞
     */
    public void delete(Long id){
        praiseInfoMapper.deleteById(id);
    }
    /**
     * 更新点赞信息
     */
    public void update(PraiseInfo info){
        praiseInfoMapper.updateById(info);
    }
    /**
     * id查询
     */
    public PraiseInfo findById(Long id){
        return praiseInfoMapper.selectById(id);
    }
    /**
     * 点赞列表
     */
    public List<PraiseInfo> findAll(){
        return praiseInfoMapper.selectList(null);
    }
    /**
     * 分页查询
     */
    public Page<PraiseInfo> findPage(String name,Integer pageNum,Integer pageSize){
        LambdaQueryWrapper<PraiseInfo> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&& !"null".equals(name)){
            wrapper.like(PraiseInfo::getName,name);
        }
        return praiseInfoMapper.selectPage(new Page<>(pageNum,pageSize),wrapper);
    }
    /**
     * 查询是否点赞
     */
    public Boolean check(Long foodsId,Long userId){
        return praiseInfoMapper.selectCount(
                new LambdaQueryWrapper<PraiseInfo>()
                        .eq(PraiseInfo::getFoodsId,foodsId)
                        .eq(PraiseInfo::getUserId,userId)
        )>0;
    }
}
