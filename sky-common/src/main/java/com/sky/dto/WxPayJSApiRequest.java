package com.sky.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WxPayJSApiRequest {
//    @JSONField(name = "appid")
    private String appId;
//    @JSONField(name = "mchid")
    private String mchId;
    private String description;
//    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    private WxPayAmount amount;
    private WxPayPayer payer;
//    @JSONField(name = "notify_url")
    private String notifyUrl;

    @Data
    public static class WxPayAmount {
        /**
         * 总金额，单位：分
         */
        private Integer total;

        /**
         * 货币类型，默认人民币：CNY
         */
        private String currency = "CNY";
    }

    @Data
    public static class WxPayPayer {
        /**
         * 用户在商户appid下的唯一标识
         */
        private String openid;
    }

}
