package com.web.service;

import com.web.bean.Permission;

import java.util.List;

/**
 * @author changmeidong
 * @date 2020/4/22 14:06
 * @Version 1.0
 */
public interface PermissionService {
    public int addPermission(Permission permission);

    List<Permission> getPermissionByaccount(String account);

     List<Permission> getPermissionTree(List<Permission> p);

    void RecursiveAddPerChildren(Integer parentid, List<Permission> children, String account);

    int delPermissionByaccount(String account);

    void updatePermisionByAccount(List<Permission> module, String account);
}
