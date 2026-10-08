package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.delicious.entity.NxSystemFileInfo;

import java.util.List;

public interface NxSystemFileInfoService extends IService<NxSystemFileInfo> {
    NxSystemFileInfo add(NxSystemFileInfo info);

    void delete(Long id);

    NxSystemFileInfo findById(Long id);

    List<NxSystemFileInfo> findAll();

    Page<NxSystemFileInfo> findPage(Integer pageNum,Integer pageSize);

}
