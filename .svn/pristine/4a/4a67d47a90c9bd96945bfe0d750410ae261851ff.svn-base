package com.web.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 菜单权限表
 * @author changmeidong
 * @date 2020/4/22 13:17
 * @Version 1.0
 */
@Getter
@Setter
@ApiModel("账号模块授权")
public class Permission {

    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("管理账号")
    private String account; //
    @ApiModelProperty("管理账号")
    private Integer parentId;   //父级id
    @ApiModelProperty("菜单路由地址")
    private String path;    //
    @ApiModelProperty("菜单名称")
    private String name;    //
    @ApiModelProperty("路由名称")
    private String routeName;   //
    @ApiModelProperty("组件")
    private String component; //
    @ApiModelProperty("图标")
    private String iconCls;       //
    @ApiModelProperty("重定向")
    private String redirect;       //
    @ApiModelProperty("是否隐藏")
    private boolean hidden;    //
    @ApiModelProperty("是否展示标签")
    private boolean showMenu;
    private boolean leafClose;
    @ApiModelProperty("标题")
    private Meta meta;
    @ApiModelProperty("子菜单")
    private List<Permission> children;//
    @ApiModelProperty("菜单权限")
    private String permission;//
    @ApiModelProperty("菜单权限")
    private String componentName;//
}
