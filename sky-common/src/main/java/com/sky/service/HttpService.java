package com.sky.service;

import java.util.Map;

public interface HttpService {

    <T> T get(String url, Map<String, ?> params, Class<T> responseType);

    <T> T post(String url, Object body, Class<T> responseType);
}
