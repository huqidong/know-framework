package com.didiglobal.logi.job.core.monitor;

/**
 * 监控器.
 *
 * @author dengshan
 */
public interface Monitor {

    /*
     * 保持执行
     */
    void maintain();

    /*
     * 停止
     */
    void stop();
}
