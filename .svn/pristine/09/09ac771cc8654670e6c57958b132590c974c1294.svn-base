package com.utils;

import org.apache.commons.codec.binary.Base64;


public class lx {

    public static String getData(int line,String pass) {
        // TODO Auto-generated method stub

        String rs1="FE";//包头
        String rs2="98";//目标地址 98 固定为控制卡
        String rs3="";//************数据总长 不含包头及目标地址(从此到最后全部字节)
        String rs4="97";//目标上位机，固定 0x97
        String rs5="54";//消息类型
        String rs6="00";//保留
        String rs7="0000000000000000";//内部码或者 485 地址，0 表示广播
        String rs8="01";//保留
        String rs9="01";//子包总数
        String rs10="01";//子包序号
//				以下为子包内容 只有一个子包
        String rs11="FE5C4B89";//包头
        String rs12="";//***********本数据长度 含包头但不包含校验字节（从子包包头到CRC校验之上所有字节
        String rs13="31";//消息类型 31 为文字 固定格式
        String rs14="00006CB9";//消息 ID 在返回时也是这个 ID
        String rs15="";//***********数据长度 从此字段到包尾之上(不包括这 4 个字节 不包括包尾和校验位)

        String rs16A="303030303030303031";//**********素材 UID 31 为第一行 32 33 34 分别为二三四行(显示屏幕第几行文字)
        String rs16B="30303030303030303";
        if (line>4){
            line = 1;
        }
        rs16B = rs16B+line;

        String rs17="2C";//标志位
        String rs18="0104FF";//固定位(01-表示从右向左 04表示移动速度  最后的1个字节00 表示停留 如果是FF就是不停留，00~FF之间表示停留*5秒
        String rs19="303130313031393931323331";//允许播放的起始、结束年、月、日（各 2 个字符）ascii 固定即可
        String rs20="13000000";//**********素材属性长度(从此到显示内容长度字段之上--不包括本身4字节)
        String rs21="55AA";//属性标志位
        String rs22="0000";//保留
        String rs23="37323231";//固定
        String rs24="32";//31 为单基色 32 为双基色
        String rs25="31";//固定
        String rs26="000008001000";//固定
        String rs27="01";//01-红，02-绿，03-黄，04-蓝
        String rs28="11";//固定
        String rs29="00";//保留
        String rs30="";//**********显示内容长度(从此往下到包尾之上-不包含自身4字节)
        String rs31="";//**********显示内容 GB2312BB B6 D3 AD B9 E2 C1D9 欢迎光临00 01 00 01 00 01 00 67 7C 固定末尾677C也可以为0000
        String rs32="FFFF";//包尾
        String rs33="";//********CRC 校验位(目标地址到包尾)
        String rs34="";//********异或和校验位（目标地址到 CRC 校验）

        //获取显示内容
        rs31=enUnicode(pass)+"FF00010001000100677C";
        int stlenth=rs31.length()/2;
        //System.out.println("485："+rs31+"长度："+stlenth);

        //计算数据总长
        int rs3length=90+stlenth;
        rs3 = "00"+Integer.toHexString(rs3length);
        //System.out.println("485："+rs3);

        //计算本数据长度
        int rs12length=71+stlenth;
        rs12 = Integer.toHexString(rs12length)+"000000";
        //System.out.println("485："+rs12);

        //计算数据长度
        int rs15length=52+stlenth;
        rs15 = Integer.toHexString(rs15length)+"000000";
        //System.out.println("485："+rs15);

        //计算数据长度
        int rs30length=stlenth;
        if (rs30length <= 18){
            rs18="0104FF";
        }else {
            rs18="010400";
        }
        rs30 = Integer.toHexString(rs30length)+"000000";
        if (rs30.length()%2 == 1){
            rs30 = "0"+rs30;
        }
        //System.out.println("485："+rs30);

        //字符串转换为数组  计算CRC
        String rs485CRC=rs2+rs3+rs4+rs5+rs6+rs7+rs8+rs9+rs10+rs11+rs12+rs13+rs14+rs15+rs16B+rs17+rs18+rs19+rs20+rs21+rs22+rs23+rs24+rs25+rs26+rs27+rs28+rs29+rs30+rs31+rs32;
        //System.out.println("485："+rs485CRC);
        getCRC(rs485CRC);
        rs33=getCRC1(getCRC(rs485CRC));

        //计算异或和校验位
        String rs485XOR=rs2+rs3+rs4+rs5+rs6+rs7+rs8+rs9+rs10+rs11+rs12+rs13+rs14+rs15+rs16B+rs17+rs18+rs19+rs20+rs21+rs22+rs23+rs24+rs25+rs26+rs27+rs28+rs29+rs30+rs31+rs32+rs33;
        //System.out.println("485："+rs485XOR);
        getCRC(rs485XOR);
        rs34=getXor(getCRC(rs485XOR));
//	        System.out.println(getXor(getCRC(rs485XOR)));
//        System.out.println(rs34);

        //base64加密
        String rs485base64=rs1+rs2+rs3+rs4+rs5+rs6+rs7+rs8+rs9+rs10+rs11+rs12+rs13+rs14+rs15+rs16B+rs17+rs18+rs19+rs20+rs21+rs22+rs23+rs24+rs25+rs26+rs27+rs28+rs29+rs30+rs31+rs32+rs33+rs34;
//        System.out.println("485协议："+rs485base64);
        String s=null;
        s= Base64.encodeBase64String(getCRC(rs485base64));
//        System.out.println("base64加密加密后："+s);

        return s;

    }

    public static String enUnicode(String str) {
        // 将汉字转换为16进制数
        String st = "";
        try{
            byte[] by = str.getBytes("gb2312");
            for(int i=0; i< by.length; i++){
                String strs = Integer.toHexString(by[i]);
                if(strs.length() > 2){
                    strs = strs.substring(strs.length() - 2);
                }
                st += strs;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return st;
    }


    public static byte[] getCRC(String data) {
        //字符串编辑为数组
        data = data.replace(" ", "");
        byte[] a={0};
        int len = data.length();
        if (!(len % 2 == 0)) {
            System.out.println("error len="+len);
            return a;
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }


//        System.out.println(Arrays.toString(para));

        return para;

    }


    public static String getCRC1(byte[] bytes) {
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
//        System.out.println(result.substring(2, 4)+result.substring(0, 2));
        //交换高低位
        return result.substring(2, 4) + result.substring(0, 2);
    }


    public static String getXor(byte[] datas){
        //异或和校验位
        byte temp=0;

        for (int i = 0; i <datas.length; i++) {
            temp ^=datas[i];
        }

        String hex = Integer.toHexString(temp);
        if(hex.length()>1){
            return hex.substring(hex.length()-2);
        }else{
            return "0"+hex;
        }



    }


}