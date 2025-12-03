package com.sky.service;

import com.sky.dto.baidu.map.BaiduMapLocation;
import com.sky.dto.baidu.map.BaiduMapDirectionLiteDTO;
import com.sky.dto.baidu.map.BaiduMapGeoCodingDTO;

public interface BaiduMapService {

    /**
     * 地理编码
     *
     * @param address
     * @return
     * @throws Exception
     */
    BaiduMapGeoCodingDTO geoCoding(String address) throws Exception;

    /**
     * 路线规划
     *
     * @param origin
     * @param destination
     * @return
     * @throws Exception
     */
    BaiduMapDirectionLiteDTO directionLite(BaiduMapLocation origin, BaiduMapLocation destination) throws Exception;

}
