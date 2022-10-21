package com.web.dao;

import com.web.bean.ReqUserInfo;
import com.web.bean.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserDao {

    public int register(UserInfo userinfo);

    public int updateLoginDate(UserInfo userinfo);

    public UserInfo selectByName(String email);

    public UserInfo getUserInfo(int userid);

    public int upEmailConf(UserInfo userinfo);

    public int updatePwd(UserInfo userinfo);

    public int upDigest(UserInfo userinfo);

    public int updateLogo(UserInfo userinfo);

    public int updatebgPicUrl(UserInfo userinfo);

    public int updateThemeColor(UserInfo userinfo);

    public int updateUserInfo(UserInfo userinfo);

    public int updateDataKeepTime(UserInfo userinfo);

    public int updateRtxConf(UserInfo userinfo);

    public List<UserInfo> getUserInfoList();

    public int updateDefaultPhoto(UserInfo userinfo);

    public int updateRefreshDate(UserInfo userinfo);

    public int updateRtxAuto(UserInfo userinfo);

    public int updateWxConf(UserInfo userinfo);

    public int updateCardText(UserInfo userinfo);

    public UserInfo getUserInfoWithExt(String email);

    public int updateDDNotify(UserInfo userinfo);

    public UserInfo getNotifyswitch(String openid);

    public int addUserinfoExtend(UserInfo userinfo);

    public int updateQRcode(UserInfo userinfo);

    public UserInfo selectBycompany(String company);

    public int updateBindingType(UserInfo userinfo);

    public List<String> getAllUser();

    public int updateCustReqUrl(UserInfo userinfo);

    public int updateComeAgain(UserInfo userinfo);

    public List<UserInfo> getAllUserinfo(ReqUserInfo reqinfo);

    public int updateCardType(UserInfo userinfo);

    public int updateUserType(UserInfo userinfo);

    public int updateCustInfo(UserInfo userinfo);

    public List<UserInfo> getDDUserinfo();

    public int updateSMSConf(UserInfo userinfo);

    public int updateRemark(UserInfo userinfo);

    public int updateSmsCount(UserInfo userinfo);

    public int updateWxSmsCount(UserInfo userinfo);

    public int updateAppSmsCount(UserInfo userinfo);

    public int updateUnSubscribe(UserInfo userinfo);

    public List<UserInfo> getUsers(List<Integer> userids);

    public UserInfo getBaseUserInfo(int userid);

    public int updateSubAccountSwitch(UserInfo userinfo);

    public int updatePreRegisterSwitch(UserInfo userinfo);

    public int updateScanerSwitch(UserInfo userinfo);

    public int updateIvrNotifySwitch(UserInfo userinfo);

    public int updateIvrPrintSwitch(UserInfo userinfo);

    public int updatePermissionSwitch(UserInfo userinfo);

    public int updateIdCardSwitch(UserInfo userinfo);

    public int updateSignOutSwitch(UserInfo userinfo);

    public UserInfo getWeixinInfo(int userid);

    public int closeWeixinBus(String corpid);

    public int updateFaceScaner(UserInfo userinfo);

    public UserInfo getExtendsInfo(int userid);

    public int updateWebWalkins(UserInfo userinfo);

    public int updateVNetConf(UserInfo userinfo);

    public int updateExtendTime(UserInfo userinfo);

    public int updateTempEditSwitch(UserInfo userinfo);

    public int updateKeyExpireTime(UserInfo userinfo);

    public int updateblackListSwitch(UserInfo userinfo);

    public int updateProcessSwitch(UserInfo userinfo);

    public int updateSecureProtocol(UserInfo userinfo);

    public int updateOffDutyTime(UserInfo userinfo);

    public UserInfo getUserByAccount(UserInfo userinfo);

    public UserInfo getUserSwitch(int userid);

    public int updateQrcodeConf(UserInfo userinfo);

    public int updateCardStyle(UserInfo userinfo);

    public int updateLeaveExpiryTime(UserInfo userinfo);

    public int updatePassableTime(UserInfo userinfo);

    public int updateQuestionnaireSwitch(UserInfo userinfo);

    public int updateEscapeClause(UserInfo userinfo);

    int updateWechatConf(UserInfo userinfo);

    int updateDDScannerConf(UserInfo userInfo);

    public int updateFsNotify(UserInfo userinfo);

    public int updateNotifyType(UserInfo userinfo);

    public int updateWxBusNotify(UserInfo userinfo);

    int updateSatisfactionSwitch(UserInfo userinfo);

    /**
     * 更新小程序轮播图
     *
     * @param ui
     * @return
     */
    int updateAppletCarouselPicUrl(UserInfo ui);

    int updateAppointmenProcessSwitch(UserInfo userinfo);
    
    public int updateCarouselPic(UserInfo userinfo);

    int updateRiskArea(UserInfo userinfo);

    UserInfo getRiskArea(UserInfo userInfo);
}
