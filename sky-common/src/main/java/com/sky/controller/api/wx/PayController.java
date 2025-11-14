package com.sky.controller.api.wx;

import com.sky.dto.WxPayDto;
import com.sky.dto.WxPayJSApiRequest;
import com.sky.dto.WxPayJSApiResponse;
import com.sky.dto.WxPayMockDto;
import com.sky.service.WxPayService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/wx/pay/transactions")
@Api("模拟微信接口")
public class PayController {

    @Autowired
    private WxPayService wxPayService;

    @PostMapping("/jsapi")
    public WxPayJSApiResponse jsapi(@RequestBody WxPayJSApiRequest wxPayJSApiRequest) {
        return wxPayService.prePay(wxPayJSApiRequest);
    }

    @PostMapping("/payment")
    public void payment(@RequestBody WxPayMockDto wxPayDto) throws IOException {
        wxPayService.payment(wxPayDto);
    }
}
