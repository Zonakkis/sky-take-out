package com.sky.dto;

import lombok.Data;

@Data
public class OrderMessageDTO {
    private int type;
    private long orderId;
    private String content;
}
