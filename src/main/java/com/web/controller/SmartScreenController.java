package com.web.controller;

import com.client.bean.*;
import com.client.service.*;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.web.bean.AuthToken;
import com.web.bean.RespInfo;
import com.web.bean.VisitorChart;
import com.web.service.TokenServer;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("smartScreen")
public class SmartScreenController {


    private final OpendoorService opendoorService;

    private final EquipmentService equipmentService;

    private final EquipmentGroupService equipmentGroupService;

    private final EGRelationService egRelationService;

    private final VehicleRecordService vehicleRecordService;

    private final TokenServer tokenServer;


    @ApiOperation(value = "/getAllEnterAndLeave 获取访客统计", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"signinType\":\"signinType\",\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getAllEnterAndLeave")
    public RespInfo getAllEnterAndLeave(@RequestBody ReqODI reqodi, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        /*DataStatistics ds = new DataStatistics();
        ds.setUserid(rv.getUserid());

        if ("0".equals(rv.getSigninType())) {
            ds = visitorService.getSignedCount(rv);
        }*/
        List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
        List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();
        if (0 != reqodi.getEgid()) {
            EquipmentGroup equipmentGroup = new EquipmentGroup();
            equipmentGroup.setUserid(reqodi.getUserid());
            equipmentGroup.setEgid(reqodi.getEgid());
            List<Equipment> equipmentByEgid = egRelationService.getEquipmentByEgid(equipmentGroup);
            if (!equipmentByEgid.isEmpty()) {
                for (Equipment equipment : equipmentByEgid) {
                    reqodi.setDeviceCode(equipment.getDeviceCode());
                    List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);
                    if (!opendoorInfo.isEmpty()) {
                        for (OpendoorInfo info : opendoorInfo) {
                            if (StringUtils.isBlank(info.getDirection())) {
                                continue;
                            }
                            if (StringUtils.isBlank(info.getOpenStatus()) || !"成功".equalsIgnoreCase(info.getOpenStatus())) {
                                continue;
                            }
                            if ("进门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoEnter.add(info);
                            } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoOut.add(info);
                            }
                        }
                    }
                }
            }
        } else {
            List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);
            if (!opendoorInfo.isEmpty()) {
                for (OpendoorInfo info : opendoorInfo) {
                    if (StringUtils.isBlank(info.getDirection())) {
                        continue;
                    }
                    if ("进门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoEnter.add(info);
                    } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoOut.add(info);
                    }
                }
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("enter", opendoorInfoEnter.size());
        map.put("out", opendoorInfoOut.size());
        //根据手机号去重
        /*Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
        opendoorInfoSetEnter.addAll(opendoorInfoEnter);
        Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
        opendoorInfoSetOut.addAll(opendoorInfoOut);*/
        //数据不准确
        map.put("active", opendoorInfoEnter.size() - opendoorInfoOut.size());
        return new RespInfo(0, "success", map);
    }

    @ApiOperation(value = "/getOpenDoor 获取访客进门出门统计", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"startDate\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getOpenDoor")
    public RespInfo getOpenDoorPassInfo(@RequestBody ReqODI reqodi, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
        List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();

        //车辆
        List<VehicleRecord> vehicleRecord = new ArrayList<>();

        //时间转换
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(new Date());

        if (0 != reqodi.getEgid()) {
            EquipmentGroup equipmentGroup = new EquipmentGroup();
            equipmentGroup.setUserid(reqodi.getUserid());
            equipmentGroup.setEgid(reqodi.getEgid());
            List<Equipment> equipmentByEgid = egRelationService.getEquipmentByEgid(equipmentGroup);
            if (!equipmentByEgid.isEmpty()) {
                for (Equipment equipment : equipmentByEgid) {

                    reqodi.setDeviceCode(equipment.getDeviceCode());
                    List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);

                    if (!opendoorInfo.isEmpty()) {
                        for (OpendoorInfo info : opendoorInfo) {
                            if (StringUtils.isBlank(info.getDirection())) {
                                continue;
                            }
                            if (StringUtils.isBlank(info.getOpenStatus()) || !"成功".equals(info.getOpenStatus())) {
                                continue;
                            }
                            if ("进门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoEnter.add(info);
                            } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoOut.add(info);
                            }
                        }
                    }

                    //车辆通行
                    ReqVR reqVR = new ReqVR();
                    reqVR.setUserid(equipmentGroup.getUserid());
                    reqVR.setStartDate(format);
                    reqVR.setEndDate(format);
                    reqVR.setDeviceCode(equipment.getDeviceCode());
                    List<VehicleRecord> vehicleRecord1 = vehicleRecordService.getVehicleRecord(reqVR);
                    for (VehicleRecord record : vehicleRecord1) {
                        vehicleRecord.add(record);
                    }
                }
            }

        } else {
            List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);
            if (!opendoorInfo.isEmpty()) {
                for (OpendoorInfo info : opendoorInfo) {
                    if (StringUtils.isBlank(info.getDirection())) {
                        continue;
                    }
                    if (StringUtils.isBlank(info.getOpenStatus()) || !"成功".equals(info.getOpenStatus())) {
                        continue;
                    }
                    if ("进门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoEnter.add(info);
                    } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoOut.add(info);
                    }
                }
            }
            //车辆通行
            ReqVR reqVR = new ReqVR();
            reqVR.setUserid(reqodi.getUserid());
            reqVR.setStartDate(format);
            reqVR.setEndDate(format);
            List<VehicleRecord> vehicleRecord1 = vehicleRecordService.getVehicleRecord(reqVR);
            for (VehicleRecord record : vehicleRecord1) {
                vehicleRecord.add(record);
            }
        }

        List<OpendoorInfo> opendoorInfosSP = new ArrayList<>();
        List<OpendoorInfo> opendoorInfosQrCode = new ArrayList<>();
        List<OpendoorInfo> opendoorInfosSK = new ArrayList<>();
        List<OpendoorInfo> opendoorInfosOther = new ArrayList<>();
        //根据手机号去重
        /**Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
         opendoorInfoSetEnter.addAll(opendoorInfoEnter);
         Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
         opendoorInfoSetOut.addAll(opendoorInfoOut);
         List<OpendoorInfo> mergn = new ArrayList<>();
         mergn.addAll(opendoorInfoEnter);
         mergn.addAll(opendoorInfoOut);
         */
        List<OpendoorInfo> OpendoorInfosMergn = new ArrayList<>();
        OpendoorInfosMergn.addAll(opendoorInfoEnter);
        OpendoorInfosMergn.addAll(opendoorInfoOut);

        if (!OpendoorInfosMergn.isEmpty()) {
            for (OpendoorInfo info : OpendoorInfosMergn) {
                /**String deviceCode = info.getDeviceCode();
                 Equipment equipment = new Equipment();
                 equipment.setDeviceCode(deviceCode);
                 Equipment equipmentbyDeviceCode = equipmentService.getEquipmentbyDeviceCode(equipment);*/
                if (StringUtils.isNotBlank(info.getPassWay())) {
                    if (info.getPassWay().equalsIgnoreCase("人脸")) {
                        opendoorInfosSP.add(info);
                    } else if (info.getPassWay().equalsIgnoreCase("二维码")) {
                        opendoorInfosQrCode.add(info);
                    } else if (info.getPassWay().equalsIgnoreCase("刷卡")) {
                        opendoorInfosSK.add(info);
                    }
                }
            }
        }
        //人脸通行
        Map<String, Integer> openSP = new HashMap<>();
        Integer openInfoEnter = 0;
        Integer openInfoOut = 0;
        if (!opendoorInfosSP.isEmpty()) {
            for (OpendoorInfo info : opendoorInfosSP) {
                if ("进门".equalsIgnoreCase(info.getDirection())) {
                    openInfoEnter++;
                } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                    openInfoOut++;
                }
            }
        }
        openSP.put("enter", openInfoEnter);
        openSP.put("out", openInfoOut);
        //二维码通行
        Map<String, Integer> openQrCode = new HashMap<>();
        Integer openQrCodeEnter = 0;
        Integer openQrCodeOut = 0;
        if (!opendoorInfosQrCode.isEmpty()) {
            for (OpendoorInfo info : opendoorInfosQrCode) {
                if ("进门".equalsIgnoreCase(info.getDirection())) {
                    openQrCodeEnter++;
                } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                    openQrCodeOut++;
                }
            }
        }
        openQrCode.put("enter", openQrCodeEnter);
        openQrCode.put("out", openQrCodeOut);
        //刷卡通行
        Map<String, Integer> openSK = new HashMap<>();
        Integer openSKEnter = 0;
        Integer openSKOut = 0;
        if (!opendoorInfosSK.isEmpty()) {
            for (OpendoorInfo info : opendoorInfosSK) {
                if ("进门".equalsIgnoreCase(info.getDirection())) {
                    openSKEnter++;
                } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                    openSKOut++;
                }
            }
        }
        openSK.put("enter", openSKEnter);
        openSK.put("out", openSKOut);
        //其他
        Map<String, Integer> openOther = new HashMap<>();

        if (!opendoorInfosOther.isEmpty()) {
            for (OpendoorInfo info : opendoorInfosOther) {
                if ("进门".equalsIgnoreCase(info.getDirection())) {
                    openSKEnter++;
                } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                    openSKOut++;
                }
            }
        }


        //List<VehicleRecord> vehicleRecord = vehicleRecordService.getVehicleRecord(reqVR);
        /** List<VehicleRecord> vehicleRecordEnter = new ArrayList<>();
         List<VehicleRecord> vehicleRecordOut = new ArrayList<>();
         List<VehicleRecord> vehicleRecordMergn = new ArrayList<>();

         if (!vehicleRecord.isEmpty()) {
         for (VehicleRecord record : vehicleRecord) {
         if (1 == record.getsType()) {
         vehicleRecordEnter.add(record);
         } else if (2 == record.getsType()) {
         vehicleRecordOut.add(record);
         }
         }
         }
         vehicleRecordMergn.addAll(vehicleRecordEnter);
         vehicleRecordMergn.addAll(vehicleRecordOut);
         Set<VehicleRecord> vehicleRecords = new TreeSet<>(Comparator.comparing(VehicleRecord::getPlateNum));
         vehicleRecords.addAll(vehicleRecordMergn);*/
        int veEnter = 0;
        int veOut = 0;
        if (!vehicleRecord.isEmpty()) {
            for (VehicleRecord record : vehicleRecord) {
                if (1 == record.getsType()) {
                    veEnter++;
                } else if (2 == record.getsType()) {
                    veOut++;
                }
            }
        }


        openOther.put("enter", veEnter);
        openOther.put("out", veOut);

        Map<String, Map<String, Integer>> objectObjectHashMap = new HashMap<>();
        //二维码
        objectObjectHashMap.put("A", openQrCode);
        //刷脸
        objectObjectHashMap.put("B", openSP);
        //刷卡
        objectObjectHashMap.put("C", openSK);
        //其他
        objectObjectHashMap.put("D", openOther);
        return new RespInfo(0, "success", objectObjectHashMap);
    }

    @ApiOperation(value = "/getVisitType 获取人员类型", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getVisitType")
    public RespInfo getVisitType(@RequestBody ReqODI reqodi, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        //List<VisitorChart> allArrivedVisitorChartSmart = visitorService.getAllArrivedVisitorChartSmart(requestVisit);
        List<VisitorChart> allArrivedVisitorChartSmart = new ArrayList<>();
        List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
        List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();
        if (0 != reqodi.getEgid()) {
            EquipmentGroup equipmentGroup = new EquipmentGroup();
            equipmentGroup.setUserid(reqodi.getUserid());
            equipmentGroup.setEgid(reqodi.getEgid());
            List<Equipment> equipmentByEgid = egRelationService.getEquipmentByEgid(equipmentGroup);
            if (!equipmentByEgid.isEmpty()) {
                for (Equipment equipment : equipmentByEgid) {
                    reqodi.setDeviceCode(equipment.getDeviceCode());
                    List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);
                    if (!opendoorInfo.isEmpty()) {
                        for (OpendoorInfo info : opendoorInfo) {
                            if (StringUtils.isBlank(info.getDirection())) {
                                continue;
                            }
                            if (StringUtils.isBlank(info.getOpenStatus()) || !"成功".equals(info.getOpenStatus())) {
                                continue;
                            }
                            if ("进门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoEnter.add(info);
                            } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                opendoorInfoOut.add(info);
                            }
                        }
                    }
                }
            }
        } else {
            List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqodi);
            if (!opendoorInfo.isEmpty()) {
                for (OpendoorInfo info : opendoorInfo) {
                    if (StringUtils.isBlank(info.getDirection())) {
                        continue;
                    }
                    if (StringUtils.isBlank(info.getOpenStatus()) || !"成功".equals(info.getOpenStatus())) {
                        continue;
                    }
                    if ("进门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoEnter.add(info);
                    } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                        opendoorInfoOut.add(info);
                    }
                }
            }
        }

        //根据手机号去重
        /**Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
         opendoorInfoSetEnter.addAll(opendoorInfoEnter);
         Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
         opendoorInfoSetOut.addAll(opendoorInfoOut);*/
        //Map<String, List<OpendoorInfo>> collectList = new HashMap<>();
        //collectList = opendoorInfoOut.stream().collect(Collectors.groupingBy(OpendoorInfo::getVtype));

        if (0 == reqodi.getPopType()) {
            if (!opendoorInfoEnter.isEmpty()) {
                List<OpendoorInfo> opendoorInfoOutMap = opendoorInfoOut;
                Map<String, List<OpendoorInfo>> collect1 = opendoorInfoOutMap.stream().collect(Collectors.groupingBy(OpendoorInfo::getMobile));

                Map<String, List<OpendoorInfo>> opendoorInfoEnterMapList = opendoorInfoEnter.stream().collect(Collectors.groupingBy(OpendoorInfo::getMobile));
                Set<Map.Entry<String, List<OpendoorInfo>>> entries = opendoorInfoEnterMapList.entrySet();
                Iterator<Map.Entry<String, List<OpendoorInfo>>> iterator = entries.iterator();
                List<OpendoorInfo> opendoorInfosEnter = new ArrayList<>();
                //获取进门人数
                while (iterator.hasNext()) {
                    Map.Entry<String, List<OpendoorInfo>> next = iterator.next();
                    String key = next.getKey();

                    List<OpendoorInfo> opendoorInfos = collect1.get(key);
                    if (ObjectUtils.isNotEmpty(opendoorInfos)) {
                        if (opendoorInfos.isEmpty() || opendoorInfos.size() < collect1.get(key).size()) {
                            opendoorInfosEnter.add(iterator.next().getValue().get(0));
                        }
                    } else {
                        if (!next.getValue().isEmpty()) {
                            opendoorInfosEnter.add(next.getValue().get(0));
                        }
                    }
                }
                //根据访客类型分组
                if (opendoorInfosEnter.size() > 0) {
                    Map<String, List<OpendoorInfo>> collect = opendoorInfosEnter.stream().collect(Collectors.groupingBy(OpendoorInfo::getVtype));
                    if (!collect.isEmpty()) {
                        Set<Map.Entry<String, List<OpendoorInfo>>> entries1 = collect.entrySet();
                        Iterator<Map.Entry<String, List<OpendoorInfo>>> iterator1 = entries1.iterator();
                        while (iterator1.hasNext()) {
                            VisitorChart visitorChart = new VisitorChart();
                            Map.Entry<String, List<OpendoorInfo>> next = iterator1.next();
                            String key = next.getKey();
                            List<OpendoorInfo> value = next.getValue();
                            visitorChart.setvType(key);
                            visitorChart.setCount(String.valueOf(value.size()));
                            allArrivedVisitorChartSmart.add(visitorChart);
                        }
                    }
                }
            }
        } else if (1 == reqodi.getPopType()) {
            Map<String, List<OpendoorInfo>> collectList = opendoorInfoOut.stream().collect(Collectors.groupingBy(OpendoorInfo::getVtype));
            if (!collectList.isEmpty()) {
                Set<Map.Entry<String, List<OpendoorInfo>>> entries = collectList.entrySet();
                Iterator<Map.Entry<String, List<OpendoorInfo>>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    VisitorChart visitorChart = new VisitorChart();
                    Map.Entry<String, List<OpendoorInfo>> next = iterator.next();
                    String key = next.getKey();
                    List<OpendoorInfo> value = next.getValue();
                    visitorChart.setvType(key);
                    visitorChart.setCount(String.valueOf(value.size()));
                    allArrivedVisitorChartSmart.add(visitorChart);
                }
            }
        }
        return new RespInfo(0, "success", allArrivedVisitorChartSmart);
    }

    @ApiOperation(value = "/getFactoryMsg 获取厂区信息", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getFactoryMsg")
    public RespInfo getFactoryMsg(@RequestBody EGRelation eg, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<EGRelation> egRelations = egRelationService.getEGRelationList(eg);
        List<EquipmentGroup> equipmentGroups = new ArrayList<>();
        Map<String, List<Equipment>> objectObjectHashMap = new HashMap<>();
        List<Equipment> equipmentListSP = new ArrayList<>();
        List<Equipment> equipmentListZJ = new ArrayList<>();
        List<Equipment> equipmentListQR = new ArrayList<>();
        List<Equipment> equipmentListCA = new ArrayList<>();
        if (!egRelations.isEmpty()) {
            for (EGRelation egRelation : egRelations) {
                EquipmentGroup equipmentGroup = new EquipmentGroup();
                equipmentGroup.setEgid(egRelation.getEgid());
                EquipmentGroup equipmentGroupByEgid = equipmentGroupService.getEquipmentGroupByEgid(equipmentGroup);
                String[] eids = egRelation.getEids().split(",");
                int enter = 0;
                int out = 0;
                for (String eid : eids) {
                    Equipment equipment = new Equipment();
                    equipment.setEid(Integer.parseInt(eid));
                    Equipment equipmentbyEid = equipmentService.getEquipmentbyEid(equipment);
                    if (ObjectUtils.isNotEmpty(equipmentbyEid)) {
                        String eType = equipmentbyEid.geteType();
                        if (StringUtils.isNotBlank(eType)) {
                            if (eType.startsWith("SP")) {
                                equipmentListSP.add(equipmentbyEid);
                            } else if (eType.startsWith("QR")) {
                                equipmentListQR.add(equipmentbyEid);
                            } else if (eType.startsWith("CA")) {
                                if (eType.endsWith("PT")) {
                                    equipmentListZJ.add(equipmentbyEid);
                                } else {
                                    equipmentListCA.add(equipmentbyEid);
                                }
                            }
                            ReqODI reqODI = new ReqODI();
                            reqODI.setDeviceCode(equipmentbyEid.getDeviceCode());
                            reqODI.setUserid(eg.getUserid());
                            reqODI.setStartDate(eg.getStartDate());
                            reqODI.setEndDate(eg.getEndDate());
                            List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqODI);
                            /**
                             List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
                             List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();
                             if (!opendoorInfo.isEmpty()) {
                             for (OpendoorInfo info : opendoorInfo) {
                             if ("进门".equalsIgnoreCase(info.getDirection())) {
                             opendoorInfoEnter.add(info);
                             } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                             opendoorInfoOut.add(info);
                             }
                             }
                             }
                             //根据手机号去重
                             Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                             opendoorInfoSetEnter.addAll(opendoorInfoEnter);
                             Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                             opendoorInfoSetOut.addAll(opendoorInfoOut);

                             List<OpendoorInfo> mergn = new ArrayList<>();
                             mergn.addAll(opendoorInfoEnter);
                             mergn.addAll(opendoorInfoOut);*/
                            if (!opendoorInfo.isEmpty()) {
                                for (OpendoorInfo info : opendoorInfo) {
                                    if (StringUtils.isNotBlank(info.getOpenStatus()) && "成功".equalsIgnoreCase(info.getOpenStatus())) {
                                        if ("进门".equalsIgnoreCase(info.getDirection())) {
                                            enter++;
                                        } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                            out++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!equipmentListSP.isEmpty()) {
                    objectObjectHashMap.put("人脸设备", equipmentListSP);
                }

                if (!equipmentListZJ.isEmpty()) {
                    objectObjectHashMap.put("车闸", equipmentListZJ);
                }

                if (!equipmentListQR.isEmpty()) {
                    objectObjectHashMap.put("二维码读头", equipmentListQR);
                }

                if (!equipmentListCA.isEmpty()) {
                    objectObjectHashMap.put("摄像头", equipmentListCA);
                }

                equipmentGroupByEgid.setEnter(enter);
                equipmentGroupByEgid.setOut(out);
                equipmentGroupByEgid.setEquipmentStatus(objectObjectHashMap);
                equipmentGroups.add(equipmentGroupByEgid);
            }
        } else if (equipmentGroups.isEmpty()) {
            EquipmentGroup equipmentGroup = new EquipmentGroup();
            equipmentGroup.setUserid(eg.getUserid());
            equipmentGroup.setEgid(eg.getEgid());
            EquipmentGroup equipmentGroupByEgid = equipmentGroupService.getEquipmentGroupByEgid(equipmentGroup);
            equipmentGroups.add(equipmentGroupByEgid);
        }
        return new RespInfo(0, "success", equipmentGroups);
    }

    @ApiOperation(value = "/getFactoryMsgPage 获取厂区信息", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getFactoryMsgPage")
    public RespInfo getFactoryMsgPage(@RequestBody EquipmentGroup equipmentGroup, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Page<EquipmentGroup> rpage = new Page<>(equipmentGroup.getStartIndex() / equipmentGroup.getRequestedCount() + 1, equipmentGroup.getRequestedCount(), 0);
        equipmentGroup.setPage(rpage);
        List<EquipmentGroup> equipmentGroupByEgid = equipmentGroupService.getEquipmentGroupByEgidSmart(equipmentGroup);
        if (!equipmentGroupByEgid.isEmpty()) {

            for (EquipmentGroup equipmentGroup1 : equipmentGroupByEgid) {
                EGRelation egRelation = new EGRelation();
                egRelation.setUserid(equipmentGroup1.getUserid());
                egRelation.setEgid(equipmentGroup1.getEgid());
                EGRelation egRelations = egRelationService.getEGRelationByEgid(egRelation);
                int enter = 0;
                int out = 0;
                int veEnter = 0;
                int veOut = 0;
                if (ObjectUtils.isNotEmpty(egRelations)) {
                    String[] eids = egRelations.getEids().split(",");
                    for (String eid : eids) {

                        Equipment equipment = new Equipment();
                        equipment.setEid(Integer.parseInt(eid));
                        Equipment equipmentbyEid = equipmentService.getEquipmentbyEid(equipment);
                        if (ObjectUtils.isNotEmpty(equipmentbyEid)) {
                            String eType = equipmentbyEid.geteType();
                            if (StringUtils.isNotBlank(eType)) {
                                //人员通行
                                ReqODI reqODI = new ReqODI();
                                reqODI.setDeviceCode(equipmentbyEid.getDeviceCode());
                                reqODI.setUserid(equipmentGroup.getUserid());
                                reqODI.setStartDate(equipmentGroup.getStartDate());
                                reqODI.setEndDate(equipmentGroup.getEndDate());
                                List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqODI);
                                /**
                                 List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
                                 List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();
                                 if (!opendoorInfo.isEmpty()) {
                                 for (OpendoorInfo info : opendoorInfo) {
                                 if ("进门".equalsIgnoreCase(info.getDirection())) {
                                 opendoorInfoEnter.add(info);
                                 } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                 opendoorInfoOut.add(info);
                                 }
                                 }
                                 }*/
                                //根据手机号去重
                                /**Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                                 opendoorInfoSetEnter.addAll(opendoorInfoEnter);
                                 Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                                 opendoorInfoSetOut.addAll(opendoorInfoOut);

                                 List<OpendoorInfo> mergn = new ArrayList<>();
                                 mergn.addAll(opendoorInfoEnter);
                                 mergn.addAll(opendoorInfoOut);*/
                                if (!opendoorInfo.isEmpty()) {
                                    for (OpendoorInfo info : opendoorInfo) {
                                        if (StringUtils.isNotBlank(info.getOpenStatus()) && "成功".equalsIgnoreCase(info.getOpenStatus())) {
                                            if ("进门".equalsIgnoreCase(info.getDirection())) {
                                                enter++;
                                            } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                                out++;
                                            }
                                        }
                                    }
                                }
                                //车辆通行
                                ReqVR reqVR = new ReqVR();
                                reqVR.setUserid(equipmentGroup.getUserid());
                                reqVR.setStartDate(equipmentGroup.getStartDate());
                                reqVR.setEndDate(equipmentGroup.getStartDate());
                                reqVR.setDeviceCode(equipmentbyEid.getDeviceCode());
                                List<VehicleRecord> vehicleRecord = vehicleRecordService.getVehicleRecord(reqVR);
                                /** List<VehicleRecord> vehicleRecordEnter = new ArrayList<>();
                                 List<VehicleRecord> vehicleRecordOut = new ArrayList<>();
                                 List<VehicleRecord> vehicleRecordMergn = new ArrayList<>();

                                 if (!vehicleRecord.isEmpty()) {
                                 for (VehicleRecord record : vehicleRecord) {
                                 if (1 == record.getsType()) {
                                 vehicleRecordEnter.add(record);
                                 } else if (2 == record.getsType()) {
                                 vehicleRecordOut.add(record);
                                 }
                                 }
                                 }
                                 vehicleRecordMergn.addAll(vehicleRecordEnter);
                                 vehicleRecordMergn.addAll(vehicleRecordOut);
                                 Set<VehicleRecord> vehicleRecords = new TreeSet<>(Comparator.comparing(VehicleRecord::getPlateNum));
                                 vehicleRecords.addAll(vehicleRecordMergn);*/
                                if (!vehicleRecord.isEmpty()) {
                                    for (VehicleRecord record : vehicleRecord) {
                                        if (1 == record.getsType()) {
                                            veEnter++;
                                        } else if (2 == record.getsType()) {
                                            veOut++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                equipmentGroup1.setOut(out);
                equipmentGroup1.setEnter(enter);
                equipmentGroup1.setVehicleEnter(veEnter);
                equipmentGroup1.setVehicleOut(veOut);
            }
        }

        rpage.setList(equipmentGroupByEgid);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/getEqptCount 获取设备汇总信息", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getEqptCount")
    public RespInfo getEqptCount(@RequestBody Equipment equipment, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        EquipmentGroup equipmentGroup = new EquipmentGroup();
        equipmentGroup.setUserid(equipment.getUserid());
        if (0 != equipment.getEgid()) {
            equipmentGroup.setEgid(equipment.getEgid());
        }

        List<Equipment> equipmentbyUserid = egRelationService.getEquipmentByEgid(equipmentGroup);

        //List<Equipment> equipmentbyUserid = equipmentService.getEquipmentbyUserid(equipment);
        //人脸
        List<Equipment> sp = new ArrayList<>();
        //闸机
        List<Equipment> zj = new ArrayList<>();
        //二维码
        List<Equipment> qr = new ArrayList<>();
        //相机
        List<Equipment> ca = new ArrayList<>();

        if (!equipmentbyUserid.isEmpty()) {
            for (Equipment equipment1 : equipmentbyUserid) {
                if (StringUtils.isNotBlank(equipment1.geteType())) {
                    String eType = equipment1.geteType();
                    if (eType.startsWith("SP")) {
                        sp.add(equipment1);
                    } else if (eType.startsWith("QR")) {
                        qr.add(equipment1);
                    } else if (eType.startsWith("CA")) {
                        if (eType.endsWith("PT")) {
                            zj.add(equipment1);
                        } else {
                            ca.add(equipment1);
                        }
                    }
                }
            }
        }
        Map<String, List<Equipment>> map = new HashMap<>();
        map.put("SP", sp);
        map.put("ZJ", zj);
        map.put("QR", qr);
        map.put("CA", ca);
        return new RespInfo(0, "success", map);
    }

    @ApiOperation(value = "/getVisitorOpenDoorInfo 获取访客和员工通行记录信息", httpMethod = "GET",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getVisitorOpenDoorInfo")
    public RespInfo getVisitorOpenDoorInfo(@RequestBody EGRelation eg, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<EGRelation> egRelations = egRelationService.getEGRelationList(eg);
        List<OpendoorInfo> opendoorInfos = new ArrayList<>();

        if (!egRelations.isEmpty()) {
            for (EGRelation egRelation : egRelations) {
                EquipmentGroup equipmentGroup = new EquipmentGroup();
                equipmentGroup.setEgid(egRelation.getEgid());
                EquipmentGroup equipmentGroupByEgid = equipmentGroupService.getEquipmentGroupByEgid(equipmentGroup);
                String[] eids = egRelation.getEids().split(",");

                for (String eid : eids) {
                    Equipment equipment = new Equipment();
                    equipment.setEid(Integer.parseInt(eid));
                    Equipment equipmentbyEid = equipmentService.getEquipmentbyEid(equipment);
                    if (ObjectUtils.isNotEmpty(equipmentbyEid)) {
                        String deviceCode = equipmentbyEid.getDeviceCode();
                        if (StringUtils.isNotBlank(deviceCode)) {
                            ReqODI reqODI = new ReqODI();
                            reqODI.setDeviceCode(deviceCode);
                            reqODI.setUserid(eg.getUserid());
                            reqODI.setStartDate(eg.getStartDate());
                            reqODI.setEndDate(eg.getEndDate());
                            List<OpendoorInfo> opendoorInfo = opendoorService.getOpendoorInfo(reqODI);
                            List<OpendoorInfo> opendoorInfoEnter = new ArrayList<>();
                            List<OpendoorInfo> opendoorInfoOut = new ArrayList<>();
                            if (!opendoorInfo.isEmpty()) {
                                for (OpendoorInfo info : opendoorInfo) {
                                    if ("进门".equalsIgnoreCase(info.getDirection())) {
                                        opendoorInfoEnter.add(info);
                                    } else if ("出门".equalsIgnoreCase(info.getDirection())) {
                                        opendoorInfoOut.add(info);
                                    }
                                }
                            }
                            //根据手机号去重
                            Set<OpendoorInfo> opendoorInfoSetEnter = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                            opendoorInfoSetEnter.addAll(opendoorInfoEnter);
                            Set<OpendoorInfo> opendoorInfoSetOut = new TreeSet<>(Comparator.comparing(OpendoorInfo::getMobile));
                            opendoorInfoSetOut.addAll(opendoorInfoOut);
                            List<OpendoorInfo> mergn = new ArrayList<>();
                            mergn.addAll(opendoorInfoEnter);
                            mergn.addAll(opendoorInfoOut);
                            if (!mergn.isEmpty()) {
                                for (OpendoorInfo info : mergn) {
                                    if (StringUtils.isNotBlank(info.getOpenStatus()) && "成功".equalsIgnoreCase(info.getOpenStatus())) {
                                        /**if (StringUtils.isNotBlank(info.getMobile())) {
                                         if ("访客".equalsIgnoreCase(info.getVtype()) || "教师".equalsIgnoreCase(info.getVtype())) {
                                         Visitor todayVisitorByPhone = visitorService.getTodayVisitorByPhone(info.getMobile(), info.getUserid());
                                         if (ObjectUtils.isNotEmpty(todayVisitorByPhone)) {
                                         info.setPhotoUrl(todayVisitorByPhone.getVphoto());
                                         }
                                         } else if ("员工".equalsIgnoreCase(info.getVtype())) {
                                         List<Employee> empInfo = employeeService.getEmpInfo(info.getUserid(), info.getMobile());
                                         if (!empInfo.isEmpty()) {
                                         Employee employee = empInfo.get(0);
                                         info.setPhotoUrl(employee.getAvatar());
                                         }
                                         } else if ("供应商".equalsIgnoreCase(info.getVtype())) {
                                         //residentVisitorService.getVisitorByPhone(info.getMobile(),info.getUserid());
                                         ResidentVisitor residentVisitor = new ResidentVisitor();
                                         residentVisitor.setUserid(info.getUserid());
                                         residentVisitor.setPhone(info.getMobile());
                                         ResidentVisitor residentVisitorByPhone = residentVisitorService.getResidentVisitorByPhone(residentVisitor);
                                         if (ObjectUtils.isNotEmpty(residentVisitorByPhone)) {
                                         info.setPhotoUrl(residentVisitorByPhone.getAvatar());
                                         }
                                         }
                                         }*/
                                        if (StringUtils.isNotBlank(info.getDeviceCode())) {
                                            Equipment equipment1 = new Equipment();
                                            equipment1.setDeviceCode(info.getDeviceCode());
                                            Equipment equipmentbyDeviceCode = equipmentService.getEquipmentbyDeviceCode(equipment1);
                                            if (ObjectUtils.isNotEmpty(equipmentbyDeviceCode)) {
                                                info.setEquimentName(equipmentbyDeviceCode.getDeviceName());
                                            }
                                        }
                                        if (ObjectUtils.isNotEmpty(equipmentGroupByEgid)) {
                                            info.setEquimentGroupName(equipmentGroupByEgid.getEgname());
                                        }
                                        if (StringUtils.isNotBlank(info.getDeviceCode())) {
                                            Equipment equipment1 = new Equipment();
                                            equipment1.setDeviceCode(info.getDeviceCode());
                                            Equipment equipmentbyDeviceCode = equipmentService.getEquipmentbyDeviceCode(equipment1);
                                            /**if (ObjectUtils.isNotEmpty(equipmentbyDeviceCode)) {
                                             if (equipmentbyDeviceCode.geteType().startsWith("SP")) {
                                             info.setPassType("人脸识别");
                                             } else if (equipmentbyDeviceCode.geteType().startsWith("QR")) {
                                             info.setPassType("二维码");
                                             } else if (equipmentbyDeviceCode.geteType().startsWith("ZJ")) {
                                             info.setPassType("闸机");
                                             } else if (equipmentbyDeviceCode.geteType().startsWith("CA")) {
                                             info.setPassType("摄像头");
                                             }
                                             }*/
                                        }
                                        opendoorInfos.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //根据开门时间倒叙排列 order by openTime desc
        if (!opendoorInfos.isEmpty()) {
            Collections.sort(opendoorInfos, Comparator.comparing(OpendoorInfo::getOpenDate, (t1, t2) -> t2.compareTo(t1)));
        }
        return new RespInfo(0, "success", opendoorInfos);
    }

    @ApiOperation(value = "/getEquipmentGroupList 获取门禁组信息", httpMethod = "POST",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"signinType\":\"0/1\",\n" +
                    "    \"type\":\"vType\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "}"
    )
    @PostMapping("/getEquipmentGroupList")
    public RespInfo getEquipmentGroupList(@RequestBody EquipmentGroup equipmentGroup, HttpServletRequest request) {
        //前台权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<EquipmentGroup> equipmentGroupByUserid = equipmentGroupService.getEquipmentGroupByUserid(equipmentGroup);
        List<EquipmentGroup> equipmentGroups = new ArrayList<>();
        if (!equipmentGroupByUserid.isEmpty()) {
            for (EquipmentGroup group : equipmentGroupByUserid) {
                if (3 == group.getStatus()) {
                    equipmentGroups.add(group);
                }
            }
        }
        return new RespInfo(0, "success", equipmentGroups);
    }
}
