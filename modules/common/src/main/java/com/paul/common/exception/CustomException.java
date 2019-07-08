package com.paul.common.exception;

import lombok.Data;

/**
 * 自定义异常
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/28 11:27
 */
@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 5132288807648359545L;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 错误码
     */
    private int code = 500;

    public CustomException(String msg) {

        super(msg);
        this.msg = msg;
    }

    public CustomException(String msg, Throwable e) {

        super(msg, e);
        this.msg = msg;
    }

    public CustomException(int code, String msg) {

        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public CustomException(int code, String msg, Throwable e) {

        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}