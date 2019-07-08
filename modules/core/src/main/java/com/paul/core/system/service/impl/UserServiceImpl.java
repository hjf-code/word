package com.paul.core.system.service.impl;

import com.paul.common.base.BaseServiceImpl;
import com.paul.core.system.dao.UserDao;
import com.paul.core.system.entity.UserEntity;
import com.paul.core.system.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务
 *
 * @author paul paul@gmail.com
 * @since 2019/3/26 18:08
 */
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserDao> implements UserService {

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return com.paul.core.system.entity.UserEntity
     */
    public UserEntity getObjectByUsername(String username) {

        return dao.getObjectByUsername(username);
    }
}