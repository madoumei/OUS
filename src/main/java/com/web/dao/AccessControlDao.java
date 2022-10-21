package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.AccessControl;
import com.web.bean.ReqAC;

@Mapper
public interface AccessControlDao {
       public int addAccessControl(List<AccessControl> acList);
       
       public int updateAccessControl(AccessControl ac);
       
       public int updateLmAccessControl(AccessControl ac);
       
       public List<AccessControl> getAcList(ReqAC rac);
       
       public List<AccessControl> getLeaderAcList(ReqAC rac);
       
       public AccessControl getAcListByEmpNo(ReqAC rac);
       
       public List<AccessControl> getAcListByCid(ReqAC rac);
		
}
