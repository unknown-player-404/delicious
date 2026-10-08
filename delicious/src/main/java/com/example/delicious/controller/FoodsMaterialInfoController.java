package com.example.delicious.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMaterialInfo;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.service.FoodsMaterialInfoService;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foodsMaterialInfo")
public class FoodsMaterialInfoController {
    @Autowired
    private FoodsMaterialInfoService foodsMaterialInfoService;

    /**
     * 增加食材
     */
    @PostMapping
    public Result<FoodsMaterialInfo> add(@RequestBody FoodsMaterialInfo info, HttpServletRequest request) {
        info.setUserName(request.getAttribute("userName").toString());
        info.setLevel((Integer) request.getAttribute("userLevel"));
        info.setUploadUserId(Long.valueOf(request.getAttribute("userId").toString()));
        return Result.success(foodsMaterialInfoService.add(info));
    }

    /**
     * 删除食材（未实现）
     */
    @Resource
    private NxSystemFileInfoService nxSystemFileInfoService;

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMaterialInfo info = foodsMaterialInfoService.findById(id);
        if (info == null) throw new CustomException("1001", "记录不存在");
        if (!userId.equals(info.getUploadUserId()) || !level.equals(info.getLevel())) {
            throw new CustomException("1001", "不能删除他人的记录");
        }
        foodsMaterialInfoService.delete(id);
        if (info.getFileId() != null) {
            NxSystemFileInfo fileInfo = nxSystemFileInfoService.findById(info.getFileId());
            if (fileInfo != null) {
                new java.io.File("./uploads/" + fileInfo.getFileName()).delete();
                nxSystemFileInfoService.delete(info.getFileId());
            }
        }
        return Result.success();
    }

    /**
     * 更新食材
     */
    @PutMapping
    public Result<?> update(@RequestBody FoodsMaterialInfo info, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMaterialInfo exist = foodsMaterialInfoService.findById(info.getId());
        if (exist == null) {
            throw new CustomException("1001", "记录不存在");
        }
        if (!userId.equals(exist.getUploadUserId()) || !level.equals(exist.getLevel())) {
            throw new CustomException("1001", "不能修改别人的记录");
        }
        foodsMaterialInfoService.update(info);
        return Result.success();
    }

    /**
     * 按id查询食材
     */
    @GetMapping("/{id}")
    public Result<FoodsMaterialInfo> findById(@PathVariable Long id) {
        return Result.success(foodsMaterialInfoService.findById(id));
    }

    /**
     * 查询全部食材
     */
    @GetMapping
    public Result<List<FoodsMaterialInfo>> findAll() {
        return Result.success(foodsMaterialInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<FoodsMaterialInfo>> findPage(@PathVariable String name,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMaterialInfoService.findPage(name, pageNum, pageSize));
    }

    /**
     * 按user信息分页查询
     */
    @GetMapping("/page/user/{name}")
    public Result<Page<FoodsMaterialInfo>> findPageByUser(@PathVariable(required = false) String name,
                                                          @RequestParam(required = false) String userName,
                                                          @RequestParam(required = false) Integer level,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMaterialInfoService.findPageByUser(name,userName,level,pageNum,pageSize));
    }
}
