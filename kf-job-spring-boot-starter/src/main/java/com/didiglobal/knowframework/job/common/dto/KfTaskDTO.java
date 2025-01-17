package com.didiglobal.knowframework.job.common.dto;

import lombok.Data;

@Data
public class KfTaskDTO {

    private String name;
    private String description;
    private String cron;
    private String className;
    private String params;
    private Integer retryTimes;
    private String consensual;

}