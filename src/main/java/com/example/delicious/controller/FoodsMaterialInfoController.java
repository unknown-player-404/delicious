package com.example.delicious.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMaterialInfo;
import com.example.delicious.service.FoodsMaterialInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/foodsMaterialInfo")
public class FoodsMaterialInfoController {
    @Resource
    private FoodsMaterialInfoService foodsMaterialInfoService;

    @PostMapping
    public Result<FoodsMaterialInfo> add(@RequestBody FoodsMaterialInfo info, HttpServletRequest request) {
        Long id = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        String userName = request.getAttribute("userName").toString();
        info.setUploadUserId(id);
        info.setLevel(level);
        info.setUserName(userName);
        return Result.success(foodsMaterialInfoService.add(info));
    }

    /**
     * 未实现
     */
//    @Resource
//    private NxSystemFileInfoService nxSystemFileInfoService;
//
//    @DeleteMapping("/{id}")
//    public Result<?> delete(@PathVariable Long id) {
//        foodsMaterialInfoService.delete(id);
//        return Result.success();
//    }
    @PutMapping
    public Result<?> update(@RequestBody FoodsMaterialInfo info, HttpServletRequest request) {
        if (info.getId() == null) {
            throw new CustomException("400", "食材id不能为空");
        }
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMaterialInfo exist = foodsMaterialInfoService.findById(info.getId());
        if (exist == null) {
            throw new CustomException("400", "食材不存在");
        }
        if (!Objects.equals(exist.getUploadUserId(), userId) || !Objects.equals(exist.getLevel(), level)) {
            throw new CustomException("400", "不能修改别人上传的食材");
        }
        // 上传者信息以数据库记录为准，防止请求体伪造
        info.setUploadUserId(exist.getUploadUserId());
        info.setLevel(exist.getLevel());
        info.setUserName(exist.getUserName());
        foodsMaterialInfoService.update(info);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FoodsMaterialInfo> findById(@PathVariable Long id) {
        return Result.success(foodsMaterialInfoService.findById(id));
    }

    @GetMapping
    public Result<List<FoodsMaterialInfo>> findAll() {
        return Result.success(foodsMaterialInfoService.findAll());
    }

    @GetMapping("/page/{name}")
    public Result<Page<FoodsMaterialInfo>> findPage(@PathVariable String name,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMaterialInfoService.findPage(name, pageNum, pageSize));
    }
}
