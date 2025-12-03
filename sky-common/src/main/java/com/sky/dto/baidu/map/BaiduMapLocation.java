package com.sky.dto.baidu.map;

import lombok.Data;

@Data
public class BaiduMapLocation {
    /** 经度 */
    private Double lng;

    /** 纬度 */
    private Double lat;

    @Override
    public String toString() {
        return String.format("%.6f,%.6f", lat, lng);
    }
}