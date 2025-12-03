package com.sky.controller.admin;

import com.sky.dto.OrderCancelDTO;
import com.sky.dto.OrderConfirmDTO;
import com.sky.dto.OrderPageQueryDTO;
import com.sky.dto.OrderRejectDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api("订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult<OrderVO>> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("订单分页查询：{}", orderPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.pageQueryWithDetailString(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderDetailVO> detail(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        OrderDetailVO orderDetailVO = orderService.detail(id);
        return Result.success(orderDetailVO);
    }

    /**
     * 各状态订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各状态订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各状态订单数量统计");
        OrderStatisticsVO statisticsVO = orderService.statistics();
        return Result.success(statisticsVO);
    }

    /**
     * 接单
     * @param orderConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<?> confirm(@RequestBody OrderConfirmDTO orderConfirmDTO) {
        log.info("接单：{}", orderConfirmDTO);
        orderService.confirm(orderConfirmDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<?> delivery(@PathVariable Long id){
        log.info("派送订单：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<?> complete(@PathVariable Long id){
        log.info("完成订单：{}", id);
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 拒单
     * @param orderRejectDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<?> reject(@RequestBody OrderRejectDTO orderRejectDTO) {
        log.info("拒单：{}", orderRejectDTO);
        orderService.reject(orderRejectDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param orderCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<?> cancel(@RequestBody OrderCancelDTO orderCancelDTO) {
        log.info("商家取消订单：{}", orderCancelDTO);
        orderService.cancelByShop(orderCancelDTO);
        return Result.success();
    }
}
