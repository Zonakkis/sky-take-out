package com.sky.controller.user;

import com.sky.dto.OrderPageQueryDTO;
import com.sky.dto.OrderPaymentDTO;
import com.sky.dto.OrderSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderPaymentMockVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api("C端-订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询历史订单
     *
     * @param orderPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult<OrderDetailVO>> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("查询历史订单：{}", orderPageQueryDTO);
        PageResult<OrderDetailVO> pageResult = orderService.pageQueryWithDetails(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderDetailVO> detail(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        OrderDetailVO orderDetailVO = orderService.detail(id);
        return Result.success(orderDetailVO);
    }

    /**
     * 用户下单
     *
     * @param orderSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info("用户下单：{}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<?> reorder(@PathVariable Long id){
        log.info("再来一单：{}", id);
        orderService.reorder(id);
        return Result.success();
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result<?> reminder(@PathVariable Long id){
        log.info("用户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }


    /**
     * 订单支付
     *
     * @param orderPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentMockVO> payment(@RequestBody OrderPaymentDTO orderPaymentDTO){
        log.info("订单支付：{}", orderPaymentDTO);
        OrderPaymentMockVO orderPaymentVO = orderService.pay(orderPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<?> cancel(@PathVariable Long id){
        log.info("用户取消订单：{}", id);
        orderService.cancelByUser(id);
        return Result.success();
    }
}
