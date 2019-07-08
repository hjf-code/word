package com.paul.core.system.service;

import com.paul.common.base.BaseService;
import com.paul.core.system.entity.UserEntity;

/**
 * 轨迹
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/28 20:05
 */
public interface UserService extends BaseService<UserEntity> {

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return com.paul.core.system.entity.UserEntity
     */
    UserEntity getObjectByUsername(String username);
}