package com.didiglobal.knowframework.observability;

import com.didiglobal.knowframework.observability.conponent.thread.ContextExecutorService;
import com.didiglobal.knowframework.observability.conponent.thread.ContextScheduledExecutorService;
import com.didiglobal.knowframework.observability.exporter.LoggingMetricExporter;
import com.didiglobal.knowframework.observability.exporter.LoggingSpanExporter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.apache.commons.lang3.StringUtils;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Observability {

    private static OpenTelemetry delegate = initOpenTelemetry();

    /**
     * 指标 export 间隔时长
     */
    private static final long METRIC_EXPORT_INTERVAL_MS = 1000l;

    /**
     * @return 初始化 OpenTelemetry 对象
     */
    private static OpenTelemetry initOpenTelemetry() {

        // Create an instance of PeriodicMetricReader and configure it
        // to export via the logging exporter
        MetricReader periodicReader =
                PeriodicMetricReader.builder(LoggingMetricExporter.create())
                        .setInterval(Duration.ofMillis(METRIC_EXPORT_INTERVAL_MS))
                        .build();
        // This will be used to create instruments
        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder().registerMetricReader(periodicReader).build();

        // Tracer provider configured to export spans with SimpleSpanProcessor using
        // the logging exporter.
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build())
                        .build();
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        Runtime.getRuntime().addShutdownHook(new Thread(meterProvider::close));
        return sdk;
    }

    /**
     * @param instrumentationScopeName 域名
     * @return 获取给定域名对应 meter 对象
     */
    public static Meter getMeter(String instrumentationScopeName) {
        return delegate.getMeter(instrumentationScopeName);
    }

    /**
     * @param instrumentationScopeName 域名
     * @return 获取给定域名对应 tracer 对象
     */
    public static Tracer getTracer(String instrumentationScopeName) {
        return delegate.getTracer(instrumentationScopeName);
    }

    /**
     * @return 获取当前 span id
     */
    public static String getCurrentSpanId() {
        Span span = Span.current();
        if(Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String spanId = span.getSpanContext().getSpanId();
            return spanId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

    /**
     * @return 获取当前 span 所属 trace 对应 trace id
     */
    public static String getCurrentTraceId() {
        Span span = Span.current();
        if(Span.getInvalid() != span && span.getSpanContext().isValid()) {
            String tracerId = span.getSpanContext().getTraceId();
            return tracerId;
        } else {
            //当前上下文不存在于任何span中
            return StringUtils.EMPTY;
        }
    }

    /**
     * @param executor 待封装线程池
     * @return 附带 trace 上下文信息线程池
     */
    public static ExecutorService wrap(ExecutorService executor) {
        return new ContextExecutorService(executor);
    }

    /**
     * @param executor 待封装调度线程池
     * @return 附带 trace 上下文信息调度线程池
     */
    public static ScheduledExecutorService wrap(ScheduledExecutorService executor) {
        return new ContextScheduledExecutorService(executor);
    }

    public static TextMapPropagator getTextMapPropagator() {
        return delegate.getPropagators().getTextMapPropagator();
    }

}
