package com.example.delicious.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.PraiseInfo;
import com.example.delicious.service.PraiseInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/praiseInfo")
public class PraiseInfoController {
    @Resource
    private PraiseInfoService praiseInfoService;

    @PostMapping
    public Result<PraiseInfo> add(@RequestBody PraiseInfo info, HttpServletRequest request) {
        Long id=Long.valueOf(request.getAttribute("userId").toString());
        Integer level=(Integer) request.getAttribute("userLevel");
        String name=request.getAttribute("userName").toString();
        info.setName(name);
        info.setUserId(id);
        info.setLevel(level);
        info.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm-ss")));
        return Result.success(praiseInfoService.add(info));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        praiseInfoService.delete(id);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody PraiseInfo info) {
        praiseInfoService.update(info);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<PraiseInfo> findById(@PathVariable Long id) {
        return Result.success(praiseInfoService.findById(id));
    }

    @GetMapping
    public Result<List<PraiseInfo>> findAll() {
        return Result.success(praiseInfoService.findAll());
    }

    @GetMapping("/page/{name}")
    public Result<Page<PraiseInfo>> findPage(@PathVariable String name,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(praiseInfoService.findPage(name, pageNum, pageSize));
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long foodsId,HttpServletRequest request){
        Long userId=Long.valueOf(request.getAttribute("userId").toString());
        return Result.success(praiseInfoService.check(foodsId,userId));
    }

    @GetMapping("/count")
    public Result<Long> getCount(@RequestParam Long foodsId){
        return Result.success(praiseInfoService.getCount(foodsId));
    }

    @PostMapping("/count/batch")
    public Result<Map<Long, Long>> getCounts(@RequestBody List<Long> foodsIds){
        return Result.success(praiseInfoService.getCounts(foodsIds));
    }
}
