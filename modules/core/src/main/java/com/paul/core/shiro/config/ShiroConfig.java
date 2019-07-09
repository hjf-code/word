package com.paul.core.shiro.config;

import com.paul.core.shiro.credential.RetryLimitCredentialsMatcher;
import com.paul.core.shiro.dao.CustomSessionDao;
import com.paul.core.shiro.realm.CustomRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/20 23:22
 */
@Configuration
@Slf4j
public class ShiroConfig {

    /**
     * 散列算法
     */
    public static final String HASH_ALGORITHM_NAME = "MD5";

    /**
     * 散列迭代次数
     */
    public static final int HASH_ITERATIONS = 2;

    /**
     * 是否使用16进制字符串加密, false则使用base64加密
     */
    public static final boolean STORED_CREDENTIALS_HEX_ENCODED = true;

    /**
     * Shiro过滤器
     *
     * @param securityManager 安全管理器
     * @return org.apache.shiro.spring.web.ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
        ShiroProperties shiroProperties) {
        // 可以将securityManager设置为静态单例的, 这样就可以在任何地方获得当前用户:
        //      Subject currentUser = SecurityUtils.getSubject()
        // 但是, Shiro官方建议不将securityManager设置为静态单例的(占用VM)
        //        SecurityUtils.setSecurityManager(securityManager)

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // Shiro的核心安全接口, 这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 要求登录时的链接, 默认会自动寻找Web工程根目录下的"/login.html"或"/login.jsp"或"/login"请求
        // 在此处设置后, 会自动给其他默认过滤器(如authc, user等)设置loginUrl属性, 无需重复配置(如下面的
        // formAuthenticationFilter方法不需要设置loginUrl了)
        shiroFilterFactoryBean.setLoginUrl("/login/a");
        // Shiro登录成功后自动跳转到上一个请求路径(如访问某个路径后, 自动跳转到登录页面的那个路径),
        // 若上一个请求是上面的loginUrl或者找不到上一个请求(即session过期), 则该属性生效
        // (此时若不配置该属性, 则默认跳转到项目根路径)
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 指定访问未授权url时, 重定向的url, 这里是统一配置, 也可以在各个Filter的实例中单独配置,
        // 这与上面的setLoginUrl情况一样.
        // 这里针对的未授权url是直接或间接继承AuthorizationFilter的Filter所配置的url
        //      这类Filter有:
        //          perms (org.apache.shiro.web.filter.authzPermissionsAuthorizationFilter):
        //              验证用户是否拥有所有权限(参数可写多个, 多参时必须加上引号, 且参数之间用逗号分割)
        //              单个例子  /user/** = perms[user:insert]
        //              多个例子: /user/** = perms["user:insert,user:query"]
        //          roles (org.apache.shiro.web.filter.authz.RolesAuthorizationFilter):
        //              验证用户是否拥有所有角色(参数可写多个, 多参时必须加上引号, 且参数之间用逗号分割)
        //              单个例子  /admin/** = perms[admin]
        //              多个例子: /admin/** = perms["admin,guest"]
        //          rest (org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter):
        //              REST风格拦截器, 自动根据请求方式构建权限字符串
        //              (对应关系为: PUT<==>update, POST<==>create, GET<==>read, DELETE<==>delete)
        //              (其他方式见官网)
        //              例: /user/** = rest[user]
        //                  当来POST请求时, 进行的权限检查类似: subject.isPermitted("user:create")
        //                  当来GET请求时, 进行的权限检查类似: subject.isPermitted("user:read")
        //          ssl (org.apache.shiro.web.filter.authz.SslFilter):
        //              只有请求协议是https才能通过, 否则自动跳转到https端口(443), 例: /admins/user/** = ssl
        //          port (org.apache.shiro.web.filter.authz.PortFilter):
        //              指定请求端口号, 非指定端口号, 则会自动跳转到指定端口号, 例: /word= port[8081]
        // 而直接或间接继承AuthenticationFilter的Filter所配置的url, 重定向的url是上面配置的loginUrl
        //      这类Filter有:
        //          anon (org.apache.shiro.web.filter.authc.AnonymousFilter):
        //              匿名拦截器, 即不需要登录即可访问, 般用于静态资源过滤, 例: /static/** = anon
        //          user (org.apache.shiro.web.filter.authc.UserFilter):
        //              用户拦截器, 用户登录过或"记住我"的都可, 例: /** = user
        //          auchc (org.apache.shiro.web.filter.authc.FormAuthenticationFilter):
        //              表单验证拦截器, 只要未登录就会拦截, 然后重定向到登录页面, 例: /shopping/pay = authc
        //          authcBasic (org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter):
        //              使用HTTP的BASIC身份认证, 其与auchc的不同之处在于:
        //                  auchc进行登出后, session中的用户信息就会被清除
        //                  而authcBasic需要关闭浏览器, session中的用户信息才会被清除
        //                      (即: 先登录然后退出登录, 只要不关闭浏览器, 页面仍然可以访问)
        //              例: /admin/user/** = authcBasic
        //          logout (org.apache.shiro.web.filter.authc.LogoutFilter):
        //              退出拦截器, 访问该url后退出登录, 退出成功后重定向的地址可以改(默认为:/, 可改redirectUrl自定义)
        //              例: /logout = logout
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        // 设置拦截链
        Map<String, String> filters = new LinkedHashMap<>();
        for (String aFilterChain : shiroProperties.getFilterChain()) {
            String[] filter = aFilterChain.split("=");
            filters.put(filter[0].trim(), filter[1].trim());
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filters);
        log.info("Shiro拦截器工厂类注入成功! ");
        return shiroFilterFactoryBean;
    }

    /**
     * AOP式方法级权限检查需要设置的两个Bean之一
     *
     * @param securityManager 安全管理器
     * @return org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
        DefaultWebSecurityManager securityManager) {

        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * Shiro生命周期处理器, 保证实现了Shiro内部lifecycle函数的bean执行
     *
     * @return org.apache.shiro.spring.LifecycleBeanPostProcessor
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {

        return new LifecycleBeanPostProcessor();
    }

    /**
     * AOP式方法级权限检查需要设置的两个Bean之一
     *
     * @return org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {

        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 基于Form表单的身份验证过滤器
     *
     * @return org.apache.shiro.web.filter.authc.FormAuthenticationFilter
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {

        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        // 表单提交的用户名参数名, 默认为username
        formAuthenticationFilter.setUsernameParam("username");
        // 表单提交的密码参数名, 默认为password
        formAuthenticationFilter.setPasswordParam("password");
        // 表单提交的记住我参数名, 默认为rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        // 认证身份失败会将抛出异常的类名放到request属性中, 这里设置键, 默认为shiroLoginFailure
        // 然后可以通过如下代码获得错误类名:
        //      String exceptionClassName = (String) req.getAttribute
        //      ("authenticationExceptionClassName")
        formAuthenticationFilter.setFailureKeyAttribute("authenticationExceptionClassName");
        return formAuthenticationFilter;
    }

    /**
     * 安全管理器
     *
     * @param customRealm             自定义Realm
     * @param ehCacheManager          缓存管理器
     * @param sessionManager          会话管理器
     * @param cookieRememberMeManager RememberMe管理器
     * @return org.apache.shiro.web.mgt.DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager(CustomRealm customRealm,
        EhCacheManager ehCacheManager, DefaultWebSessionManager sessionManager,
        CookieRememberMeManager cookieRememberMeManager) {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置了单个Realm, 也可以用securityManager.setRealms()配置多个
        securityManager.setRealm(customRealm);
        securityManager.setCacheManager(ehCacheManager);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(cookieRememberMeManager);
        return securityManager;
    }

    /**
     * 使用Cookie存储rememberMe
     *
     * @return org.apache.shiro.web.servlet.SimpleCookie
     */
    @Bean(name = "rememberMeCookie")
    public SimpleCookie rememberMeCookie() {

        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // 如果在cookie中设置了HttpOnly属性为true, 那么通过js脚本将无法读取到该cookie的信息, 这样能有效的防止XSS攻击
        simpleCookie.setHttpOnly(true);
        // 设置此cookie最大存在时间: 30天
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * rememberMe管理器
     *
     * @param rememberMeCookie 存储rememberMe的cookie
     * @param cipherKey        rememberMeCookie加密的密钥
     * @return org.apache.shiro.web.mgt.CookieRememberMeManager
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(SimpleCookie rememberMeCookie,
        @Value("${spring.shiro.remember-me.cipher-key}") String cipherKey) {

        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        // rememberMeCookie加密的密钥, 建议每个项目都不一样, 默认AES算法, 密钥长度(128 256 512)位
        cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
        // 设置对应的cookie
        cookieRememberMeManager.setCookie(rememberMeCookie);
        return cookieRememberMeManager;
    }

    /**
     * 使用Cookie存储会话ID
     *
     * @return org.apache.shiro.web.servlet.SimpleCookie
     */
    @Bean(name = "sessionIdCookie")
    public SimpleCookie sessionIdCookie() {

        SimpleCookie simpleCookie = new SimpleCookie("sessionId");
        // 如果在cookie中设置了HttpOnly属性为true, 那么通过js脚本将无法读取到该cookie的信息, 这样能有效的防止XSS攻击
        simpleCookie.setHttpOnly(true);
        // 设置此cookie最大存在时间, 单位为秒, 默认为-1, 代表关闭浏览器, 此cookie就会消失
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

    /**
     * 会话验证调度器, 用于定期的验证会话是否已过期, 如果过期将停止会话.
     * 出于对性能的考虑, 一般情况下都是获取会话时来验证会话是否过期并停止会话的, 但是如果在Web环境
     * 中, 如果用户不主动退出是不知道会话是否过期的, 因此需要定期的检测会话是否过期.
     *
     * @return org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler
     */
    @Bean
    public QuartzSessionValidationScheduler sessionValidationScheduler() {

        QuartzSessionValidationScheduler sessionValidationScheduler =
            new QuartzSessionValidationScheduler();
        // 设置会话验证间隔为30分钟(单位: 毫秒)
        sessionValidationScheduler.setSessionValidationInterval(1800000);
        sessionValidationScheduler.setSessionManager(new DefaultWebSessionManager());
        return sessionValidationScheduler;
    }

    /**
     * 会话管理器, 用于Web环境的实现, 可以替代ServletContainerSessionManager, 自己维护着会话,
     * 直接废弃了Servlet容器的会话管理
     *
     * @param sessionValidationScheduler 会话验证调度器
     * @param customSessionDao           自定义的shiro会话的DAO
     * @param sessionIdCookie            存储会话ID的Cookie
     * @return org.apache.shiro.web.session.mgt.DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager(
        QuartzSessionValidationScheduler sessionValidationScheduler,
        CustomSessionDao customSessionDao, SimpleCookie sessionIdCookie) {

        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置session过期时间为1小时(单位: 毫秒), 默认为30分钟.
        sessionManager.setGlobalSessionTimeout(3600000);
        // 默认为true, 在会话过期后会调用SessionDAO的delete方法删除会话. 如会话是持久化存储的, 可以调用此方法进行删除
        sessionManager.setDeleteInvalidSessions(true);
        // 是否开启会话验证器, 默认true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 设置会话验证调度器, 默认使用ExecutorServiceSessionValidationScheduler
        sessionManager.setSessionValidationScheduler(sessionValidationScheduler);
        // 设置自定义的shiro会话的DAO
        //        sessionManager.setSessionDAO(customSessionDao);
        // 是否启用SessionIdCookie, 默认启用, 若禁用则不会设置SessionIdCookie, 即默认使用了Servlet容器的JSESSIONID,
        // 且通过URL重写(URL中的“;JSESSIONID=id”部分)保存SessionId
        sessionManager.setSessionIdCookieEnabled(true);
        // 设置SessionIdCookie
        sessionManager.setSessionIdCookie(sessionIdCookie);
        return sessionManager;
    }

    /**
     * 自定义realm
     *
     * @param retryLimitCredentialsMatcher 凭证匹配器
     * @return com.paul.core.shiro.realm.CustomRealm
     */
    @Bean
    public CustomRealm myRealm(RetryLimitCredentialsMatcher retryLimitCredentialsMatcher) {

        CustomRealm customRealm = new CustomRealm();
        // 指定凭证匹配器
        customRealm.setCredentialsMatcher(retryLimitCredentialsMatcher);
        // 是否启用缓存, 默认为false
        customRealm.setCachingEnabled(true);
        // 是否启用身份验证信息缓存, 默认为false
        customRealm.setAuthenticationCachingEnabled(true);
        // 身份认证的缓存名
        customRealm.setAuthenticationCacheName("authenticationCache");
        // 是否启用授权信息缓存，默认为false
        customRealm.setAuthorizationCachingEnabled(true);
        // 授权信息的缓存名
        customRealm.setAuthorizationCacheName("authorizationCache");
        return customRealm;
    }

    /**
     * 凭证匹配器, 这些设置要和加密时的设置一样
     *
     * @param ehCacheManager EhCache缓存管理器
     * @return com.paul.core.shiro.credential.RetryLimitCredentialsMatcher
     */
    @Bean
    public RetryLimitCredentialsMatcher credentialsMatcher(EhCacheManager ehCacheManager) {

        RetryLimitCredentialsMatcher credentialsMatcher =
            new RetryLimitCredentialsMatcher(ehCacheManager);
        // 散列算法
        credentialsMatcher.setHashAlgorithmName(HASH_ALGORITHM_NAME);
        // 散列迭代次数
        credentialsMatcher.setHashIterations(HASH_ITERATIONS);
        // 是否使用16进制字符串即toHex()方法加密, false则使用base64加密
        credentialsMatcher.setStoredCredentialsHexEncoded(STORED_CREDENTIALS_HEX_ENCODED);
        return credentialsMatcher;
    }

    /**
     * 缓存管理器, 使用EhCache实现.
     *
     * @return org.apache.shiro.cache.ehcache.EhCacheManager
     */
    @Bean
    public EhCacheManager cacheManager() {

        EhCacheManager ehCacheManager = new EhCacheManager();
        // 指定缓存配置文件位置
        ehCacheManager.setCacheManagerConfigFile("classpath:config/ehcache.xml");
        return ehCacheManager;
    }
}