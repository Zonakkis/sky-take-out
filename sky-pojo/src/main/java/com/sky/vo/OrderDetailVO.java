package com.sky.vo;

import com.sky.entity.OrderDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDetailVO extends OrderVO {

    //订单详情
    private List<OrderDetail> orderDetailList;
}
