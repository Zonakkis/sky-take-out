package com.sky.dto;

import lombok.Data;

@Data
public class WxPayJSApiResponse {

    // 成功的返回结果
    private String prepayId;

    // 失败的返回结果
    private String code;
    private Detail detail;
    private String message;

    @Data
    public static class Detail {
        private String location;
        private String value;
    }
}
