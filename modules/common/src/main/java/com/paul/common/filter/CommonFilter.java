package com.paul.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.paul.common.constant.WebConstant.DEFAULT_CHARSET;
import static com.paul.common.constant.WebConstant.TEXT_HTML_UTF8;

/**
 * 通用过滤器, 用来设置编码, 响应内容类型
 * Order(1)主键表示执行顺序, 值越小, 越先执行
 *
 * @author paul paul@gmail.com
 * @since 2019/3/27 19:54
 */
@Order(1)
@WebFilter(filterName = "common", urlPatterns = "/*")
@Slf4j
public class CommonFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        // Spring Boot默认的请求编码就是UTF-8
        req.setCharacterEncoding(DEFAULT_CHARSET);
        // Spring Boot默认的响应编码就是UTF-8
        resp.setCharacterEncoding(DEFAULT_CHARSET);
        // 响应内容类型默认设置为HTML
        resp.setContentType(TEXT_HTML_UTF8);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}