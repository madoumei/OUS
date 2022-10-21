package com.web.service;

import java.util.List;

import com.client.bean.Gate;
import com.web.bean.Address;

public interface AddressService {
	
	public  int addAddress(Address adr);
	
	public List<Address> getAddressList();
	
	public int delAddress(int id);
	
	public int updateAddress(Address adr);
	
	public int addGate(Gate gate);
	
	public int updateGate(Gate gate);
	
	public int delGate(Gate gate);
	
	public List<Gate> getGateList(Gate gate);
	
	public List<Gate> getGateById(Gate gate);
	
	public Gate getGateByName(Gate gate);


}
