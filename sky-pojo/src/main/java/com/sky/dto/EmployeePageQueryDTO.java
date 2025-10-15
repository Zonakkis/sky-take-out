package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("员工分页查询DTO")
public class EmployeePageQueryDTO implements Serializable {

    @ApiModelProperty(value = "员工姓名", required = false)
    private String name;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
