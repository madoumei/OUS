package com.web.service.impl;

import com.client.bean.*;
import com.client.service.EquipmentGroupService;
import com.client.service.VisitorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.ExcelUtils;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;

import com.web.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AgentInfoExcelDownLoad extends BaseExcelDownLoad {

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    private static AgentInfoExcelDownLoad agentInfoExcelDownLoad;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        agentInfoExcelDownLoad = this;
        agentInfoExcelDownLoad.equipmentGroupService = this.equipmentGroupService;
    }

    /**
     * 导出访客记录
     * @param list List　填充了 Excel表格数据的集合
     * @param wb HSSFWorkbook Excel表的对象模型
     * @param exportCols
     * @param evmap
     * @param expExtCols
     * @return
     * @throws Exception
     */
    @Override
    public HSSFWorkbook createDownLoadExcel(List<RespVisitor> list, HSSFWorkbook wb, String exportCols,
                                          Map<String, String> evmap, String expExtCols) throws Exception {
        //样式设置========
        HSSFCellStyle styleTitle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, true);
        HSSFCellStyle fontStyle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, false);

        HSSFSheet sheet = wb.createSheet();
        //创建第0行表头
        HSSFRow row0 = sheet.createRow(0);
        //设置行高
        row0.setHeightInPoints((short) 30);
        //创建单元格标题

        List<String> cols = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();
        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int i = 0; i < exportCols.length(); i++) {
            cols.add(exportCols.substring(i, i + 1));
        }

        String[] titleTemp = {"拜访者", "联系方式", "被访者", "被访者联系方式", "拜访事由",
                "签入门岗", "签出门岗", "前台预约人", "前台登记人", "工作单位",
                "证件号码", "照片", "来访人数", "随访人员清单", "备注",
                "预约时间","签到时间", "签出时间", "车牌号码", "访客类型",
                "访客邮箱","会面地点", "性别", "前台发卡人"};
        for (int a = 0; a < cols.size() && a<titleTemp.length; a++) {
            if ("1".equals(cols.get(a))) {
                titles.add(titleTemp[a]);
            }
        }

        //扩展字段
        String[] extcols = expExtCols.split(",");
        for (int e = 0; e < extcols.length; e++) {
            titles.add(evmap.get(extcols[e]));
        }
        for (int i = 0; i < titles.size(); i++) {
            HSSFCell c0 = row0.createCell(i);
            c0.setCellValue(new HSSFRichTextString(titles.get(i)));
            c0.setCellStyle(styleTitle);
            sheet.autoSizeColumn(i);
        }

        Iterator<RespVisitor> ir = list.iterator();
        int cells = 0;
        int rowCont = 1;
        int colsIndex = 0;
        while (ir.hasNext()) {

            //List<String> rowData = new ArrayList<String>();
            colsIndex = 0;
            RespVisitor rv = (RespVisitor) ir.next();
            HSSFRow row = sheet.createRow(rowCont);

            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVname());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVphone());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getEmpName());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getEmpPhone());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVisitType());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getSignInGate());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getSignOutGate());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getSignInOpName());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getSignOutOpName());
            }

            //工作单位
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVcompany());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getCardId());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVphoto());
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                if (rv.getPeopleCount() == 0) {
                    cells = addCells(fontStyle, sheet, cells, row,"1");
                } else {
                    cells = addCells(fontStyle, sheet, cells, row, String.valueOf(rv.getPeopleCount()));
                }
            }
            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getMemberName());
            }
            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getRemark());
            }

            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                if (null != rv.getAppointmentDate()){
                    cells = addCells(fontStyle, sheet, cells, row,time.format(rv.getAppointmentDate()));
                }else {
                    cells = addCells(fontStyle, sheet, cells, row,"");
                }
            }

            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                if (null != rv.getVisitdate()){
                    cells = addCells(fontStyle, sheet, cells, row,time.format(rv.getVisitdate()));
                }else {
                    cells = addCells(fontStyle, sheet, cells, row,"");
                }
            }
            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                if (null != rv.getSignOutDate()) {
                    cells = addCells(fontStyle, sheet, cells, row,time.format(rv.getSignOutDate()));
                } else {
                    cells = addCells(fontStyle, sheet, cells, row,"");
                }

            }
            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getPlateNum());
            }
            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getvType());
            }

            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getVemail());
            }

            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getMeetingPoint());
            }

            if (cols.size()>colsIndex &&"1".equals(cols.get(colsIndex++))) {
                if (null != rv.getSex()) {
                    if ("0".equals(rv.getSex())) {
                        cells = addCells(fontStyle, sheet, cells, row,"女");
                    } else if ("1".equals(rv.getSex())) {
                        cells = addCells(fontStyle, sheet, cells, row,"男");
                    }
                } else {
                    cells = addCells(fontStyle, sheet, cells, row,"");
                }
            }
            if (cols.size()>colsIndex && "1".equals(cols.get(colsIndex++))) {
                cells = addCells(fontStyle, sheet, cells, row,rv.getCardOpName());
            }

            //扩展字段数据
            for (int e = 0; e < extcols.length; e++) {
                if (StringUtils.isNotEmpty(rv.getExtendCol())) {
                    ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                    rv.setExtendCol(rv.getExtendCol().replaceAll("[\\t\\n\\r]", ""));
                    JsonNode rootNode = mapper.readValue(rv.getExtendCol(), JsonNode.class);
                    JsonNode jn = rootNode.path(extcols[e]);
                    String s = jn.asText().replaceAll("\"\"", "");
                    if (jn != null && StringUtils.isNotBlank(s) && StringUtils.isNotEmpty(jn.asText())) {
                        if (extcols[e].equals("access")) {
                            String[] accessId = s.split(",");
                            List<EquipmentGroup> equipmentGroup = agentInfoExcelDownLoad.equipmentGroupService.getEquipmentGroupByEgidArray(accessId);
                            if (equipmentGroup.size() > 0) {
                                List<String> collect = equipmentGroup.stream().map(equipmentGroup1 -> equipmentGroup1.getEgname()).collect(Collectors.toList());
                                cells = addCells(fontStyle, sheet, cells, row,String.join(",", collect.toString()));
                            }
                        } else if (extcols[e].equals("secret")) {
                            if (jn.asText().equals("n")) {
                                cells = addCells(fontStyle, sheet, cells, row,"否");
                            } else {
                                cells = addCells(fontStyle, sheet, cells, row,"是");
                            }
                        } else if (extcols[e].equals(VisitorService.EXTEND_KEY_ENDDATE)) {
                            try {
                                SimpleDateFormat loaclFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                format.setTimeZone(TimeZone.getTimeZone("gmt"));
                                cells = addCells(fontStyle, sheet, cells, row,loaclFormat.format(format.parse(jn.asText())));
                            }catch (Exception e1){
                                cells = addCells(fontStyle, sheet, cells, row,jn.asText());
                            }

                        } else {
                            cells = addCells(fontStyle, sheet, cells, row,jn.asText());
                        }
                    } else {
                        cells = addCells(fontStyle, sheet, cells, row,"");
                    }

                }
            }
            rowCont++;
            cells = 0;
        }



        sheet.createFreezePane(0, 1, 0, 1);
        return wb;
    }

    protected int addCells(HSSFCellStyle fontStyle, HSSFSheet sheet, int cells, HSSFRow row,String cellValue) {
        HSSFCell cell = row.createCell(cells);
        cell.setCellValue(cellValue);
        cell.setCellStyle(fontStyle);
        sheet.setColumnWidth(cells, 10 * 300 + 512);
        cells++;
        return cells;
    }

    /**
     * 导出物流记录
     * @param list
     * @param excel
     * @return
     * @throws Exception
     */
    @Override
    public ExcelModel createDownLoadExcel(List<Logistics> list, ExcelModel excel)
            throws Exception {
        // TODO Auto-generated method stub


        String titleStr = "姓名;车牌号;手机号;身份证号;预约时间;到达时间;结束时间;离开时间;物流类型;审批状态";

        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Iterator<Logistics> ir = list.iterator();
        while (ir.hasNext()) {

            List<String> rowData = new ArrayList<String>();

            Logistics rv = (Logistics) ir.next();

           // rowData.add(rv.getSname());
            rowData.add(rv.getPlateNum());
           // rowData.add(rv.getSmobile());
           // rowData.add(rv.getScardId());
            if (null != rv.getAppointmentDate()) {
                rowData.add(time.format(rv.getAppointmentDate()));
            } else {
                rowData.add("");
            }
            if (null != rv.getVisitdate()) {
                rowData.add(time.format(rv.getVisitdate()));
            } else {
                rowData.add("");
            }
            if (null != rv.getFinishTime()) {
                rowData.add(time.format(rv.getFinishTime()));
            } else {
                rowData.add("");
            }
            if (null != rv.getLeaveTime()) {
                rowData.add(time.format(rv.getLeaveTime()));
            } else {
                rowData.add("");
            }
            rowData.add(rv.getLogType());
            if (rv.getPstatus() == 1) {
                rowData.add("已审批");
            } else if (rv.getPstatus() == 2) {
                rowData.add("已拒绝");
            } else if (rv.getPstatus() == 3) {
                rowData.add("已结束");
            } else {
                rowData.add("待审批");
            }

            data.add(rowData);

        }


        String[] titles = titleStr.split(";");
        
        /*for(int i=0;i<titles.length;i++){
            System.out.print(titles[i]+" ");
        }*/

        List<String> header = new ArrayList<String>();
        for (int i = 0; i < titles.length; i++) {
            header.add(titles[i]);
        }

        //设置报表标题
        excel.setHeader(header);
        //设置报表内容
        excel.setData(data);
        return excel;
    }

    /**
     * 导出车辆通行记录
     * @param list
     * @param wb
     * @return
     * @throws Exception
     */
    @Override
    public HSSFWorkbook createDownLoadVRExcel(List<VehicleRecord> list,
                                              HSSFWorkbook wb) throws Exception {
        //样式设置========
        HSSFCellStyle styleTitle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, true);
        HSSFCellStyle fontStyle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, false);
        HSSFSheet sheet = wb.createSheet();
        //创建单元格标题
        List<String> titles = new ArrayList<String>();
        String titleStr = "设备名称;设备标识;姓名;人员身份;工号;车牌;进出方向;时间;通行照片";
        String[] titlesArr = titleStr.split(";");
        for (String s : titlesArr) {
            titles.add(s);
        }
        //创建第0行表头
        HSSFRow row0 = sheet.createRow(0);
        //设置行高
        row0.setHeightInPoints((short) 30);
        for (int i = 0; i < titles.size(); i++) {
            HSSFCell c0 = row0.createCell(i);
            c0.setCellValue(new HSSFRichTextString(titles.get(i)));
            c0.setCellStyle(styleTitle);
            sheet.autoSizeColumn(i);
        }

        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterator<VehicleRecord> ir = list.iterator();
        int cells = 0;
        int rowCont = 1;
        while (ir.hasNext()) {
            HSSFRow row = sheet.createRow(rowCont);
            VehicleRecord rv = (VehicleRecord) ir.next();
            if (null != rv.getDeviceName()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getDeviceName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getDeviceCode()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getDeviceCode());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 3 + 512);
                cells++;
            }
            if (null != rv.getName()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (rv.getVehType() == 1) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue("员工");
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            } else if (rv.getVehType() == 2) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue("访客");
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            } else {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue("未知");
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getEmpNo()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getEmpNo());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 250 + 512);
                cells++;
            }
            if (null != rv.getPlateNum()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getPlateNum());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (rv.getsType() == 1) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue("进");
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 250 + 512);
                cells++;
            } else {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue("出");
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 250 + 512);
                cells++;
            }
            if (null != rv.getsTime()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(time.format(rv.getsTime()));
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            rowCont++;
            cells = 0;
        }
        sheet.createFreezePane(0, 1, 0, 1);
        return wb;
    }

    /**
     * 导出员工临时卡记录
     * @param list
     * @param excel
     * @return
     * @throws Exception
     */
    @Override
    public ExcelModel createDownLoadExcelIc(List<InterimCard> list,
                                            ExcelModel excel) throws Exception {
        // TODO Auto-generated method stub


        String titleStr = "姓名#Name;部门#Department;工号#UserCode;卡号#CardNo;申请时间#SubmitTime;归还时间#ReturnTime;";

        List<List<String>> data = new ArrayList<List<String>>();

        Iterator<InterimCard> ir = list.iterator();
        while (ir.hasNext()) {

            List<String> rowData = new ArrayList<String>();

            InterimCard rv = (InterimCard) ir.next();

            rowData.add(rv.getName());
            rowData.add(rv.getDeptName());
            rowData.add(rv.getEmpNo());
            rowData.add(rv.getCardNo());
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (null != rv.getSubmitTime()) {
                rowData.add(time.format(rv.getSubmitTime()));
            } else {
                rowData.add("");
            }

            if (null != rv.getReturnTime()) {
                rowData.add(time.format(rv.getReturnTime()));
            } else {
                rowData.add("");
            }

            data.add(rowData);

        }

        String[] titles = titleStr.split(";");

        /**
         * for(int i=0;i<titles.length;i++){
         System.out.print(titles[i]+" ");
         }*/

        List<String> header = new ArrayList<String>();
        for (int i = 0; i < titles.length; i++) {
            header.add(titles[i]);
        }

        //设置报表标题
        excel.setHeader(header);
        //设置报表内容
        excel.setData(data);
        return excel;

    }

    /**
     * 导出人员通行记录
     * @param list
     * @param wb
     * @param exportCols
     * @return
     * @throws Exception
     */
    @Override
    public HSSFWorkbook createDownLoadExcel(List<OpendoorInfo> list,
                                            HSSFWorkbook wb, String exportCols) throws Exception {

        //样式设置========
        HSSFCellStyle styleTitle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, true);
        HSSFCellStyle fontStyle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, false);

        HSSFSheet sheet = wb.createSheet();
        //创建第0行表头
        HSSFRow row0 = sheet.createRow(0);
        //设置行高
        row0.setHeightInPoints((short) 30);
        //创建单元格标题
        List<String> cols = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();
        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int i = 0; i < exportCols.length(); i++) {
            cols.add(exportCols.substring(i, i + 1));
        }

        for (int a = 0; a < cols.size(); a++) {
            if ("1".equals(cols.get(a))) {
                switch (a) {
                    case 0:
                        titles.add("设备名称");
                        break;
                    case 1:
                        titles.add("设备标识");
                        break;
                    case 2:
                        titles.add("门岗");
                        break;
                    case 3:
                        titles.add("姓名");
                        break;
                    case 4:
                        titles.add("人员类型");
                        break;
                    case 5:
                        titles.add("所属公司");
                        break;
                    case 6:
                        titles.add("方向");
                        break;
                    case 7:
                        titles.add("电话号码");
                        break;
                    case 8:
                        titles.add("温度");
                        break;
                    case 9:
                        titles.add("时间");
                        break;
                    case 10:
                        titles.add("状态");
                        break;
                }
            }
        }

        for (int i = 0; i < titles.size(); i++) {
            HSSFCell c0 = row0.createCell(i);
            c0.setCellValue(new HSSFRichTextString(titles.get(i)));
            c0.setCellStyle(styleTitle);
            sheet.autoSizeColumn(i);
        }

        Iterator<OpendoorInfo> ir = list.iterator();
        int cells = 0;
        int rowCont = 1;
        while (ir.hasNext()) {
            OpendoorInfo rv = (OpendoorInfo) ir.next();
            HSSFRow row = sheet.createRow(rowCont);

            if ("1".equals(cols.get(0))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getDeviceName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 460 + 512);
                cells++;

            }
            if ("1".equals(cols.get(1))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getDeviceCode());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 4 + 512);
                cells++;
            }
            if ("1".equals(cols.get(2))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getGname());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(3))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getVname());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(4))) {
                HSSFCell cell = row.createCell(cells);
                if (null == rv.getVtype()) {
                    cell.setCellValue("");
                } else {
                    if ("0".equals(rv.getVtype())) {
                        cell.setCellValue("员工");
                    } else if ("1".equals(rv.getVtype())) {
                        cell.setCellValue("邀请访客");
                    } else if ("2".equals(rv.getVtype())) {
                        cell.setCellValue("签到访客");
                    } else if ("3".equals(rv.getVtype())) {
                        cell.setCellValue("常驻访客");
                    } else {
                        cell.setCellValue(rv.getVtype());
                    }
                }
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(5))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getCompany());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(6))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getDirection());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(7))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getMobile());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(8))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getTemp());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if ("1".equals(cols.get(9))) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(time.format(rv.getOpenDate()));
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            if ("1".equals(cols.get(10))) {
                HSSFCell cell = row.createCell(cells);
                if ("1".equals(rv.getOpenStatus())){
                    cell.setCellValue("成功");
                }else if ("0".equals(rv.getOpenStatus())){
                    cell.setCellValue("失败");
                }else {
                    cell.setCellValue(rv.getOpenStatus());
                }
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            rowCont++;
            cells = 0;
        }
        sheet.createFreezePane(0, 1, 0, 1);
        return wb;
    }

    /**
     * 导出操作日志
     * @param olist
     * @param wb
     * @return
     * @throws Exception
     */
    @Override
    public HSSFWorkbook createDownLoadLogExcel(List<OperateLog> olist,
                                               HSSFWorkbook wb) throws Exception {
        // TODO Auto-generated method stub
        //样式设置========
        HSSFCellStyle styleTitle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, true);
        HSSFCellStyle fontStyle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, false);
        HSSFSheet sheet = wb.createSheet();
        //创建单元格标题
        List<String> cols = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();
        String titleStr = "操作者;操作账号;操作角色;IP地址;操作模块;操作端;操作事件;操作对象;操作id;操作结果;操作时间;";
        String[] titlesArr = titleStr.split(";");
        for (String s : titlesArr) {
            titles.add(s);
        }
        //创建第0行表头
        HSSFRow row0 = sheet.createRow(0);
        //设置行高
        row0.setHeightInPoints((short) 30);
        for (int i = 0; i < titles.size(); i++) {
            HSSFCell c0 = row0.createCell(i);
            c0.setCellValue(new HSSFRichTextString(titles.get(i)));
            c0.setCellStyle(styleTitle);
            sheet.autoSizeColumn(i);
        }
        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterator<OperateLog> ir = olist.iterator();
        int cells = 0;
        int rowCont = 1;
        while (ir.hasNext()) {
            OperateLog rv = (OperateLog) ir.next();
            HSSFRow row = sheet.createRow(rowCont);
            if (null != rv.getOptName()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getOptName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getOptId()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getOptId());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            if (null != rv.getOptRole()) {
                HSSFCell cell = row.createCell(cells);
                if ("0".equals(rv.getOptRole())) {
                    cell.setCellValue("管理员");
                } else if ("1".equals(rv.getOptRole())) {
                    cell.setCellValue("HSE");
                } else if ("2".equals(rv.getOptRole())) {
                    cell.setCellValue("员工");
                } else if ("3".equals(rv.getOptRole())) {
                    cell.setCellValue("前台");
                } else {
                    cell.setCellValue("其他");
                }
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getIpAddr()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getIpAddr());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 450 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 450 + 512);
                cells++;
            }
            if (null != rv.getOptModule()) {
                HSSFCell cell = row.createCell(cells);
                if ("0".equals(rv.getOptModule())) {
                    cell.setCellValue("登录");
                } else if ("1".equals(rv.getOptModule())) {
                    cell.setCellValue("员工");
                } else if ("2".equals(rv.getOptModule())) {
                    cell.setCellValue("访客");
                } else if ("3".equals(rv.getOptModule())) {
                    cell.setCellValue("供应商");
                } else if ("4".equals(rv.getOptModule())) {
                    cell.setCellValue("入驻企业");
                } else if ("5".equals(rv.getOptModule())) {
                    cell.setCellValue("门禁");
                } else if ("6".equals(rv.getOptModule())) {
                    cell.setCellValue("黑名单");
                } else {
                    cell.setCellValue("其他");
                }
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getOptClient()) {
                HSSFCell cell = row.createCell(cells);
                if ("0".equals(rv.getOptClient())) {
                    cell.setCellValue("PC");
                } else if ("1".equals(rv.getOptClient())) {
                    cell.setCellValue("移动");
                } else {
                    cell.setCellValue("其他");
                }
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }

            if (null != rv.getOptEvent()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getOptEvent());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getObjName()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getObjName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getObjId()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getObjId());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getOptDesc()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rv.getOptDesc());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 50 * 300 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rv.getoTime()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(time.format(rv.getoTime()));
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }else {
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            rowCont++;
            cells = 0;
        }
        sheet.createFreezePane(0, 1, 0, 1);
        return wb;
    }

    /**
     * 导出员工异常状态
     * @param olist
     * @param excel
     * @return
     * @throws Exception
     */
    @Override
    public ExcelModel createDownLoadEmpExcel(List<Employee> olist,
                                             ExcelModel excel) throws Exception {
        // TODO Auto-generated method stub
        String titleStr = "姓名;工号;邮箱;手机号;部门;人脸错误";

        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterator<Employee> ir = olist.iterator();
        while (ir.hasNext()) {

            List<String> rowData = new ArrayList<String>();

            Employee rv = (Employee) ir.next();
            if (null != rv.getEmpName()) {
                rowData.add(rv.getEmpName());
            } else {
                rowData.add("");
            }
            if (null != rv.getEmpNo()) {
                rowData.add(rv.getEmpNo());
            } else {
                rowData.add("");
            }
            if (null != rv.getEmpEmail()) {
                rowData.add(rv.getEmpEmail());
            } else {
                rowData.add("");
            }
            if (null != rv.getEmpPhone()) {
                rowData.add(rv.getEmpPhone());
            } else {
                rowData.add("");
            }
            if (null != rv.getDeptName()) {
                rowData.add(rv.getDeptName());
            } else {
                rowData.add("");
            }
            if (rv.getFace() == 0) {
                rowData.add("正常");
            } else if (rv.getFace() == 1) {
                rowData.add("照片错误");
            } else if (rv.getFace() == 2) {
                rowData.add("未检测到人脸");
            } else if (rv.getFace() == 3) {
                rowData.add("检测到多个人脸");
            } else if (rv.getFace() == 4) {
                rowData.add("照片太大");
            } else if (rv.getFace() == 5) {
                rowData.add("工号重复");
            } else {
                rowData.add("照片未同步");
            }


            data.add(rowData);

        }


        String[] titles = titleStr.split(";");
        
        /*for(int i=0;i<titles.length;i++){
            System.out.print(titles[i]+" ");
        }*/

        List<String> header = new ArrayList<String>();
        for (int i = 0; i < titles.length; i++) {
            header.add(titles[i]);
        }

        //设置报表标题
        excel.setHeader(header);
        //设置报表内容
        excel.setData(data);
        return excel;
    }

    /**
     * 导出常驻供应商通行记录
     * @param olist
     * @param excel
     * @return
     * @throws Exception
     */
    @Override
    public ExcelModel createDownLoadRvExcel(List<RvReport> olist,
                                            ExcelModel excel) throws Exception {
        // TODO Auto-generated method stub
        String titleStr = "供应商名称;项目名称;人员姓名;进入时间;离开时间;负责人姓名";

        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterator<RvReport> ir = olist.iterator();
        while (ir.hasNext()) {
            List<String> rowData = new ArrayList<String>();

            RvReport rv = (RvReport) ir.next();

            rowData.add(rv.getCompany());
            rowData.add(rv.getpName());
            rowData.add(rv.getName());

            if (null != rv.getEnterDoor()) {
                rowData.add(time.format(rv.getEnterDoor()));
            } else {
                rowData.add("");
            }
            if (null != rv.getOutDoor()) {
                rowData.add(time.format(rv.getOutDoor()));
            } else {
                rowData.add("");
            }
            if (null != rv.getLeader()) {
                rowData.add(rv.getLeader());
            } else {
                rowData.add("");
            }

            data.add(rowData);
        }


        String[] titles = titleStr.split(";");
	        
	        /*for(int i=0;i<titles.length;i++){
	            System.out.print(titles[i]+" ");
	        }*/

        List<String> header = new ArrayList<String>();
        for (int i = 0; i < titles.length; i++) {
            header.add(titles[i]);
        }

        //设置报表标题
        excel.setHeader(header);

        //设置报表内容
        excel.setData(data);
        return excel;
    }

    /**
     * 导出常驻供应商
     * @param olist
     * @param excel
     * @return
     * @throws Exception
     */
    @Override
    public ExcelModel createDownLoadRvrExcel(List<RvReport> olist,
                                             ExcelModel excel) throws Exception {
        String titleStr = "日期;供应商名称;项目名称;到场人数";

        List<List<String>> data = new ArrayList<List<String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Iterator<RvReport> ir = olist.iterator();
        while (ir.hasNext()) {
            List<String> rowData = new ArrayList<String>();

            RvReport rv = (RvReport) ir.next();
            rowData.add(time.format(rv.getrDate()));
            rowData.add(rv.getCompany());
            rowData.add(rv.getpName());
            rowData.add(String.valueOf(rv.getrCount()));

            data.add(rowData);
        }


        String[] titles = titleStr.split(";");

        List<String> header = new ArrayList<String>();
        for (int i = 0; i < titles.length; i++) {
            header.add(titles[i]);
        }

        //设置报表标题
        excel.setHeader(header);

        //设置报表内容
        excel.setData(data);
        return excel;
    }

    /**
     * 导出黑名单
     * @param olist
     * @param wb
     * @param userService
     * @param userid
     * @return
     * @throws Exception
     */
    @Override
    public HSSFWorkbook createDownLoadRblExcel(List<Blacklist> olist,
                                               HSSFWorkbook wb, UserService userService,int userid) throws Exception {
        //样式设置========
        HSSFCellStyle styleTitle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, true);
        HSSFCellStyle fontStyle = creatStyle(wb, "楷体", 12, HSSFCellStyle.ALIGN_CENTER, false, false);

        HSSFSheet sheet = wb.createSheet();


        //创建单元格标题
        List<String> cols = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();
        // TODO Auto-generated method stub
        String titleStr = "姓名;手机号;身份证;企业;备注";
        String[] titlesArr = titleStr.split(";");
        UserInfo userInfo = userService.getUserInfoByUserId(userid);
        for (int i = 0; i < titlesArr.length; i++) {
            if (0==userInfo.getSubAccount() && "企业".equals(titlesArr[i])){
                continue;
            }else {
                titles.add(titlesArr[i]);
            }
        }

        //创建第0行表头
        HSSFRow row0 = sheet.createRow(0);
        //设置行高
        row0.setHeightInPoints((short) 30);
        for (int i = 0; i < titles.size(); i++) {
            HSSFCell c0 = row0.createCell(i);
            c0.setCellValue(new HSSFRichTextString(titles.get(i)));
            c0.setCellStyle(styleTitle);
            sheet.autoSizeColumn(i);
        }

        Iterator<Blacklist> ir = olist.iterator();
        int cells = 0;
        int rowCont = 1;
        while (ir.hasNext()) {
            List<String> rowData = new ArrayList<String>();
            Blacklist rbl = (Blacklist) ir.next();
            HSSFRow row = sheet.createRow(rowCont);
            if (null != rbl.getName()) {
                rowData.add(rbl.getName());
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rbl.getName());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 + 512);
                cells++;
            }
            if (null != rbl.getPhone()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rbl.getPhone());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 350 + 512);
                cells++;
            }
            if (null != rbl.getCredentialNo()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rbl.getCredentialNo());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            if (0!=userInfo.getSubAccount()){
                if (null != rbl.getSname()) {
                    HSSFCell cell = row.createCell(cells);
                    cell.setCellValue(rbl.getSname());
                    cell.setCellStyle(fontStyle);
                    sheet.setColumnWidth(cells, 10 * 300 * 4 + 512);
                    cells++;
                }
            }

            if (null != rbl.getRemark()) {
                HSSFCell cell = row.createCell(cells);
                cell.setCellValue(rbl.getRemark());
                cell.setCellStyle(fontStyle);
                sheet.setColumnWidth(cells, 10 * 300 * 2 + 512);
                cells++;
            }
            rowCont++;
            cells = 0;
        }
        sheet.createFreezePane(0, 1, 0, 1);
        return wb;

    }

    /**
     * 创建IFS访客记录表
     *
     * @param list
     * @param exportCols
     * @param gateMap
     * @param workBook
     * @return
     */
    @Override
    public SXSSFWorkbook createDownLoadExcel(List<VisitorRecord> list, String exportCols, Map<String, String> gateMap, SXSSFWorkbook workBook) {

        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //建立新的sheet对象（excel的表单）
        Sheet sheet = workBook.createSheet("通行记录");       //创建Excel工作表（页签）

        int[] width = {3500, 2000, 3000, 5000, 8000, 3000, 3000, 3300, 5000, 3000,
                4000, 3000, 3000, 3000, 5000, 3000, 3000, 8000, 8000, 8000};
        for (int i = 0; i < width.length; i++) {
            //设置列宽
            sheet.setColumnWidth(i, width[i]);
        }

        //excel列
        List<String> cols = new ArrayList<String>();
        for (int i = 0; i < exportCols.length(); i++) {
            cols.add(exportCols.substring(i, i + 1));
        }

        List<String> titles = new ArrayList<String>();
        for (int a = 0; a < cols.size(); a++) {
            if ("1".equals(cols.get(a))) {
                switch (a) {
                    case 0:
                        titles.add("日期");
                        break;
                    case 1:
                        titles.add("时间");
                        break;
                    case 2:
                        titles.add("登记授权等待时长(s)");
                        break;
                    case 3:
                        titles.add("楼栋");
                        break;
                    case 4:
                        titles.add("访问租户名称");
                        break;
                    case 5:
                        titles.add("访客姓名");
                        break;
                    case 6:
                        titles.add("访客电话");
                        break;
                    case 7:
                        titles.add("访问目的");
                        break;
                    case 8:
                        titles.add("黑名单" + "\n" + "(是/否)");
                        break;
                    case 9:
                        titles.add("签到时间");
                        break;
                    case 10:
                        titles.add("预约途径");
                        break;
                    case 11:
                        titles.add("性别");
                        break;
                    case 12:
                        titles.add("团队人数");
                        break;
                    case 13:
                        titles.add("次数");
                        break;
                    case 14:
                        titles.add("授权人");
                        break;
                    case 15:
                        titles.add("是否取卡");
                        break;
                    case 16:
                        titles.add("门禁卡号");
                        break;
                    case 17:
                        titles.add("是否周末时段拜访");
                        break;
                    case 18:
                        titles.add("是否节假日时段拜访");
                        break;
                    case 19:
                        titles.add("是否梯控时间时段拜访");
                        break;
//                    case 19:
//                        titles.add("是否调休日拜访");
//                        break;
                }
            }
        }

        Row row = sheet.createRow(0);
        CellStyle style = ExcelUtils.getHeaderStyle(workBook);             //获取表头样式
        for (int i = 0; i < titles.size(); i++) {
            row.createCell(i).setCellValue(titles.get(i));
            row.getCell(i).setCellStyle(style);                 //设置标题样式
        }

        //excel内容赋值
        int index = 0;//记录额外创建的sheet数量
        int totalRow = 1048576;
//        int totalRow = 56181;
        for (int i = 0; i < list.size(); i++) {
            Row sheetRow3 = null;
            if ((i + 1) % totalRow == 0) {
                index++;
                sheet = workBook.createSheet("IFS人员通行记录(序)" + index);
                //设置列宽
                for (int a = 0; a < width.length; a++) {
                    sheet.setColumnWidth(a, width[a]);
                }
                Row sheetRow1 = sheet.createRow(0);
                for (int j = 0; j < titles.size(); j++) {
                    sheetRow1.createCell(j).setCellValue(titles.get(j));
                    sheetRow1.getCell(j).setCellStyle(style);                 //设置标题样式
                }
                sheetRow3 = sheet.createRow((i + 1) - (index * totalRow) + 1);
            } else {
                sheetRow3 = sheet.createRow((i + 1) - (index * totalRow));
            }
            VisitorRecord record = list.get(i);
            if ("1".equals(cols.get(0))) {
                Cell cell0 = sheetRow3.createCell(0);
                cell0.setCellValue(record.getAppDate());
            }
            if ("1".equals(cols.get(1))) {
                Cell cell1 = sheetRow3.createCell(1);
                if (null == record.getAppVisitTime()) {
                    cell1.setCellValue("");
                } else {
                    cell1.setCellValue(record.getAppVisitTime());
                }
            }
            if ("1".equals(cols.get(2))) {
                Cell cell2 = sheetRow3.createCell(2);
                if (null == record.getWaitPermissionSeconds()) {
                    cell2.setCellValue("");
                } else {
                    cell2.setCellValue(record.getWaitPermissionSeconds());
                }
                sheet.setColumnWidth(2, 256 * 20 + 184);
            }
            if ("1".equals(cols.get(3))) {
                Cell cell3 = sheetRow3.createCell(3);
                if (StringUtils.isNotEmpty(record.getFloors())) {
                    cell3.setCellValue(record.getFloors());
                } else {
                    cell3.setCellValue("");
                }
            }
            if ("1".equals(cols.get(4))) {
                Cell cell4 = sheetRow3.createCell(4);
                if (null == record.getCompany()) {
                    cell4.setCellValue("");
                } else {
                    cell4.setCellValue(record.getCompany());
                }
            }
            if ("1".equals(cols.get(5))) {
                Cell cell5 = sheetRow3.createCell(5);
                if (null == record.getVName()) {
                    cell5.setCellValue("");
                } else {
                    cell5.setCellValue(record.getVName());
                }
            }
            if ("1".equals(cols.get(6))) {
                Cell cell6 = sheetRow3.createCell(6);
                if (null == record.getVPhone()) {
                    cell6.setCellValue("");
                } else {
                    cell6.setCellValue(record.getVPhone());
                }
            }
            if ("1".equals(cols.get(7))) {
                Cell cell7 = sheetRow3.createCell(7);
                if (null == record.getVisitType()) {
                    cell7.setCellValue("");
                } else {
                    cell7.setCellValue(record.getVisitType());
                }
            }
            if ("1".equals(cols.get(8))) {
                Cell cell8 = sheetRow3.createCell(8);
                if (StringUtils.isNotEmpty(record.getBCompany())) {
                    cell8.setCellValue(record.getBCompany());
                } else {
                    cell8.setCellValue("");
                }
            }
            if ("1".equals(cols.get(9))) {
                Cell cell9 = sheetRow3.createCell(9);
                if (null == record.getVisitdate()) {
                    cell9.setCellValue("");
                } else {
                    cell9.setCellValue(record.getVisitdate());
                }
            }
            if ("1".equals(cols.get(10))) {
                Cell cell10 = sheetRow3.createCell(10);
                if (null == record.getClientNo()) {
                    cell10.setCellValue("");
                } else {
                    int clientNo = Integer.parseInt(record.getClientNo());
                    //1-小程序 2-PC端 3-前台 4-访客机 5-pad
                    switch (clientNo) {
                        case 1:
                            cell10.setCellValue("小程序");
                            break;
                        case 2:
                            cell10.setCellValue("PC端");
                            break;
                        case 3:
                            cell10.setCellValue("前台");
                            break;
                        case 4:
                            cell10.setCellValue("访客机");
                            break;
                        case 5:
                            cell10.setCellValue("pad端");
                            break;
                    }
                }
            }
            if ("1".equals(cols.get(11))) {
                Cell cell11 = sheetRow3.createCell(11);
                if (null == record.getSex()) {
                    cell11.setCellValue("");
                } else {
                    if (0 == Integer.parseInt(record.getSex())) {
                        cell11.setCellValue("女");
                    } else if (1 == Integer.parseInt(record.getSex())) {
                        cell11.setCellValue("男");
                    }
                }
            }
            if ("1".equals(cols.get(12))) {
                Cell cell12 = sheetRow3.createCell(12);
                if (StringUtils.isEmpty(record.getPeopleCount())) {
                    cell12.setCellValue(0);
                } else {
                    cell12.setCellValue(record.getPeopleCount());
                }
            }
            if ("1".equals(cols.get(13))) {
                Cell cell13 = sheetRow3.createCell(13);
                if (0 == record.getVisitorCount()) {
                    cell13.setCellValue(0);
                } else {
                    cell13.setCellValue(record.getVisitorCount());
                }
            }
            if ("1".equals(cols.get(14))) {
                Cell cell14 = sheetRow3.createCell(14);
                if (null == record.getPermissionName()) {
                    cell14.setCellValue("");
                } else {
                    cell14.setCellValue(record.getPermissionName());
                }
            }
            if ("1".equals(cols.get(15))) {
                Cell cell15 = sheetRow3.createCell(15);
                if (null == record.getIsTakeCard()) {
                    cell15.setCellValue("");
                } else {
                    cell15.setCellValue(record.getIsTakeCard());
                }
            }
            if ("1".equals(cols.get(16))) {
                Cell cell16 = sheetRow3.createCell(16);
                if (null == record.getCardNo()) {
                    cell16.setCellValue("");
                } else {
                    cell16.setCellValue(record.getCardNo());
                }
            }
            if ("1".equals(cols.get(17))) {
                Cell cell17 = sheetRow3.createCell(17);
                if (null == record.getIsWeekendVisitor()) {
                    cell17.setCellValue("");
                } else {
                    cell17.setCellValue(record.getIsWeekendVisitor());
                }
            }
            if ("1".equals(cols.get(18))) {
                Cell cell18 = sheetRow3.createCell(18);
                if (null == record.getIsHolidayVisitor()) {
                    cell18.setCellValue("");
                } else {
                    cell18.setCellValue(record.getIsHolidayVisitor());
                }
            }
            if ("1".equals(cols.get(19))) {
                Cell cell19 = sheetRow3.createCell(19);
                if (null == record.getIsSCTimeVisitor()) {
                    cell19.setCellValue("");
                } else {
                    cell19.setCellValue(record.getIsSCTimeVisitor());
                }
            }

        }
        return workBook;
    }

    public static HSSFCellStyle creatStyle(HSSFWorkbook wb, String fontName, int fontHeightInPoints, short alignment, boolean border, boolean boldweight) {
        //设置内容字体
        HSSFFont fontContent = wb.createFont();
        if (boldweight) {
            // 字体加粗
            fontContent.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        }
        fontContent.setFontName(fontName);
        fontContent.setFontHeightInPoints((short) fontHeightInPoints);
        //创建内容样式
        HSSFCellStyle styleContent = wb.createCellStyle();
        styleContent.setFont(fontContent);
        styleContent.setWrapText(true);
        styleContent.setAlignment(alignment);
        styleContent.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        if (border) {
            styleContent.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleContent.setBottomBorderColor(HSSFColor.BLACK.index);
            styleContent.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleContent.setLeftBorderColor(HSSFColor.BLACK.index);
            styleContent.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleContent.setRightBorderColor(HSSFColor.BLACK.index);
            styleContent.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleContent.setTopBorderColor(HSSFColor.BLACK.index);
        }
        return styleContent;
    }


}