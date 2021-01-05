package com.hien.base.mvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.hien.base.JsonSerializer;
import com.hien.base.log.LogHelpers;
import com.hien.base.log.LogObj;

@Component
@DependsOn("springApplicationContextHolder")
@Aspect
public class CustomRestAPIAdvice {

    @Around("@annotation(customRestAPI)")
    public Object around(ProceedingJoinPoint pjp, CustomRestAPI customRestAPI) throws Throwable {
        LogObj log = new LogObj();
        try {
            HttpServletRequest request = HttpHelpers.getCurrentRequest();
            HttpServletResponse response = HttpHelpers.getCurrentResponse();

            log.logName(String.format("%s:%s", request.getMethod(), request.getRequestURI()));

            Object responseBody = execute(customRestAPI, pjp, request, response, log);

            log.putResponseBody(responseBody);

            return responseBody;

        } catch (Throwable tr) {
            log.error(tr);
            throw tr;
        } finally {
            if ("failure".equals(log.status)) {
                LogHelpers.logger.error(JsonSerializer.object2Json(log));
            } else {
                if (customRestAPI.log()) {
                    LogHelpers.logger.info(JsonSerializer.object2Json(log));
                }
            }
        }
    }

    private Object execute(CustomRestAPI jwtRestAPI, ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, LogObj log) throws Throwable {

        Object[] args = tweakParams(pjp, request, log);

        Object responseBody = pjp.proceed(args);

        response.setStatus(200);

        return responseBody;
    }

    private Object[] tweakParams(ProceedingJoinPoint pjp, HttpServletRequest request, LogObj log) throws NoSuchMethodException, SecurityException, IOException {
        Object args[] = pjp.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof LogObj) {
                args[i] = log;
            }
        }

        logRequest(pjp, request, log, args);

        return args;
    }

    private void logRequest(ProceedingJoinPoint pjp, HttpServletRequest request, LogObj log, Object[] args) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String methodName = signature.getMethod().getName();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        Annotation[][] annotations = pjp.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();

        for (int i = 0; i < parameterTypes.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof RequestBody) {
                    log.putRequestBody(args[i]);
                }
            }
        }

        log.putRequestHeader(Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(n -> n, n -> request.getHeader(n))));
    }

}
