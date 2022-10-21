# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: dev.coolvisit.top (MySQL 5.5.5-10.2.43-MariaDB)
# Database: qcvisit
# Generation Time: 2022-05-23 05:56:48 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table qcv_appointment_usertemplate
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_appointment_usertemplate`;

CREATE TABLE `qcv_appointment_usertemplate` (
  `userid` int(11) unsigned NOT NULL,
  `templateType` varchar(20) CHARACTER SET utf8 NOT NULL,
  `inviteContent` text CHARACTER SET utf8 NOT NULL,
  `address` varchar(255) CHARACTER SET utf8 NOT NULL,
  `longitude` varchar(20) CHARACTER SET utf8 NOT NULL,
  `latitude` varchar(20) CHARACTER SET utf8 NOT NULL,
  `companyProfile` text CHARACTER SET utf8 DEFAULT NULL,
  `traffic` text CHARACTER SET utf8 DEFAULT NULL,
  `gid` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`userid`,`templateType`,`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `qcv_appointment_usertemplate` WRITE;
/*!40000 ALTER TABLE `qcv_appointment_usertemplate` DISABLE KEYS */;

INSERT INTO `qcv_appointment_usertemplate` (`userid`, `templateType`, `inviteContent`, `address`, `longitude`, `latitude`, `companyProfile`, `traffic`, `gid`)
VALUES
	(2147483647,'参会#Meeting','<p><strong>尊敬的{visitor}：</strong></p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">您好！</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">我是{company}的{empid}，很高兴代表我司与您联系。为更好的沟通交流工作事宜，诚挚希望与您进行会面，期待您的来访！</p>','南京市栖霞区南京紫东国际创意园','118.91930528165994','32.085457273577745','来访通','<p>地下停车场</p>',1),
	(2147483647,'商务#Business','<p><strong>尊敬的{visitor}：</strong></p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">您好！</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">我是{company}的{empid}，很高兴代表我司与您联系。为更好的沟通交流工作事宜，诚挚希望与您进行会面，期待您的来访！</p>','南京市栖霞区南京紫东国际创意园','118.91930528165994','32.085457273577745','<p>来访通<br></p>','<p><br data-mce-bogus=\"1\"></p>',1),
	(2147483647,'商务#Business','<p><strong>尊敬的{visitor}：</strong></p><p style=\"text-indent: 24px;\" data-mce-style=\"text-indent: 24px;\">您好！</p><p style=\"text-indent: 24px;\" data-mce-style=\"text-indent: 24px;\">我是{company}的{empid}，很高兴代表我司与您联系。为更好的沟通交流工作事宜，诚挚希望与您进行会面，期待您的来访！</p>','南京市栖霞区南京紫东国际创意园东区-B6栋','118.91891245266044','32.086997911239486','来访通','停车',2),
	(2147483647,'商务#Business','<p>尊敬的{visitor}：</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">您好！</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">我是{company}的{empid}，很高兴代表我司与您联系。为更好的沟通交流工作事宜，诚挚希望与您进行会面，期待您的来访！</p>','南京市栖霞区南京紫东国际创意园东区-B6栋','118.91891245266044','32.086997911239486','来访通','停车',3),
	(2147483647,'面试#Interview','<p><strong>尊敬的{visitor}：</strong></p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">您好！</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">我是{company}的{empid}，很高兴代表我司与您联系。为更好的沟通交流工作事宜，诚挚希望与您进行会面，期待您的来访！</p>','南京市栖霞区南京紫东国际创意园','118.91930528165994','32.085457273577745','<p><br data-mce-bogus=\"1\"></p>','<p><br data-mce-bogus=\"1\"></p>',1),
	(2147483647,'面试#Interview','<p><strong>尊敬的{visitor}：</strong></p><p style=\"text-indent: 24px;\" data-mce-style=\"text-indent: 24px;\">您好！</p><p style=\"text-indent: 24px;\" data-mce-style=\"text-indent: 24px;\">这里是{company}，感谢您对我公司的信任和选择。通过对您简历的认真审核，我们认为您已具备进入下一轮筛选的资格。为了进一步了解，现邀请您参加面试，具体安排如下：</p><p><br></p><p><br></p>','南京市栖霞区南京紫东国际创意园东区-B6栋','118.91891245266044','32.086997911239486','来访通','停车',2),
	(2147483647,'面试#Interview','<p><strong>尊敬的{visitor}：</strong></p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">您好！</p><p data-mce-style=\"text-indent: 24px;\" style=\"text-indent: 24px;\">这里是{company}，感谢您对我公司的信任和选择。通过对您简历的认真审核，我们认为您已具备进入下一轮筛选的资格。为了进一步了解，现邀请您参加面试，具体安排如下：</p><p><br></p>','南京市栖霞区南京紫东国际创意园东区-B6栋','118.91891245266044','32.086997911239486','','',3);

/*!40000 ALTER TABLE `qcv_appointment_usertemplate` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_eqpt
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_eqpt`;

CREATE TABLE `qcv_eqpt` (
  `eid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) unsigned NOT NULL,
  `deviceName` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '设备名称',
  `deviceCode` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '设备码',
  `extendCode` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '设备码扩展字段',
  `roomNum` varchar(100) DEFAULT NULL,
  `deviceIp` varchar(30) NOT NULL,
  `devicePort` int(11) NOT NULL,
  `deviceQrcode` varchar(100) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态 0 停用 1启用',
  `eType` varchar(22) DEFAULT '1' COMMENT 'QR111表示二维码设备111，CAHK1表示摄像头海康1设备CAHM表示车牌摄像头',
  `enterStatus` tinyint(1) DEFAULT NULL COMMENT '进门出门状态： 1：进门2 出门',
  `onlineStatus` tinyint(1) DEFAULT 0 COMMENT '设备在线状态0表示默认状态1表示在线2表示离线',
  PRIMARY KEY (`eid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `qcv_eqpt` WRITE;
/*!40000 ALTER TABLE `qcv_eqpt` DISABLE KEYS */;

INSERT INTO `qcv_eqpt` (`eid`, `userid`, `deviceName`, `deviceCode`, `extendCode`, `roomNum`, `deviceIp`, `devicePort`, `deviceQrcode`, `status`, `eType`, `enterStatus`, `onlineStatus`)
VALUES
	(1,2147483647,'大门','QR888888','','','',0,'888888',1,'QR11',0,0),
	(2,2147483647,'测试A_01','ZJ253109562','','1','172.16.109.87',60000,'ZJ1',1,'ZJ1',2,0),
	(3,2147483647,'QR1-int','QR1-int','','','',0,'1-int',1,'QR2',1,0),
	(4,2147483647,'Nike工厂2号门','CA111112','','','',0,'111112',1,'CA1',2,0),
	(5,2147483647,'11111','SP1111111','','','',0,'111111',1,'SP1',1,0),
	(6,2147483647,'测试','20003','','','',0,'20003',1,'QR3',0,0),
	(7,2147483647,'海1','8888','admin','admin','',0,'',1,'CA2',NULL,0),
	(8,2147483647,'qralalla','QR00001','','','',0,'111111111222222',1,'QR4',1,2),
	(9,2147483647,'二维码测试的','QR99999','','','',0,'123123123123',1,'QR5',0,2),
	(10,2147483647,'统计二维码都头','SP000001','','','',0,'0000000012345',1,'SP2',0,2),
	(11,2147483647,'uiuiuiuiui','QRjjjjjjjj','','','',0,'555555',1,'QR6',1,1),
	(12,2147483647,'QR1-out','QR1-out','QQ','','',0,'1-out',1,'QRout',2,0),
	(13,2147483647,'SP1-int','SP1-int','','','',0,'',1,'5',1,0),
	(14,2147483647,'SP1-out','SP1-out','','','',0,'',1,'5',2,0),
	(15,2147483647,'QR0001','asdasdasd','','','',0,'112223123123',1,'1',1,0),
	(16,2147483647,'44','43434354','','','',0,'2314231',1,'1',1,0),
	(17,2147483647,'QR2-int','QR2-int','','','',0,'QR2-int',1,'1',1,0),
	(18,2147483647,'QR2-out','QR2-out','','','',0,'QR2-out',1,'1',2,0);

