package com.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {
    /**
     * 标题样式
     *
     * @param workbook 创建并初始化标题样式
     */
    public static void getTitleStyle(SXSSFWorkbook workbook, Row title) {
        CellStyle style = workbook.createCellStyle();               // 创建样式
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);             // 字体居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  // 垂直居中
        Font font = workbook.createFont();                          // 创建字体样式
        font.setFontName("宋体");                                     // 字体
        font.setFontHeightInPoints((short) 16);                     // 字体大小
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);               // 加粗
        style.setFont(font);                                        //给样式指定字体
        title.getCell(0).setCellStyle(style);                    //给标题设置样式
    }

    /**
     * (初始化Excel表头)
     *
     * @param workBook
     * @param sheet
     * @param head
     * @return
     */
    public static Row InitExcelHead(SXSSFWorkbook workBook, Sheet sheet, String[] head) {
        Row row = sheet.createRow(1);
        CellStyle style = getHeaderStyle(workBook);             //获取表头样式
        for (int i = 0; i < head.length; i++) {
            row.createCell(i).setCellValue(head[i]);
            row.getCell(i).setCellStyle(style);                 //设置标题样式
        }
        return row;
    }

    /**
     * excel正文内容的填充
     *
     * @throws
     * @Title: setExcelValue
     * @Description: TODO(excel正文内容的填充)
     * @param: @param sheet  Excel页签对象名
     * @return: void
     */
    public static void setExcelValue(SXSSFWorkbook workBook, Sheet sheet, String[] head) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 1000000; i++) {
            //sheet.createRow(i+2) 2003excel参数里面的类型是int，所以一次只能导出65535条数据
            Row row = sheet.createRow(i + 2);
            for (int j = 0; j < head.length; j++) {
                buffer.append("数据行" + (i + 1));
                buffer.append("列" + (j + 1));
                row.createCell(j).setCellValue(buffer.toString());
                buffer.delete(0, buffer.length());
            }
        }
    }

    /**
     * @throws
     * @Title: excelExport
     * @Description: TODO(excel导出类)
     * @param: @param sheet
     * @return: void
     */
    public static void excelExport(SXSSFWorkbook workBook) {

        String filePath = getSavePath();  //获取文件保存路径
        if (filePath == null) {
            //SysUtil.abort();                //终止程序
        }

        String srcFile = filePath + "\\Excel导出测试根目录\\Excel多线程导出.xlsx";
        FileOutputStream fileOut = null;
        try {
            File file = new File(srcFile);
            if (file.exists()) {  //当文件已存在时
                //删除原Excel      打开新导出的Excel时，最好刷新下当前文件夹，以免重复操作有时出现缓存。
                file.delete();
            }
            fileOut = new FileOutputStream(file);
            workBook.write(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            MsgBox.showError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
//            MsgBox.showError(e.getMessage());
        } finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件保存路径
     *
     * @throws
     * @Title: getSavePath
     * @Description: TODO(获取文件保存路径)
     * @param: @return
     * @return: String
     */
    private static String getSavePath() {
        // 选择保存路径
        String selectPath = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设置只能选择目录
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectPath = chooser.getSelectedFile().getPath();
        }
        return selectPath;
    }

    /**
     * 获取Excel表头样式(第二行)
     *
     * @param workbook
     * @return
     */
    public static CellStyle getHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
//        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);  //下边框
//        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);    //左边框
//        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
//        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);      //居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
//        style.setTopBorderColor(HSSFColor.BLACK.index);     //上边框颜色
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
//        style.setRightBorderColor(HSSFColor.BLACK.index);
        Font font = workbook.createFont();               // 创建字体样式
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 14);              // 字体大小
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);        // 加粗
        style.setFont(font);                                 //给样式指定字体
        return style;
    }
}
