package com.sky.service;

public interface ShopService {

    /**
     * 获取营业状态
     *
     * @return
     */
    Integer getStatus();


    /**
     * 修改营业状态
     *
     * @param status
     */
    void setStatus(Long status);
}
