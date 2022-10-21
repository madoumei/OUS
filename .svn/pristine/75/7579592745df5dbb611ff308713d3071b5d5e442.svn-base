package com.web.service.impl;

import com.web.bean.Permission;
import com.web.dao.PermissionDao;
import com.web.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author changmeidong
 * @date 2020/4/22 14:07
 * @Version 1.0
 */
@Service("PermissionService")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public int addPermission(Permission permission) {
        return permissionDao.addPermission(permission);
    }

    @Override
    public List<Permission> getPermissionByaccount(String account) {
        return permissionDao.getPermissionByaccount(account);
    }

    @Override
    public List<Permission> getPermissionTree(List<Permission> p) {
        if (null != p && p.size()>0) {
            List<Permission> rootNode = p.stream().filter(permission -> 0 == (permission.getParentId())).collect(Collectors.toList());
            for (Permission rootPermission : rootNode) {
                findChildNode(rootPermission, p);
            }
            return rootNode;
        }
        return null;
    }

    @Override
    public void RecursiveAddPerChildren(Integer parentid, List<Permission> children, String account) {
        if (null != children && children.size() > 0) {
            for (Permission child : children) {
                child.setParentId(parentid);
                child.setAccount(account);
                this.addPermission(child);
                Integer childId = child.getId();
                if (null != child.getChildren() && child.getChildren().size()>0) {
                    this.RecursiveAddPerChildren(childId, child.getChildren(), account);
                }
            }
        }
    }

    @Override
    public int delPermissionByaccount(String account) {
        return permissionDao.delPermissionByaccount(account);
    }

    @Override
    public void updatePermisionByAccount(List<Permission> module, String account) {
        permissionDao.delPermissionByaccount(account);
        if (null != module && module.size()>0) {
            for (Permission permission : module) {
                //添加一级菜单
                permission.setParentId(0);
                permission.setAccount(account);
                this.addPermission(permission);
                Integer parentid = permission.getId();
                //递归添加子权限菜单
                if (null != permission.getChildren() && permission.getChildren().size()>0) {
                    RecursiveAddPerChildren(parentid, permission.getChildren(), account);
                }
            }
        }
    }


    private void findChildNode(Permission rootNode, List<Permission> allNode) {
        List<Permission> subPermission = allNode.stream().filter(child -> rootNode.getId().equals(child.getParentId())).collect(Collectors.toList());
        rootNode.setChildren(subPermission);
        if (subPermission != null && subPermission.size() > 0) {
            for (Permission permission : subPermission) {
                findChildNode(permission, allNode);
            }
        }
    }
}
