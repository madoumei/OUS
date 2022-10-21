package com.web.service.impl;

import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.utils.StringUtils;
import com.ql.util.express.exception.QLException;
import com.web.bean.ReqUserInfo;
import com.web.bean.SubAccount;
import com.web.bean.UserInfo;
import com.web.dao.UserDao;
import com.web.service.SubAccountService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SubAccountService subAccountService;

    @Override
    public int register(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.register(userinfo);
    }

    @Override
    public int updateLoginDate(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateLoginDate(userinfo);
    }

    @Override
    public UserInfo selectByName(String email) {
        // TODO Auto-generated method stub
        return userDao.selectByName(email);
    }

    @Override
    public UserInfo getUserInfoByUserId(int userid) {
        // TODO Auto-generated method stub
        return userDao.getUserInfo(userid);
    }

    @Override
    public int upEmailConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.upEmailConf(userinfo);
    }

    @Override
    public int updatePwd(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updatePwd(userinfo);
    }

    @Override
    public int upDigest(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.upDigest(userinfo);
    }

    @Override
    public int updateLogo(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateLogo(userinfo);
    }

    @Override
    public int updatebgPicUrl(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updatebgPicUrl(userinfo);
    }

    @Override
    public int updateThemeColor(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateThemeColor(userinfo);
    }

    @Override
    public int updateUserInfo(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateUserInfo(userinfo);
    }

    @Override
    public int updateRtxConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateRtxConf(userinfo);
    }

    @Override
    public List<UserInfo> getUserInfoList() {
        // TODO Auto-generated method stub
        return userDao.getUserInfoList();
    }

    @Override
    public int updateDefaultPhoto(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateDefaultPhoto(userinfo);
    }

    @Override
    public int updateRefreshDate(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateRefreshDate(userinfo);
    }

    @Override
    public int updateRtxAuto(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateRtxAuto(userinfo);
    }

    @Override
    public int updateWxConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateWxConf(userinfo);
    }

    @Override
    public int updateCardText(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateCardText(userinfo);
    }

    @Override
    public UserInfo getUserInfoWithExt(String email) {
        // TODO Auto-generated method stub
        return userDao.getUserInfoWithExt(email);
    }

    @Override
    public int updateDDNotify(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateDDNotify(userinfo);
    }

    @Override
    public UserInfo getNotifyswitch(String openid) {
        // TODO Auto-generated method stub
        return userDao.getNotifyswitch(openid);
    }

    @Override
    public int addUserinfoExtend(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.addUserinfoExtend(userinfo);
    }

    @Override
    public int updateQRcode(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateQRcode(userinfo);
    }

    @Override
    public UserInfo selectBycompany(String company) {
        // TODO Auto-generated method stub
        return userDao.selectBycompany(company);
    }

    @Override
    public int updateBindingType(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateBindingType(userinfo);
    }

    @Override
    public List<String> getAllUser() {
        // TODO Auto-generated method stub
        return userDao.getAllUser();
    }

    @Override
    public int updateCustReqUrl(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateCustReqUrl(userinfo);
    }

    @Override
    public int updateComeAgain(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateComeAgain(userinfo);
    }

    @Override
    public List<UserInfo> getAllUserinfo(ReqUserInfo reqinfo) {
        // TODO Auto-generated method stub
        return userDao.getAllUserinfo(reqinfo);
    }

    @Override
    public int updateCardType(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateCardType(userinfo);
    }

    @Override
    public int updateUserType(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateUserType(userinfo);
    }

    @Override
    public int updateCustInfo(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateCustInfo(userinfo);
    }

    @Override
    public List<UserInfo> getDDUserinfo() {
        // TODO Auto-generated method stub
        return userDao.getDDUserinfo();
    }

    @Override
    public int updateSMSConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSMSConf(userinfo);
    }

    @Override
    public int updateRemark(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateRemark(userinfo);
    }

    @Override
    public int updateSmsCount(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSmsCount(userinfo);
    }

    @Override
    public int updateUnSubscribe(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateUnSubscribe(userinfo);
    }

    @Override
    public List<UserInfo> getUsers(List<Integer> userids) {
        // TODO Auto-generated method stub
        return userDao.getUsers(userids);
    }

    @Override
    public UserInfo getBaseUserInfo(int userid) {
        // TODO Auto-generated method stub
        return userDao.getBaseUserInfo(userid);
    }

    @Override
    public int updateWxSmsCount(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateWxSmsCount(userinfo);
    }

    @Override
    public int updateAppSmsCount(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateAppSmsCount(userinfo);
    }

    @Override
    public int updateSubAccountSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSubAccountSwitch(userinfo);
    }

    @Override
    public int updatePreRegisterSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updatePreRegisterSwitch(userinfo);
    }

    @Override
    public int updateScanerSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateScanerSwitch(userinfo);
    }

    @Override
    public int updateIvrNotifySwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateIvrNotifySwitch(userinfo);
    }

    @Override
    public int updateIvrPrintSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateIvrPrintSwitch(userinfo);
    }

    @Override
    public int updatePermissionSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updatePermissionSwitch(userinfo);
    }

    @Override
    public int updateIdCardSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateIdCardSwitch(userinfo);
    }

    @Override
    public int updateSignOutSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSignOutSwitch(userinfo);
    }

    @Override
    public UserInfo getWeixinInfo(int userid) {
        // TODO Auto-generated method stub
        return userDao.getWeixinInfo(userid);
    }

    @Override
    public int closeWeixinBus(String corpid) {
        // TODO Auto-generated method stub
        return userDao.closeWeixinBus(corpid);
    }

    @Override
    public int updateFaceScaner(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateFaceScaner(userinfo);
    }

    @Override
    public UserInfo getExtendsInfo(int userid) {
        // TODO Auto-generated method stub
        return userDao.getExtendsInfo(userid);
    }

    @Override
    public int updateWebWalkins(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateWebWalkins(userinfo);
    }

    @Override
    public int updateVNetConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateVNetConf(userinfo);
    }

    @Override
    public int updateExtendTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateExtendTime(userinfo);
    }

    @Override
    public int updateTempEditSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateTempEditSwitch(userinfo);
    }

    @Override
    public int updateKeyExpireTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateKeyExpireTime(userinfo);
    }

    @Override
    public int updateblackListSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateblackListSwitch(userinfo);
    }

    @Override
    public int updateProcessSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateProcessSwitch(userinfo);
    }

    @Override
    public int updateSecureProtocol(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSecureProtocol(userinfo);
    }

    @Override
    public int updateOffDutyTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateOffDutyTime(userinfo);
    }

    @Override
    public UserInfo getUserSwitch(int userid) {
        // TODO Auto-generated method stub
        return userDao.getUserSwitch(userid);
    }

    @Override
    public int updateQrcodeConf(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateQrcodeConf(userinfo);
    }

    @Override
    public int updateDataKeepTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateDataKeepTime(userinfo);
    }

    @Override
    public UserInfo getUserByAccount(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.getUserByAccount(userinfo);
    }

    @Override
    public int updateCardStyle(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateCardStyle(userinfo);
    }

    @Override
    public int updateLeaveExpiryTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateLeaveExpiryTime(userinfo);
    }

    @Override
    public int updatePassableTime(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updatePassableTime(userinfo);
    }

    @Override
    public int updateQuestionnaireSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateQuestionnaireSwitch(userinfo);
    }

    @Override
    public int updateEscapeClause(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateEscapeClause(userinfo);
    }

    @Override
    public int updateWechatConf(UserInfo userinfo) {
        return userDao.updateWechatConf(userinfo);
    }

    @Override
    public int updateDDScannerConf(UserInfo userInfo) {
        return userDao.updateDDScannerConf(userInfo);
    }

    @Override
    public UserInfo getUserInfo(int userid) {
        // TODO Auto-generated method stub
        return userDao.getUserInfo(userid);
    }

    @Override
    public int updateFsNotify(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateFsNotify(userinfo);
    }

    @Override
    public int updateNotifyType(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateNotifyType(userinfo);
    }

    @Override
    public int updateWxBusNotify(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateWxBusNotify(userinfo);
    }

    @Override
    public int updateSatisfactionSwitch(UserInfo userinfo) {
        // TODO Auto-generated method stub
        return userDao.updateSatisfactionSwitch(userinfo);
    }

    /**
     * 更新小程序轮播图
     *
     * @param ui
     * @return
     */
    @Override
    public int updateAppletCarouselPicUrl(UserInfo ui) {
        return userDao.updateAppletCarouselPicUrl(ui);
    }

    /**
     * 根据userid获取小程序轮播图
     *
     * @param userid
     * @return
     */
    @Override
    public List<String> getAppletPicsByUserid(int userid) {
        UserInfo userInfo = this.userDao.getUserInfo(userid);
        List<String> pics = new ArrayList<>();
        String appletBackgroundPic = userInfo.getAppletCarouselPic();
        if (StringUtils.isNotBlank(appletBackgroundPic)){
            String[] split = appletBackgroundPic.split(",");
            for (int i = 0; i < split.length; i++) {
                pics.add(split[i]);
            }
        }
        return pics;
    }

    @Override
    public int updateAppointmenProcessSwitch(UserInfo userinfo) {
        return userDao.updateAppointmenProcessSwitch(userinfo);
    }

    @Override
    public int needPermission(int userid,int subid) {
        if(subid != 0){
            SubAccount sa = subAccountService.getSubAccountById(subid);
            if(sa == null){
                throw new ErrorException(ErrorEnum.E_001);
            }

            //入驻企业
            int purviewValue = 4;
            if ((sa.getVaPerm() & purviewValue) == purviewValue) {
                return 1;
            } else {
                return 0;
            }

        }

        UserInfo userInfo = getUserInfo(userid);
        if(userInfo == null){
            throw new ErrorException(ErrorEnum.E_001);
        }
        return userInfo.getPermissionSwitch();
    }

    @Override
    public int needProcess(int userid,int subid) {

        UserInfo userInfo = getUserInfo(userid);
        if(userInfo == null){
            throw new ErrorException(ErrorEnum.E_001);
        }
        return userInfo.getProcessSwitch();
    }
    

	@Override
	public int updateCarouselPic(UserInfo userinfo) {
		// TODO Auto-generated method stub
		return userDao.updateCarouselPic(userinfo);
	}

    @Override
    public int updateRiskArea(UserInfo userinfo) {
        return userDao.updateRiskArea(userinfo);
    }

    @Override
    public UserInfo getRiskArea(UserInfo userInfo) {
        return userDao.getRiskArea(userInfo);
    }
}
