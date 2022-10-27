package com.client.service.Impl;

import com.client.bean.dto.QcvLogistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AAAQcvLogistics {
    public static void main(String[] args) {
        QcvLogistics qcvLogistics = new QcvLogistics();
        qcvLogistics.setAppointmentenddate("2022-10-23 10:12:12");
        QcvLogistics qcvLogistics1 = new QcvLogistics();
        qcvLogistics1.setAppointmentenddate("2022-09-23 10:12");
        QcvLogistics qcvLogistics2 = new QcvLogistics();
        qcvLogistics2.setAppointmentenddate("2022-10-23 17:12");
        List<QcvLogistics> qcvLogisticsList = new ArrayList<>();
        qcvLogisticsList.add(qcvLogistics);
        qcvLogisticsList.add(qcvLogistics1);
        qcvLogisticsList.add(qcvLogistics2);
        List<QcvLogistics> collect = qcvLogisticsList.stream().sorted(Comparator.comparing(QcvLogistics::getAppointmentenddate)).collect(Collectors.toList());
        LinkedList<QcvLogistics> qcvLogistics3 = new LinkedList<>();
        qcvLogistics3.addAll(collect);

        for (QcvLogistics logistics : qcvLogistics3) {
            if ("2022-10-23 17:12".equalsIgnoreCase(logistics.getAppointmentenddate())){
                int i = qcvLogistics3.indexOf(logistics);
                System.out.println(i);
            }
            System.out.println(logistics);
        }
    }
}
