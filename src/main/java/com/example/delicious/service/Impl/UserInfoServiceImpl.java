package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.UserInfo;
import com.example.delicious.mapper.UserInfoMapper;
import com.example.delicious.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(String name, String password) {
        UserInfo info = userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getName, name)
        );
        if (info == null || !password.equals(info.getPassword())) {
            throw new CustomException("400", "用户名或密码错误");
        }
        return info;
    }

    @Override
    public UserInfo add(UserInfo info) {
        Long count = userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "用户已存在");
        }
        userInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        userInfoMapper.deleteById(id);
    }

    @Override
    public void update(UserInfo info) {
        userInfoMapper.updateById(info);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public UserInfo findByName(String name) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(UserInfo::getName, name);
        }
        return userInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectList(null);
    }

    @Override
    public Page<UserInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(UserInfo::getName, name);
        }
        return userInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
