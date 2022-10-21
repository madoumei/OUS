package com.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.Geofence;
import com.client.bean.RequestVisit;
import com.client.bean.Track;

import java.util.Collection;
import java.util.List;

public interface GeofenceService extends IService<Geofence> {

    int addGFPolyline(Geofence geofence);

    int updateGFPolyline(Geofence geofence);

    boolean isIntersects(double longitude, double latitude, int id);

}
