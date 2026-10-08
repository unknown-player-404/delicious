package com.example.delicious.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.UserInfo;
import com.example.delicious.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
    /**
     * 用户登录
     */
    @Autowired
    private UserInfoMapper userInfoMapper;
    public UserInfo login(String name, String password) {
        UserInfo info = userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getName, name)
        );
        if (info == null || !info.getPassword().equals(password)) {
            throw new CustomException("1001", "用户名或密码错误");
        }
        return info;
    }
    /**
     * 注册
     */
    public UserInfo add(UserInfo info){
        Long count=userInfoMapper.selectCount(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getName,info.getName())
        );
        if (count>0){
            throw new CustomException("1001","用户名已存在");
        }
        info.setLevel(2);
        userInfoMapper.insert(info);
        return info;
    }
    /**
     * 删
     */
    public void delete(Long id){
        userInfoMapper.deleteById(id);
    }
    /**改
     *
     */
    public void update(UserInfo info){
        userInfoMapper.updateById(info);
    }
    /**
     * 查
     */
    public UserInfo findById(Long id){
        return userInfoMapper.selectById(id);
    }

    public List<UserInfo> findAll(){
        return userInfoMapper.selectList(null);
    }
    public UserInfo findByName(String name){
        return userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getName,name)
        );
    }
    public Page<UserInfo> findPage(String name, Integer pageNum, Integer pageSize){
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&& !"null".equals(name)){
            wrapper.like(UserInfo::getName,name);
        }
        return userInfoMapper.selectPage(new Page<>(pageNum,pageSize),wrapper);
    }
}
