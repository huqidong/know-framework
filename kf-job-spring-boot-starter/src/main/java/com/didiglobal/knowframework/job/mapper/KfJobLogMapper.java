package com.didiglobal.knowframework.job.mapper;

import com.didiglobal.knowframework.job.common.po.KfJobLogPO;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.*;

/**
 * <p>
 * job执行历史日志 Mapper 接口.
 * </p>
 *
 * @author ds
 * @since 2020-11-10
 */
@Mapper
public interface KfJobLogMapper {

    @Insert("INSERT INTO kf_job_log(job_code, task_code, task_id, task_name, "
            + "task_desc, class_name, try_times, worker_code, worker_ip,"
            + "start_time, end_time, status, error, result, create_time, update_time, app_name) "
            + "VALUES(#{jobCode}, #{taskCode}, #{taskId}, #{taskName}, #{taskDesc},#{className}, "
            + "#{tryTimes}, #{workerCode}, #{workerIp}, #{startTime}"
            + ", #{endTime}, #{status}, #{error}, #{result}, #{createTime}, #{updateTime}, #{appName})")
    int insert(KfJobLogPO kfJobLogPO);

    @Select("select id, job_code, task_code, task_id, task_name, task_desc, "
            + "class_name, try_times, worker_code, worker_ip, start_time, "
            + "end_time, status, error, result, create_time, update_time, app_name from kf_job_log where "
            + "task_code=#{taskCode} and app_name=#{appName} order by id desc limit #{start}, #{size}")
    List<KfJobLogPO> selectByTaskCode(@Param("taskCode") String taskCode,
                                      @Param("appName") String appName,
                                      @Param("start") Integer start,
                                      @Param("size") Integer size);

    @Update("update kf_job_log set job_code=#{jobCode}, task_code=#{taskCode}, task_id=#{taskId}, "
            + "task_name=#{taskName}, task_desc=#{taskDesc}, class_name=#{className}, try_times="
            + "#{tryTimes}, worker_code=#{workerCode}, worker_ip=#{workerIp}, start_time="
            + "#{startTime}, end_time=#{endTime}, status=#{status}, error=#{error}, result=#{result}, "
            + "update_time=#{updateTime}, app_name=#{appName} where job_code=#{jobCode}")
    int updateByCode(KfJobLogPO kfJobLogPO);

    @Delete("delete from kf_job_log where create_time<=#{createTime} and app_name=#{appName} limit #{limits}")
    int deleteByCreateTime(@Param("createTime") Timestamp createTime, @Param("appName") String appName,
                           @Param("limits") Integer limits);

    @Select("select count(1) from kf_job_log where app_name=#{appName} and create_time<=#{createTime}")
    int selectCountByAppNameAndCreateTime(@Param("appName") String appName, @Param("createTime") Timestamp createTime);

    List<KfJobLogPO> pagineListByCondition(@Param("appName") String appName,
                                           @Param("taskId") Long taskId,
                                           @Param("taskDesc") String taskDesc,
                                           @Param("jobStatus") Integer jobStatus,
                                           @Param("start") Integer start,
                                           @Param("size") Integer size,
                                           @Param("sortName") String sortName,
                                           @Param("sortAsc") String sortAsc,
                                           @Param("beginTime") Timestamp beginTime,
                                           @Param("endTime") Timestamp endTime);

    Integer pagineCountByCondition(@Param("appName") String appName, @Param("taskId") Long taskId,
                                   @Param("taskDesc") String taskDesc, @Param("jobStatus") Integer jobStatus,
                                   @Param("beginTime") Timestamp beginTime, @Param("endTime") Timestamp endTime);
}
