package com.web.dao;

import com.web.bean.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author changmeidong
 * @date 2020/4/22 14:08
 * @Version 1.0
 */
@Mapper
public interface PermissionDao {
    int addPermission(Permission permission);

    List<Permission> getPermissionByaccount(String account);

    int delPermissionByaccount(String account);
}
