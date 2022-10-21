
package com.utils;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.utils.FileUtils.MinioTools;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PdfTools {
    public static boolean createPdf3(String filename, byte[] bytes, String content, String vname, String date) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter("/work/signpdf/" + filename));
        Document doc = new Document(pdfDoc);//构建文档对象
//			PdfFont sysFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
        Table tab = new Table(new float[]{700});
        tab.setWidthPercent(100);
        tab.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Image Img = new Image(ImageDataFactory.create(bytes));
        Cell cellimg = new Cell().add(Img.setAutoScale(true).setHorizontalAlignment(HorizontalAlignment.CENTER))// 向第一个表格中添加图片
                .setBorder(Border.NO_BORDER);
        tab.addCell(cellimg);
        doc.add(tab.setHorizontalAlignment(HorizontalAlignment.LEFT));// 将表格添加入文档并页面居中

//			Cell cell1 = new Cell();
//			// 文字样式
//			Text text5 = new Text("安全协议").setFont(sysFont)
//					.setFontSize((float) 28)
//					.setFontColor(new DeviceRgb(46, 46, 46)).setBold();// setBold()字体为加粗
//			cell1.setTextAlignment(TextAlignment.CENTER)
//			// 字体居左
//			.add(new Paragraph().add(text5)
//					.setFixedLeading(15))// setFixedLeading为设置行间距
//			.setBorder(Border.NO_BORDER);
//	
//			tab.addCell(cell1);
//			Cell cell2 = new Cell();
//			Text text2 = new Text("\n");
//			cell2.add(new Paragraph().add(text2)).setBorder(Border.NO_BORDER);// setFixedLeading为设置行间距
//			tab.addCell(cell2);
//			
//			Cell cell3 = new Cell();
//			// 文字样式
//			Text text4 = new Text(content).setFont(sysFont)
//					.setFontSize((float) 12)
//					.setFontColor(new DeviceRgb(46, 46, 46));// setBold()字体为加粗
//			cell3.setTextAlignment(TextAlignment.LEFT)
//			// 字体居左
//			.add(new Paragraph().add(text4)
//					.setFixedLeading(18))// setFixedLeading为设置行间距
//			.setBorder(Border.NO_BORDER);
//	
//			tab.addCell(cell3);

//			Image Img = new Image(ImageDataFactory.create(bytes));
//			Cell cellimg = new Cell().add(Img.setAutoScale(false).setHorizontalAlignment(HorizontalAlignment.CENTER))// 向第一个表格中添加图片
//					.setBorder(Border.NO_BORDER);
//			tab.addCell(cellimg);
//			doc.add(tab.setHorizontalAlignment(HorizontalAlignment.LEFT));// 将表格添加入文档并页面居中

        UnitValue[] unitValue = new UnitValue[]{
                UnitValue.createPercentValue((float) 50),
                UnitValue.createPercentValue((float) 50)};

//			Table tab2 = new Table(unitValue);
//			tab2.setWidthPercent(100);  
//			Cell cell4 = new Cell();
//			// 文字样式
//			Text text1 = new Text("姓名："+vname).setFont(sysFont).setFontSize((float) 12)
//					.setFontColor(new DeviceRgb(46, 46, 46));// setBold()字体为加粗
//			cell4.setTextAlignment(TextAlignment.LEFT)
//					// 字体居左
//					.add(new Paragraph().add(text1).setFixedLeading(15))
//					.setBorder(Border.NO_BORDER);
//	
//			tab2.addCell(cell4);
//			
//			Cell cell5 = new Cell();
//			Text text3 = new Text("日期："+date)
//			.setFont(sysFont).setFontSize((float) 12)
//			.setFontColor(new DeviceRgb(46, 46, 46));
//			cell5.setTextAlignment(TextAlignment.LEFT)
//			// 字体居左
//			.add(new Paragraph().add(text3).setFixedLeading(15))
//			.setBorder(Border.NO_BORDER);
//			tab2.addCell(cell5);
//			 Paragraph blankRow1 = new Paragraph(); 
//			  for (int i = 0; i < 3; i++) {  
//				  doc.add(blankRow1);
//			}
//		
//			doc.add(tab2.setHorizontalAlignment(HorizontalAlignment.CENTER));// 将表格添加入文档并页面居中
        doc.close();

        return true;
    }
    
    public static boolean createPdfByte(String filename, byte[] bytes, String content, String vname, String date) throws IOException {
    	  ByteArrayOutputStream  outputStream = new ByteArrayOutputStream(); 
    	  PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
          Document doc = new Document(pdfDoc);//构建文档对象
          Table tab = new Table(new float[]{700});
          tab.setWidthPercent(100);
          tab.setHorizontalAlignment(HorizontalAlignment.CENTER);
          Image Img = new Image(ImageDataFactory.create(bytes));
          Cell cellimg = new Cell().add(Img.setAutoScale(true).setHorizontalAlignment(HorizontalAlignment.CENTER))// 向第一个表格中添加图片
                  .setBorder(Border.NO_BORDER);
          tab.addCell(cellimg);
          doc.add(tab.setHorizontalAlignment(HorizontalAlignment.LEFT));// 将表格添加入文档并页面居中
          doc.close();
          MinioTools.uploadFile(new ByteArrayInputStream(outputStream.toByteArray()),Constant.BUCKET_NAME,"application/pdf",
  	    		"/pdf/"+filename);
          outputStream.close();
          return true;
    }

    public static void main(String[] args) throws Exception {
    }

}