/*!40000 ALTER TABLE `qcv_eqpt` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_eqpt_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_eqpt_group`;

CREATE TABLE `qcv_eqpt_group` (
  `egid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) unsigned NOT NULL,
  `egname` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '组名称',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态  1 启用 0不启用',
  `etype` tinyint(1) NOT NULL DEFAULT 0,
  `gids` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`egid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `qcv_eqpt_group` WRITE;
/*!40000 ALTER TABLE `qcv_eqpt_group` DISABLE KEYS */;

INSERT INTO `qcv_eqpt_group` (`egid`, `userid`, `egname`, `status`, `etype`, `gids`)
VALUES
	(1132,2147483647,'T3-42F',1,0,'3'),
	(1266,2147483647,'T2-32F',1,0,'2'),
	(1390,2147483647,'T1-16F1',3,0,'1,2'),
	(1397,2147483647,'ceshi78',1,2,'1'),
	(1402,2147483647,'Nike工厂1号门',3,0,''),
	(1403,2147483647,'Nike工厂2号门',3,0,''),
	(1404,2147483647,'Nike工厂3号门',3,0,'');

/*!40000 ALTER TABLE `qcv_eqpt_group` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_user_extend
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_user_extend`;

CREATE TABLE `qcv_user_extend` (
  `userid` int(11) unsigned NOT NULL,
  `ddnotify` tinyint(2) NOT NULL DEFAULT 0 COMMENT '丁丁开关',
  `ddautosync` tinyint(2) NOT NULL DEFAULT 0 COMMENT '丁丁自动同步开关',
  `ddcorpid` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '丁丁corpid',
  `ddcorpsecret` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '丁丁corpsecret',
  `ddagentid` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '微应用agentid',
  `ddAppid` varchar(50) DEFAULT NULL,
  `ddAppSccessSecret` varchar(100) DEFAULT NULL,
  `qrcode` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '二维码',
  `qrcodeSwitch` tinyint(3) NOT NULL DEFAULT 0 COMMENT '二维码开关',
  `qrcodeType` tinyint(3) NOT NULL DEFAULT 0 COMMENT '二维码类型',
  `custrequrl` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '私有云服务器请求域名',
  `bindingType` tinyint(3) NOT NULL DEFAULT 1 COMMENT '微信绑定方式',
  `comeAgain` tinyint(3) NOT NULL DEFAULT 1 COMMENT '曾经来过开关',
  `printType` tinyint(3) NOT NULL DEFAULT 1 COMMENT '黑白或彩色打印',
  `cardType` tinyint(3) NOT NULL DEFAULT 1 COMMENT '卡片样式',
  `cardSize` tinyint(3) NOT NULL DEFAULT 1 COMMENT '卡片尺寸',
  `cardLogo` tinyint(3) NOT NULL DEFAULT 1 COMMENT '卡片上公司名称或logo',
  `custSource` tinyint(3) NOT NULL DEFAULT 1 COMMENT '客户来源类型',
  `custWeb` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '客户官网',
  `custAddress` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '客户地址',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `smsCount` int(11) NOT NULL DEFAULT 0 COMMENT '通知已发短信数量',
  `wxSmsCount` int(11) NOT NULL DEFAULT 0 COMMENT '微信快捷回复短信数量',
  `appSmsCount` int(11) NOT NULL DEFAULT 0 COMMENT '预约短信数量',
  `unsubscribe` tinyint(3) NOT NULL DEFAULT 0 COMMENT '邮件退订',
  `preRegisterSwitch` tinyint(3) NOT NULL DEFAULT 0 COMMENT '预约开关',
  `scaner` tinyint(3) NOT NULL DEFAULT 0 COMMENT '扫码器开关',
  `ivrNotify` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'ivr通知开关',
  `ivrPrint` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'ivr打印开关',
  `permissionSwitch` tinyint(3) NOT NULL DEFAULT 1 COMMENT '权限开关',
  `idCardSwitch` tinyint(3) NOT NULL DEFAULT 0 COMMENT '身份证读码器开关',
  `signOutSwitch` tinyint(3) NOT NULL DEFAULT 1 COMMENT '登出开关',
  `permanentCode` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '微信企业号授权码',
  `corpid` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '微信企业号corpid',
  `corpsecret` varchar(100) DEFAULT NULL,
  `agentid` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '微信企业号agentid',
  `serviceID` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'v网通服务号id',
  `securityID` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'v网通安全身份ID',
  `securityKey` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'v网通秘钥',
  `tempEditSwitch` tinyint(3) DEFAULT 1 COMMENT '模版可编辑开关',
  `blackListSwitch` tinyint(3) DEFAULT 0 COMMENT '黑名单开关',
  `processSwitch` tinyint(3) DEFAULT 0 COMMENT '流程开关',
  `secureProtocol` text DEFAULT NULL COMMENT '安全协议',
  `upDuty` varchar(20) NOT NULL DEFAULT '09:00' COMMENT '上班时间',
  `offDuty` varchar(20) NOT NULL DEFAULT '18:00' COMMENT '下班时间',
  `mailInvSmsSwitch` tinyint(1) DEFAULT 1 COMMENT '邮件短信开关',
  `webInvSmsSwitch` tinyint(1) DEFAULT 1,
  `qrMaxCount` int(11) DEFAULT 1 COMMENT '二维码最大使用次数',
  `qrMaxDuration` int(11) DEFAULT 1 COMMENT '二维码最长使用时间',
  `badgeMode` tinyint(1) DEFAULT NULL COMMENT '0-普通，1-定制',
  `badgeCustom` varchar(250) DEFAULT NULL COMMENT '定制网页名',
  `brandType` tinyint(1) DEFAULT NULL COMMENT '0-文字，1-照片',
  `brandPosition` tinyint(1) DEFAULT NULL COMMENT '0-顶部，1-底部，2-照片下方',
  `showAvatar` tinyint(1) DEFAULT NULL COMMENT '0-无图像，1-有图像',
  `avatarType` tinyint(1) DEFAULT NULL COMMENT '0-用户图片，1-固定图片，2-二维码',
  `customText` varchar(512) DEFAULT NULL COMMENT '自定义字段',
  `passableSTime` varchar(20) DEFAULT '08:30' COMMENT '访客通行开始时间',
  `passableETime` varchar(20) DEFAULT '19:00' COMMENT '访客通行结束时间',
  `epidemic` int(1) DEFAULT 0 COMMENT '疫情开关，0-关闭，1-开启',
  `satisfactionQuestionnaire` tinyint(1) DEFAULT 0 COMMENT '满意度调查开关，0-关,1-开',
  `dataKeepTime` int(2) NOT NULL DEFAULT 6 COMMENT '访客数据保留时间（月）',
  `appointmenProcessSwitch` tinyint(1) NOT NULL DEFAULT 1 COMMENT '邀请访客更新信息开关',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `qcv_user_extend` WRITE;
/*!40000 ALTER TABLE `qcv_user_extend` DISABLE KEYS */;

