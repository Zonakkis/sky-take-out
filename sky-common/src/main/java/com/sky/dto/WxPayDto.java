package com.sky.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WxPayDto {
    private String code;
    private String timeStamp;
    private String nonceStr;
    @JSONField(name = "package")
    private String packageStr;
    private String signType;
    private String paySign;
}
