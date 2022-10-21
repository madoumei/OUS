package com.utils.imageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * image图片压缩
 */
public class ImgCompress {
    private Image img;
    private int width;
    private int height;
//	    @SuppressWarnings("deprecation")  
//	    public static void main(String[] args) throws Exception {  
//	        System.out.println("开始：" + new Date());  
//	        ImgCompress imgCom = new ImgCompress("C:\\temp\\rBKW61pUaiqADLkjAAUOHGmRMtI965.jpg");  
//	        imgCom.resizeFix(300,300);  
//	        System.out.println("结束：" + new Date());  
//	    }  

    /**
     * 构造函数
     * @param img
     * @throws IOException
     */
    public ImgCompress(Image img) throws IOException {
        this.img = img;      // 构造Image对象
        width = img.getWidth(null);    // 得到源图宽
        height = img.getHeight(null);  // 得到源图长
    }

    /**
     * 按照宽度、高度进行压缩
     * @param w 最大宽度
     * @param h 最大高度
     * @return
     * @throws IOException
     */
    public BufferedImage resizeFix(int w, int h) throws IOException {
        BufferedImage image = null;
        if (width / height > w / h) {
            image = resizeByWidth(w);
        } else {
            image = resizeByHeight(h);
        }

        return image;
    }

    /**
     * 以宽度为基准，等比例放缩图片
     * @param w int 新宽度
     */
    public BufferedImage resizeByWidth(int w) throws IOException {
        int h = (int) (height * w / width);
        BufferedImage image = resize(w, h);

        return image;
    }

    /**
     * 以高度为基准，等比例缩放图片
     * @param h int 新高度
     */
    public BufferedImage resizeByHeight(int h) throws IOException {
        int w = (int) (width * h / height);
        BufferedImage image = resize(w, h);
        return image;
    }

    /**
     * 强制压缩/放大图片到固定的大小
     * @param w int 新宽度
     * @param h int 新高度
     */
    public BufferedImage resize(int w, int h) throws IOException {
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图

        return image;
//	        File destFile = new File("C:\\temp\\456.jpg");  
//	        FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流  
//	        // 可以正常实现bmp、png、gif转jpg  
//	        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
//	        encoder.encode(image); // JPEG编码  
//	        out.close();  
    }

}
