package com.example.mentalhealth.aspect;

import com.example.mentalhealth.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Around("@annotation(com.example.mentalhealth.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        com.example.mentalhealth.entity.OperationLog logEntity = new com.example.mentalhealth.entity.OperationLog();
        logEntity.setCreateTime(LocalDateTime.now());
        logEntity.setRequestMethod(request != null ? request.getMethod() : "UNKNOWN");
        logEntity.setRequestUrl(request != null ? request.getRequestURI() : "");
        logEntity.setIpAddress(request != null ? getClientIp(request) : "0.0.0.0");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.example.mentalhealth.annotation.OperationLog operationLog = method.getAnnotation(com.example.mentalhealth.annotation.OperationLog.class);

        if (operationLog != null) {
            logEntity.setOperationModule(operationLog.module());
            logEntity.setOperationType(operationLog.type());
            logEntity.setOperationDesc(operationLog.description());
        }

        StringBuilder params = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null && isSimpleType(arg)) {
                    params.append(arg.toString()).append(",");
                }
            }
        }
        logEntity.setRequestParams(params.toString());

        Object result = null;
        String status = "成功";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = "失败";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logEntity.setOperationStatus(status + " (耗时:" + duration + "ms)");
            logEntity.setErrorMessage(errorMessage);

            try {
                operationLogRepository.save(logEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSimpleType(Object obj) {
        return obj instanceof String || obj instanceof Number ||
               obj instanceof Boolean || obj instanceof Character;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
