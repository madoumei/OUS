package com.client.dao2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.client.bean.Geofence;
import com.client.bean.RequestVisit;
import com.client.bean.Track;

import java.util.Collection;
import java.util.List;

public interface GeofenceDao extends BaseMapper<Geofence> {

    int addGFPolyline(Geofence geofence);

    int updateGFPolyline(Geofence geofence);

    /**
     * 指定坐标点是否在围栏中
     * @param longitude
     * @param latitude
     * @param id 围栏id
     * @return
     */
    boolean isIntersects(double longitude, double latitude, int id);

    /**
     * 轨迹是否与围栏相交，
     * @param points
     * @param id
     * @return true 在围栏内 false 在围栏外
     */
    boolean isLineIntersects(String points, int id);

}
