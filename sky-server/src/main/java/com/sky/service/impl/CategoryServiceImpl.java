package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.NotFoundException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    public void create(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        category.setStatus(StatusConstant.DISABLE); // 默认禁用状态

        categoryMapper.insert(category);
    }

    /**
     * 根据id查询分类
     *
     * @param id
     * @return
     */
    public CategoryVO getById(Long id) {
        Category category = categoryMapper.getById(id);
        if (category == null) {
            throw new NotFoundException(MessageConstant.CATEGORY_NOT_FOUND);
        }
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 根据类型查询分类列表
     *
     * @param type
     * @return
     */
    public List<CategoryVO> list(Integer type) {
        List<Category> categories = categoryMapper.list(type);
        List<CategoryVO> list = categories
                .stream().map(category -> {
                    CategoryVO categoryVO = new CategoryVO();
                    BeanUtils.copyProperties(category, categoryVO);
                    return categoryVO;
                }).collect(Collectors.toList());
        return list;
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult<CategoryVO> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        int pageNum = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<CategoryVO> records = page.getResult()
                .stream().map(category -> {
                    CategoryVO categoryVO = new CategoryVO();
                    BeanUtils.copyProperties(category, categoryVO);
                    return categoryVO;
                }).collect(Collectors.toList());

        return new PageResult<>(total, records);
    }

    /**
     * 设置分类状态
     *
     * @param status
     * @param id
     */
    public void setStatus(Integer status, Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);

        categoryMapper.update(category);
    }

    /**
     * 修改分类信息
     *
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);
    }

    /**
     * 删除分类
     *
     * @param id
     */
    public void deleteById(Long id) {
        Integer count = dishMapper.count(null, id);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        categoryMapper.deleteById(id);
    }
}
