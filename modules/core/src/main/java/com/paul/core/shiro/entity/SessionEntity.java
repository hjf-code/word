package com.paul.core.shiro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * shiro会话
 *
 * @author paul paul@gmail.com
 * @since 2019/3/21 22:57
 */
@Data
@AllArgsConstructor
public class SessionEntity {

    private static final long serialVersionUID = -1058318866957363412L;

    private String id;

    private String session;
}