package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 新增菜品
     *
     * @param dish
     */
    @Insert("insert into " +
            "dish(name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
            "values" +
            "(#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 统计菜品数量
     *
     * @param status
     * @param categoryId
     * @return
     */
    Integer count(Integer status, Long categoryId);

    /**
     * 根据分类id和状态查询菜品列表
     *
     * @param categoryId
     * @return
     */
    List<Dish> listByCategoryId(Long categoryId, Integer status);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 根据id批量查询菜品
     *
     * @param ids
     * @return
     */
    List<Dish> listByIds(List<Long> ids);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user " +
            "from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 更新菜品信息
     *
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);
}
