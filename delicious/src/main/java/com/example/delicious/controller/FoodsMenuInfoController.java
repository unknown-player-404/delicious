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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/foodsMenuInfo")
public class FoodsMenuInfoController {
    @Resource
    private FoodsMenuInfoService foodsMenuInfoService;

    @PostMapping
    public Result<FoodsMenuInfo> add(@RequestBody FoodsMenuInfo info, HttpServletRequest request) {
        String userName = request.getAttribute("userName").toString();
        Integer level = (Integer) request.getAttribute("userLevel");
        Long uploadUserId = Long.valueOf(request.getAttribute("userId").toString());
        info.setUserName(userName);
        info.setLevel(level);
        info.setUploadUserId(uploadUserId);
        return Result.success(foodsMenuInfoService.add(info));
    }

    /**
     * 删除
     */
    @Resource
    private NxSystemFileInfoService nxSystemFileInfoService;

    @Value("${file.upload-path}")
    private String uploadPath;

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id,HttpServletRequest request) throws IOException {
        FoodsMenuInfo info = foodsMenuInfoService.findById(id);
        if(info==null){
            throw new CustomException("400","菜单不存在");
        }
        Long userId=Long.valueOf(request.getAttribute("userId").toString());
        Integer level=(Integer) request.getAttribute("userLevel");
        if (!Objects.equals(info.getUploadUserId(), userId) || !Objects.equals(info.getLevel(), level)) {
            throw new CustomException("400","不能删除别人的菜谱");
        }
        foodsMenuInfoService.delete(id);
        if(info.getFileId()!=null){
            NxSystemFileInfo fileInfo = nxSystemFileInfoService.findById(info.getFileId());
            if (fileInfo != null) {
                Files.deleteIfExists(Paths.get(uploadPath).toAbsolutePath().normalize()
                        .resolve(fileInfo.getFileName()));
                nxSystemFileInfoService.delete(fileInfo.getId());
            }
        }
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody FoodsMenuInfo info, HttpServletRequest request) {
        if (info.getId() == null) {
            throw new CustomException("400", "菜谱id不能为空");
        }
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer level = (Integer) request.getAttribute("userLevel");
        FoodsMenuInfo exist = foodsMenuInfoService.findById(info.getId());
        if (exist == null) {
            throw new CustomException("400", "菜谱不存在");
        }
        if (!Objects.equals(exist.getUploadUserId(), userId) || !Objects.equals(exist.getLevel(), level)) {
            throw new CustomException("400", "不能修改别人上传的菜谱");
        }
        // 上传者信息以数据库记录为准，防止请求体伪造
        info.setUploadUserId(exist.getUploadUserId());
        info.setLevel(exist.getLevel());
        info.setUserName(exist.getUserName());
        foodsMenuInfoService.update(info);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FoodsMenuInfo> findById(@PathVariable Long id) {
        return Result.success(foodsMenuInfoService.findById(id));
    }

    @GetMapping
    public Result<List<FoodsMenuInfo>> findAll() {
        return Result.success(foodsMenuInfoService.findAll());
    }

    @GetMapping("/page/{name}")
    public Result<Page<FoodsMenuInfo>> findPage(@PathVariable String name,
                                                @RequestParam(required = false) Long classifyId,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(foodsMenuInfoService.findPage(name, classifyId, pageNum, pageSize));
    }
}
