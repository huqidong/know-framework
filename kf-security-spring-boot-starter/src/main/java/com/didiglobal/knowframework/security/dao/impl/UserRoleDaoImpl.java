package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.common.po.UserRolePO;
import com.didiglobal.knowframework.security.dao.UserRoleDao;
import com.didiglobal.knowframework.security.common.entity.UserRole;
import com.didiglobal.knowframework.security.dao.mapper.UserRoleMapper;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cjm
 */
@Component
public class UserRoleDaoImpl extends BaseDaoImpl<UserRolePO> implements UserRoleDao {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Integer> selectUserIdListByRoleId(Integer roleId) {
        if(roleId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.select( FieldConstant.USER_ID).eq(FieldConstant.ROLE_ID, roleId);
        List<Object> userIdList = userRoleMapper.selectObjs(queryWrapper);

        if(CollectionUtils.isEmpty(userIdList)){
            return new ArrayList<>();
        }

        return userIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public List<Integer> selectRoleIdListByUserId(Integer userId) {
        if(userId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<UserRolePO> userRoleWrapper = getQueryWrapperWithAppName();
        userRoleWrapper.select(FieldConstant.ROLE_ID).eq(FieldConstant.USER_ID, userId);
        List<Object> roleIdList = userRoleMapper.selectObjs(userRoleWrapper);
        return roleIdList.stream().map(Integer.class::cast).collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<UserRole> userRoleList) {
        if(!CollectionUtils.isEmpty(userRoleList)) {
            List<UserRolePO> userRolePOList = CopyBeanUtil.copyList(userRoleList, UserRolePO.class);
            for(UserRolePO userRolePO : userRolePOList) {
                userRolePO.setAppName( kfSecurityProper.getAppName());
                userRoleMapper.insert(userRolePO);
            }
        }
    }

    @Override
    public int deleteByUserIdOrRoleId(Integer userId, Integer roleId) {
        if(userId == null && roleId == null) {
            return 0 ;
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper
                .eq(userId != null, FieldConstant.USER_ID, userId)
                .eq(roleId != null, FieldConstant.ROLE_ID, roleId);
        return userRoleMapper.delete(queryWrapper);
    }

    @Override
    public int selectCountByRoleId(Integer roleId) {
        if(roleId == null) {
            return 0;
        }
        QueryWrapper<UserRolePO> queryWrapper = getQueryWrapperWithAppName();
        queryWrapper.eq(FieldConstant.ROLE_ID, roleId);
        return userRoleMapper.selectCount(queryWrapper);
    }
}
