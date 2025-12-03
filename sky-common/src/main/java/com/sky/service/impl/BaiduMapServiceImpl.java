package com.sky.service.impl;

import com.sky.constant.UrlConstant;
import com.sky.dto.baidu.map.BaiduMapLocation;
import com.sky.dto.baidu.map.BaiduMapDirectionLiteDTO;
import com.sky.dto.baidu.map.BaiduMapGeoCodingDTO;
import com.sky.properties.BaiduMapProperties;
import com.sky.service.BaiduMapService;
import com.sky.service.HttpService;
import com.sky.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BaiduMapServiceImpl implements BaiduMapService {
    @Autowired
    private BaiduMapProperties baiduMapProperties;
    @Autowired
    private HttpService httpService;


    /**
     * 地理编码
     * @param address
     * @return
     * @throws Exception
     */
    public BaiduMapGeoCodingDTO geoCoding(String address) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("address", address);
        params.put("output", "json");
        params.put("ak", baiduMapProperties.getAk());
        String sn = getSn(UrlConstant.BAIDU_MAP_GEOCODING, params);
        params.put("sn", sn);
        return httpService.get(
                UrlConstant.BAIDU_MAP_GEOCODING, params, BaiduMapGeoCodingDTO.class);
    }

    /**
     * 路线规划
     * @param origin
     * @param destination
     * @return
     * @throws Exception
     */
    public BaiduMapDirectionLiteDTO directionLite(BaiduMapLocation origin, BaiduMapLocation destination) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("origin", origin.toString());
        params.put("destination", destination.toString());
        params.put("output", "json");
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("ak", baiduMapProperties.getAk());
        String sn = getSn(UrlConstant.BAIDU_MAP_DIRECTION_LITE, params);
        params.put("sn", sn);
        return httpService.get(
                UrlConstant.BAIDU_MAP_DIRECTION_LITE, params, BaiduMapDirectionLiteDTO.class);
    }


    private String getSn(String url, Map<String, String> params) throws Exception {

        // 计算sn跟参数对出现顺序有关，get请求请使用LinkedHashMap保存<key,value>，该方法根据key的插入顺序排序；
        // post请使用TreeMap保存<key,value>，该方法会自动将key按照字母a-z顺序排序。
        // 所以get请求可自定义参数顺序（sn参数必须在最后）发送请求，但是post请求必须按照字母a-z顺序填充body（sn参数必须在最后）。
        // 以get请求为例：http://api.map.baidu.com/geocoder/v2/?address=百度大厦&output=json&ak=ak，
        // paramsMap中先放入address，再放output，然后放ak，放入顺序必须跟get请求中对应参数的出现顺序保持一致。

        // 对paramsStr前面拼接上/geocoder/v2/?
        // 后面直接拼接sk得到/geocoder/v2/?address=%E7%99%BE%E5%BA%A6%E5%A4%A7%E5%8E%A6&output=json&ak=aksk
        String path = url.replaceAll("https?://api.map.baidu.com","");
        String queryString = UrlUtils.paramsMapToQueryString(params);
        String wholeStr = path + "?" + queryString + baiduMapProperties.getSk();

        // 对上面wholeStr再作utf8编码
        String tempStr = URLEncoder.encode(wholeStr, StandardCharsets.UTF_8.name());

        // 调用下面的MD5方法得到最后的sn签名7de5a22212ffaa9e326444c75a58f9a0
        return MD5(tempStr);
    }

    // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
    private String MD5(String md5) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(md5.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }
}
