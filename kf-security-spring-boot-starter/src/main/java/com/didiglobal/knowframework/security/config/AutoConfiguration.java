package com.didiglobal.knowframework.security.config;

import com.didiglobal.knowframework.security.properties.LogiSecurityProper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjm
 */
@Configuration("logiSecurityAutoConfiguration")
@EnableConfigurationProperties(LogiSecurityProper.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.didiglobal.knowframework.security")
public class AutoConfiguration {

    private final LogiSecurityProper proper;

    public AutoConfiguration(LogiSecurityProper proper) {
        this.proper = proper;
    }

    public LogiSecurityProper getProper() {
        return proper;
    }
}

