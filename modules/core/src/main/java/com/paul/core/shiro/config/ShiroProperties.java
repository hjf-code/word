package com.paul.core.shiro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 读取配置文件, 某参数若不需要重复使用, 则直接@Value也方便
 * 用这个类比@Value好的还有一点: 可以轻松读取List和Map类型的配置
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/23 14:13
 */
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "spring.shiro")
@Data
public class ShiroProperties {

    private List<String> filterChain;
}