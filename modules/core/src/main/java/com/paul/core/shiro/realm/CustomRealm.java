package com.paul.core.shiro.realm;

import com.paul.core.system.entity.UserEntity;
import com.paul.core.system.service.UserService;
import com.paul.core.system.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 自定义Realm
 * 身份认证(authentication)和授权信息(authorization)松耦合的原因:
 * 例如: 当使用"记住我"功能时, 首次登录时会认证身份并授权,
 * 而在rememberMe过期时间内(一般时间比较长, 可能7天甚至更长)访问某些功能,
 * 都不需要再次登录去认证身份, 但是会需要授权, 授权缓存一般比较短(可能1小时甚至更短).
 * 不是特别重要的功能(如主页), 可以将过滤器级别设置为user, 已认证身份或"记住我"的都可以访问.
 * 重要的功能(如支付), 可以将过滤器级别设置为authc, 访问功能前必须登录.
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/18 16:43
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 获得身份认证, 登录时先要认证身份
     *
     * @param token 包含用户的账号和密码
     * @return org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException {
        //        // 系统生成的验证码
        //        String captcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        //        // 用户填写的验证码
        //        String userCaptcha = HttpUtils.getRequest().getParameter("captcha");
        //        if (!captcha.equalsIgnoreCase(userCaptcha)) {
        //            log.error("验证码错误");
        //            throw new AuthenticationException();
        //        }

        UserEntity userEntity = userService.getObjectByUsername((String) token.getPrincipal());
        if (userEntity == null) {
            log.info("帐号不存在! ");
            throw new UnknownAccountException();
        }
        if (Objects.equals(Boolean.TRUE, userEntity.getLocked())) {
            log.info("用户" + userEntity.getUsername() + "的账号被锁定! ");
            throw new LockedAccountException();
        }

        // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配, 如果觉得人家的不好可以自定义实现
        return new SimpleAuthenticationInfo(userEntity, userEntity.getPassword(),
            ByteSource.Util.bytes(userEntity.getSalt()), getName());
    }

    /**
     * 获得授权信息, 认证身份后执行
     * (1) 若未启用缓存, 则需要授权信息时会直接调用doGetAuthorizationInfo方法.
     * (2) 若启用了缓存, 则需要授权信息时会调用getAuthorizationInfo方法.
     * 1> 若缓存中有当前subject的授权信息, 会直接返回缓存中的授权信息.
     * 2> 若缓存中没有当前subject的授权信息, 则调用doGetAuthorizationInfo方法,
     * 并将返回的授权信息存入缓存, 方便下次使用.
     * 3> 若在项目运行时, 某subject的权限发生变化, 则需要在变化时调用clearCachedAuthorizationInfo方法
     * 来清空缓存中的授权信息, 以便下次获得的是最新的授权信息, 而不是之前缓存的过时授权信息.
     *
     * @param principals subject的身份集合, 用于查找subject的授权信息
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        if (principals == null) {
            return null;
        }
        // 从身份集合中获取用户账号信息
        String username = ((UserEntity) principals.getPrimaryPrincipal()).getUsername();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 查询用户角色并在授权信息中设置角色
        //        authorizationInfo.setRoles(userService.getRoles(username));
        //        // 在授权信息中设置权限
        //        authorizationInfo.setStringPermissions(userService.getPermissions(username));
        return authorizationInfo;
    }
}