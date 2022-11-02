package com.didiglobal.knowframework.job.rest;

import com.didiglobal.knowframework.job.common.dto.TaskLogPageQueryDTO;
import com.didiglobal.knowframework.job.common.PagingResult;
import com.didiglobal.knowframework.job.common.vo.LogIJobLogVO;
import com.didiglobal.knowframework.job.core.job.JobLogManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.didiglobal.knowframework.job.common.CommonUtil.sqlFuzzyQueryTransfer;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author ds
 */
@RestController
@RequestMapping(Constants.V1 + "/logi-job/logs")
@Api(tags = "kf-job 执行生成的任务日志相关接口")
public class JobLogsController {
    @Autowired
    private JobLogManager jobLogManager;

    @PostMapping("/list")
    public PagingResult<LogIJobLogVO> getJobLogs(@RequestBody TaskLogPageQueryDTO pageQueryDTO) {
        pageQueryDTO.setTaskDesc(sqlFuzzyQueryTransfer(pageQueryDTO.getTaskDesc()));

        List<LogIJobLogVO> logIJobLogVOS = jobLogManager.pageJobLogs(pageQueryDTO);
        int totalCount = jobLogManager.getJobLogsCount(pageQueryDTO);

        return PagingResult.buildSucc(logIJobLogVOS, totalCount, pageQueryDTO.getPage(), pageQueryDTO.getSize());
    }
}
