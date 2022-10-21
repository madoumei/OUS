package com.utils.imageUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图像改变
 */
public class ImageChange {

    /**
     *旋转图像
     * @param image
     * @param degree  旋转度数
     * @param bgcolor 背景颜色
     * @return
     * @throws IOException
     */
    public static InputStream rotateImg(BufferedImage image, int degree, Color bgcolor) throws IOException {  
  
        int iw = image.getWidth();//原始图象的宽度   
        int ih = image.getHeight();//原始图象的高度  
        int w = 0;  
        int h = 0;  
        int x = 0;  
        int y = 0;  
        degree = degree % 360;  
        if (degree < 0)  
            degree = 360 + degree;//将角度转换到0-360度之间  
        double ang = Math.toRadians(degree);//将角度转为弧度  
  
        /** 
         *确定旋转后的图象的高度和宽度 
         */  
  
        if (degree == 180 || degree == 0 || degree == 360) {  
            w = iw;  
            h = ih;  
        } else if (degree == 90 || degree == 270) {  
            w = ih;  
            h = iw;  
        } else {  
            int d = iw + ih;  
            w = (int) (d * Math.abs(Math.cos(ang)));  
            h = (int) (d * Math.abs(Math.sin(ang)));  
        }  
  
        x = (w / 2) - (iw / 2);//确定原点坐标  
        y = (h / 2) - (ih / 2);  
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());  
        Graphics2D gs = (Graphics2D)rotatedImage.getGraphics();  
//        if(bgcolor==null){  
            rotatedImage  = gs.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);  
//        }else{  
//            gs.setColor(bgcolor);  
//            gs.fillRect(0, 0, w, h);//以给定颜色绘制旋转后图片的背景  
//        }  
          
        AffineTransform at = new AffineTransform();  
        at.rotate(ang, w / 2, h / 2);//旋转图象  
        at.translate(x, y);  
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);  
        op.filter(image, rotatedImage);  
        image = rotatedImage;  
          
        ByteArrayOutputStream  byteOut= new ByteArrayOutputStream();  
        ImageOutputStream iamgeOut = ImageIO.createImageOutputStream(byteOut);  
          
        ImageIO.write(image, "png", iamgeOut);  
        InputStream  inputStream = new ByteArrayInputStream(byteOut.toByteArray());  
          
        return inputStream;  
    }

    /**
     * 顺时针旋转90度
     * @param bi
     * @return
     */
    public static BufferedImage rotate90DX(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage biFlip = new BufferedImage(height, width, bi.getType());
        for(int i=0; i<width; i++)
            for(int j=0; j<height; j++)
                biFlip.setRGB(height-1-j, width-1-i, bi.getRGB(i, j));
        return biFlip;
    }

    /**
     * 逆时针旋转90度
     * @param bi
     * @return
     */
    public static  BufferedImage rotate90SX(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage biFlip = new BufferedImage(height, width, bi.getType());
        for(int i=0; i<width; i++)
            for(int j=0; j<height; j++)
                biFlip.setRGB(j, i, bi.getRGB(i, j));
        return biFlip;
    }


    /**
     * 测试
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
    	File file1=new File("D:/Ci2aT1arABWAYYi1AAQnTe04lM8476_jpg_320x320.jpg");             //用file1取得图片名字
        String name=file1.getName();

         BufferedImage bi = ImageIO.read(file1);
         bi=ImageChange.rotate90SX(bi);
         ImageIO.write(bi, "jpg", new File("D:/Ci2aT1arABWAYYi1AAQnTe04lM8476_jpg_320x320.jpg")); 
    }
    
      
      
}  

