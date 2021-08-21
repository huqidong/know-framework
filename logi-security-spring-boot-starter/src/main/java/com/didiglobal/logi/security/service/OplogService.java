package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.common.vo.oplog.OplogVO;

import java.util.List;

/**
 * @author cjm
 */
public interface OplogService {

    /**
     * 保存操作日志，并获取操作日志id
     * @param oplogDto 操作日志
     * @return 操作日志id
     */
    Integer saveOplog(OplogDTO oplogDto);

    /**
     * 分页查询操作日志
     * @param queryVo 查询条件
     * @return 分页信息
     */
    PagingData<OplogVO> getOplogPage(OplogQueryDTO queryVo);

    /**
     * 根据id查找操作日志详情
     * @param opLogId 操作日志id
     * @return OplogVo 详情
     */
    OplogVO getOplogDetailByOplogId(Integer opLogId);

    /**
     * 获取操作日志列表的查询条件信息
     * @param type 1 操作页面、2 操作类型、3 对象分类
     * @return 操作页面NameList、操作类型NameList、对象分类NameList
     */
    List<String> getOplogExtraList(Integer type);

}
