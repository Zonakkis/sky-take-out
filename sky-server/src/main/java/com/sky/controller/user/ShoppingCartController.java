package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import com.sky.vo.ShoppingCartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("C端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCartVO>> list() {
        List<ShoppingCartVO> shoppingCartVOS = shoppingCartService.list();
        return Result.success(shoppingCartVOS);
    }

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result<?> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 删除购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation("删除购物车")
    public Result<?> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车：{}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<?> clean() {
        shoppingCartService.clean();
        return Result.success();
    }
}
