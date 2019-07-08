package com.paul.common.exception;

import com.alibaba.fastjson.JSON;
import com.paul.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.paul.common.constant.WebConstant.APPLICATION_JSON_UTF8;

/**
 * 异常处理器, 捕获运行时异常
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/27 19:41
 */
@Slf4j
public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp,
        Object handler, Exception e) {

        try {
            // 因为要返回JSON所以响应内容类型设置为JSON
            resp.setContentType(APPLICATION_JSON_UTF8);

            //记录异常日志
            log.error(e.getMessage(), e);

            String json = JSON.toJSONString(R.err(e.getMessage()));
            resp.getWriter().print(json);
        } catch (Exception ex) {
            log.error("ExceptionHandler 异常处理失败! ", ex);
            ex.printStackTrace();
        }
        return new ModelAndView();
    }
}