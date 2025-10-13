package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("新增员工传递数据对象")
public class EmployeeDTO implements Serializable {

    @ApiModelProperty(value = "主键值")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("性别 0->女 1->男")
    private String sex;

    @ApiModelProperty(value = "身份证号码")
    private String idNumber;

}
