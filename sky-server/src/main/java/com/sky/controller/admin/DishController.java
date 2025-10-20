package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品管理接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<?> create(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.createWithFalvors(dishDTO);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询菜品信息")
    public Result<DishVO> getById(@PathVariable Integer id) {
        log.info("查询菜品信息：{}", id);
        DishVO dishVO = dishService.getWithFlavorsById(id);
        return Result.success(dishVO);
    }

    /**
     * 查询菜品列表
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询菜品列表")
    public Result<List<DishVO>> list(Integer categoryId) {
        log.info("查询菜品列表：{}", categoryId);
        List<DishVO> dishVOS = dishService.listByCategoryId(categoryId);
        return Result.success(dishVOS);
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult<DishVO>> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult<DishVO> pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 设置菜品状态
     *
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("设置菜品状态")
    public Result<?> setStatus(Long id, @PathVariable Integer status) {
        dishService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 更新菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新菜品")
    public Result<?> update(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<?> delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }
}
