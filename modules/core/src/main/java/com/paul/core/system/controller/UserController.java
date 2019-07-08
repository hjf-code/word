package com.paul.core.system.controller;

import com.paul.common.base.BaseController;
import com.paul.core.system.entity.UserEntity;
import com.paul.core.system.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户
 *
 * @author paul paul@gmail.com
 * @since 2019/3/28 14:54
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController<UserEntity, UserService> {
}