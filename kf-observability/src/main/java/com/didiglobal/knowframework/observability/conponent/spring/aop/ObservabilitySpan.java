//package com.didiglobal.logi.observability.conponent.spring.aop;
//
//import com.didiglobal.logi.observability.Observability;
//import io.opentelemetry.api.trace.Span;
//import io.opentelemetry.api.trace.StatusCode;
//import io.opentelemetry.api.trace.Tracer;
//import io.opentelemetry.context.Scope;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class ObservabilitySpan {
//
//    private Tracer tracer = Observability.getTracer(ObservabilitySpan.class.getName());
//
//    @Pointcut(value = "execution(* com.didiglobal..*.*(..)) || execution(* com.didichuxing..*.*(..))")//TODO：切片可配 后续有时间
//    public void aopSpan() {}
//
//    @Around("aopSpan()")
//    public Object markSpan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        String clazzName = proceedingJoinPoint.getSignature().getDeclaringTypeName();
//        String methodName = proceedingJoinPoint.getSignature().getName();
//        Span span = tracer.spanBuilder(String.format("%s.%s", clazzName, methodName)).startSpan();
//        try (Scope scope = span.makeCurrent()) {
//            Object result = proceedingJoinPoint.proceed();
//            span.setStatus(StatusCode.OK);
//            return result;
//        } catch (Throwable ex) {
//            span.setStatus(StatusCode.ERROR, ex.getMessage());
//            throw ex;
//        } finally {
//            span.end();
//        }
//    }
//
//}
