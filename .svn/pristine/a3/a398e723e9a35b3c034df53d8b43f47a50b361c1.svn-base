package com.client.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.DrivingRoute;
import com.client.bean.Geofence;
import com.client.dao2.DrivingRouteDao;
import com.client.dao2.GeofenceDao;
import com.client.service.DrivingRouteService;
import com.client.service.GeofenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("drivingRouteServiceImpl")
public class DrivingRouteServiceImpl extends ServiceImpl<DrivingRouteDao, DrivingRoute> implements DrivingRouteService {
    @Autowired
    private DrivingRouteDao drivingRouteDao;


    @Override
    public int addRoute(DrivingRoute route) {
        return drivingRouteDao.addRoute(route);
    }

    @Override
    public int updateRoute(DrivingRoute route) {
        return drivingRouteDao.updateRoute(route);
    }

    @Override
    public List<DrivingRoute> getRoutesByGeofenceId(int id1, int id2) {
        return drivingRouteDao.getRoutesByGeofenceId(id1,id2);
    }
}
