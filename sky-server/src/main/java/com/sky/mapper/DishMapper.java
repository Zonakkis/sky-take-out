package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

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
     * 根据分类id查询菜品列表
     *
     * @param categoryId
     * @return
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user " +
            "from dish " +
            "where category_id = #{categoryId}")
    List<Dish> listByCategoryId(Integer categoryId);

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
    List<Dish> getByIds(List<Long> ids);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user " +
            "from dish where id = #{id}")
    Dish getById(Integer id);

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
