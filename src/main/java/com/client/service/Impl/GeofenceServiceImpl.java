package com.client.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.Geofence;
import com.client.dao2.GeofenceDao;
import com.client.service.GeofenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("geofenceServiceImpl")
public class GeofenceServiceImpl extends ServiceImpl<GeofenceDao, Geofence> implements GeofenceService {
    @Autowired
    private GeofenceDao geofenceDao;

    /**
     * 创建矩形电子围栏
     * @param geofence
     * @return
     */
    @Override
    public int addGFPolyline(Geofence geofence){
        return geofenceDao.addGFPolyline(geofence);
    }

    @Override
    public int updateGFPolyline(Geofence geofence){
        return geofenceDao.updateGFPolyline(geofence);
    }

    /**
     * 指定坐标点是否在围栏中
     * @param longitude
     * @param latitude
     * @param id 围栏id
     * @return
     */
    @Override
    public boolean isIntersects(double longitude, double latitude, int id) {
        return geofenceDao.isIntersects(longitude,latitude,id);
    }

}
