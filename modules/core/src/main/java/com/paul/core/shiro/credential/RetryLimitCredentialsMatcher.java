package com.paul.core.shiro.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带有重试密码次数限制的凭证匹配器
 *
 * @author paul paul@gmail.com
 * @since 2019/3/20 23:22
 */
public class RetryLimitCredentialsMatcher extends HashedCredentialsMatcher {

    /**
     * 允许重试密码次数
     */
    @Value("${spring.shiro.allow-count}")
    private int allowCount;

    /**
     * 集群中可能会导致出现验证多过5次的现象，因为AtomicInteger只能保证单节点并发
     */
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitCredentialsMatcher(CacheManager cacheManager) {

        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 若配置文件中未指定该参数, 或该参数为0, 则不限制重试密码次数
        if (allowCount <= 0) {
            return super.doCredentialsMatch(token, info);
        }
        // 从token中获得账号
        String username = (String) token.getPrincipal();
        // 每个用户的重试密码次数
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (null == retryCount) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if (retryCount.incrementAndGet() > allowCount) {
            throw new ExcessiveAttemptsException();
        }
        // 真正的密码匹配验证逻辑
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            // clear retry data
            passwordRetryCache.remove(username);
        }
        return matches;
    }
}