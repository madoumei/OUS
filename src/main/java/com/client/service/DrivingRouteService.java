package com.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.DrivingRoute;

import java.util.List;

public interface DrivingRouteService extends IService<DrivingRoute> {

    int addRoute(DrivingRoute route);

    int updateRoute(DrivingRoute route);

    List<DrivingRoute> getRoutesByGeofenceId(int id1, int id2);

}
