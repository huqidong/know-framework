package com.didiglobal.knowframework.job.core.job;

import com.didiglobal.knowframework.job.KfJobProperties;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * job thread pool executor.
 *
 * @author ds
 */
@Component
public class JobExecutor {

    public ThreadPoolExecutor threadPoolExecutor;

    /**
     * construct
     * @param properties 配置信息
     */
    @Autowired
    public JobExecutor(KfJobProperties properties) {
        this.threadPoolExecutor = new ThreadPoolExecutor(properties.getInitThreadNum(),
                properties.getMaxThreadNum(), 10L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
    }

    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }
}
