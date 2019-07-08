package com.paul.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 无需参数获得Request和Response
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/20 21:04
 */
public class HttpUtils {

    /**
     * 获取Request
     *
     * @return javax.servlet.http.HttpServletRequest
     */
    public static HttpServletRequest getRequest() {

        return ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 获取Response
     *
     * @return javax.servlet.http.HttpServletResponse
     */
    public static HttpServletResponse getResponse() {

        return ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}