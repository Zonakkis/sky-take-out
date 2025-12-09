package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 插入购物车
     *
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart " +
            "(user_id, dish_id, dish_flavor, setmeal_id, name, image, amount, number, create_time) " +
            "values " +
            "(#{userId}, #{dishId}, #{dishFlavor}, #{setmealId}, #{name}, #{image}, #{amount}, #{number}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 批量插入购物车
     *
     * @param shoppingCarts
     */
    void insertBatch(List<ShoppingCart> shoppingCarts);

    /**
     * 查询购物车列表
     *
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新购物车
     *
     * @param shoppingCart
     */
    void update(ShoppingCart shoppingCart);

    /**
     * 通过id删除购物车
     *
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 通过用户id删除购物车
     *
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
