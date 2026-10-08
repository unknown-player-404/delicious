package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.PraiseInfo;
import com.example.delicious.mapper.PraiseInfoMapper;
import com.example.delicious.service.PraiseInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PraiseInfoServiceImpl extends ServiceImpl<PraiseInfoMapper, PraiseInfo> implements PraiseInfoService {
    @Resource
    private PraiseInfoMapper praiseInfoMapper;

    @Override
    public PraiseInfo add(PraiseInfo info) {
        if (info.getFoodsId() == null || info.getUserId() == null) {
            throw new CustomException("400", "参数错误");
        }
        Long count = praiseInfoMapper.selectCount(
                new LambdaQueryWrapper<PraiseInfo>()
                        .eq(PraiseInfo::getFoodsId, info.getFoodsId())
                        .eq(PraiseInfo::getUserId, info.getUserId())
        );
        if (count > 0) {
            throw new CustomException("400", "已点赞");
        }
        praiseInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        praiseInfoMapper.deleteById(id);
    }

    @Override
    public void update(PraiseInfo info) {
        praiseInfoMapper.updateById(info);
    }

    @Override
    public PraiseInfo findById(Long id) {
        return praiseInfoMapper.selectById(id);
    }

    @Override
    public List<PraiseInfo> findAll() {
        return praiseInfoMapper.selectList(null);
    }

    @Override
    public PraiseInfo findByName(String name) {
        LambdaQueryWrapper<PraiseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(PraiseInfo::getName, name);
        }
        return praiseInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<PraiseInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PraiseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(PraiseInfo::getName, name);
        }
        return praiseInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Boolean check(Long foodsId, Long userId) {
        return praiseInfoMapper.selectCount(
                new LambdaQueryWrapper<PraiseInfo>()
                        .eq(PraiseInfo::getFoodsId, foodsId)
                        .eq(PraiseInfo::getUserId, userId)
        ) > 0;
    }
}
