package com.didiglobal.logi.job;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.bean.AuvTask;
import com.didiglobal.logi.job.common.enums.TaskStatusEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobFactory;
import com.didiglobal.logi.job.mapper.AuvTaskMapper;
import com.didiglobal.logi.job.utils.CronExpression;
import com.didiglobal.logi.job.utils.IdWorker;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskBeanPostProcessor implements BeanPostProcessor {

  private static final Logger logger = LoggerFactory.getLogger(TaskBeanPostProcessor.class);

  private static Map<String, AuvTask> taskMap = new HashMap<>();

  @Autowired
  private AuvTaskMapper auvTaskMapper;

  @Autowired
  private JobFactory jobFactory;

  @Autowired
  private LogIJobProperties logIJobProperties;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    // add job to jobFactory
    if (bean instanceof Job) {
      jobFactory.addJob(beanClass.getCanonicalName(), (Job) bean);
    } else {
      return bean;
    }

    // check and register to db

    Task taskAnnotation = beanClass.getAnnotation(Task.class);
    if (taskAnnotation == null || !taskAnnotation.autoRegister()) {
      return bean;
    }
    // check
    if (!check(taskAnnotation)) {
      logger.error("class=TaskBeanPostProcessor||method=blacklist||url=||msg=invalid schedule {}",
              taskAnnotation.toString());
    }
    // not exists register
    AuvTask task = getAuvTask(beanClass, taskAnnotation);
    task.setCode(IdWorker.getIdStr());
    task.setStatus(TaskStatusEnum.WAITING.getValue());
    if (!contains(task)) {
      auvTaskMapper.insert(task);
    }
    return bean;
  }

  //########################## private method #################################

  private boolean check(Task schedule) {
    return CronExpression.isValidExpression(schedule.cron());
  }

  private AuvTask getAuvTask(Class<?> beanClass, Task schedule) {
    AuvTask auvTask = new AuvTask();
    auvTask.setName(schedule.name());
    auvTask.setDescription(schedule.description());
    auvTask.setCron(schedule.cron());
    auvTask.setClassName(beanClass.getCanonicalName());
    auvTask.setParams("");
    auvTask.setRetryTimes(schedule.retryTimes());
    auvTask.setLastFireTime(new Timestamp(System.currentTimeMillis()));
    auvTask.setTimeout(schedule.timeout());
    auvTask.setSubTaskCodes("");
    auvTask.setConsensual(schedule.consensual().name());
    auvTask.setTaskWorkerStr("");
    auvTask.setAppName( logIJobProperties.getAppName());
    return auvTask;
  }

  private boolean contains(AuvTask task) {
    if (taskMap.isEmpty()) {
      List<AuvTask> auvTasks = auvTaskMapper.selectByAppName( logIJobProperties.getAppName());
      taskMap = auvTasks.stream().collect(Collectors.toMap(AuvTask::getClassName,
              Function.identity()));
    }
    return taskMap.containsKey(task.getClassName());
  }
}