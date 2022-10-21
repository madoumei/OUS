package com.web.service;

import java.util.List;

import com.web.bean.CompanyInfo;
import com.web.bean.ReqSubAccount;
import com.web.bean.SubAccount;

public interface SubAccountService {
	
	public List<SubAccount> getSubAccountByUserid(ReqSubAccount rsa);
	
	public SubAccount getSubAccountById(int id);
	
	public int  addSubAccount(SubAccount sa);
	
	public int updateSubAccount(SubAccount sa);
	
	public int updateSARefreshDate(SubAccount sa);
	
	public int delSubAccount(int id);
	
	public SubAccount getSubAccountByCompany(String company);
	
	public List<CompanyInfo> getAllCompany();
	
	public int  batchAddSubAccount(List<SubAccount> salist);
	
	public List<SubAccount> CheckSubAccountByCompany(String company);
	
	public SubAccount getSubAccountByEmail(String email);
	
	public List<SubAccount> getAllSubAccountEmail();
	
	public List<CompanyInfo> getAllCompanybySA();
	
	public int updateSubAccountPwd(SubAccount sa);
	
	public SubAccount getSubAccountPassword(int id);
	
	public int updateSABalance(SubAccount sa);
	
	public int updateSType(SubAccount sa);

	List<SubAccount> getSubAccountTree(List<SubAccount> subAccountList);

    List<SubAccount> getSubAccountByUseridPage(ReqSubAccount reqSubAccount);
    
    public int batchDelSubAccount(ReqSubAccount reqSubAccount);

    int batchUpdateSubAccountLogo(List<SubAccount> subAccountList);
    
    public int  updateIsUse(SubAccount sa);
}
