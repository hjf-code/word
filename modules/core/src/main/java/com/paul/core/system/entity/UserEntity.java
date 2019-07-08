package com.paul.core.system.entity;


import com.paul.common.base.BaseEntity;
import lombok.Data;

/**
 * 系统用户
 *
 * @author paul paul@gmail.com
 * @since 2019/3/18 16:51
 */
@Data
public class UserEntity extends BaseEntity {

    private static final long serialVersionUID = -4419074652204830866L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码, 数据库中存加密后的密码
     */
    private String password;

    /**
     * 密码的盐, 其作用是: 即使两个相同的原始密码, 加密后的密码也不一样
     */
    private String salt;

    /**
     * 是否锁定
     */
    private Boolean locked;
}