package com.web.service.impl;

import com.web.bean.CompanyInfo;
import com.web.bean.Permission;
import com.web.bean.ReqSubAccount;
import com.web.bean.SubAccount;
import com.web.dao.SubAccountDao;
import com.web.service.PermissionService;
import com.web.service.SubAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("subAccountService")
public class SubAccountServiceImpl implements SubAccountService {

	@Autowired
	private SubAccountDao subAccountDao;

	@Autowired
	private PermissionService permissionService;

	@Override
	public List<SubAccount> getSubAccountByUserid(ReqSubAccount rsa) {
		// TODO Auto-generated method stub
		return subAccountDao.getSubAccountByUserid(rsa);
	}

	@Override
	public int addSubAccount(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.addSubAccount(sa);
	}

	@Override
	public int updateSubAccount(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateSubAccount(sa);
	}

	@Override
	public int delSubAccount(int id) {
		// TODO Auto-generated method stub
		return subAccountDao.delSubAccount(id);
	}

	@Override
	public SubAccount getSubAccountById(int id) {
		// TODO Auto-generated method stub
		return subAccountDao.getSubAccountById(id);
	}

	@Override
	public int updateSARefreshDate(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateSARefreshDate(sa);
	}

	@Override
	public SubAccount getSubAccountByCompany(String company) {
		// TODO Auto-generated method stub
		return subAccountDao.getSubAccountByCompany(company);
	}

	@Override
	public List<CompanyInfo> getAllCompany() {
		// TODO Auto-generated method stub
		return subAccountDao.getAllCompany();
	}

	@Override
	public int batchAddSubAccount(List<SubAccount> salist) {
		// TODO Auto-generated method stub
		return subAccountDao.batchAddSubAccount(salist);
	}

	@Override
	public List<SubAccount> CheckSubAccountByCompany(String company) {
		// TODO Auto-generated method stub
		return subAccountDao.CheckSubAccountByCompany(company);
	}

	@Override
	public SubAccount getSubAccountByEmail(String email) {
		// TODO Auto-generated method stub
		return subAccountDao.getSubAccountByEmail(email);
	}

	@Override
	public List<SubAccount> getAllSubAccountEmail() {
		// TODO Auto-generated method stub
		return subAccountDao.getAllSubAccountEmail();
	}

	@Override
	public List<CompanyInfo> getAllCompanybySA() {
		// TODO Auto-generated method stub
		return subAccountDao.getAllCompanybySA();
	}

	@Override
	public int updateSubAccountPwd(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateSubAccountPwd(sa);
	}

	@Override
	public SubAccount getSubAccountPassword(int id) {
		// TODO Auto-generated method stub
		return subAccountDao.getSubAccountPassword(id);
	}

	@Override
	public int updateSABalance(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateSABalance(sa);
	}

	@Override
	public int updateSType(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateSType(sa);
	}


	@Override
	public List<SubAccount> getSubAccountTree(List<SubAccount> allsubAccountList) {
		if (allsubAccountList.size() > 0) {
			List<SubAccount> rootNode = allsubAccountList.stream().filter(subAccount -> 0 == subAccount.getSubAccountiId()).collect(Collectors.toList());
			for (SubAccount childs : rootNode) {
				findChildNode(childs, allsubAccountList);
			}
			return rootNode;
		}
		return null;
	}

	private void findChildNode(SubAccount childs, List<SubAccount> allSubAccountList) {
		List<SubAccount> collect = allSubAccountList.stream().filter(child -> childs.getId() == child.getSubAccountiId()).collect(Collectors.toList());
		childs.setSubAccountList(collect);
		List<Permission> permissions = permissionService.getPermissionByaccount(childs.getCompanyName());
		if (permissions.size()>0){
			List<Permission> permissionTree = permissionService.getPermissionTree(permissions);
			childs.setModule(permissionTree);
		}
		if (collect.size() > 0 && collect != null) {
			for (SubAccount account : collect) {
				findChildNode(account, allSubAccountList);
			}
		}
	}

	@Override
	public List<SubAccount> getSubAccountByUseridPage(ReqSubAccount reqSubAccount) {
		return subAccountDao.getSubAccountByUseridPage(reqSubAccount);
	}

	@Override
	public int batchDelSubAccount(ReqSubAccount reqSubAccount) {
		// TODO Auto-generated method stub
		return subAccountDao.batchDelSubAccount(reqSubAccount);
	}

	@Override
	public int batchUpdateSubAccountLogo(List<SubAccount> subAccountList) {
		return subAccountDao.batchUpdateSubAccountLogo(subAccountList);
	}

	@Override
	public int updateIsUse(SubAccount sa) {
		// TODO Auto-generated method stub
		return subAccountDao.updateIsUse(sa);
	}
}
