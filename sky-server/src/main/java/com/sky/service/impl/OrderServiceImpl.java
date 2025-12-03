package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.OrderConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.dto.baidu.map.BaiduMapDirectionLiteDTO;
import com.sky.dto.baidu.map.BaiduMapGeoCodingDTO;
import com.sky.dto.baidu.map.BaiduMapLocation;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.properties.OrderProperties;
import com.sky.properties.ShopProperties;
import com.sky.result.PageResult;
import com.sky.service.BaiduMapService;
import com.sky.service.OrderService;
import com.sky.service.WeChatService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderProperties orderProperties;
    @Autowired
    private ShopProperties shopProperties;
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
    @Autowired
    private BaiduMapService baiduMapService;

    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    public PageResult<OrderVO> pageQueryWithDetailString(OrderPageQueryDTO orderPageQueryDTO) {
        int pageNum = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<Order> page = orderMapper.pageQuery(orderPageQueryDTO);
        List<Order> orders = page.getResult();
        long total = page.getTotal();
        if (orders.isEmpty()) {
            return new PageResult<>(total, new ArrayList<>());
        }

        // 获取订单对应的菜品
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderIds(orderIds);
        Map<Long, List<OrderDetail>> orderDetailMap = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        // 封装OrderVO
        List<OrderVO> records = orders.stream().map(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            List<OrderDetail> details = orderDetailMap.get(order.getId());
            String orderDishes = orderDetailsToString(details);
            orderVO.setOrderDishes(orderDishes);
            return orderVO;
        }).collect(Collectors.toList());
        return new PageResult<>(total, records);
    }

    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    public PageResult<OrderDetailVO> pageQueryWithDetails(OrderPageQueryDTO orderPageQueryDTO) {
        int pageNum = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<Order> page = orderMapper.pageQuery(orderPageQueryDTO);
        List<Order> orders = page.getResult();
        long total = page.getTotal();
        if (orders.isEmpty()) {
            return new PageResult<>(total, new ArrayList<>());
        }

        // 获取订单对应的菜品
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderIds(orderIds);
        Map<Long, List<OrderDetail>> orderDetailMap = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        // 封装OrderDetailVO
        List<OrderDetailVO> records = orders.stream().map(order -> {
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            BeanUtils.copyProperties(order, orderDetailVO);
            List<OrderDetail> details = orderDetailMap.get(order.getId());
            orderDetailVO.setOrderDetailList(details);
            return orderDetailVO;
        }).collect(Collectors.toList());
        return new PageResult<>(total, records);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    public OrderDetailVO detail(Long id) {
        Order order = orderMapper.getById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        // 封装OrderDetailVO
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        String orderDishes = orderDetailsToString(orderDetails);
        orderDetailVO.setOrderDishes(orderDishes);
        orderDetailVO.setOrderDetailList(orderDetails);
        return orderDetailVO;
    }

    /**
     * 各状态订单数量统计
     *
     * @return
     */
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmedCount = orderMapper.countByStatus(OrderConstant.Status.TO_BE_CONFIRMED);
        Integer confirmedCount = orderMapper.countByStatus(OrderConstant.Status.CONFIRMED);
        Integer deliveryInProgressCount = orderMapper.countByStatus(OrderConstant.Status.DELIVERY_IN_PROGRESS);

        // 封装OrderStatisticsVO
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedCount);
        orderStatisticsVO.setConfirmed(confirmedCount);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressCount);
        return orderStatisticsVO;
    }


    /**
     * 用户下单
     *
     * @param orderSubmitDTO
     */
    @Transactional
    public OrderSubmitVO submit(OrderSubmitDTO orderSubmitDTO) {
        // 获取地址
        if (orderSubmitDTO.getAddressBookId() == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        AddressBook addressBook = addressBookMapper.getById(orderSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        // 判断下单地址是否在配送范围内（5千米）
        try {
            BaiduMapGeoCodingDTO userGeoCodingDTO = baiduMapService.geoCoding(addressBook.getDetail());
            BaiduMapGeoCodingDTO shopGeoCodingDTO = baiduMapService.geoCoding(shopProperties.getAddress());
            BaiduMapLocation userLocation = userGeoCodingDTO.getResult().getLocation();
            BaiduMapLocation shopLocation = shopGeoCodingDTO.getResult().getLocation();
            BaiduMapDirectionLiteDTO directionLiteDTO =
                    baiduMapService.directionLite(userLocation, shopLocation);
            Integer distance = directionLiteDTO.getResult().getRoutes()[0].getDistance();
            if(distance > shopProperties.getDeliveryRange()){
                throw new OrderBusinessException(MessageConstant.OUT_OF_DELIVERY_RANGE);
            }
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e){
            log.error("检查配送范围失败：{}", e.getMessage());
            throw new OrderBusinessException(MessageConstant.SERVER_ERROR);
        }

        // 获取购物车
        ShoppingCart shoppingCartQuery = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCartQuery.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCartQuery);
        if (shoppingCarts == null || shoppingCarts.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(orderSubmitDTO, order);
        // 设置订单时间
        order.setOrderTime(LocalDateTime.now());
        // 订单默认状态为待支付
        order.setStatus(OrderConstant.Status.PENDING_PAYMENT);
        // 订单支付状态为未支付
        order.setPayStatus(OrderConstant.PayStatus.UNPAID);
        // 生成订单号
        order.setNumber(UUID.randomUUID().toString());
        // 设置用户信息
        order.setUserId(userId);
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        orderMapper.insert(order);

        // 创建订单明细
        BigDecimal packagingFee = new BigDecimal(0);
        BigDecimal total = new BigDecimal(0);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
            // 计算包装费和总金额
            packagingFee = packagingFee.add(orderProperties.getPackagingFeeCoefficient());
            BigDecimal amount = shoppingCart.getAmount()
                    .multiply(BigDecimal.valueOf(shoppingCart.getNumber()));
            total = total.add(amount);
        }
        total = total.add(packagingFee);
        total = total.add(orderProperties.getDeliveryFee());
        order.setAmount(total);
        orderDetailMapper.insertBatch(orderDetails);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 返回订单信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(order.getId());
        orderSubmitVO.setOrderNumber(order.getNumber());
        orderSubmitVO.setOrderTime(order.getOrderTime());
        orderSubmitVO.setOrderAmount(order.getAmount());

        return orderSubmitVO;
    }

    /**
     * 再来一单
     *
     * @param id 订单ID
     */
    public void reorder(Long id) {
        Long userId = BaseContext.getCurrentId();
        Order order = orderMapper.getById(id);
        if (!order.getUserId().equals(userId)) {
            throw new OrderBusinessException(MessageConstant.USER_NOT_MATCH);
        }
        order.setId(null);
        // 设置订单时间
        order.setOrderTime(LocalDateTime.now());
        // 订单默认状态为待支付
        order.setStatus(OrderConstant.Status.PENDING_PAYMENT);
        // 订单支付状态为未支付
        order.setPayStatus(OrderConstant.PayStatus.UNPAID);
        // 生成订单号
        order.setNumber(UUID.randomUUID().toString());
        orderMapper.insert(order);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id)
                .stream().peek(detail -> {
                    detail.setId(null);
                    detail.setOrderId(order.getId());
                }).collect(Collectors.toList());
        orderDetailMapper.insertBatch(orderDetails);
    }


    /**
     * 接单
     *
     * @param orderConfirmDTO
     */
    public void confirm(OrderConfirmDTO orderConfirmDTO) {
        Order order = new Order();
        order.setId(orderConfirmDTO.getId());
        order.setStatus(OrderConstant.Status.CONFIRMED);
        orderMapper.update(order);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    public void delivery(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderConstant.Status.DELIVERY_IN_PROGRESS);
        orderMapper.update(order);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderConstant.Status.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 拒单
     *
     * @param orderRejectDTO
     */
    public void reject(OrderRejectDTO orderRejectDTO) {
        Order order = new Order();
        order.setId(orderRejectDTO.getId());
        order.setStatus(OrderConstant.Status.CANCELLED);
        order.setCancelReason(orderRejectDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 商家取消订单
     *
     * @param orderCancelDTO
     */
    public void cancelByShop(OrderCancelDTO orderCancelDTO) {
        Order order = new Order();
        order.setId(orderCancelDTO.getId());
        order.setStatus(OrderConstant.Status.CANCELLED);
        order.setCancelReason(orderCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    public void cancelByUser(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderConstant.Status.CANCELLED);
        order.setCancelReason("用户取消订单");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }


    /**
     * 订单支付
     *
     * @param orderPaymentDTO
     * @return
     */
    public OrderPaymentMockVO pay(OrderPaymentDTO orderPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        try {

            WxPayMockDto wxPayDto = weChatService.pay(
                    orderPaymentDTO.getOrderNumber(), //商户订单号
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
        Order orderDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Order order = Order.builder()
                .id(orderDB.getId())
                .status(OrderConstant.Status.TO_BE_CONFIRMED)
                .payStatus(OrderConstant.PayStatus.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(order);
    }

    /**
     * 订单包含的菜品和数量
     *
     * @param orderDetails
     * @return
     */
    private String orderDetailsToString(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(detail -> detail.getName() + "*" + detail.getNumber())
                .collect(Collectors.joining(","));
    }
}
