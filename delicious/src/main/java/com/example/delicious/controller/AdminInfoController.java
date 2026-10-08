package com.example.delicious.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.AdminInfo;
import com.example.delicious.service.AdminInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminInfo")
public class AdminInfoController {
    @Autowired
    private AdminInfoService adminInfoService;

    /**
     * 增加
     */
    @PostMapping
    public Result<AdminInfo> add(AdminInfo info) {
        adminInfoService.add(info);
        return Result.success(info);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminInfoService.delete(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<?> update(@RequestBody AdminInfo info) {
        adminInfoService.update(info);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/{id}")
    public Result<AdminInfo> findById(@PathVariable Long id) {
        return Result.success(adminInfoService.findById(id));
    }

    /**
     * 查询全部
     */
    @GetMapping
    public Result<List<AdminInfo>> findALl() {
        return Result.success(adminInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<AdminInfo>> findPage(@PathVariable String name,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(adminInfoService.findPage(name,pageNum,pageSize));
    }
}
