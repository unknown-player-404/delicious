package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.mapper.NxSystemFileInfoMapper;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NxSystemFileInfoServiceImpl extends ServiceImpl<NxSystemFileInfoMapper, NxSystemFileInfo> implements NxSystemFileInfoService {
    @Resource
    private NxSystemFileInfoMapper nxSystemFileInfoMapper;

    @Override
    public NxSystemFileInfo add(NxSystemFileInfo info) {
        nxSystemFileInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        nxSystemFileInfoMapper.deleteById(id);
    }

    @Override
    public NxSystemFileInfo findById(Long id) {
        return nxSystemFileInfoMapper.selectById(id);
    }

    @Override
    public List<NxSystemFileInfo> findAll() {
        return nxSystemFileInfoMapper.selectList(null);
    }

    @Override
    public Page<NxSystemFileInfo> findPage(Integer pageNum, Integer pageSize) {
        return nxSystemFileInfoMapper.selectPage(new Page<>(pageNum, pageSize),null);
    }
}
