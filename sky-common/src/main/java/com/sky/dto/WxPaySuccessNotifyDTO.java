package com.sky.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WxPaySuccessNotifyDTO {
    private String id;
    @JSONField(name = "create_time")
    private String createTime;
    @JSONField(name = "resource_type")
    private String resourceType;
    @JSONField(name = "event_type")
    private String eventType;
    private String summary;
    private Resource resource;

    @Data
    public static class Resource {
        @JSONField(name = "original_type")
        private String originalType;
        private String algorithm;
        private String ciphertext;
        @JSONField(name = "associated_data")
        private String associatedData;
        private String nonce;
    }
}
