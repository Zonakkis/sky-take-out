package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.OrderConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.WxPayDto;
import com.sky.dto.WxPayMockDto;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.properties.OrderProperties;
import com.sky.service.OrderService;
import com.sky.service.WeChatService;
import com.sky.vo.OrderPaymentMockVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderProperties orderProperties;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatService weChatService;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     */
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 获取地址
        if (ordersSubmitDTO.getAddressBookId() == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        // 获取购物车
        ShoppingCart shoppingCartQuery = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCartQuery.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCartQuery);
        if (shoppingCarts == null || shoppingCarts.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 创建订单
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        // 订单默认状态为待支付
        orders.setStatus(OrderConstant.Status.PENDING_PAYMENT);
        // 订单支付状态为未支付
        orders.setPayStatus(OrderConstant.PayStatus.UNPAID);
        // 生成订单号
        orders.setNumber(UUID.randomUUID().toString());
        // 设置用户信息
        orders.setUserId(userId);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orderMapper.insert(orders);

        // 创建订单明细
        BigDecimal packagingFee = new BigDecimal(0);
        BigDecimal total = new BigDecimal(0);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
            // 计算包装费和总金额
            packagingFee = packagingFee.add(orderProperties.getPackagingFeeCoefficient());
            BigDecimal amount = shoppingCart.getAmount()
                    .multiply(BigDecimal.valueOf(shoppingCart.getNumber()));
            total = total.add(amount);
        }
        total = total.add(packagingFee);
        total = total.add(orderProperties.getDeliveryFee());
        orders.setAmount(total);
        orderDetailMapper.insertBatch(orderDetails);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 返回订单信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderAmount(orders.getAmount());

        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentMockVO pay(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        try {

            WxPayMockDto wxPayDto = weChatService.pay(
                    ordersPaymentDTO.getOrderNumber(), //商户订单号
                    new BigDecimal("0.01"), //支付金额，单位 元
                    "苍穹外卖订单", //商品描述
                    user.getOpenid() //微信用户的openid
            );

//            if (wxPayDto.getCode().equals("ORDERPAID")) {
//                throw new OrderBusinessException("该订单已支付");
//            }

            OrderPaymentMockVO orderPaymentVO = new OrderPaymentMockVO();
            BeanUtils.copyProperties(wxPayDto, orderPaymentVO);

            return orderPaymentVO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new OrderBusinessException("生成预支付交易单失败");
        }
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(OrderConstant.Status.TO_BE_CONFIRMED)
                .payStatus(OrderConstant.PayStatus.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

}
