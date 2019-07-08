package com.paul.core.system.controller;

import com.paul.common.util.IDUtils;
import com.paul.core.shiro.credential.PasswordHelper;
import com.paul.core.system.entity.UserEntity;
import com.paul.core.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录
 *
 * @author paul paul@gmail.com
 * @since 2019/3/24 13:18
 */
@Controller
@RequestMapping("login")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/a")
    public String login() {

        return "login";
    }

    @RequestMapping("index")
    public String index() {

        return "index";
    }

    @RequestMapping("do")
    @ResponseBody
    public String doIt() {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(IDUtils.getId());
        userEntity.setUsername("admin");
        userEntity.setPassword("admin");
        PasswordHelper.encryptPassword(userEntity);
        userService.insert(userEntity);
        return "ok";
    }
}