package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入套餐菜品关联信息
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id获取套餐菜品关联信息
     * @param id
     */
    @Select("select id, setmeal_id, dish_id, name, price, copies " +
            "from setmeal_dish " +
            "where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    Integer getCountByDishIds(List<Long> ids);

    /**
     * 根据套餐id删除套餐菜品关联信息
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id批量删除套餐菜品关联信息
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);
}
