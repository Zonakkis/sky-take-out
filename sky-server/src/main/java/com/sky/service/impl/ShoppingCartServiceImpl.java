package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.ShoppingCartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 查看购物车
     *
     * @return
     */
    public List<ShoppingCartVO> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        List<ShoppingCartVO> shoppingCartVOS = shoppingCarts.stream().map(cart -> {
            ShoppingCartVO shoppingCartVO = new ShoppingCartVO();
            BeanUtils.copyProperties(cart, shoppingCartVO);
            return shoppingCartVO;
        }).collect(Collectors.toList());
        return shoppingCartVOS;
    }


    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 若购物车已存在该菜品或套餐，则数量加一
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            ShoppingCart cart = shoppingCarts.get(0);
            shoppingCart = new ShoppingCart();
            shoppingCart.setId(cart.getId());
            shoppingCart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
            return;
        }

        // 若该条购物车在数据库中不存在且为菜品，则新增该菜品到购物车，数量默认为1
        if (shoppingCart.getDishId() != null) {
            Dish dish = dishMapper.getById(shoppingCart.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            return;
        }

        // 若该条购物车在数据库中不存在且为套餐，则新增该套餐到购物车，数量默认为1
        if (shoppingCart.getSetmealId() != null) {
            Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 删除购物车
     *
     * @param shoppingCartDTO
     */
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 查询购物车中该菜品或套餐
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            ShoppingCart cart = shoppingCarts.get(0);
            if (cart.getNumber() > 1) {
                // 多于1个时，数量减一
                shoppingCart = new ShoppingCart();
                shoppingCart.setId(cart.getId());
                shoppingCart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.update(shoppingCart);
            } else {
                // 数量为1时，删除该条购物车
                shoppingCartMapper.deleteById(cart.getId());
            }
        }
    }

    /**
     * 清空购物车
     */
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
