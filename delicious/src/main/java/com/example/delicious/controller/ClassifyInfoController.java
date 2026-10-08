package com.example.delicious.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.ClassifyInfo;
import com.example.delicious.service.ClassifyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classifyInfo")
public class ClassifyInfoController {
    @Autowired
    private ClassifyInfoService classifyInfoService;

    /**
     * 增加分类
     */
    @PostMapping
    public Result<ClassifyInfo> add(@RequestBody ClassifyInfo info) {
        return Result.success(classifyInfoService.add(info));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        classifyInfoService.delete(id);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<?> update(@RequestBody ClassifyInfo info) {
        classifyInfoService.update(info);
        return Result.success();
    }

    /**
     * 按id查询
     */
    @GetMapping("/{id}")
    public Result<ClassifyInfo> findById(@PathVariable Long id) {
        return Result.success(classifyInfoService.findById(id));
    }

    /**
     * 查询全部分类
     */
    @GetMapping
    public Result<List<ClassifyInfo>> findAll() {
        return Result.success(classifyInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<ClassifyInfo>> findPage(@PathVariable String name,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(classifyInfoService.findPage(name,pageNum,pageSize));
    }
}
