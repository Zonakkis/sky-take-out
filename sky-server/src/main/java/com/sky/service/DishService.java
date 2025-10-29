package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品，同时保存口味数据
     *
     * @param dishDTO
     */
    void createWithFalvors(DishDTO dishDTO);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO getWithFlavorsById(Long id);


    /**
     * 根据分类id查询菜品列表
     *
     * @param categoryId
     * @return
     */
    List<DishVO> listByCategoryId(Long categoryId);

    /**
     * 根据分类id和状态查询菜品列表
     *
     * @param categoryId
     * @param status
     * @return
     */
    List<DishVO> listWithFlavorsByCategoryId(Long categoryId, Integer status);

    List<DishItemVO> listBySetmealId(Long setmealId);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 设置菜品状态
     *
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 更新菜品信息
     *
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */

    void deleteByIds(List<Long> ids);
}
