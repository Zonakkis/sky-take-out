package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisKeyConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品，同时保存对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    public void createWithFalvors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 菜品默认为禁用状态
        dish.setStatus(StatusConstant.DISABLE);

        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }

        // 清除分类下的菜品缓存
        String key = RedisKeyConstant.DISHES_CATEGORY + dish.getCategoryId();
        clearCache(key);

    }


    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    public DishVO getWithFlavorsById(Long id) {
        Dish dish = dishMapper.getById(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> flavors = dishFlavorMapper.listByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 根据分类id查询菜品列表
     *
     * @param categoryId
     * @return
     */
    public List<DishVO> listByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.listByCategoryId(categoryId, null);

        return dishes.stream().map(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            return dishVO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据分类id查询菜品及其口味列表
     *
     * @param categoryId
     * @param status
     * @return
     */
    public List<DishVO> listWithFlavorsByCategoryId(Long categoryId, Integer status) {
        String key = RedisKeyConstant.DISHES_CATEGORY + categoryId;
        List<DishVO> dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishVOS != null) {
            return dishVOS;
        }

        List<Dish> dishes = dishMapper.listByCategoryId(categoryId, StatusConstant.ENABLE);

        dishVOS = dishes.stream().map(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            List<DishFlavor> flavors = dishFlavorMapper.listByDishId(dish.getId());
            dishVO.setFlavors(flavors);
            return dishVO;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, dishVOS);
        return dishVOS;
    }


    public List<DishItemVO> listBySetmealId(Long setmealId) {
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmealId);
        Map<Long, SetmealDish> setmealDishMap = setmealDishes.stream()
                .collect(Collectors.toMap(SetmealDish::getDishId, sd -> sd));
        List<Long> dishIds = setmealDishes.stream()
                .map(SetmealDish::getDishId)
                .collect(Collectors.toList());
        List<Dish> dishes = dishMapper.listByIds(dishIds);
        return dishes.stream().map(dish -> {
            SetmealDish setmealDish = setmealDishMap.get(dish.getId());
            return DishItemVO.builder()
                    .name(setmealDish.getName())
                    .copies(setmealDish.getCopies())
                    .image(dish.getImage())
                    .description(dish.getDescription())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        int pageNum = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();

        // 进行分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        List<Dish> dishes = page.getResult();

        // 获取菜品对应的分类信息
        List<Long> categoryIds = dishes.stream()
                .map(Dish::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        List<Category> categories = categoryMapper.getByIds(categoryIds);
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        long total = page.getTotal();
        // 封装DishVO
        List<DishVO> records = dishes.stream().map(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            Category category = categoryMap.get(dish.getCategoryId());
            if (category != null) {
                dishVO.setCategoryName(category.getName());
            }
            return dishVO;
        }).collect(Collectors.toList());
        return new PageResult<>(total, records);
    }

    /**
     * 设置菜品状态
     *
     * @param id
     * @param status
     */
    public void setStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }


    /**
     * 更新菜品信息
     *
     * @param dishDTO
     */
    @Transactional
    public void update(DishDTO dishDTO) {
        // 更新菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 更新菜品口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishFlavorMapper.deleteByDishId(dish.getId());
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }

        // 清除分类下的菜品缓存
        String key = RedisKeyConstant.DISHES_CATEGORY + "*";
        clearCache(key);
    }

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 检查菜品状态，确保没有在售的菜品被删除
        List<Dish> dishes = dishMapper.listByIds(ids);
        for (Dish dish : dishes) {
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 检查菜品是否关联了套餐
        if (setmealDishMapper.getCountByDishIds(ids) > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品关联的口味
        dishFlavorMapper.deleteByDishIds(ids);

        // 删除菜品
        dishMapper.deleteByIds(ids);

        // 清除分类菜品缓存
        String key = RedisKeyConstant.DISHES_CATEGORY + "*";
        clearCache(key);
    }

    private void clearCache(String key) {
        Set<String> keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }
}
