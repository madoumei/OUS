package com.client.dao2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.client.bean.DrivingRoute;
import com.client.bean.Geofence;

import java.util.List;

public interface DrivingRouteDao extends BaseMapper<DrivingRoute> {

    int addRoute(DrivingRoute route);

    int updateRoute(DrivingRoute route);

    /**
     * 查找连接两个电子围栏的路径
     * @param id1 开始点
     * @param id2 结束点
     * @return
     */
    List<DrivingRoute> getRoutesByGeofenceId(int id1, int id2);

}
