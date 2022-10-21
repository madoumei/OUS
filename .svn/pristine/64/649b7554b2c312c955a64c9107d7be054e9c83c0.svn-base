package com.utils;

import com.client.bean.Equipment;
import com.client.bean.RequestVisit;
import com.client.bean.RespVisitor;
import com.client.bean.Visitor;
import com.event.bean.Litigant;
import com.web.bean.Appointment;
import com.web.bean.Employee;

import java.util.Date;

/**
 * 内部bean之间的转换
 */
public class BeanUtils {

   static public Visitor appointmentToVisitor(Appointment app) {
        Visitor vt = new Visitor();
        vt.setEmpid(app.getEmpid());
        vt.setEmpdeptid(app.getEmpdeptid());
        vt.setEmpName(app.getEmpName());
        vt.setUserid(app.getUserid());
        vt.setVphoto(app.getPhotoUrl());
        vt.setVname(app.getName());
        vt.setVisitdate(app.getVisitDate());
        vt.setVphone(app.getPhone());
        vt.setVisitType(app.getVisitType());
        vt.setEmpPhone(app.getEmpPhone());
        vt.setAppointmentDate(app.getAppointmentDate());
        vt.setPermission(app.getPermission());
        vt.setSigninType(1);
        vt.setCardId(app.getCardId());
        vt.setPeopleCount(1);
        vt.setRemark(app.getRemark());
        vt.setVcompany(app.getVcompany());
        vt.setSubaccountId(app.getSubaccountId());
        vt.setCompany(app.getCompany());
        vt.setSignInGate(app.getSignInGate());
        vt.setSignInOpName(app.getSignInOpName());
        vt.setvType(app.getvType());
        vt.setAppid(app.getId());
        vt.setTid(app.getTid());
        vt.setGid(app.getGid() + "");
        vt.setPlateNum(app.getPlateNum());
        vt.setSex(app.getSex());
        vt.setClientNo(app.getClientNo());
        vt.setQrcodeConf(app.getQrcodeConf());
        vt.setLeaveTime(app.getLeaveTime());
        vt.setVemail(app.getVemail());
        vt.setStatus(app.getStatus());
        vt.setExtendCol(app.getAppExtendCol());
        vt.setHealthDeclaration(app.getHealthDeclaration());
        return vt;
    }

     static public Appointment VisitorToAppointment(Visitor app) {
          Appointment vt = new Appointment();
          vt.setEmpid(app.getEmpid());
          vt.setEmpdeptid(app.getEmpdeptid());
          vt.setEmpName(app.getEmpName());
          vt.setUserid(app.getUserid());
          vt.setPhotoUrl(app.getVphoto());
          vt.setName(app.getVname());
          vt.setVisitDate(app.getVisitdate());
          vt.setPhone(app.getVphone());
          vt.setVisitType(app.getVisitType());
          vt.setEmpPhone(app.getEmpPhone());
          vt.setAppointmentDate(app.getAppointmentDate());
          vt.setPermission(app.getPermission());
          vt.setCardId(app.getCardId());
          vt.setRemark(app.getRemark());
          vt.setVcompany(app.getVcompany());
          vt.setSubaccountId(app.getSubaccountId());
          vt.setCompany(app.getCompany());
          vt.setSignInGate(app.getSignInGate());
          vt.setSignInOpName(app.getSignInOpName());
          vt.setvType(app.getvType());
          vt.setId(app.getVid());
          vt.setTid(app.getTid());
          try {
               vt.setGid(Integer.getInteger(app.getGid()));
          }catch (Exception e){

          }
          vt.setPlateNum(app.getPlateNum());
          vt.setSex(app.getSex());
          vt.setClientNo(app.getClientNo());
          vt.setQrcodeConf(app.getQrcodeConf());
          vt.setLeaveTime(app.getLeaveTime());
          vt.setVemail(app.getVemail());
          vt.setAppExtendCol(app.getExtendCol());
          vt.setStatus(app.getStatus());
          vt.setHealthDeclaration(app.getHealthDeclaration());
          vt.setCreateTime(app.getCreateTime());
          return vt;
     }

