package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.po.PermissionPO;
import com.didiglobal.logi.security.dao.PermissionDao;
import com.didiglobal.logi.security.dao.mapper.PermissionMapper;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class PermissionDaoImpl implements PermissionDao {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> selectAllAndAscOrderByLevel() {
        QueryWrapper<PermissionPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("level");
        return CopyBeanUtil.copyList(permissionMapper.selectList(queryWrapper), Permission.class);
    }
}
