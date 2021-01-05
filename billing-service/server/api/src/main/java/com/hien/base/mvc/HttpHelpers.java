package com.hien.base.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpHelpers {
    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getCurrentResponse() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getResponse();
    }
}
