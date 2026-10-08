package com.example.delicious.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.mapper.AdminInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminInfoService {
    /**
     * 管理员登录
     */
    @Autowired
    private AdminInfoMapper adminInfoMapper;

    public AdminInfo login(String name, String password) {
        AdminInfo info = adminInfoMapper.selectOne(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getName, name)
        );
        if (info == null || !info.getPassword().equals(password)) {
            throw new CustomException("1001", "用户名或密码错误，请重新登陆");
        }
        return info;
    }

    /**
     * 增
     */
    public AdminInfo add(AdminInfo info){
        Long count=adminInfoMapper.selectCount(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getName,info.getName())
        );
        if (count>0){
            throw new CustomException("1001","用户名已存在");
        }
        info.setLevel(1);
        adminInfoMapper.insert(info);
        return info;
    }

    /**
     * 删除
     */
    public void delete(Long id){
        adminInfoMapper.deleteById(id);
    }
    /**
     * 改
     */
    public void update(AdminInfo info){
        adminInfoMapper.updateById(info);
    }
    /**
     * 查
     */

    public AdminInfo findById(Long id){
        return adminInfoMapper.selectById(id);
    }

    public AdminInfo findByName(String name){
        return adminInfoMapper.selectOne(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getName, name)
        );
    }
    public List<AdminInfo> findAll(){
        return adminInfoMapper.selectList(null);
    }

    public Page<AdminInfo> findPage(String name,Integer pageNum,Integer pageSize){
        LambdaQueryWrapper<AdminInfo> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&& !"null".equals(name)){
            wrapper.like(AdminInfo::getName,name);
        }
        return adminInfoMapper.selectPage(new Page<>(pageNum,pageSize),wrapper);
    }
}
