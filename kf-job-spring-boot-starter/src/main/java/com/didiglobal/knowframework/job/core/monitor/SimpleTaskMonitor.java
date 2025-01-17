package com.didiglobal.knowframework.job.core.monitor;

import com.didiglobal.knowframework.job.common.domain.KfTask;
import com.didiglobal.knowframework.job.core.task.TaskManager;
import com.didiglobal.knowframework.job.utils.ThreadUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple task monitor.
 *
 * @author ds
 */
@Service
public class SimpleTaskMonitor implements TaskMonitor {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTaskMonitor.class);

    public static final long SCAN_INTERVAL_SLEEP_SECONDS = 10;
    public static final long INTERVAL_SECONDS = 1;

    /*
     * 任务管理器
     */
    private TaskManager taskManager;

    /*
     * 任务监听器执行线程
     */
    private Thread monitorThread;

    @Autowired
    public SimpleTaskMonitor(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void maintain() {
        monitorThread = new Thread(new TaskMonitorExecutor(), "TaskMonitorExecutor_Thread");
        // 设置为守护线程
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    @Override
    public void stop() {
        logger.info("class=SimpleTaskMonitor||method=stop||msg=task monitor stopByJobCode!");
        try {
            taskManager.stopAll();
            if (monitorThread != null && monitorThread.isAlive()) {
                monitorThread.interrupt();
            }
        } catch (Exception e) {
            logger.error("class=SimpleTaskMonitor||method=stop||msg=exception!", e);
        }
    }

    class TaskMonitorExecutor implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    logger.info("class=TaskMonitorExecutor||method=run||msg=fetch tasks at regular {}",
                            SCAN_INTERVAL_SLEEP_SECONDS);

                    List<KfTask> kfTaskList = taskManager.nextTriggers(INTERVAL_SECONDS);

                    if (kfTaskList == null || kfTaskList.size() == 0) {
                        logger.info("class=TaskMonitorExecutor||method=run||msg=no tasks need run!");
                        ThreadUtil.sleep(INTERVAL_SECONDS, TimeUnit.SECONDS);
                        continue;
                    }

                    // 未到执行时间，等待
                    logger.info("class=TaskMonitorExecutor||method=run||msg=fetch tasks {}",
                            kfTaskList.stream().map( KfTask::getTaskName).collect(Collectors.toList()));

                    Long firstFireTime = kfTaskList.stream().findFirst().get().getNextFireTime().getTime();
                    Long nowTime = System.currentTimeMillis();
                    if (nowTime < firstFireTime) {
                        Long between = firstFireTime - nowTime;
                        ThreadUtil.sleep(between + 1, TimeUnit.MILLISECONDS);
                    }

                    logger.info("class=TaskMonitorExecutor||method=run||msg=start tasks={}, "
                                    + "firstFireTime={}, nowTime={}",
                            kfTaskList.stream().map( KfTask::getTaskName).collect(Collectors.toList()),
                            firstFireTime, nowTime);

                    // 提交任务
                    taskManager.submit( kfTaskList );
                } catch (Exception e) {
                    logger.error("class=TaskMonitorExecutor||method=run||msg=exception!", e);
                }

                // 每次扫描，间隔1s。为了线程终端创造条件
                ThreadUtil.sleep(SCAN_INTERVAL_SLEEP_SECONDS, TimeUnit.SECONDS);
            }
        }
    }
}
