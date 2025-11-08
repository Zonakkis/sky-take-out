package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.vo.ShoppingCartVO;

import java.util.List;

public interface ShoppingCartService {


    /**
     * 查看购物车
     *
     * @return
     */
    List<ShoppingCartVO> list();
    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 删除购物车
     *
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();
}
