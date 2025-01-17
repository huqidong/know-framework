package com.didiglobal.knowframework.job.core.consensual;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.core.task.TaskLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author ds
 */
@Service
public class RandomConsensual extends AbstractConsensual {
    private static final Logger logger = LoggerFactory.getLogger(RandomConsensual.class);

    @Autowired
    private TaskLockService taskLockService;

    @Override
    public String getName() {
        return ConsensualEnum.RANDOM.name();
    }

    @Override
    public boolean tryClaim(KfTask kfTask) {
        if (taskLockService.tryAcquire( kfTask.getTaskCode())) {
            kfTask.setTaskCallback( taskCode -> {
                logger.info("class=RandomConsensual||method=tryClaim||msg=release task lock "
                        + "taskCode {}", taskCode);
                taskLockService.tryRelease(taskCode);
            });
            return true;
        }
        return false;
    }
}