     static public Appointment RequestVisitToAppointment(RequestVisit rv) {
          Appointment vt = new Appointment();
          vt.setEmpid(rv.getEmpid());
          vt.setEmpName(rv.getEmpName());
          vt.setUserid(rv.getUserid());
          vt.setPhotoUrl(rv.getPhotoUrl());
          vt.setName(rv.getName());
          vt.setPhone(rv.getPhone());
          vt.setVisitType(rv.getVisitType());
          vt.setEmpPhone(rv.getEmpPhone());
          vt.setCardId(rv.getCardId());
          vt.setRemark(rv.getRemark());
          vt.setVcompany(rv.getVcompany());
          vt.setSubaccountId(rv.getSubaccountId());
          vt.setCompany(rv.getCompany());
          vt.setSignInGate(rv.getSignInGate());
          vt.setSignInOpName(rv.getSignInOpName());
          vt.setvType(rv.getvType());
          vt.setId(rv.getAppid());
          vt.setTid(rv.getTid());
          try {
               vt.setGid(Integer.getInteger(rv.getGid()));
          }catch (Exception e){

          }
          vt.setPlateNum(rv.getPlateNum());
          vt.setSex(rv.getSex());
          vt.setClientNo(rv.getClientNo());
          vt.setQrcodeConf(rv.getQrcodeConf());
          vt.setLeaveTime(rv.getLeaveTime());
          vt.setVemail(rv.getEmail());
          vt.setAppExtendCol(rv.getVtExtendCol());
          vt.setHealthDeclaration(rv.getHealthDeclaration());
          return vt;
     }


     static public Visitor RequestVisitToVisitor(RequestVisit rv) {
          Visitor vt = new Visitor();
          vt.setEmpid(rv.getEmpid());
          vt.setEmpName(rv.getEmpName());
          vt.setUserid(rv.getUserid());
          vt.setVphoto(rv.getPhotoUrl());
          vt.setVname(rv.getName());
          vt.setVphone(rv.getPhone());
          vt.setVisitType(rv.getVisitType());
          vt.setEmpPhone(rv.getEmpPhone());
          vt.setCardId(rv.getCardId());
          vt.setRemark(rv.getRemark());
          vt.setVcompany(rv.getVcompany());
          vt.setSubaccountId(rv.getSubaccountId());
          vt.setCompany(rv.getCompany());
          vt.setSignInGate(rv.getSignInGate());
          vt.setSignInOpName(rv.getSignInOpName());
          vt.setvType(rv.getvType());
          vt.setVid(rv.getVid());
          vt.setTid(rv.getTid());
          vt.setGid(rv.getGid());
          vt.setPlateNum(rv.getPlateNum());
          vt.setSex(rv.getSex());
          vt.setClientNo(rv.getClientNo());
          vt.setQrcodeConf(rv.getQrcodeConf());
          vt.setLeaveTime(rv.getLeaveTime());
          vt.setVemail(rv.getEmail());
          vt.setExtendCol(rv.getVtExtendCol());
          vt.setHealthDeclaration(rv.getHealthDeclaration());
          return vt;
     }


     static public Visitor RespVisitToVisitor(RespVisitor rv) {
          Visitor vt = new Visitor();
          vt.setEmpid(rv.getEmpid());
          vt.setEmpName(rv.getEmpName());
          vt.setUserid(rv.getUserid());
          vt.setVphoto(rv.getVphoto());
          vt.setVname(rv.getVname());
          vt.setVphone(rv.getVphone());
          vt.setVisitType(rv.getVisitType());
          vt.setEmpPhone(rv.getEmpPhone());
          vt.setCardId(rv.getCardId());
          vt.setRemark(rv.getRemark());
          vt.setVcompany(rv.getVcompany());
          vt.setSubaccountId(rv.getSubaccountId());
          vt.setCompany(rv.getCompany());
          vt.setSignInGate(rv.getSignInGate());
          vt.setSignInOpName(rv.getSignInOpName());
          vt.setvType(rv.getvType());
          vt.setVid(rv.getVid());
          vt.setTid(rv.getTid());
          vt.setGid(rv.getGid());
          vt.setPlateNum(rv.getPlateNum());
          vt.setSex(rv.getSex());
          vt.setClientNo(rv.getClientNo());
          vt.setQrcodeConf(rv.getQrcodeConf());
          vt.setLeaveTime(rv.getLeaveTime());
          vt.setVemail(rv.getVemail());
          vt.setExtendCol(rv.getVtExtendCol());
          vt.setHealthDeclaration(rv.getHealthDeclaration());
          return vt;
     }

     public static Litigant getLitigant(Employee employee) {
          Litigant litigant = new Litigant();
          litigant.setName(employee.getEmpName());
          litigant.setPhone(employee.getEmpPhone());
          litigant.setEmail(employee.getEmpEmail());
          litigant.setOpenid(employee.getOpenid());
          litigant.setType("员工");
          return litigant;
     }
    public void doNothing(){
        
    }

}