INSERT INTO `qcv_user_extend` (`userid`, `ddnotify`, `ddautosync`, `ddcorpid`, `ddcorpsecret`, `ddagentid`, `ddAppid`, `ddAppSccessSecret`, `qrcode`, `qrcodeSwitch`, `qrcodeType`, `custrequrl`, `bindingType`, `comeAgain`, `printType`, `cardType`, `cardSize`, `cardLogo`, `custSource`, `custWeb`, `custAddress`, `remark`, `smsCount`, `wxSmsCount`, `appSmsCount`, `unsubscribe`, `preRegisterSwitch`, `scaner`, `ivrNotify`, `ivrPrint`, `permissionSwitch`, `idCardSwitch`, `signOutSwitch`, `permanentCode`, `corpid`, `corpsecret`, `agentid`, `serviceID`, `securityID`, `securityKey`, `tempEditSwitch`, `blackListSwitch`, `processSwitch`, `secureProtocol`, `upDuty`, `offDuty`, `mailInvSmsSwitch`, `webInvSmsSwitch`, `qrMaxCount`, `qrMaxDuration`, `badgeMode`, `badgeCustom`, `brandType`, `brandPosition`, `showAvatar`, `avatarType`, `customText`, `passableSTime`, `passableETime`, `epidemic`, `satisfactionQuestionnaire`, `dataKeepTime`, `appointmenProcessSwitch`)
VALUES
	(143536107,0,0,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,1,1,1,1,1,1,1,NULL,NULL,NULL,0,0,2,0,1,1,0,0,1,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,NULL,'09:00','18:00',1,1,1,10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'08:30','19:00',0,0,6,1),
	(1436063934,0,0,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,1,1,1,1,1,1,1,NULL,NULL,NULL,0,0,0,0,0,0,0,0,1,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,0,NULL,'09:00','18:00',1,1,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'08:30','19:00',0,0,6,1),
	(1866922598,0,0,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,1,1,1,1,1,1,1,NULL,NULL,NULL,0,0,35,0,1,0,0,0,1,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1,0,'<p>嘎嘎嘎嘎嘎过过</p>','09:00','18:00',1,1,1,10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'08:30','19:00',0,0,6,1),
	(2147483647,1,0,'dingaba74be5d82e34c835c2f4657eb6378f',NULL,'1083826831','dingo3vl9s6iopf1hnly','cP2SAgBvs0MmMj5sgo2K4U635Y8rqZ5IgL0nBdzZtVMqSoUddWpNxuT3C-3fglW1','#',1,2,NULL,1,0,1,1,1,1,1,NULL,NULL,NULL,48,0,12603,0,1,1,0,0,1,0,1,NULL,'wxc0246bc1c25b4d62','qrWS3BxiCNockT5lATRdanyTSakOVNZJuIua4PGTwUc','2',NULL,'cli_a2f0855deafbd013','gEOYXJBrZ2UUFMmPDWtjWg4UaBaXYYAg',1,1,0,'<p class=\"MsoNormal\" align=\"center\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\"><span data-mce-style=\"font-size: 18px;\" style=\"font-size: 18px;\"><strong><span data-mce-style=\"font-family: 微软雅黑, sans-serif;\" style=\"font-family: 微软雅黑, sans-serif;\"><br data-mce-bogus=\"1\"></span></strong></span></p><p class=\"MsoNormal\" align=\"center\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\"><span data-mce-style=\"font-size: 18px;\" style=\"font-size: 18px;\"><strong><span data-mce-style=\"font-family: 微软雅黑, sans-serif;\" style=\"font-family: 微软雅黑, sans-serif;\"><br data-mce-bogus=\"1\"></span></strong></span></p><p class=\"MsoNormal\" align=\"center\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: center;\"><span data-mce-style=\"font-size: 18px;\" style=\"font-size: 18px;\"><strong><span data-mce-style=\"font-family: 微软雅黑, sans-serif;\" style=\"font-family: 微软雅黑, sans-serif;\">访客安全须知</span></strong></span></p><p class=\"MsoNormal\" align=\"center\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif; text-align: left;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px;\" style=\"font-size: 16px;\"><span data-mce-style=\"font-family: 微软雅黑, sans-serif;\" style=\"font-family: 微软雅黑, sans-serif;\">请仔细阅读并遵守以下信息及规定。</span></span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span style=\"font-family: \" data-mce-style=\"font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\"><span lang=\"EN-US\">1</span>、所有访客需签到登记并获取访客贴。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">2</span>、请全程将访客贴佩戴在显眼的位置，并易于辨识。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">3</span>、请不要随意转借您的访客贴给其他人使用。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">4</span>、在被访者接见你之前，请在前台或指定区域等候。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">5</span>、来访者只允许进入经授权参观的区域。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">6</span>、未经许可，不得在大楼内拍照、摄像或进行其他视听记录（包括手机记录）。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">7</span>、未经许可，不得将宠物带入大楼。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">8</span>、本大楼内禁止吸烟。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">9</span>、访客不以任何方式打扰员工及公司的正常运营。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">10</span>、遵循所有张贴在楼宇内的标示。<span lang=\"EN-US\">&nbsp;</span></span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">11</span>、如果您的访客贴遗失，请及时联系前台。</span></p><p class=\"MsoNormal\" data-mce-style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\" style=\"margin: 0cm 0cm 8pt; line-height: 15.6933px; font-size: 11pt; font-family: Calibri, sans-serif;\"><span data-mce-style=\"font-size: 16px; font-family: \'Microsoft YaHei\', \'Helvetica Neue\', \'PingFang SC\', sans-serif;\" style=\"font-size: 16px; font-family: \"><span lang=\"EN-US\">12</span>、请您在离开公司之前，及时将访客贴归还至前台。</span></p>','00:00','23:59',1,1,0,0,1,'ceibs',0,0,1,2,'呵呵呵呵','08:30','19:00',1,0,5,0);

/*!40000 ALTER TABLE `qcv_user_extend` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_users`;

CREATE TABLE `qcv_users` (
  `userid` int(11) unsigned NOT NULL,
  `username` varchar(80) CHARACTER SET utf8 NOT NULL,
  `password` varchar(50) CHARACTER SET utf8 NOT NULL,
  `email` varchar(80) CHARACTER SET utf8 NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8 NOT NULL,
  `company` varchar(100) CHARACTER SET utf8 NOT NULL,
  `regDate` datetime NOT NULL,
  `loginDate` datetime DEFAULT NULL,
  `refreshDate` datetime DEFAULT NULL,
  `logo` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
  `emailType` tinyint(3) DEFAULT NULL,
  `emailAccount` varchar(80) CHARACTER SET utf8 DEFAULT NULL,
  `emailPwd` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `smtp` varchar(80) CHARACTER SET utf8 DEFAULT NULL,
  `smtpPort` int(8) DEFAULT NULL,
  `smsNotify` tinyint(3) NOT NULL DEFAULT 0,
  `exchange` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `domain` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `rtxAuto` tinyint(3) NOT NULL DEFAULT 0,
  `rtxip` varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  `rtxport` int(8) DEFAULT NULL,
  `ssl` tinyint(3) DEFAULT NULL,
  `userType` tinyint(3) DEFAULT NULL,
  `token` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `backgroundPic` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
  `msgNotify` tinyint(3) NOT NULL DEFAULT 1,
  `digest` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `themecolor` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `defaultPhoto` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
  `refreshCount` tinyint(3) NOT NULL DEFAULT 0,
  `cardText` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `cardPic` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `expireDate` datetime DEFAULT NULL,
  `serverLocation` tinyint(3) NOT NULL DEFAULT 0,
  `subAccount` tinyint(3) NOT NULL DEFAULT 0,
  `faceScaner` tinyint(3) NOT NULL DEFAULT 0 COMMENT '面部识别',
  `webwalkins` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'web临时预约',
  `preExtendTime` int(11) NOT NULL DEFAULT 0 COMMENT '预约扩展时间(之前)',
  `latExtendTime` int(11) NOT NULL DEFAULT 0 COMMENT '预约扩展时间(之后)',
  `keyExpireTime` int(11) NOT NULL DEFAULT 15 COMMENT '开门钥匙有效期',
  `leaveExpiryTime` int(11) DEFAULT 0 COMMENT '离开时间期限',
  `questionnaireSwitch` tinyint(1) DEFAULT 0 COMMENT '答题开关 0 关 1开',
  `cryptoperiod` int(11) NOT NULL DEFAULT 0 COMMENT '密码有效期',
  `escapeClause` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fsNotify` tinyint(1) DEFAULT 0 COMMENT '飞书开关 0关 1开',
  `wxBusNotify` tinyint(1) DEFAULT 0 COMMENT '企业微信开关',
  `notifyType` tinyint(1) DEFAULT 0 COMMENT '通知类型0 全部 1只用一个',
  `appletCarouselPic` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序轮播图',
  `carouselPic` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `index_email` (`email`),
  UNIQUE KEY `index_company` (`company`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

LOCK TABLES `qcv_users` WRITE;
/*!40000 ALTER TABLE `qcv_users` DISABLE KEYS */;

