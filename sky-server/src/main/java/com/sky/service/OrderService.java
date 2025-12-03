package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {

    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    PageResult<OrderVO> pageQueryWithDetailString(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    PageResult<OrderDetailVO> pageQueryWithDetails(OrderPageQueryDTO orderPageQueryDTO);


    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderDetailVO detail(Long id);


    /**
     * 各状态订单数量统计
     *
     * @return
     */
    OrderStatisticsVO statistics();


    /**
     * 用户下单
     *
     * @param orderSubmitDTO
     */
    OrderSubmitVO submit(OrderSubmitDTO orderSubmitDTO);


    /**
     * 再来一单
     *
     * @param id
     */
    void reorder(Long id);


    /**
     * 接单
     *
     * @param orderConfirmDTO
     */
    void confirm(OrderConfirmDTO orderConfirmDTO);


    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);


    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);


    /**
     * 拒单
     *
     * @param orderRejectDTO
     */
    void reject(OrderRejectDTO orderRejectDTO);

    /**
     * 商家取消订单
     *
     * @param orderCancelDTO
     */
    void cancelByShop(OrderCancelDTO orderCancelDTO);

    /**
     * 用户取消订单
     *
     * @param id
     */
    void cancelByUser(Long id);


    /**
     * 订单支付
     *
     * @param orderPaymentDTO
     * @return
     */
    OrderPaymentMockVO pay(OrderPaymentDTO orderPaymentDTO);

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
}
