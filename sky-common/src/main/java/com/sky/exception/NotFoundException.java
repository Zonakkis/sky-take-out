package com.sky.exception;

/**
 * 查询不存在异常
 */
public class NotFoundException extends BaseException {

    public NotFoundException() {
    }

    public NotFoundException(String msg) {
        super(msg);
    }

}