INSERT INTO `qcv_users` (`userid`, `username`, `password`, `email`, `phone`, `company`, `regDate`, `loginDate`, `refreshDate`, `logo`, `emailType`, `emailAccount`, `emailPwd`, `smtp`, `smtpPort`, `smsNotify`, `exchange`, `domain`, `rtxAuto`, `rtxip`, `rtxport`, `ssl`, `userType`, `token`, `backgroundPic`, `msgNotify`, `digest`, `themecolor`, `defaultPhoto`, `refreshCount`, `cardText`, `cardPic`, `expireDate`, `serverLocation`, `subAccount`, `faceScaner`, `webwalkins`, `preExtendTime`, `latExtendTime`, `keyExpireTime`, `leaveExpiryTime`, `questionnaireSwitch`, `cryptoperiod`, `escapeClause`, `fsNotify`, `wxBusNotify`, `notifyType`, `appletCarouselPic`, `carouselPic`)
VALUES
	(1436063934,'susie','0b68518adf024c7176ba1ace57ee9a63','susie.sun@jototech.cn','13951547281','JOTO Tech','2020-12-08 12:10:01',NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0,NULL,NULL,0,NULL,NULL,NULL,3,NULL,NULL,1,NULL,NULL,'http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUay2AMMBSAAJxxMJj124287.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUa7yADG32AAI_PZSKDIk170.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUbBSAGqZXAAOuhcLLS5M998.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUbG2AUFEvAAN5192X5Fs058.png',0,NULL,NULL,'2315-11-12 12:10:01',0,0,0,0,0,0,15,0,0,0,NULL,0,0,0,NULL,NULL),
	(1866922598,'lana','2979e0bbd0f161457c28637579084560','1520949673@qq.com','18851650702','lana科技','2020-05-26 13:20:35','2020-12-18 15:18:35','2020-08-18 16:40:26','https://reg.cdifs.cn/group1/M00/00/0A/rBEAAl8lKeuAUlobAAClwHSGK6g324.png',0,NULL,NULL,NULL,NULL,0,NULL,NULL,0,NULL,NULL,NULL,3,NULL,'http://www.coolvisit.top/group1/M00/00/4D/rBHIpl7V2iGAbrbhAADAGxEjskU950.jpg,http://cdifs.coolvisit.top/group1/M00/00/01/rBEAAl8IDTmASSt0AACQl7tH1Lw753.png,http://cdifs.coolvisit.top/group1/M00/00/01/rBEAAl8IDT6AN_JEAAC6Yf6C4lM433.png,http://cdifs.coolvisit.top/group1/M00/00/01/rBEAAl8IDUWAD2doAACIpDsDzMc537.png,http://cdifs.coolvisit.top/group1/M00/00/01/rBEAAl8IDUqAXdSSAACpF-tuyCk151.png,http://cdifs.coolvisit.top/group1/M00/00/01/rBEAAl8IDU-AFS43AACZJ1uYxqY339.png',1,'0ee1611f63a2abbceeb22d50f6d3154c','#C0B193','http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUay2AMMBSAAJxxMJj124287.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUa7yADG32AAI_PZSKDIk170.png,http://www.coolvisit.top/group1/M00/00/4D/rBHIpl7VxHuAE6F-AABS92qwzpY933.jpg,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUbG2AUFEvAAN5192X5Fs058.png',0,NULL,NULL,'2315-11-12 13:20:35',0,1,0,0,0,0,15,0,0,0,'<p>请认真阅读免责条款，并点击确认<br data-mce-bogus=\"1\"></p>',0,0,0,NULL,NULL),
	(2147483647,'来访通','2979e0bbd0f161457c28637579084560','visitor@coolvisit.com','13764467846','来访通','2020-02-06 16:37:35','2022-05-23 11:11:24','2022-05-22 23:33:31',NULL,3,NULL,NULL,NULL,0,1,NULL,NULL,0,NULL,NULL,NULL,3,NULL,'https://dev.coolvisit.top/group1/M00/00/01/rBEAAmEkTUCAAef4AABuoFSq9co98.jpeg,https://dev.coolvisit.top/group1/M00/00/02/rBEAAmFviV6AEDfnAACoqBsf4tM891.jpg,https://dev.coolvisit.top/group1/M00/00/04/rBEAAmHOrcaAes8zACOHPdH9E50479.jpg',1,'','#00A0D9','http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUay2AMMBSAAJxxMJj124287.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUa7yADG32AAI_PZSKDIk170.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUbG2AUFEvAAN5192X5Fs058.png,http://www.coolvisit.com/group1/M00/00/3D/Cv7fv1aUbBSAGqZXAAOuhcLLS5M998.png',0,'来访通',NULL,'2315-11-12 16:37:35',0,0,1,0,0,0,15,5,0,0,'<p data-mce-style=\"text-align: center;\" style=\"text-align: center;\"><strong><span data-mce-style=\"font-family: 宋体; font-size: 24px; text-indent: 28px;\" style=\"font-family: 宋体; font-size: 24px; text-indent: 28px;\">安全协议</span></strong></p><p><span style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\" data-mce-style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\">1. 为确保您个人信息的安全，我们使用安全技术和程序，以防信息的丢失、不当使用、未经授权阅览或披露。您的个人信息存储在阿里云端，秉承安全性、隐私与管控、合规性及透明度四项原则。尽管已采取上述合理有效措施，并遵守了相关法律规定的要求标准，但请您理解，在互联网领域，由于技术的限制以及可能存在的各种恶意手段，即便竭尽所能加强安全措施，也不可能始终保证信息百分之百的安全，成都</span><span lang=\"EN-US\" style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\" data-mce-style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\">IFS</span><span style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\" data-mce-style=\"font-family: 宋体; font-size: 12px; text-indent: 28px;\">亦会严格按照政策内容使用和保护您的个人信息，感谢您的信任。</span></p>',0,1,0,'https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1hxWAPuU7AAWvtH_SgqU950.png,https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1hxuAH-ttAATz8Ik52NI390.png,https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1hyGAXa7tAAcjf6ZtQ8s045.png,https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1hyuAJd0UAAYBoeoAXgg324.png,https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1hzKAcyTZAAToHxO3_Mo496.png,https://dev.coolvisit.top/group1/M00/00/01/rBEAAmE1h0qAQtgHAASty6BOclQ030.png,https://dev.coolvisit.top/group1/M00/00/04/rBEAAmHOrnaAAG-CACOHPdH9E50059.jpg','[{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/15/rBEABmHbi1aAbWA1AAOBwBmTzpY863.png\",\"linkUrl\":\"http://test4.coolvisit.top/\",\"status\":0},{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/16/rBEABmHbjgaAFwVtAABFjxG7HCM030.png\",\"linkUrl\":\"http://www.coolvisit.com/\",\"status\":1},{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/15/rBEABmHbi_WAEvjWAAAeMIOit6I472.png\",\"linkUrl\":\"http://master.coolvisit.com/\",\"status\":0},{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/16/rBEABmHbjq-AF39NAABRtUfFKMc163.png\",\"linkUrl\":\"https://tower.im/\",\"status\":1},{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/15/rBEABmHbjceAVP1BAALtr7ilmyM904.png\",\"linkUrl\":\"https://saas.coolvisit.top/\",\"status\":1},{\"picUrl\":\"http://test4.coolvisit.top/group2/M00/00/15/rBEABmHbigyAEx50AAWHM7UZeJg225.png\",\"linkUrl\":\"https://www.baidu.com/\",\"status\":1}]');

