package com.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.config.qicool.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Gate;
import com.web.bean.Address;
import com.web.dao.AddressDao;
import com.web.service.AddressService;


@Service("addressService")
public class AddressServiceImpl implements AddressService{
	@Autowired
	private AddressDao addressDao;

	@Override
	public int addAddress(Address adr) {
		// TODO Auto-generated method stub
		return addressDao.addAddress(adr);
	}

	@Override
	public List<Address> getAddressList() {
		// TODO Auto-generated method stub
		return addressDao.getAddressList();
	}

	@Override
	public int delAddress(int id) {
		// TODO Auto-generated method stub
		return addressDao.delAddress(id);
	}

	@Override
	public int updateAddress(Address adr) {
		// TODO Auto-generated method stub
		return addressDao.updateAddress(adr);
	}

	@Override
	public int addGate(Gate gate) {
		// TODO Auto-generated method stub
		return addressDao.addGate(gate);
	}

	@Override
	public int updateGate(Gate gate) {
		// TODO Auto-generated method stub
		return addressDao.updateGate(gate);
	}

	@Override
	public List<Gate> getGateList(Gate gate) {
		// TODO Auto-generated method stub
		return addressDao.getGateList(gate);
	}

	@Override
	public int delGate(Gate gate) {
		// TODO Auto-generated method stub
		return addressDao.delGate(gate);
	}

	@Override
	public List<Gate> getGateById(Gate gate) {
		if(StringUtils.isEmpty(gate.getGids())){
			return new ArrayList<Gate>();
		}
		return addressDao.getGateById(gate);
	}

	@Override
	public Gate getGateByName(Gate gate) {
		// TODO Auto-generated method stub
		return addressDao.getGateByName(gate);
	}


}
