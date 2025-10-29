package com.sky.dto;

import lombok.Data;

@Data
public class WXLoginDTO {
    private String sessionKey;
    private String unionId;
    private String errMsg;
    private String openId;
    private int errCode;
}
