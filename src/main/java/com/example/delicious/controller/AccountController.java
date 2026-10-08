package com.example.delicious.controller;

import com.example.delicious.common.Result;
import com.example.delicious.common.config.JwtUtil;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.Account;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.entity.UserInfo;
import com.example.delicious.service.AdminInfoService;
import com.example.delicious.service.UserInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AccountController {
    @Resource
    private AdminInfoService adminInfoService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private JwtUtil jwtUtil;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Account account) {
        Map<String, Object> claims = new HashMap<>();
        if (account.getLevel() == 1) {
            AdminInfo info = adminInfoService.login(account.getName(), account.getPassword());
            claims.put("id", info.getId());
            claims.put("name", info.getName());
            claims.put("nickName", info.getNickName());
            claims.put("level", info.getLevel());
        } else if (account.getLevel() == 2) {
            UserInfo info = userInfoService.login(account.getName(), account.getPassword());
            claims.put("id", info.getId());
            claims.put("name", info.getName());
            claims.put("nickName", info.getNickName());
            claims.put("level", info.getLevel());
        } else {
            throw new CustomException("400", "权限错误");
        }
        Map<String, Object> result = new HashMap<>(claims);
        String token = jwtUtil.generateToken(claims);
        result.put("token", token);
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Account account) {
        if (account.getName() == null || account.getPassword() == null || account.getLevel() == null) {
            throw new CustomException("400", "信息不全");
        }
        if (account.getLevel() == 1) {
            AdminInfo exist = adminInfoService.findByName(account.getName());
            if (exist != null) {
                throw new CustomException("400", "用户已存在");
            }
            AdminInfo info = new AdminInfo();
            info.setName(account.getName());
            info.setPassword(account.getPassword());
            info.setNickName(account.getNickName());
            return Result.success(adminInfoService.add(info));
        } else if (account.getLevel() == 2) {
            UserInfo exist = userInfoService.findByName(account.getName());
            if (exist != null) {
                throw new CustomException("400", "用户已存在");
            }
            UserInfo info = new UserInfo();
            info.setName(account.getName());
            info.setPassword(account.getPassword());
            info.setNickName(account.getNickName());
            return Result.success(userInfoService.add(info));
        } else {
            throw new CustomException("400", "权限错误");
        }
    }

    /**
     * 更新密码
     */
    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody Account account, HttpServletRequest request) {
        if (account.getPassword() == null || account.getNewPassword() == null
                || account.getNewPassword().isBlank()) {
            throw new CustomException("400", "原密码或新密码不能为空");
        }
        Integer level = (Integer) request.getAttribute("userLevel");
        Long id = Long.valueOf(request.getAttribute("userId").toString());
        if (level == 1) {
            AdminInfo info = adminInfoService.findById(id);
            if (info == null) {
                throw new CustomException("400", "用户不存在");
            }
            if (!account.getPassword().equals(info.getPassword())) {
                throw new CustomException("400", "原密码错误");
            }
            info.setPassword(account.getNewPassword());
            adminInfoService.update(info);
            return Result.success(info);
        } else if (level == 2) {
            UserInfo info = userInfoService.findById(id);
            if (info == null) {
                throw new CustomException("400", "用户不存在");
            }
            if (!account.getPassword().equals(info.getPassword())) {
                throw new CustomException("400", "原密码错误");
            }
            info.setPassword(account.getNewPassword());
            userInfoService.update(info);
            return Result.success(info);
        } else {
            throw new CustomException("400", "权限错误");
        }
    }

    /**
     * 初始化密码
     */
    @PutMapping("/resetPassword")
    public Result<?> resetPassword(HttpServletRequest request) {
        Integer level = (Integer) request.getAttribute("userLevel");
        Long id = Long.valueOf(request.getAttribute("userId").toString());
        if (level == 1) {
            AdminInfo info = adminInfoService.findById(id);
            if (info == null) {
                throw new CustomException("400", "用户不存在");
            }
            info.setPassword("123456");
            adminInfoService.update(info);
            return Result.success();
        } else if (level == 2) {
            UserInfo info = userInfoService.findById(id);
            if (info == null) {
                throw new CustomException("400", "用户不存在");
            }
            info.setPassword("123456");
            userInfoService.update(info);
            return Result.success();
        } else {
            throw new CustomException("400", "权限错误");
        }
    }

    /**
     * 查看个人信息
     */
    @GetMapping("/getAccountInfo")
    public Result<?> getAccountInfo(HttpServletRequest request) {
        Integer level = (Integer) request.getAttribute("userLevel");
        Long id = Long.valueOf(request.getAttribute("userId").toString());
        if (level == 1) {
            return Result.success(adminInfoService.findById(id));
        } else if (level == 2) {
            return Result.success(userInfoService.findById(id));
        } else {
            throw new CustomException("400", "权限错误");
        }
    }
}