/*!40000 ALTER TABLE `qcv_users` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_visitor_extend
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_visitor_extend`;

CREATE TABLE `qcv_visitor_extend` (
  `eid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `userid` int(11) unsigned NOT NULL,
  `displayName` varchar(50) CHARACTER SET utf8 NOT NULL,
  `fieldName` varchar(50) CHARACTER SET utf8 NOT NULL,
  `inputType` varchar(10) CHARACTER SET utf8 NOT NULL,
  `inputValue` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `inputOrder` int(3) DEFAULT NULL,
  `required` int(3) DEFAULT NULL,
  `placeholder` varchar(10) CHARACTER SET utf8 DEFAULT NULL,
  `isDisplay` tinyint(1) NOT NULL DEFAULT 1,
  `eType` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`eid`),
  UNIQUE KEY `userid` (`userid`,`fieldName`,`eType`),
  KEY `index_userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `qcv_visitor_extend` WRITE;
/*!40000 ALTER TABLE `qcv_visitor_extend` DISABLE KEYS */;

INSERT INTO `qcv_visitor_extend` (`eid`, `userid`, `displayName`, `fieldName`, `inputType`, `inputValue`, `inputOrder`, `required`, `placeholder`, `isDisplay`, `eType`)
VALUES
	(1,1866922598,'日期#Date','appointmentDate','date','',1,11,'访客类型',15,'普通访客#Normal visitor'),
	(2,1866922598,'访客姓名#Visitor name','name','text','',2,11,'访客类型',15,'普通访客#Normal visitor'),
	(3,1866922598,'访客手机号#Visitor phone number','phone','text','',3,11,'访客类型',15,'普通访客#Normal visitor'),
	(4,1866922598,'拜访事由#Cause of visit','visitType','button','私人,其他,面试#Interview,商务#Business',4,11,'访客类型',15,'普通访客#Normal visitor'),
	(5,1866922598,'您要拜访的人','empid','text','',5,3,'访客类型',3,'普通访客#Normal visitor'),
	(6,1866922598,'门禁权限#Access control','access','access','',6,1,'访客类型',5,'普通访客#Normal visitor'),
	(7,1866922598,'会面内容','meetContent','text','',7,0,'访客类型',1,'普通访客#Normal visitor'),
	(8,1866922598,'会面地点','meetAddress','text','',8,0,'访客类型',1,'普通访客#Normal visitor'),
	(9,1866922598,'权限时间(天)','qrcodeConf','text','',9,1,'访客类型',5,'普通访客#Normal visitor'),
	(10,1866922598,'身份证号','cardId','text','',10,1,'访客类型',1,'普通访客#Normal visitor'),
	(11,1866922598,'车牌号','plateNum','text','',11,1,'访客类型',1,'普通访客#Normal visitor'),
	(12,1866922598,'邮箱','email','text','',12,0,'访客类型',7,'普通访客#Normal visitor'),
	(13,1866922598,'来访公司','vcompany','text','',13,1,'访客类型',3,'普通访客#Normal visitor'),
	(14,1866922598,'同行人数','peopleCount','text','1',14,0,'访客类型',3,'普通访客#Normal visitor'),
	(15,1866922598,'签入门岗','gatein','text','',15,0,'访客类型',1,'普通访客#Normal visitor'),
	(16,1866922598,'签出门岗','gateout','text','',16,0,'访客类型',1,'普通访客#Normal visitor'),
	(17,1866922598,'签入警卫','guardin','text','',17,0,'访客类型',1,'普通访客#Normal visitor'),
	(18,1866922598,'签出警卫','guardout','text','',18,0,'访客类型',1,'普通访客#Normal visitor'),
	(19,1866922598,'备注','remark','text','',19,0,'访客类型',7,'普通访客#Normal visitor'),
	(20,1866922598,'邀请正文','invitText','text','',20,0,'访客类型',1,'普通访客#Normal visitor'),
	(21,1866922598,'结束时间','visitorEndDate','date','',21,1,'访客类型',1,'普通访客#Normal visitor'),
	(22,1866922598,'拜访区域','building','button','',22,0,'访客类型',0,'普通访客#Normal visitor'),
	(23,1866922598,'门岗设置','gateType','button','',23,0,'访客类型',0,'普通访客#Normal visitor'),
	(24,1866922598,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'普通访客#Normal visitor'),
	(25,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'提货'),
	(26,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'提货'),
	(27,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'提货'),
	(28,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'提货'),
	(29,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'提货'),
	(30,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'提货'),
	(31,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'发货'),
	(32,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'发货'),
	(33,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'发货'),
	(34,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'发货'),
	(35,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'发货'),
	(36,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'发货'),
	(37,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'上门取货'),
	(38,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'上门取货'),
	(39,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'上门取货'),
	(40,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'上门取货'),
	(41,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'上门取货'),
	(42,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'上门取货'),
	(43,2147483647,'数量','input_6800','text','',2,0,'货物信息',1,'上门取货'),
	(44,2147483647,'物品','input_5580','text','',3,0,'货物信息',1,'上门取货'),
	(45,2147483647,'工作证','input_5930','button','黄牌,红牌,绿牌',1,0,'其他信息',1,'上门取货'),
	(46,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'配送'),
	(47,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'配送'),
	(48,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'配送'),
	(49,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'配送'),
	(50,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'配送'),
	(51,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'配送'),
	(52,2147483647,'42311234','input_8550','text','',1,0,'物流信息',1,'22'),
	(53,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'22'),
	(54,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'22'),
	(55,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'22'),
	(56,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'22'),
	(57,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'22'),
	(58,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',1,'22'),
	(59,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'货物配送'),
	(60,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'货物配送'),
	(61,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'货物配送'),
	(62,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'货物配送'),
	(63,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'货物配送'),
	(64,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'货物配送'),
	(65,2147483647,'姓名','sname','text','',1,1,'司机信息',1,'送水'),
	(66,2147483647,'手机号码','mobile','text','',2,1,'司机信息',1,'送水'),
	(67,2147483647,'身份证号','cardid','text','',3,1,'司机信息',1,'送水'),
	(68,2147483647,'车牌','plateNum','text','',1,1,'车辆信息',1,'送水'),
	(69,2147483647,'车辆签出照片','vehicleLeavingPhoto','text','',3,1,'车辆信息',1,'送水'),
	(70,2147483647,'车辆签到照片','vehicleAccessPhoto','text','',4,0,'车辆信息',0,'送水'),
	(71,1436063934,'您的姓名','name','text','',1,1,'',0,NULL),
	(72,1436063934,'拜访事由','visitType','button','面试,商务,私人,其他',2,1,'',0,NULL),
	(73,1436063934,'您要拜访的人','empid','text','',3,1,'',0,NULL),
	(74,1436063934,'电话','phone','text','',4,1,'',0,NULL),
	(75,2147483647,'日期','appointmentDate','date','',1,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(76,2147483647,'您的姓名','name','text','',2,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(77,2147483647,'您的电话','phone','text','',3,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(78,2147483647,'拜访事由','visitType','button','面试,商务,私人,其他',4,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(79,2147483647,'您要拜访的人','empid','text','',5,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(80,2147483647,'门禁权限','access','access','',6,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(81,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(82,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(83,2147483647,'权限时间(天)','qrcodeConf','text','',9,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(84,2147483647,'身份证号','cardId','text','',10,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(85,2147483647,'车牌号','plateNum','text','',11,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(86,2147483647,'邮箱','email','text','',12,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(87,2147483647,'来访公司','vcompany','text','',13,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(88,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(89,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(90,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(91,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(92,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(93,2147483647,'备注','remark','text','',19,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(94,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',7,'南京访客乐网络科技有限公司'),
	(95,2147483647,'结束时间','visitorEndDate','date','',21,1,'访客类型',7,'南京访客乐网络科技有限公司'),
	(96,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'南京访客乐网络科技有限公司'),
	(97,2147483647,'门岗设置','gateType','button','',23,0,'访客类型',0,'南京访客乐网络科技有限公司'),
	(98,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'南京访客乐网络科技有限公司'),
	(99,2147483647,'日期','appointmentDate','date','',1,1,'访客类型',7,'1232'),
	(100,2147483647,'您的姓名','name','text','',2,1,'访客类型',7,'1232'),
	(101,2147483647,'您的电话','phone','text','',3,1,'访客类型',7,'1232'),
	(102,2147483647,'拜访事由','visitType','button','面试,商务,私人,其他',4,1,'访客类型',7,'1232'),
	(103,2147483647,'您要拜访的人','empid','text','',5,1,'访客类型',7,'1232'),
	(104,2147483647,'门禁权限','access','access','',6,1,'访客类型',7,'1232'),
	(105,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',7,'1232'),
	(106,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',7,'1232'),
	(107,2147483647,'权限时间(天)','qrcodeConf','text','',9,1,'访客类型',7,'1232'),
	(108,2147483647,'身份证号','cardId','text','',10,1,'访客类型',7,'1232'),
	(109,2147483647,'车牌号','plateNum','text','',11,1,'访客类型',7,'1232'),
	(110,2147483647,'邮箱','email','text','',12,0,'访客类型',7,'1232'),
	(111,2147483647,'来访公司','vcompany','text','',13,1,'访客类型',7,'1232'),
	(112,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',7,'1232'),
	(113,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',7,'1232'),
	(114,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',7,'1232'),
	(115,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',7,'1232'),
	(116,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',7,'1232'),
	(117,2147483647,'备注','remark','text','',19,0,'访客类型',7,'1232'),
	(118,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',7,'1232'),
	(119,2147483647,'结束时间','visitorEndDate','date','',21,1,'访客类型',7,'1232'),
	(120,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'1232'),
	(121,2147483647,'门岗设置','gateType','button','',23,0,'访客类型',0,'1232'),
	(122,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'1232'),
	(123,2147483647,'日期','appointmentDate','date','',1,1,'访客类型',7,'333'),
	(124,2147483647,'您的姓名','name','text','',2,1,'访客类型',7,'333'),
	(125,2147483647,'您的电话','phone','text','',3,1,'访客类型',7,'333'),
	(126,2147483647,'拜访事由','visitType','button','面试,商务,私人,其他',4,1,'访客类型',7,'333'),
	(127,2147483647,'您要拜访的人','empid','text','',5,1,'访客类型',7,'333'),
	(128,2147483647,'门禁权限','access','access','',6,1,'访客类型',7,'333'),
	(129,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',7,'333'),
	(130,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',7,'333'),
	(131,2147483647,'权限时间(天)','qrcodeConf','text','',9,1,'访客类型',7,'333'),
	(132,2147483647,'身份证号','cardId','text','',10,1,'访客类型',7,'333'),
	(133,2147483647,'车牌号','plateNum','text','',11,1,'访客类型',7,'333'),
	(134,2147483647,'邮箱','email','text','',12,0,'访客类型',7,'333'),
	(135,2147483647,'来访公司','vcompany','text','',13,1,'访客类型',7,'333'),
	(136,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',7,'333'),
	(137,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',7,'333'),
	(138,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',7,'333'),
	(139,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',7,'333'),
	(140,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',7,'333'),
	(141,2147483647,'备注','remark','text','',19,0,'访客类型',7,'333'),
	(142,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',7,'333'),
	(143,2147483647,'结束时间','visitorEndDate','date','',21,1,'访客类型',7,'333'),
	(144,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'333'),
	(145,2147483647,'门岗设置','gateType','button','',23,0,'访客类型',0,'333'),
	(146,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'333'),
	(7177,2147483647,'拜访时间#Time','appointmentDate','date','',1,3,'访客类型',3,'参会访客'),
	(7178,2147483647,'参会人姓名#Name','name','text','',2,15,'访客类型',15,'参会访客'),
	(7179,2147483647,'参会人手机号#Phone','phone','text','',3,4,'访客类型',15,'参会访客'),
	(7180,2147483647,'拜访事由#Reason','visitType','button','参会#Meeting',4,14,'访客类型',14,'参会访客'),
	(7181,2147483647,'您要拜访的人','empid','text','',5,0,'访客类型',0,'参会访客'),
	(7182,2147483647,'门禁权限#Acces','access','access','',6,0,'访客类型',16,'参会访客'),
	(7183,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',0,'参会访客'),
	(7184,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',0,'参会访客'),
	(7185,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,1,'访客类型',3,'参会访客'),
	(7186,2147483647,'身份证号','cardId','text','',10,0,'访客类型',0,'参会访客'),
	(7187,2147483647,'车牌号','plateNum','text','',11,0,'访客类型',0,'参会访客'),
	(7188,2147483647,'访客邮箱#Email','email','text','',12,0,'访客类型',11,'参会访客'),
	(7189,2147483647,'参会人公司#Company','vcompany','text','',13,4,'访客类型',4,'参会访客'),
	(7190,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',0,'参会访客'),
	(7191,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',0,'参会访客'),
	(7192,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',0,'参会访客'),
	(7193,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',0,'参会访客'),
	(7194,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',0,'参会访客'),
	(7195,2147483647,'备注#Remarks','remark','text','',19,0,'访客类型',27,'参会访客'),
	(7196,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',0,'参会访客'),
	(7197,2147483647,'结束时间','visitorEndDate','date','',21,0,'访客类型',0,'参会访客'),
	(7198,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'参会访客'),
	(7199,2147483647,'门岗设置','gateType','button','1',23,4,'访客类型',4,'参会访客'),
	(7200,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'参会访客'),
	(7201,2147483647,'是否vip#VIP','input_4420','button','是,否',25,0,'访客类型',31,'参会访客'),
	(7202,2147483647,'通行方式','accessType','setting','0',26,0,'',0,'参会访客'),
	(7203,2147483647,'多天进出','moreDays','setting','0',27,0,'',0,'参会访客'),
	(7204,2147483647,'当天多次进出','moreTimes','setting','0',28,0,'',0,'参会访客'),
	(7205,2147483647,'提前预约天数','daysAdvance','setting','3',29,0,'',0,'参会访客'),
	(10808,2147483647,'拜访时间#Time','appointmentDate','date','',1,7,'访客类型',7,'ceshi'),
	(10809,2147483647,'访客姓名#Name','name','text','',2,15,'访客类型',15,'ceshi'),
	(10810,2147483647,'访客手机号#Phone','phone','text','',3,0,'访客类型',15,'ceshi'),
	(10811,2147483647,'拜访事由#Reason','visitType','button','面试,商务,私人,其他',4,14,'访客类型',14,'ceshi'),
	(10812,2147483647,'您要拜访的人','empid','text','',5,8,'访客类型',8,'ceshi'),
	(10813,2147483647,'门禁权限#Acces','access','access','',6,0,'访客类型',16,'ceshi'),
	(10814,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',0,'ceshi'),
	(10815,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',0,'ceshi'),
	(10816,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,1,'访客类型',7,'ceshi'),
	(10817,2147483647,'身份证号','cardId','text','',10,0,'访客类型',0,'ceshi'),
	(10818,2147483647,'车牌号','plateNum','text','',11,0,'访客类型',0,'ceshi'),
	(10819,2147483647,'访客邮箱#Email','email','text','',12,0,'访客类型',15,'ceshi'),
	(10820,2147483647,'来访公司','vcompany','text','',13,0,'访客类型',0,'ceshi'),
	(10821,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',0,'ceshi'),
	(10822,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',0,'ceshi'),
	(10823,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',0,'ceshi'),
	(10824,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',0,'ceshi'),
	(10825,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',0,'ceshi'),
	(10826,2147483647,'备注#Remarks','remark','text','',19,0,'访客类型',31,'ceshi'),
	(10827,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',0,'ceshi'),
	(10828,2147483647,'结束时间','visitorEndDate','date','',21,0,'访客类型',0,'ceshi'),
	(10829,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'ceshi'),
	(10830,2147483647,'门岗设置','gateType','button','',23,4,'访客类型',4,'ceshi'),
	(10831,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'ceshi'),
	(10832,2147483647,'通行方式','accessType','setting','0',25,0,'',0,'ceshi'),
	(10833,2147483647,'多天进出','moreDays','setting','0',26,0,'',0,'ceshi'),
	(10834,2147483647,'当天多次进出','moreTimes','setting','0',27,0,'',0,'ceshi'),
	(10835,2147483647,'拜访时间#Time','appointmentDate','date','',1,7,'访客类型',7,'测试2'),
	(10836,2147483647,'访客姓名#Name','name','text','',2,15,'访客类型',15,'测试2'),
	(10837,2147483647,'访客手机号#Phone','phone','text','',3,0,'访客类型',15,'测试2'),
	(10838,2147483647,'拜访事由#Reason','visitType','button','面试,商务,私人,其他',4,14,'访客类型',14,'测试2'),
	(10839,2147483647,'您要拜访的人','empid','text','',5,0,'访客类型',8,'测试2'),
	(10840,2147483647,'门禁权限#Acces','access','access','',6,0,'访客类型',16,'测试2'),
	(10841,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',0,'测试2'),
	(10842,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',0,'测试2'),
	(10843,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,1,'访客类型',7,'测试2'),
	(10844,2147483647,'身份证号','cardId','text','',10,0,'访客类型',0,'测试2'),
	(10845,2147483647,'车牌号','plateNum','text','',11,0,'访客类型',0,'测试2'),
	(10846,2147483647,'访客邮箱#Email','email','text','',12,0,'访客类型',15,'测试2'),
	(10847,2147483647,'来访公司','vcompany','text','',13,0,'访客类型',0,'测试2'),
	(10848,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',0,'测试2'),
	(10849,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',0,'测试2'),
	(10850,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',0,'测试2'),
	(10851,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',0,'测试2'),
	(10852,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',0,'测试2'),
	(10853,2147483647,'备注#Remarks','remark','text','',19,0,'访客类型',31,'测试2'),
	(10854,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',0,'测试2'),
	(10855,2147483647,'结束时间','visitorEndDate','date','',21,0,'访客类型',0,'测试2'),
	(10856,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'测试2'),
	(10857,2147483647,'门岗设置','gateType','button','',23,4,'访客类型',4,'测试2'),
	(10858,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'测试2'),
	(10859,2147483647,'通行方式','accessType','setting','0',25,0,'',0,'测试2'),
	(10860,2147483647,'多天进出','moreDays','setting','0',26,0,'',0,'测试2'),
	(10861,2147483647,'当天多次进出','moreTimes','setting','0',27,0,'',0,'测试2'),
	(10862,2147483647,'可以提前预约天数','daysAdvance','setting','1',28,0,'',0,'测试2'),
	(10863,2147483647,'拜访时间#Time','appointmentDate','date','',1,7,'访客类型',7,'111'),
	(10864,2147483647,'访客姓名#Name','name','text','',2,15,'访客类型',15,'111'),
	(10865,2147483647,'访客手机号#Phone','phone','text','',3,0,'访客类型',15,'111'),
	(10866,2147483647,'拜访事由#Reason','visitType','button','面试,商务,私人,其他',4,14,'访客类型',14,'111'),
	(10867,2147483647,'您要拜访的人','empid','text','',5,0,'访客类型',8,'111'),
	(10868,2147483647,'门禁权限#Acces','access','access','',6,0,'访客类型',16,'111'),
	(10869,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',0,'111'),
	(10870,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',0,'111'),
	(10871,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,1,'访客类型',7,'111'),
	(10872,2147483647,'身份证号','cardId','text','',10,0,'访客类型',0,'111'),
	(10873,2147483647,'车牌号','plateNum','text','',11,0,'访客类型',0,'111'),
	(10874,2147483647,'访客邮箱#Email','email','text','',12,0,'访客类型',15,'111'),
	(10875,2147483647,'来访公司','vcompany','text','',13,0,'访客类型',0,'111'),
	(10876,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',0,'111'),
	(10877,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',0,'111'),
	(10878,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',0,'111'),
	(10879,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',0,'111'),
	(10880,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',0,'111'),
	(10881,2147483647,'备注#Remarks','remark','text','',19,0,'访客类型',31,'111'),
	(10882,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',0,'111'),
	(10883,2147483647,'结束时间','visitorEndDate','date','',21,0,'访客类型',0,'111'),
	(10884,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'111'),
	(10885,2147483647,'门岗设置','gateType','button','',23,4,'访客类型',4,'111'),
	(10886,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'111'),
	(10887,2147483647,'通行方式','accessType','setting','0,1',25,0,'',0,'111'),
	(10888,2147483647,'多天进出','moreDays','setting','0',26,0,'',0,'111'),
	(10889,2147483647,'当天多次进出','moreTimes','setting','0',27,0,'',0,'111'),
	(10890,2147483647,'可以提前预约天数','daysAdvance','setting','123',28,0,'',0,'111'),
	(12231,2147483647,'拜访时间#Time','appointmentDate','date','',1,7,'访客类型',23,'其他访客'),
	(12232,2147483647,'访客姓名#Name','name','text','',2,15,'访客类型',31,'其他访客'),
	(12233,2147483647,'访客手机号#Phone','phone','text','',3,2,'访客类型',15,'其他访客'),
	(12234,2147483647,'拜访事由#Reason','visitType','button','面试,商务,私人,其他',4,14,'访客类型',14,'其他访客'),
	(12235,2147483647,'您要拜访的人','empid','text','',5,8,'访客类型',8,'其他访客'),
	(12236,2147483647,'门禁权限#Acces','access','access','',6,0,'访客类型',16,'其他访客'),
	(12237,2147483647,'会面内容','meetContent','text','',7,0,'访客类型',0,'其他访客'),
	(12238,2147483647,'会面地点','meetAddress','text','',8,0,'访客类型',0,'其他访客'),
	(12239,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,1,'访客类型',7,'其他访客'),
	(12240,2147483647,'身份证号','cardId','text','',10,0,'访客类型',0,'其他访客'),
	(12241,2147483647,'车牌号','plateNum','text','',11,0,'访客类型',32,'其他访客'),
	(12242,2147483647,'访客邮箱#Email','email','text','',12,0,'访客类型',15,'其他访客'),
	(12243,2147483647,'来访公司','vcompany','text','',13,0,'访客类型',0,'其他访客'),
	(12244,2147483647,'同行人数','peopleCount','text','1',14,0,'访客类型',0,'其他访客'),
	(12245,2147483647,'签入门岗','gatein','text','',15,0,'访客类型',0,'其他访客'),
	(12246,2147483647,'签出门岗','gateout','text','',16,0,'访客类型',0,'其他访客'),
	(12247,2147483647,'签入警卫','guardin','text','',17,0,'访客类型',0,'其他访客'),
	(12248,2147483647,'签出警卫','guardout','text','',18,0,'访客类型',0,'其他访客'),
	(12249,2147483647,'备注#Remarks','remark','text','',19,0,'访客类型',31,'其他访客'),
	(12250,2147483647,'邀请正文','invitText','text','',20,0,'访客类型',0,'其他访客'),
	(12251,2147483647,'结束时间','visitorEndDate','date','',21,0,'访客类型',0,'其他访客'),
	(12252,2147483647,'拜访区域','building','button','',22,0,'访客类型',0,'其他访客'),
	(12253,2147483647,'门岗设置','gateType','button','',23,4,'访客类型',20,'其他访客'),
	(12254,2147483647,'发卡日期','pCardDate','date','',24,0,'访客类型',0,'其他访客'),
	(12255,2147483647,'通行方式','accessType','setting','0',25,0,'',0,'其他访客'),
	(12256,2147483647,'多天进出','moreDays','setting','0',26,0,'',0,'其他访客'),
	(12257,2147483647,'当天多次进出','moreTimes','setting','0',27,0,'',0,'其他访客'),
	(12258,2147483647,'提前预约天数','daysAdvance','setting','1',28,0,'',0,'其他访客'),
	(14979,2147483647,'当天多次进出','moreTimes','setting','1',1,0,'',0,'普通访客#Normal Visitor'),
	(14980,2147483647,'访客的姓名#Name','name','text','',2,15,'访客类型',15,'普通访客#Normal Visitor'),
	(14981,2147483647,'访客手机号#Phone','phone','text','',3,15,'访客类型',15,'普通访客#Normal Visitor'),
	(14982,2147483647,'拜访时间#Time','appointmentDate','date','',4,15,'访客类型',15,'普通访客#Normal Visitor'),
	(14983,2147483647,'门禁权限#Acces','access','access','T1-16F,T2-32F,T3-42F',5,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14984,2147483647,'您要拜访的人','empid','text','',6,10,'访客类型',10,'普通访客#Normal Visitor'),
	(14985,2147483647,'拜访事由#Reason','visitType','button','商务#Business,面试#Interview',7,14,'访客类型',14,'普通访客#Normal Visitor'),
	(14986,2147483647,'访客邮箱#Email','email','text','',8,8,'访客类型',9,'普通访客#Normal Visitor'),
	(14987,2147483647,'有效期(天)','qrcodeConf','button','1,2,3,4,5,6,7,8,9,10',9,5,'访客类型',13,'普通访客#Normal Visitor'),
	(14988,2147483647,'会议主题','meetContent','text','',10,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14989,2147483647,'会议时间','meetAddress','text','',11,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14990,2147483647,'证件类型','input_6120','text','',12,1,'访客类型',1,'普通访客#Normal Visitor'),
	(14991,2147483647,'身份证号','cardId','text','',13,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14992,2147483647,'车牌号','plateNum','text','',14,0,'访客类型',12,'普通访客#Normal Visitor'),
	(14993,2147483647,'来访公司','vcompany','text','',15,4,'访客类型',14,'普通访客#Normal Visitor'),
	(14994,2147483647,'同行人数','peopleCount','text','1',16,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14995,2147483647,'签入门岗','gatein','text','',17,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14996,2147483647,'签出门岗','gateout','text','',18,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14997,2147483647,'签入警卫','guardin','text','',19,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14998,2147483647,'签出警卫','guardout','text','',20,0,'访客类型',0,'普通访客#Normal Visitor'),
	(14999,2147483647,'备注#Remarks','remark','text','',21,0,'访客类型',31,'普通访客#Normal Visitor'),
	(15000,2147483647,'邀请正文','invitText','text','',22,0,'访客类型',0,'普通访客#Normal Visitor'),
	(15001,2147483647,'结束时间','visitorEndDate','date','',23,0,'访客类型',0,'普通访客#Normal Visitor'),
	(15002,2147483647,'拜访区域','building','button','',24,0,'访客类型',0,'普通访客#Normal Visitor'),
	(15003,2147483647,'门岗设置','gateType','button','1',25,16,'访客类型',16,'普通访客#Normal Visitor'),
	(15004,2147483647,'健康码','followcode','img','',26,0,'请填写随申码',32,'普通访客#Normal Visitor'),
	(15005,2147483647,'行程码','tripcode','img','',27,0,'请填写行程码',32,'普通访客#Normal Visitor'),
	(15006,2147483647,'访客人脸','visitorFace','text','',28,0,'访客类型',0,'普通访客#Normal Visitor'),
	(15007,2147483647,'核酸报告','nucleicAcidReport','img','',29,0,'访客类型',32,'普通访客#Normal Visitor'),
	(15008,2147483647,'通行方式','accessType','setting','1,0',30,0,'',0,'普通访客#Normal Visitor'),
	(15009,2147483647,'多天进出','moreDays','setting','1',31,0,'',0,'普通访客#Normal Visitor'),
	(15010,2147483647,'提前预约天数','daysAdvance','setting','2',32,0,'',0,'普通访客#Normal Visitor');

/*!40000 ALTER TABLE `qcv_visitor_extend` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table qcv_visitor_type
# ------------------------------------------------------------

DROP TABLE IF EXISTS `qcv_visitor_type`;

CREATE TABLE `qcv_visitor_type` (
  `tid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `vType` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `povDays` int(11) NOT NULL DEFAULT 1,
  `qid` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` tinyint(1) NOT NULL DEFAULT 0,
  `gateType` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

LOCK TABLES `qcv_visitor_type` WRITE;
/*!40000 ALTER TABLE `qcv_visitor_type` DISABLE KEYS */;

INSERT INTO `qcv_visitor_type` (`tid`, `userid`, `vType`, `povDays`, `qid`, `category`, `gateType`)
VALUES
	(168,1866922598,'普通访客#Normal visitor',0,NULL,2,NULL),
	(172,2147483647,'上门取货',1,'1600925518330',1,NULL),
	(173,2147483647,'提货',0,NULL,1,NULL),
	(185,2147483647,'配送',0,NULL,0,NULL),
	(186,2147483647,'测试',0,NULL,0,NULL),
	(187,2147483647,'货物配送',0,NULL,0,NULL),
	(188,2147483647,'22',0,NULL,0,NULL),
	(190,2147483647,'送水',0,NULL,1,NULL),
	(221,2147483647,'普通访客#Normal Visitor',1,'1614230253411',2,NULL),
	(223,2147483647,'测试',0,NULL,1,NULL),
	(225,2147483647,'其他访客',0,NULL,2,NULL),
	(226,2147483647,'供应商',0,NULL,0,NULL),
	(228,2147483647,'参会访客',0,NULL,2,NULL),
	(229,2147483647,'ceshi',0,NULL,2,NULL),
	(230,2147483647,'测试2',0,NULL,2,NULL),
	(231,2147483647,'111',0,NULL,2,NULL);

/*!40000 ALTER TABLE `qcv_visitor_type` ENABLE KEYS */;
UNLOCK TABLES;



--
-- Dumping routines (FUNCTION) for database 'qcvisit'
--
DELIMITER ;;

# Dump of FUNCTION fristPinyin
# ------------------------------------------------------------

/*!50003 DROP FUNCTION IF EXISTS `fristPinyin` */;;
/*!50003 SET SESSION SQL_MODE="STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION"*/;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`127.0.0.1`*/ /*!50003 FUNCTION `fristPinyin`(P_NAME VARCHAR(255)) RETURNS varchar(255) CHARSET utf8
    DETERMINISTIC
BEGIN 
    DECLARE V_RETURN VARCHAR(255);
    DECLARE V_BOOL INT DEFAULT 0;
          DECLARE FIRST_VARCHAR VARCHAR(1);
 
    SET FIRST_VARCHAR = LEFT(CONVERT(P_NAME USING gbk),1);
    SELECT FIRST_VARCHAR REGEXP '[a-zA-Z]' INTO V_BOOL;
    IF V_BOOL = 1 THEN
      SET V_RETURN = FIRST_VARCHAR;
    ELSE
      SET V_RETURN = ELT(INTERVAL(CONV(HEX(LEFT(CONVERT(P_NAME USING gbk),1)),16,10),   
          0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,   
          0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,  
          0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1),   
      'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z');  
    END IF;
    RETURN V_RETURN;
END */;;

/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE */;;
DELIMITER ;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
