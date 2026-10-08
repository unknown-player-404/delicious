package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.mapper.AdminInfoMapper;
import com.example.delicious.service.AdminInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo> implements AdminInfoService {

    @Resource
    private AdminInfoMapper adminInfoMapper;

    @Override
    public AdminInfo login(String name, String password) {
        if (StringUtils.isBlank(name)) {
            throw new CustomException("400", "用户名为空");
        }
        AdminInfo info = adminInfoMapper.selectOne(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getName, name)
        );
        if (info == null || !password.equals(info.getPassword())) {
            throw new CustomException("400", "用户名或密码错误");
        }
        return info;
    }

    @Override
    public AdminInfo add(AdminInfo info) {
        Long count = adminInfoMapper.selectCount(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "用户已存在");
        }
        adminInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        adminInfoMapper.deleteById(id);
    }

    @Override
    public void update(AdminInfo info) {
        adminInfoMapper.updateById(info);
    }

    @Override
    public AdminInfo findById(Long id) {
        return adminInfoMapper.selectById(id);
    }

    @Override
    public List<AdminInfo> findAll() {
        return adminInfoMapper.selectList(null);
    }

    @Override
    public AdminInfo findByName(String name) {
        LambdaQueryWrapper<AdminInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(AdminInfo::getName, name);
        }
        return adminInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<AdminInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AdminInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(AdminInfo::getName, name);
        }
        return adminInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
