package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.UrlConstant;
import com.sky.dto.*;
import com.sky.properties.WeChatProperties;
import com.sky.service.HttpService;
import com.sky.service.WeChatService;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * 微信支付工具类
 */
@Component
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private HttpService httpService;

    /**
     * 获取调用微信接口的客户端工具对象
     *
     * @return
     */
    private CloseableHttpClient getClient() {
        PrivateKey merchantPrivateKey = null;
        try {
            //merchantPrivateKey商户API私钥，如何加载商户API私钥请看常见问题
            merchantPrivateKey = PemUtil.loadPrivateKey(new FileInputStream(weChatProperties.getPrivateKeyFilePath()));
            //加载平台证书文件
            X509Certificate x509Certificate = PemUtil.loadCertificate(new FileInputStream(weChatProperties.getWeChatPayCertFilePath()));
            //wechatPayCertificates微信支付平台证书列表。你也可以使用后面章节提到的“定时更新平台证书功能”，而不需要关心平台证书的来龙去脉
            List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                    .withWechatPay(wechatPayCertificates);

            // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
            return builder.build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送post方式请求
     *
     * @param url
     * @param body
     * @return
     */
    private String post(String url, String body) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            return EntityUtils.toString(response.getEntity());
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * 发送get方式请求
     *
     * @param url
     * @return
     */
    private String get(String url) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            return EntityUtils.toString(response.getEntity());
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * jsapi下单
     *
     * @param orderNum    商户订单号
     * @param total       总金额
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    private WxPayJSApiResponse jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        WxPayJSApiRequest wxPayJSApiRequest = new WxPayJSApiRequest();
        wxPayJSApiRequest.setAppId(weChatProperties.getAppid());
        wxPayJSApiRequest.setMchId(weChatProperties.getMchid());
        wxPayJSApiRequest.setDescription(description);
        wxPayJSApiRequest.setOutTradeNo(orderNum);
        wxPayJSApiRequest.setNotifyUrl(weChatProperties.getNotifyUrl());
        WxPayJSApiRequest.WxPayAmount amount = new WxPayJSApiRequest.WxPayAmount();
        amount.setTotal(total.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.setCurrency("CNY");
        wxPayJSApiRequest.setAmount(amount);
        WxPayJSApiRequest.WxPayPayer payer = new WxPayJSApiRequest.WxPayPayer();
        payer.setOpenid(openid);
        wxPayJSApiRequest.setPayer(payer);
//        return post(UrlConstant.WX_PAY_JSAPI, body);
        return httpService.post(UrlConstant.WX_PAY_JSAPI_MOCK, wxPayJSApiRequest,WxPayJSApiResponse.class);
    }


    /**
     * 小程序支付
     *
     * @param orderNum    商户订单号
     * @param total       金额，单位 元
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    public WxPayMockDto pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
/*
        //统一下单，生成预支付交易单
        String bodyAsString = jsapi(orderNum, total, description, openid);
        //解析返回结果
        WxPayJSApiResponse response = JSON.parseObject(bodyAsString, WxPayJSApiResponse.class);

        String prepayId = response.getPrepayId();
        if (prepayId != null) {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = RandomStringUtils.randomNumeric(32);
            ArrayList<Object> list = new ArrayList<>();
            list.add(weChatProperties.getAppid());
            list.add(timeStamp);
            list.add(nonceStr);
            list.add("prepay_id=" + prepayId);
            //二次签名，调起支付需要重新签名
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : list) {
                stringBuilder.append(o).append("\n");
            }
            String signMessage = stringBuilder.toString();
            byte[] message = signMessage.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(PemUtil.loadPrivateKey(Files.newInputStream(new File(weChatProperties.getPrivateKeyFilePath()).toPath())));
            signature.update(message);
            String packageSign = Base64.getEncoder().encodeToString(signature.sign());

            //构造数据给微信小程序，用于调起微信支付
            WxPayDto wxPayDto = new WxPayDto();
            wxPayDto.setTimeStamp(timeStamp);
            wxPayDto.setNonceStr(nonceStr);
            wxPayDto.setPackageStr("prepay_id=" + prepayId);
            wxPayDto.setSignType("RSA");
            wxPayDto.setPaySign(packageSign);
            return wxPayDto;
        }
        WxPayDto wxPayDto = new WxPayDto();
        wxPayDto.setCode(response.getCode());
        return wxPayDto;
*/
        WxPayJSApiResponse response = jsapi(orderNum, total, description, openid);
        WxPayMockDto wxPayDto = new WxPayMockDto();
        wxPayDto.setPrepayId(response.getPrepayId());
        return wxPayDto;
    }

    /**
     * 申请退款
     *
     * @param outTradeNo  商户订单号
     * @param outRefundNo 商户退款单号
     * @param refund      退款金额
     * @param total       原订单金额
     * @return
     */
    @Override
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);

        JSONObject amount = new JSONObject();
        amount.put("refund", refund.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = jsonObject.toJSONString();

        //调用申请退款接口
        return post(UrlConstant.WX_PAY_REFUNDS, body);
    }

}
