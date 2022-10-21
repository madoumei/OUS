package com.utils.imageUtils;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 图片工具类
 */
public class ImageUtils {

    /**
     * 将网络链接图片或者本地图片文件转换成Base64编码字符串
     *
     * @param imgStr 网络图片Url/本地图片目录路径
     * @return
     */
    public static String getImgStrToBase64(String imgStr) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] buffer = null;
        try {
            //判断网络链接图片文件/本地目录图片文件
            if (imgStr.startsWith("http://") || imgStr.startsWith("https://")) {
                // 创建URL
                URL url = new URL(imgStr);
                // 创建链接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                inputStream = conn.getInputStream();
                outputStream = new ByteArrayOutputStream();
                // 将内容读取内存中
                buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                buffer = outputStream.toByteArray();
            } else {
                inputStream = new FileInputStream(imgStr);
                int count = 0;
                while (count == 0) {
                    count = inputStream.available();
                }
                buffer = new byte[count];
                inputStream.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        String encode = new BASE64Encoder().encode(buffer);
        encode = encode.replaceAll("\r\n", "");
        encode = encode.replaceAll("\r", "");
        encode = encode.replaceAll("\n", "");
        return encode;
    }

    /**
     * 根据url下载图片到本地
     *
     * @param urlList
     * @param id
     * @param pathName
     */
    private static void downloadPicture(String urlList, String id, String pathName) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(pathName + id + ".png"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encodeImgageToBase64(URL imageUrl) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();	}
        catch (IOException e) {
            e.printStackTrace();
        }	// 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
        encode = encode.replace("\r\n","");
        encode = encode.replace("\r","");
        encode = encode.replace("\n","");
        return encode;
    }

    public static void main(String[] args) {
        String url = "https://www.coolvisit.top/group1/M00/00/5B/rBHIpmAD826Ad_zuAAckyAC--ps500.png";
//        String imgStrToBase64 = null;
//        try {
//            imgStrToBase64 = encodeImgageToBase64(new URL(url));
//            System.out.println(imgStrToBase64);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        String imgStrToBase64 = getImgStrToBase64(url);
        System.out.println("data:image/jpg;base64,"+imgStrToBase64);

    }
}
