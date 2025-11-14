package com.sky.service;

import com.sky.dto.WxPayJSApiRequest;
import com.sky.dto.WxPayJSApiResponse;
import com.sky.dto.WxPayMockDto;

import java.io.IOException;

public interface WxPayService {
    WxPayJSApiResponse prePay(WxPayJSApiRequest request);

    void payment(WxPayMockDto wxPayDto) throws IOException;
}
