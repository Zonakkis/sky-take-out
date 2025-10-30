package com.sky.service.impl;

import com.sky.constant.RedisKeyConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 获取营业状态
     *
     * @return
     */
    public Integer getStatus() {
        return (Integer) redisTemplate.opsForValue().get(RedisKeyConstant.SHOP_STATUS);
    }

    /**
     * 修改营业状态
     *
     * @param status
     */
    public void setStatus(Long status) {
        redisTemplate.opsForValue().set(RedisKeyConstant.SHOP_STATUS, status);
    }
}
