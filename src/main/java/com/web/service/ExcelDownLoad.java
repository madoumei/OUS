package com.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.client.bean.*;
import com.web.bean.Blacklist;
import com.web.bean.Employee;
import com.web.bean.ExcelModel;
import com.web.bean.OperateLog;
import com.web.bean.RvReport;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ExcelDownLoad {
	 /**
     * 初始化要生成的Excel的表模型
     * @param list List　填充了 Excel表格数据的集合
     * @param wb ExcelModel Excel表的对象模型
     * @see ExcelModel
     * @throws Exception
     */
    public HSSFWorkbook createDownLoadExcel (List<RespVisitor> list,
                                             HSSFWorkbook wb,String exportCols,Map<String, String> evmap,String expExtCols)throws Exception;
    
    public HSSFWorkbook createDownLoadExcel (List<OpendoorInfo> list,
                                           HSSFWorkbook wb,String exportCols)throws Exception;
    
    public ExcelModel createDownLoadExcel (List<Logistics> list,
            ExcelModel excel)throws Exception;
    
    public HSSFWorkbook createDownLoadVRExcel (List<VehicleRecord> list,
                                               HSSFWorkbook workbook)throws Exception;
    
    public ExcelModel createDownLoadExcelIc (List<InterimCard> list,
            ExcelModel excel)throws Exception;
    
    public HSSFWorkbook createDownLoadLogExcel (List<OperateLog> olist,
                                              HSSFWorkbook wb)throws Exception;
    
    public ExcelModel createDownLoadEmpExcel (List<Employee> olist,
            ExcelModel excel)throws Exception;
    
    public ExcelModel createDownLoadRvExcel (List<RvReport> olist,
            ExcelModel excel)throws Exception;
    
    public ExcelModel createDownLoadRvrExcel (List<RvReport> olist,
            ExcelModel excel)throws Exception;
    
    public HSSFWorkbook createDownLoadRblExcel (List<Blacklist> olist,
                                              HSSFWorkbook wb,UserService userService,int userid)throws Exception;
    
    /**
     * 在已文件已存在的情况下，采用读取文件流的方式实现左键点击下载功能，
     * 本系统没有采取这个方法,而是直接将数据传往输出流,效率更高。
     * @param inPutFileName 读出的文件名
     * @param outPutFileName　保存的文件名
     * @param HttpServletResponse　    
     * @see HttpServletResponse
     * @throws IOException
     */
    public void downLoad(String inPutFileName, String outPutFileName,HttpServletRequest req,
            HttpServletResponse response) throws IOException ;
    
    /**
     * 在已文件不存在的情况下，采用生成输出流的方式实现左键点击下载功能。
     * @param outPutFileName　保存的文件名
     * @param out ServletOutputStream对象    
     * @param downExcel 填充了数据的ExcelModel
     * @param HttpServletResponse　    
     * @see HttpServletResponse
     * @throws Exception
     */
    public void downLoad(String outPutFileName, ExcelModel downExcel,HttpServletRequest req,
            HttpServletResponse response) throws Exception ;

    SXSSFWorkbook createDownLoadExcel(List<VisitorRecord> vrs, String exportCols, Map<String, String> gateMap, SXSSFWorkbook wb);
}
