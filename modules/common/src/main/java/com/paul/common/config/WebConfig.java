package com.paul.common.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.paul.common.constant.DateConstant.DEFAULT_DATE_PATTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * 默认首页, 静态资源访问, 全局跨域, 以及FastJson转换配置
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/6/5 13:00
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * 是否全局跨域
     */
    @Value("${spring.all-cors}")
    private boolean allCors;

    /**
     * 配置全局跨域
     *
     * @param corsRegistry 跨域注册
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        if (allCors) {
            //设置允许跨域的路径
            corsRegistry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("*")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
        }
    }

    /**
     * 设置默认主页, 该功能需要导thymeleaf包
     *
     * @param viewControllerRegistry 视图层注册
     */
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {

        viewControllerRegistry.addViewController("/").setViewName("forward:/login/index");
        viewControllerRegistry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 配置静态资源的默认访问文件夹
     *
     * @param resourceHandlerRegistry 静态资源注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

        resourceHandlerRegistry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置FastJson
     *
     * @param converters 转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 需要定义一个converter转换消息的对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 添加FastJSON的配置信息
        com.alibaba.fastjson.support.config.FastJsonConfig fastJsonConfig =
            new com.alibaba.fastjson.support.config.FastJsonConfig();
        // 日期格式化, 只能使@ResponseBody的日期格式化, 不能使JSON.toJsonString()格式化
        // 这里定义的是全局日期格式化, 但是会覆盖@JSONField的日期格式化
        // 本项目中重写了com.alibaba.fastjson.serializer.SerializeFilterable源码后,
        // @JSONField的日期格式化会覆盖全局日期格式化
        fastJsonConfig.setDateFormat(DEFAULT_DATE_PATTERN);
        // 格式化输出
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 消除对同一对象循环引用的问题1
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteDateUseDateFormat);
        converter.setFastJsonConfig(fastJsonConfig);

        // MappingJacksonHttpMessageConverter会默认设置contentType为application/json,
        // 在IE9下返回会出现提示下载的现象, 出现这种情况可以手动指定头信息为"text/html"
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);

        converters.add(converter);
    }
}