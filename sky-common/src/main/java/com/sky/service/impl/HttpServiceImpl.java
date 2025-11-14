package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class HttpServiceImpl implements HttpService {

    @Autowired
    private HttpClient httpClient;

    private static final int TIMEOUT_MSEC = 5 * 1000;

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .setSocketTimeout(TIMEOUT_MSEC)
                .build();
    }


    public <T> T get(String url, Map<String, ?> params, Class<T> responseType) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                for (Map.Entry<String, ?> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(requestConfig());
            HttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (responseType == String.class) {
                return responseType.cast(body);
            }
            return JSON.parseObject(body, responseType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("HTTP GET request failed: " + url, e);
        }
    }


    public <T> T post(String url, Object body, Class<T> responseType) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig());
        try {
            if (body != null) {
                String json = JSON.toJSONString(body);
                StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
                entity.setContentType("application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            String resp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (responseType == String.class) {
                return responseType.cast(resp);
            }
            return JSON.parseObject(resp, responseType);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("HTTP POST request failed: " + url, e);
        }
    }
}
