package com;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.client.bean.Equipment;
import com.client.bean.Visitor;
import com.client.dao.EquipmentDao;
import com.client.service.VisitorService;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.MsgTemplate;
import com.web.dao.AppointmentDao;
import com.web.dao.EmployeeDao;
import com.web.service.AppointmentService;
import com.web.service.MsgTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev") //指定profiles
public class MybatisPlusTests {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EquipmentDao equipmentDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MsgTemplateService msgTemplateService;


    @Test
    public void test11() {
        List<Employee> oldEmployeeList = employeeDao.getOldEmployeeList(2147483647);
        if (!oldEmployeeList.isEmpty()) {
            for (Employee employee : oldEmployeeList) {
                System.out.println(employee.getEmpName());
            }
        }
    }

    @Test
    public void testEquipment() {
        List<Equipment> equipment = equipmentDao.selectList(null);
        System.out.println("equipment="+equipment);
    }

    @Test
    public void testAppointment() {
        //方式一：
        Appointment appointment = new Appointment();
        appointment.setStatus(6);

        int ret = appointmentDao.update(appointment,new LambdaUpdateWrapper<Appointment>().eq(Appointment::getId,1832));
        System.out.println("appointment="+ret);
    }

    @Test
    public void testVisitorService() {
        Appointment app = appointmentService.getAppointmentbyId(2429);
        System.out.println("appointment="+app);
    }

    @Test
    public void testgetVisitorByVgroup() {
        List<Visitor> visitorByVgroup = visitorService.getVisitorByVgroup("285");
        System.out.println("visitorByVgroup="+visitorByVgroup);
    }

    @Test
    public void testgetTemplateList() {
        MsgTemplate conf = new MsgTemplate();
//        conf.setId("sms_acceptAppointment");
        conf.setType("SMS");
        List<MsgTemplate> templateList = msgTemplateService.getTemplateList(conf);
        System.out.println("templateList="+templateList);
    }
}
