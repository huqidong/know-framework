package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.didiglobal.logi.security.common.po.ConfigPO;
import com.didiglobal.logi.security.dao.ConfigDao;
import com.didiglobal.logi.security.dao.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cjm
 */
@Component
public class ConfigDaoImpl extends BaseDaoImpl<ConfigPO> implements ConfigDao {

    private final static String ID              = "id";
    private final static String VALUE_GROUP     = "value_group";
    private final static String VALUE_NAME      = "value_name";
    private final static String VALUE           = "value";
    private final static String STATUS          = "status";
    private final static String MEMO            = "memo";
    private final static String OPERATOR        = "operator";
    private final static String CREATE_TIME     = "create_time";
    private final static String UPDATE_TIME     = "update_time";

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public int insert(ConfigPO param) {
        return configMapper.insert(param);
    }

    @Override
    public int update(ConfigPO param) {
        return configMapper.updateById(param);
    }

    @Override
    public List<ConfigPO> listByCondition(ConfigPO condt) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        if(!StringUtils.isBlank(condt.getValueGroup())){
            queryWrapper.eq(VALUE_GROUP, condt.getValueGroup());
        }

        if(!StringUtils.isBlank(condt.getValueName())){
            queryWrapper.eq(VALUE_NAME, condt.getValueName());
        }

        if(null != condt.getStatus()){
            queryWrapper.eq(STATUS, condt.getStatus());
        }

        return configMapper.selectList(queryWrapper);
    }

    @Override
    public ConfigPO getbyId(Integer configId) {
        return configMapper.selectById(configId);
    }

    @Override
    public ConfigPO getByGroupAndName(String valueGroup, String valueName) {
        QueryWrapper<ConfigPO> queryWrapper = wrapBriefQuery();
        queryWrapper.eq(VALUE_GROUP, valueGroup);
        queryWrapper.eq(VALUE_NAME, valueName);
        return configMapper.selectOne(queryWrapper);
    }

    private QueryWrapper<ConfigPO> wrapBriefQuery() {
        QueryWrapper<ConfigPO> queryWrapper = getQueryWrapper();
        queryWrapper.select(ID, VALUE_GROUP, VALUE_NAME, VALUE, STATUS, MEMO, OPERATOR, CREATE_TIME, UPDATE_TIME);
        return queryWrapper;
    }
}
