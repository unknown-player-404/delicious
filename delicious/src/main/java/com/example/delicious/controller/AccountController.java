package com.example.delicious.controller;


import com.example.delicious.common.Result;
import com.example.delicious.common.config.JwtUtil;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.Account;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.entity.UserInfo;
import com.example.delicious.service.AdminInfoService;
import com.example.delicious.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AccountController {
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Account account) {
        Map<String, Object> claims = new HashMap<>();
        if (account.getLevel() == 1) {
            AdminInfo login = adminInfoService.login(account.getName(), account.getPassword());
            claims.put("id", login.getId());
            claims.put("name", login.getName());
            claims.put("password", login.getPassword());
            claims.put("nickName", login.getNickName());
            claims.put("level", login.getLevel());
        } else if (account.getLevel() == 2) {
            UserInfo login = userInfoService.login(account.getName(), account.getPassword());
            claims.put("id", login.getId());
            claims.put("name", login.getName());
            claims.put("password", login.getPassword());
            claims.put("nickName", login.getNickName());
            claims.put("level", login.getLevel());
        } else {
            throw new CustomException("400", "角色类型错误");
        }
        String token = jwtUtil.generateToken(claims);
        Map<String, Object> result = new HashMap<>(claims);
        result.put("token", token);
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Account account) {
        if (account.getName() == null || account.getPassword() == null || account.getLevel() == null) {
            throw new CustomException("1001", "参数错误");
        }
        if (account.getLevel() == 1) {
            AdminInfo info = new AdminInfo();
            info.setName(account.getName());
            info.setPassword(account.getPassword());
            info.setNickName(account.getNickName());
            return Result.success(adminInfoService.add(info));
        } else if (account.getLevel() == 2) {
            UserInfo info = new UserInfo();
            info.setName(account.getName());
            info.setPassword(account.getPassword());
            info.setNickName(account.getNickName());
            return Result.success(userInfoService.add(info));
        } else {
            throw new CustomException("1001", "登录权限错误");
        }
    }

    /**
     * 忘记密码
     */
    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody Account account, HttpServletRequest request) {
        Integer level = (Integer) request.getAttribute("userLevel");
        Long id = Long.valueOf(request.getAttribute("userId").toString());
        if (level == 1) {
            AdminInfo info = adminInfoService.findById(id);
            if (info.getPassword().equals(account.getPassword())) {
                info.setPassword(account.getNewPassword());
                adminInfoService.update(info);
            } else {
                throw new CustomException("1001", "原密码错误");
            }
        } else if (level == 2) {
            UserInfo info = userInfoService.findById(id);
            if (info.getPassword().equals(account.getPassword())) {
                info.setPassword(account.getNewPassword());
                userInfoService.update(info);
            } else {
                throw new CustomException("1001", "原密码错误");
            }
        } else {
            throw new CustomException("1001", "登录权限错误");
        }
        return Result.success();
    }

    /**
     * 初始化密码
     */
    @PutMapping("/resetPassword")
    public Result<?> resetPassword(@RequestBody Account account){
        if(account.getLevel()==1){
            AdminInfo info = adminInfoService.findById(account.getId());
            if(info==null){
                throw new CustomException("1001","用户不存在");
            }else {
                info.setPassword("123456");
                adminInfoService.update(info);
            }
        }else if(account.getLevel()==2){
            UserInfo info = userInfoService.findById(account.getId());
            if(info==null){
                throw new CustomException("1001","用户不存在");
            }else{
                info.setPassword("123456");
                userInfoService.update(info);
            }
        }else{
            throw new CustomException("1001","登录权限错误");
        }
        return Result.success();
    }

    /**
     * 查看个人信息
     */
    @GetMapping("/getAccountInfo")
    public Result<?> getAccountInfo(HttpServletRequest request){
        Integer level=(Integer) request.getAttribute("userLevel");
        Long id=Long.valueOf(request.getAttribute("userId").toString());
        if(level==1){
            return Result.success(adminInfoService.findById(id));
        }else if(level==2){
            return Result.success(userInfoService.findById(id));
        }else{
            throw new CustomException("1001","登录权限错误");
        }
    }
}