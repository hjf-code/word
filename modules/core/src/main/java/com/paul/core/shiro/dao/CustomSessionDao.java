package com.paul.core.shiro.dao;

import com.alibaba.fastjson.JSON;
import com.paul.common.util.IDUtils;
import com.paul.core.shiro.entity.SessionEntity;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义sessionDao, 这其实是一个Service, 真正的DAO其实是RealSessionDAO, 由于sessionManager.setSessionDAO(), 所以起名DAO
 * 对于分布式系统, 一般都牵扯到Session共享问题, 而想实现Session共享, 就要实现Session的持久化操作,
 * 即将内存中的Session持久化至缓存数据库.
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/21 20:41
 */
@Component
public class CustomSessionDao extends EnterpriseCacheSessionDAO {

    @Autowired
    private RealSessionDAO realSessionDAO;

    public CustomSessionDao() {

        super();
        // 设置Session的缓存名, 默认是shiro-activeSessionCache
        this.setActiveSessionsCacheName("activeSessionCache");
        // 用于生成会话ID, 默认是JavaUuidSessionIdGenerator, 即: 使用java.util.UUID生成, 这里去掉"-", 使生成的ID为32位
        this.setSessionIdGenerator(session -> IDUtils.getId());
    }

    @Override
    protected Serializable doCreate(org.apache.shiro.session.Session session) {

        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        realSessionDAO.insert(new SessionEntity(sessionId.toString(), JSON.toJSONString
            (session)));
        return sessionId;
    }

    @Override
    protected org.apache.shiro.session.Session doReadSession(Serializable sessionId) {

        return JSON.parseObject(realSessionDAO.selectById(sessionId.toString()).getSession(),
                                org.apache.shiro.session.Session.class);
    }

    @Override
    protected void doUpdate(org.apache.shiro.session.Session session) {

        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid
            ()) {
            return;
        }
        realSessionDAO.updateById(new SessionEntity(session.getId().toString(), JSON
            .toJSONString(session)));
    }

    @Override
    protected void doDelete(org.apache.shiro.session.Session session) {

        realSessionDAO.deleteById(session.getId().toString());
    }
}