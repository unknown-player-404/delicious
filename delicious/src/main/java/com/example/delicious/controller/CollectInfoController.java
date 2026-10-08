package com.example.delicious.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.entity.CollectInfo;
import com.example.delicious.service.CollectInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("collectInfo")
public class CollectInfoController {
    @Autowired
    private CollectInfoService collectInfoService;

    /**
     * 进行收藏
     */
    @PostMapping
    public Result<CollectInfo> add(@RequestBody CollectInfo info, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        String name = request.getAttribute("userName").toString();
        Integer level = (Integer) request.getAttribute("userLevel");
        info.setUserId(userId);
        info.setName(name);
        info.setLevel(level);
        info.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm-ss")));
        return Result.success(collectInfoService.add(info));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        collectInfoService.delete(id);
        return Result.success();
    }

    /**
     * 更新收藏信息
     */
    @PutMapping
    public Result<?> update(@RequestBody CollectInfo info) {
        collectInfoService.update(info);
        return Result.success();
    }

    /**
     * 按id查询
     */
    @GetMapping("/{id}")
    public Result<CollectInfo> findById(@PathVariable Long id) {
        return Result.success(collectInfoService.findById(id));
    }

    /**
     * 查询全部
     */
    @GetMapping
    public Result<List<CollectInfo>> findAll() {
        return Result.success(collectInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<CollectInfo>> findPage(@PathVariable String name,
                                              @RequestParam(required = false) Long userId,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(collectInfoService.findPage(name,userId,pageNum,pageSize));
    }
    /**
     * 判断是否收藏
     */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long foodsId,HttpServletRequest request){
        Long userId=Long.valueOf(request.getAttribute("userId").toString());
        return Result.success(collectInfoService.check(foodsId, userId));
    }

}
