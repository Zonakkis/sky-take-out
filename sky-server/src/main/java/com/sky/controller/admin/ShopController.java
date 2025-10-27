package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api("商铺管理接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取商铺状态
     *
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取商铺状态")
    public Result<Integer> getStatus() {
        log.info("获取商铺状态");
        Integer status = shopService.getStatus();
        return Result.success(status);
    }


    /**
     * 修改商铺状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("修改商铺状态")
    public Result<?> setStatus(@PathVariable Long status) {
        log.info("修改商铺状态：{}", status);
        shopService.setStatus(status);
        return Result.success();
    }
}
