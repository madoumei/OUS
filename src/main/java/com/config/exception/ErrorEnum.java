package com.config.exception;


import lombok.Getter;

@Getter
public enum ErrorEnum {
    /*
     * 错误信息
     * */
    E_0(0, "success"),
    E_001(1, "invalid user", "无效用户"),
    E_002(2, "invalid password"),
    E_003(3, "user already exist"),
    E_004(4, "invalid mail address"),
    E_005(5, "user available"),
    E_006(6, "user no update!"),
    E_007(7, "invalid verify code"),
    E_008(8, "nickname available"),
    E_009(9, "nickname already exist"),
    E_010(10, "no such post"),
    E_024(24, "phone no unique"),
    E_027(27, "invalid token"),
    E_029(29, "token expired"),
    E_043(43, "exceed the maximum limit"),
    E_055(55, "no employee"),//没有找到该员工
    E_057(57, "no records"),//没有找到该访客记录
    E_058(58, "Updates are not allowed"),//当前状态不允许更新
    E_059(59, "no template is found"),//没有找到访客邀请模板
    E_063(63, "update data failed"),
    E_064(64, "You are early for your appointment"),
    E_065(65, "You are already late for your appointment"),
    E_066(66, "user was added to the blacklist"),
    E_067(67, "invite was refused"),
    E_068(68, "拜访事由不存在"),
    E_069(69, "Please take the exam"),
    E_070(70, "not allow to permission"),
    E_075(75, "Invalid username or password"),
    E_094(94, "subaccount is invalid"),//入驻企业不存在
    E_100(100, "当天预约同个公司次数超过15次"),
    E_101(101, "gate exceed the maximum limit"),
    E_102(102, "该门岗已删除"),
    E_103(103, "该门岗已停用或不在有效期内"),
    E_104(104, "访客类型不存在"),

    /**
     * 会议
     */
    E_105(105, "invalid add meeting"),
    E_106(106, "start time or end time not null"),
    E_107(107, "Cannot exceed the current time"),
    E_108(108, "The meeting name is unique on the day"),
    E_109(109, "No repeated cancellations allowed"),
    E_110(110, "meeting is null"),
    E_111(111, "Deadline exceeded"),
    E_112(112, "Do not allow appointments with the same mobile phone number"),
    E_113(113, "Failed to schedule a meeting"),
    E_114(114, "Appointment is null"),

    E_115(115, "The meeting room has been reserved at the current time"),
    E_116(116, "邀请功能被关闭，请联系管理员"),
    E_125(125, "门岗不存在"),
    E_144(144, "smscode expired"),//验证码已过期
    E_145(145, "error smscode"),//验证码错误

    E_200(200, "success"),
    E_301(301, "please retry  after 24 hour"),//锁定


    E_400(400, "参数列表错误（缺少，格式不匹配）"),
    E_500(500, "系统运行异常"),
    E_501(501, "鉴权失败，sign的值错误"),
    E_505(505, "业务参数错误"),
    E_604(604, "非法参数异常"),
    E_605(605, "空指针异常"),
    E_606(606, "数据格式化异常"),
    E_607(607, "数组越界异常"),
    E_608(608, "无效token"),
    E_609(609, "无效userid"),
    E_610(610, "没有权限请求该数据"),//token中的userid和请求中的userid不匹配,或者角色不允许
    E_620(620, "File type error"),//文件类型错误
    E_670(670, "import department failed"),//导入部门失败
    E_671(671, "import employee failed"),//导入员工失败
    E_672(672, "no department"),//员工没有部门
    E_673(673, "no leader"),//没有主管


    E_701(701, "无效openid"),
    E_702(702, "获取openid失败"),
    E_703(703, "无效记录"),
    E_704(704, "签名错误"),
    E_705(705, "无效appCode"),
    E_706(706, "timestamp is expired"),
    E_707(707, "运行时缺少数据"),


    /**
     * 二维码扫码错误
     */

    E_1110(1110, "IOException"),
    E_1111(1111, "device number error or not allowed"),//设备码不存在，或者不允许该类型人员进出，或者非授权范围
    E_1112(1112, "qrcode format error"),//非法的二维码或二维码被修改过
    E_1113(1113, "person invalid date"),//人员已过期
    E_1114(1114, "exceed the maximum limit"),//超过允许的进出次数限制
    E_1115(1115, "Not within the validity period"),//不在有效期内
    E_1116(1116, "no appointment date"),
    E_1119(1119, "invalid employee"),
    E_1120(1120, "Scan code is not allowed to enter"),//管理员后台未配置该类型访客允许二维码通行
    E_1121(1121, "visitor unauthorized"),//访客未授权或已拒绝
    E_1122(1122, "QrcodeType error"),//错误的QrcodeType类型
    E_1123(1123, "Unknown record"),//没有找到对应的记录，或者其他参数错误
    E_1124(1124, "Not within visitor's pass time"),//不在访客通行时间内
    E_1125(1125, "Not Allowed without signin"),//没有签到不允许进门
    E_1126(1126, "have finish visit"),//已结束拜访，只允许出
    E_1127(1127, "not allowed invite code access"),//只允许签到二维码开门 vtype=40
    E_1128(1128, "QR code has expired, please refresh"),//二维码已过期
    E_1129(1129, "Employee not authorized"),//员工未授权
    E_1130(1130, "device is disable"),//设备已停用

    /**
     * 电子围栏
     */
    E_1201(1201, "get no route"),//获取路径失败
    E_1202(1202, "偏离推荐路线"),//偏离推荐路径
    E_1203(1203, "访客已签出"),//

    /**
     * 物流车辆
     */
    E_1301(1301, "already in line"),//已在排队
    E_1302(1302, "偏离推荐路线"),//偏离推荐路径
    E_1303(1303, "访客已签出"),//

    //系统错误
    E_2000(2000, "system error"),//其他各种错误
    E_LOCK_IP(2001, "IP locked"),//ip被锁定

    E_2323(2323, "create pdf failed"),//创建手写签名错误
    ;

    private Integer Code;
    private String Msg;
    private String schema;

    ErrorEnum(Integer Code, String Msg) {
        this.Code = Code;
        this.Msg = Msg;
    }

    ErrorEnum(Integer Code, String Msg, String schema) {
        this.Code = Code;
        this.Msg = Msg;
        this.schema = schema;
    }

    public Integer getCode() {
        return Code;
    }

    public String getMsg() {
        return Msg;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String toString(){
        return "["+Code+"]"+Msg+":"+schema;
    }

    public static ErrorEnum getByCode(int code) {
        for(ErrorEnum tEnum : values()) {
            if(tEnum.getCode() == code) {
                return tEnum ;
            }
        }
        return E_2000;
    }
}
