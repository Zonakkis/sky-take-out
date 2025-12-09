package com.sky.dto;

import lombok.Data;

@Data
public class OrderMessageDTO {
    private int type;
    private String orderId;
    private String content;
}
