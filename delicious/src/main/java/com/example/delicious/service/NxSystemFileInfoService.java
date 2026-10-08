package com.example.delicious.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.mapper.NxSystemFileInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NxSystemFileInfoService {
    @Autowired
    private NxSystemFileInfoMapper nxSystemFileInfoMapper;

    /**
     * 增加
     */
    public NxSystemFileInfo add(NxSystemFileInfo info){
        nxSystemFileInfoMapper.insert(info);
        return info;
    }
    /**
     * 删除
     */
    public void delete(Long id){
        nxSystemFileInfoMapper.deleteById(id);
    }
    /**
     * id查询
     */
    public NxSystemFileInfo findById(Long id){
        return nxSystemFileInfoMapper.selectById(id);
    }
    /**
     * 分页查询(不用模糊查询）
     */
    public Page<NxSystemFileInfo> findPage(Integer pageNum,Integer pageSize){
        return nxSystemFileInfoMapper.selectPage(new Page<>(pageNum,pageSize),null);
    }
}
