package com.utils.licenseUtils;

import com.VisitorApplication;
import com.utils.*;
import org.apache.commons.io.IOUtils;
import org.smartboot.license.client.License;
import org.smartboot.license.client.LicenseEntity;
import org.smartboot.license.client.LicenseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class LicenseUtil {

    public List<String> getMacAddress() throws Exception {
        List<String> result = null;

        //1. 获取所有网络接口
        List<InetAddress> inetAddresses = getLocalAllInetAddress();

        if (inetAddresses != null && inetAddresses.size() > 0) {
            //2. 获取所有网络接口的Mac地址
            result = inetAddresses.stream().map(this::getMacByInetAddress).distinct().collect(Collectors.toList());
        }

        return result;
    }

    /**
     * 获取当前服务器所有符合条件的InetAddress
     *
     * @return java.util.List<java.net.InetAddress>
     */
    protected List<InetAddress> getLocalAllInetAddress() throws Exception {
        List<InetAddress> result = new ArrayList<>(4);

        // 遍历所有的网络接口
        for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
            NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();
            // 在所有的接口下再遍历IP
            for (Enumeration inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();

                //排除LoopbackAddress、SiteLocalAddress、LinkLocalAddress、MulticastAddress类型的IP地址
                if (!inetAddr.isLoopbackAddress() /*&& !inetAddr.isSiteLocalAddress()*/
                        && !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()) {
                    result.add(inetAddr);
                }
            }
        }

        return result;
    }

    /**
     * 获取某个网络接口的Mac地址
     *
     * @return void
     */
    public String getMacByInetAddress(InetAddress inetAddr) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    stringBuffer.append("-");
                }

                //将十六进制byte转化为字符串
                String temp = Integer.toHexString(mac[i] & 0xff);
                if (temp.length() == 1) {
                    stringBuffer.append("0" + temp);
                } else {
                    stringBuffer.append(temp);
                }
            }

            return stringBuffer.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean checkLicenseData() {
        List<String> macAddress = null;
        String data ="";
        try {
            macAddress = getMacAddress();
        } catch (Exception e) {
            throw new LicenseException("get mac failed");
        }

        try {
            InputStream inputStream = new FileInputStream(new File("/work/license.txt"));
            InputStream isPublicKey = VisitorApplication.class.getResourceAsStream("/license/public.key");
            License license = new License();
            LicenseEntity licenseData = license.loadLicense(inputStream, Base64.getDecoder().decode(IOUtils.toString(isPublicKey, StandardCharsets.UTF_8)));
            data = new String(licenseData.getData());
        } catch (Exception e) {
            throw new LicenseException("need create license with: " + AESUtil.encode(macAddress.get(0), Constant.AES_KEY));
        }

        for (String mac : macAddress) {
            mac = AESUtil.encode(mac, Constant.AES_KEY);
            if (data.equals(mac)) {
                return true;
            }
        }
        throw new LicenseException("need create license with: " + AESUtil.encode(macAddress.get(0), Constant.AES_KEY));
    }
}
