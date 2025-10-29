package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    void create(CategoryDTO categoryDTO);

    /**
     * 根据id查询分类
     *
     * @param id
     * @return
     */
    CategoryVO getById(Long id);


    /**
     * 根据类型查询分类列表
     *
     * @param type
     * @return
     */
    List<CategoryVO> list(Integer type);

    /**
     * 分类分页查询
     *
     * @param pageQueryDTO
     * @return
     */
    PageResult<CategoryVO> pageQuery(CategoryPageQueryDTO pageQueryDTO);

    /**
     * 设置分类状态
     *
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 修改分类信息
     *
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     *
     * @param id
     */
    void deleteById(Long id);
}
