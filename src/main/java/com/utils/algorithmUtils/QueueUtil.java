package com.utils.algorithmUtils;

import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogQueueReq;
import com.google.gson.Gson;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QueueUtil {
    private static final Gson gson = new Gson();

    public static String getAddQueueIndex(List<QcvLogistics> qcvLogistics, QcvLogistics trgetLog, List<QcvLogQueueReq> qcvLogQueueReqs) {
        qcvLogistics.add(trgetLog);
        List<QcvLogistics> collect = qcvLogistics.stream().sorted(Comparator.comparing(QcvLogistics::getAppointmentenddate))
                .collect(Collectors.toList());
        LinkedList<QcvLogistics> linkedListLog = new LinkedList<>();
        linkedListLog.addAll(collect);
        int logIndex = -1;
        for (QcvLogistics logistics : linkedListLog) {
            if (trgetLog.getSid() == logistics.getSid()) {
                logIndex = linkedListLog.indexOf(logistics);
                logIndex++;
            }
        }
        String json = null;
        QcvLogQueueReq queueReq = new QcvLogQueueReq();
        if (ObjectUtils.isNotEmpty(linkedListLog.get(logIndex))) {
            //return linkedListLog.get(logIndex).getSid() + "";
            QcvLogistics qcvLogisticsBe = linkedListLog.get(logIndex);
            if (ObjectUtils.isNotEmpty(qcvLogisticsBe)) {
                for (QcvLogQueueReq qcvLogQueueReq : qcvLogQueueReqs) {
                    if (qcvLogQueueReq.getSid()==qcvLogisticsBe.getSid()) {
                        queueReq.setSid(qcvLogisticsBe.getSid());
                        queueReq.setPlateNum(qcvLogisticsBe.getPlatenum());
                        queueReq.setQueueNum(qcvLogQueueReq.getQueueNum());
                        json = gson.toJson(queueReq);
                        return json;
                    }
                }
            }
        }
        return json;
    }
}
