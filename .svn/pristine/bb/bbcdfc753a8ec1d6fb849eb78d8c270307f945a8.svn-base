package com.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * int width = 70, height = 30;
 * 验证码的w=70 h=30   用Label来接收
 * @author 小风微灵
 *
 */
public class ValidationCode {

    /**
     * 获取随机颜色值
     * @param minColor
     * @param maxColor
     * @return
     */
	static StringBuffer vcode=new StringBuffer();
	
    private static Color getRandomColor(int minColor, int maxColor) {
        
        Random random = new Random();
        // 保存minColor最大不会超过255
        if (minColor > 255)
            minColor = 255;
        // 保存minColor最大不会超过255
        if (maxColor > 255)
            maxColor = 255;
        // 获得红色的随机颜色值
        int red = minColor + random.nextInt(maxColor - minColor);
        // 获得绿色的随机颜色值
        int green = minColor + random.nextInt(maxColor - minColor);
        // 获得蓝色的随机颜色值
        int blue = minColor + random.nextInt(maxColor - minColor);
        return new Color(red, green, blue);
    
    }
    
    public static String getValidationCode() throws IOException {
    	String validationCode="";
        // 用于保存最后随机生成的验证码
        try {
        		
            // 设置图形验证码的长和宽（图形的大小）
                int width = 70, height = 30;
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);                
                Graphics g = image.getGraphics();// 获得用于输出文字的Graphics对象
                Random random = new Random();
                g.setColor(getRandomColor(180, 250));// 随机设置要填充的颜色
                g.fillRect(0, 0, width, height);// 填充图形背景
            // 设置初始字体
                g.setFont(new Font("Times New Roman", Font.ITALIC, 22));
                g.setColor(getRandomColor(120, 180));// 随机设置字体颜色
            
            //干扰线
//                for(int i=0;i<155;i++){
//                    int x=random.nextInt(width);
//                    int y=random.nextInt(height);
//                    int x1=random.nextInt(width);
//                    int y1=random.nextInt(height);
//                    g.drawLine(x, y, x+x1, y+y1);
//                }
            
            // 随机生成4个验证码
                for (int i = 0; i < 4; i++) {
                    // 随机获得当前验证码的字符串
                    String code=UtilTools.produceId(1);
                    vcode.append(code);
                    // 随机设置当前验证码字符的颜色
                    g.setColor(getRandomColor(10, 100));
                    // 在图形上输出验证码字符，x和y都是随机生成的
                    g.drawString(code, 16 * i + random.nextInt(6), height-random.nextInt(5)-1);
                }
                //名称重置
                ByteArrayOutputStream os=new ByteArrayOutputStream();
                
                ImageIO.write(image, "jpg", os);
              
                validationCode=Encodes.encodeBase64(os.toByteArray());

                os.close();
                g.dispose();
                
            } catch (Exception e){
                e.printStackTrace();
            }
            return validationCode;    
        }
    
   public static String getValidationCodeStr() {
	   		String code=vcode.toString();
	   		vcode.setLength(0);;
            return code;    
        }
    
}
