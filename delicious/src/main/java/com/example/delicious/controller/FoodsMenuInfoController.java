package com.example.delicious.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.common.Result;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMenuInfo;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.service.FoodsMenuInfoService;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foodsMenuInfo")
public class FoodsMenuInfoController {
    @Autowired
    private FoodsMenuInfoService foodsMenuInfoService;


    /**
     * 添加
     */
    @PostMapping
    public Result<FoodsMenuInfo> add(@RequestBody FoodsMenuInfo info, HttpServletRequest request) {
        info.setUserName(request.getAttribute("userName").toString());
        info.setLevel((Integer) request.getAttribute("userLevel"));
        info.setUploadUserId(Long.valueOf(request.getAttribute("userId").toString()));
        return Result.success(foodsMenuInfoService.add(info));
    }

    /**
     * 删除菜单
     */
    @Resource
    private NxSystemFileInfoService nxSystemFileInfoService;

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMenuInfo info = foodsMenuInfoService.findById(id);
        if (info == null) throw new CustomException("1001", "记录不存在");
        if (!userId.equals(info.getUploadUserId()) || !level.equals(info.getLevel())) {
            throw new CustomException("1001", "不能删除他人的记录");
        }
        foodsMenuInfoService.delete(id);
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
     * 更新菜单
     */
    @PutMapping
    public Result<?> update(@RequestBody FoodsMenuInfo info, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMenuInfo exist = foodsMenuInfoService.findById(info.getId());
        if (exist == null) {
            throw new CustomException("1001", "记录不存在");
        }
        if (!userId.equals(exist.getUploadUserId()) || !level.equals(exist.getLevel())) {
            throw new CustomException("1001", "不能修改别人的记录");
        }
        foodsMenuInfoService.update(info);
        return Result.success();
    }

    /**
     * 按id查询
     */
    @GetMapping("/{id}")
    public Result<FoodsMenuInfo> findById(@PathVariable Long id) {
        return Result.success(foodsMenuInfoService.findById(id));
    }

    /**
     * 查询全部
     */
    @GetMapping
    public Result<List<FoodsMenuInfo>> findAll() {
        return Result.success(foodsMenuInfoService.findAll());
    }

    /**
     * 分页查询
     */
    @GetMapping("/page/{name}")
    public Result<Page<FoodsMenuInfo>> findPage(@PathVariable String name,
                                            @RequestParam(required = false) Long classifyId,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMenuInfoService.findPage(name, classifyId, pageNum, pageSize));
    }

    /**
     * 按user信息分页查询
     */
    @GetMapping("/page/user/{name}")
    public Result<Page<FoodsMenuInfo>> findPageByUser(@PathVariable String name,
                                                  @RequestParam(required = false) String userName,
                                                  @RequestParam(required = false) Integer level,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMenuInfoService.findPageByUser(name, userName, level, pageNum, pageSize));
    }
}
