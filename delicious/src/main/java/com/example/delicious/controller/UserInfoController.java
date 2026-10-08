package com.example.delicious.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.UserInfo;
import com.example.delicious.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 增加
     */
    @PostMapping
    public Result<UserInfo> add(@RequestBody UserInfo info) {
        userInfoService.add(info);
        return Result.success(info);
    }
    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        userInfoService.delete(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<?> update(@RequestBody UserInfo info) {
        userInfoService.update(info);
        return Result.success();
    }

    /**
     * 查询
     */
    @GetMapping("/{id}")
    public Result<UserInfo> findById(@PathVariable Long id) {
        return Result.success(userInfoService.findById(id));
    }

    @GetMapping
    public Result<List<UserInfo>> findAll() {
        return Result.success(userInfoService.findAll());
    }

    @GetMapping("/page/{name}")
    public Result<Page<UserInfo>> findPage(@PathVariable String name,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userInfoService.findPage(name, pageNum, pageSize));
    }
}
