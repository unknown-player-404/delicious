package com.example.delicious.controller;

import cn.hutool.core.io.FileUtil;
import com.example.delicious.common.Result;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileInfoController {
    @Resource
    private NxSystemFileInfoService nxSystemFileInfoService;

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostConstruct
    private void init() {
        FileUtil.mkdir(uploadPath);
    }

    /**
     * 上传目录，统一转成绝对路径，避免拼接时缺少分隔符
     */
    private Path uploadDir() {
        return Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    /**
     * 拼出文件真实路径
     */
    private Path resolveFile(String fileName) {
        return uploadDir().resolve(fileName);
    }

    /**
     * 生成入库的文件名
     */
    private String buildFileName(String originName) {
        return FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);
    }

    /**
     * 取原始文件名（去掉浏览器可能带上的路径）
     */
    private String getOriginName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("400", "文件不能为空");
        }
        String originName = file.getOriginalFilename();
        if (originName == null || originName.isBlank()) {
            throw new CustomException("400", "文件名不能为空");
        }
        return FileUtil.getName(originName);
    }

    /**
     * 上传图片
     */
    @PostMapping("/image/upload")
    public Result<Map<String, Object>> imageUpload(MultipartFile file) throws IOException {
        String originName = getOriginName(file);
        String fileName = buildFileName(originName);

        Thumbnails.of(file.getInputStream()).width(400).toFile(resolveFile(fileName).toFile());

        NxSystemFileInfo info = new NxSystemFileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        nxSystemFileInfoService.add(info);

        Map<String, Object> map = new HashMap<>();
        map.put("id", info.getId());
        map.put("src", "files/download/" + info.getId());
        map.put("title", info.getOriginName());
        return Result.success(map);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<NxSystemFileInfo> upload(MultipartFile file) throws IOException {
        String originName = getOriginName(file);
        String fileName = buildFileName(originName);

        Files.write(resolveFile(fileName), file.getBytes());

        NxSystemFileInfo info = new NxSystemFileInfo();
        info.setFileName(fileName);
        info.setOriginName(originName);
        nxSystemFileInfoService.add(info);

        return Result.success(info);
    }


    /**
     * 下载文件
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        NxSystemFileInfo info = nxSystemFileInfoService.findById(id);
        if(info==null){
            throw new CustomException("400","文件不存在");
        }
        Path path = resolveFile(info.getFileName());
        if (!Files.exists(path)) {
            throw new CustomException("400", "文件不存在");
        }
        byte[] bytes = Files.readAllBytes(path);
        response.reset();
        response.setHeader("Content-Disposition", ContentDisposition.inline()
                .filename(info.getOriginName(), StandardCharsets.UTF_8)
                .build()
                .toString());
        response.setContentLength(bytes.length);
        String contentType = URLConnection.guessContentTypeFromName(info.getOriginName());
        response.setContentType(contentType == null ? "application/octet-stream" : contentType);
        try (OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            os.write(bytes);
            os.flush();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) throws IOException {
        NxSystemFileInfo info = nxSystemFileInfoService.findById(id);
        if(info==null){
            throw new CustomException("400","文件不存在");
        }
        Files.deleteIfExists(resolveFile(info.getFileName()));
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
            throw new CustomException("400","文件不存在");
        }
        return Result.success(info);
    }
}
