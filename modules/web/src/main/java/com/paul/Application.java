package com.paul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletComponentScan注解后, Servlet, Filter, Listener
 * 可以直接通过@WebServlet, @WebFilter, @WebListener注解自动注册
 *
 * @author paul paul@gmail.com
 * @since 2019/3/27 22:27
 */
@SpringBootApplication
@ServletComponentScan("com.paul.web.common.common")
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}