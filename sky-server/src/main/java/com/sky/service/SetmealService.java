package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    void createWithSetmealDishes(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐信息
     *
     * @param id
     * @return
     */
    SetmealVO getWithSetmealDishesById(Long id);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 设置套餐状态
     *
     * @param id
     * @param status
     */
    void setStatus(Long id, Integer status);

    /**
     * 修改套餐信息
     *
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    void deleteByIds(List<Long> ids);
}
