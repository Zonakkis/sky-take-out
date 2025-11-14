package com.sky.constant;

public class UrlConstant {
    //微信登录接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    //微信支付下单接口地址
    public static final String WX_PAY_JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    //微信支付下单接口地址-模拟环境
    public static final String WX_PAY_JSAPI_MOCK = "http://localhost:8080/wx/pay/transactions/jsapi";
    //申请退款接口地址
    public static final String WX_PAY_REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
}
