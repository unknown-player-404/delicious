package com.example.delicious.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.PraiseInfo;
import com.example.delicious.service.PraiseInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/praiseInfo")
public class PraiseInfoController {
    @Autowired
    private PraiseInfoService praiseInfoService;

    /**
     * 点赞
     */
    @PostMapping
    public Result<PraiseInfo> add(@RequestBody PraiseInfo info, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        info.setUserId(userId);
        info.setLevel(level);
        info.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm-ss")));
        return Result.success(praiseInfoService.add(info));
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        praiseInfoService.delete(id);
        return Result.success();
    }

    /**
     * 更新点赞内容
     */
    @PutMapping
    public Result<?> update(@RequestBody PraiseInfo info) {
        praiseInfoService.update(info);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/{id}")
    public Result<PraiseInfo> findById(@PathVariable Long id) {
        return Result.success(praiseInfoService.findById(id));
    }

    /**
     * 点赞列表
     */
    @GetMapping
    public Result<List<PraiseInfo>> findAll() {
        return Result.success(praiseInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<PraiseInfo>> findPage(@PathVariable String name,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(praiseInfoService.findPage(name,pageNum,pageSize));
    }
    /**
     * 查询是否点赞
     */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long foodsId,HttpServletRequest request){
        Long userId=Long.valueOf(request.getAttribute("userId").toString());
        return Result.success(praiseInfoService.check(foodsId,userId));
    }
}
