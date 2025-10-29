package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 插入套餐
     *
     * @param setmeal
     */
    @Insert("insert into " +
            "setmeal(name, category_id, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "values" +
            "(#{name}, #{categoryId}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user " +
            "from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据分类id和状态查询套餐列表
     *
     * @param categoryId
     * @param status
     * @return
     */
    List<Setmeal> listByCategoryId(Long categoryId, Integer status);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 更新套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    void deleteByIds(List<Long> ids);
}
