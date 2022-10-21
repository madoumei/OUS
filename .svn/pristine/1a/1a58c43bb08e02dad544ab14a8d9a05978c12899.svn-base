package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.Gate;
import com.web.bean.Address;


@Mapper
public interface AddressDao {
	public  int addAddress(Address adr);
	
	public List<Address> getAddressList();
	
	public int delAddress(@Param("id") int id);
	
	public int updateAddress(Address adr);
	
	public int addGate(Gate gate);
	
	public int updateGate(Gate gate);
	
	public int delGate(Gate gate);
	
	public List<Gate> getGateList(Gate gate);
	
	public List<Gate> getGateById(Gate gate);
	
	public Gate getGateByName(Gate gate);

}
