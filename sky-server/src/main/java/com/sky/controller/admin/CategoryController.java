package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.CategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api("分类管理接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<?> create(@RequestBody CategoryDTO categoryDTO) {
        categoryService.create(categoryDTO);
        return Result.success();
    }

    /**
     * 查询分类信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询分类信息")
    public Result<CategoryVO> get(@PathVariable Long id) {
        CategoryVO categoryVO = categoryService.getById(id);
        return Result.success(categoryVO);
    }

    /**
     * 查询分类列表
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询分类列表")
    public Result<List<CategoryVO>> list(Integer type){
        List<CategoryVO> list = categoryService.listByType(type);
        return Result.success(list);
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult<CategoryVO>> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult<CategoryVO> pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 设置分类状态
     *
     * @param status
     * @param id
     */
    @PostMapping("/status/{status}")
    @ApiOperation("设置分类状态")
    public Result<?> setStatus(@PathVariable Integer status, Long id) {
        categoryService.setStatus(status, id);
        return Result.success();
    }

    /**
     * 修改分类信息
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类信息")
    public Result<?> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<?> delete(Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }
}
