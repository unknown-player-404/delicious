package com.example.delicious.controller;


import cn.hutool.core.io.FileUtil;
import com.example.delicious.common.Result;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {
    @Value("${file.upload-path}")
    private String uploadPath;
    @Autowired
    private NxSystemFileInfoService nxSystemFileInfoService;

    /**
     * 初始化创建文件夹
     */
    @PostConstruct
    public void init() {
        FileUtil.mkdir(uploadPath);
    }

    /**
     * 上传图片
     */
    @PostMapping("/image/upload")
    public Result<Map<String, String>> imageUpload(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        String ext = FileUtil.extName(originName);
        String main = FileUtil.mainName(originName);
        String fileName = main + System.currentTimeMillis() + "." + ext;

        Thumbnails.of(file.getInputStream()).width(400).toFile(uploadPath + fileName);

        NxSystemFileInfo info = new NxSystemFileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        nxSystemFileInfoService.add(info);

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(info.getId()));
        map.put("src", "/files/download/" + info.getId());
        map.put("title", info.getOriginName());
        return Result.success(map);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<NxSystemFileInfo> upload(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        String ext = FileUtil.extName(originName);
        String main = FileUtil.mainName(originName);
        String fileName = main + System.currentTimeMillis() + "." + ext;

        FileUtil.writeBytes(file.getBytes(), uploadPath + fileName);

        NxSystemFileInfo info = new NxSystemFileInfo();
        info.setFileName(fileName);
        info.setOriginName(originName);
        nxSystemFileInfoService.add(info);
        return Result.success(info);
    }

    /**
     * 加载（下载）文件
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        NxSystemFileInfo info = nxSystemFileInfoService.findById(id);
        if (info == null) {
            throw new CustomException("1001", "文件不存在");
        }
        byte[] bytes = FileUtil.readBytes(uploadPath + info.getFileName());
        response.reset();
        response.addHeader("Content-Disposition",
                "inline;filename=" + URLEncoder.encode(info.getOriginName(), StandardCharsets.UTF_8));
        response.addHeader("Content-Length", String.valueOf(bytes.length));
        response.setContentType("image/jpeg");
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        os.write(bytes);
        os.flush();
        os.close();
    }
    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        NxSystemFileInfo info = nxSystemFileInfoService.findById(id);
        if(info==null){
            throw new CustomException("1001","文件不存在");
        }
        new File(uploadPath+info.getFileName()).delete();
        nxSystemFileInfoService.delete(id);
        return Result.success();
    }
    /**
     * 查找文件
     */
    @GetMapping("/{id}")
    public Result<NxSystemFileInfo> findById(@PathVariable Long id){
        NxSystemFileInfo info = nxSystemFileInfoService.findById(id);
        if(info==null){
            throw new CustomException("1001","文件不存在");
        }
        return Result.success(info);
    }

}
