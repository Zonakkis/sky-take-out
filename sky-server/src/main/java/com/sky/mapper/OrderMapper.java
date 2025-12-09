package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrderPageQueryDTO;
import com.sky.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     *
     * @param order
     */
    @Insert("insert into `order` " +
            " (number, status, user_id, address_book_id, order_time, checkout_time, " +
            "pay_method, pay_status, amount, remark, phone, address, user_name, consignee, " +
            "cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, " +
            "delivery_status, delivery_time, pack_amount, tableware_number, tableware_status) " +
            "values " +
            " (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}," +
            " #{payMethod}, #{payStatus}, #{amount}, #{remark}, #{phone}, #{address}, #{userName}, #{consignee}," +
            " #{cancelReason}, #{rejectionReason}, #{cancelTime}, #{estimatedDeliveryTime}, " +
            "#{deliveryStatus}, #{deliveryTime}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Order order);

    /**
     * 根据订单状态查询订单数量
     *
     * @param status
     * @return
     */
    @Select("select count(*) from `order` where status = #{status}")
    Integer countByStatus(Integer status);


    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, " +
            "pay_method, pay_status, amount, remark, phone, address, user_name, consignee, " +
            "cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, " +
            "delivery_status, delivery_time, pack_amount, tableware_number, tableware_status " +
            "from `order` where id = #{id}")
    Order getById(Long id);

    /**
     * 根据订单号查询订单
     *
     * @param number
     * @return
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, " +
            "pay_method, pay_status, amount, remark, phone, address, user_name, consignee, " +
            "cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, " +
            "delivery_status, delivery_time, pack_amount, tableware_number, tableware_status " +
            "from `order` where number = #{number}")
    Order getByNumber(String number);

    /**
     * 根据订单状态查询订单列表
     *
     * @param status
     * @return
     */
    @Select("select id, number, status, user_id, address_book_id, order_time, checkout_time, " +
            "pay_method, pay_status, amount, remark, phone, address, user_name, consignee, " +
            "cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, " +
            "delivery_status, delivery_time, pack_amount, tableware_number, tableware_status " +
            "from `order` where status = #{status}")
    List<Order> listByStatus(Integer status);

    /**
     * 订单分页查询
     *
     * @param orderPageQueryDTO
     * @return
     */
    Page<Order> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 更新订单
     *
     * @param order
     */
    void update(Order order);
}
