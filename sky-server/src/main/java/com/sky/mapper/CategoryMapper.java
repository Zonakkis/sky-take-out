package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 插入分类
     *
     * @param category
     */
    @Insert("insert into " +
            "category(name, type, sort, create_time, update_time, create_user, update_user) " +
            "values" +
            "(#{name}, #{type}, #{sort}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    /**
     * 根据id查询分类
     *
     * @param id
     * @return
     */
    @Select("select * from category where id = #{id}")
    Category getById(Long id);


    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 更新分类
     *
     * @param category
     */
    void update(Category category);

    /**
     * 根据id删除分类
     *
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据类型查询分类列表
     *
     * @param type
     * @return
     */
    @Select("select * from category where type = #{type} order by sort")
    List<Category> listByType(Integer type);
}
