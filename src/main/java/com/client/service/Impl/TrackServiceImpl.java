package com.client.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.Geofence;
import com.client.bean.RequestVisit;
import com.client.bean.Track;
import com.client.dao2.TrackDao;
import com.client.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

@Service("trackServiceImpl")
public class TrackServiceImpl extends ServiceImpl<TrackDao, Track> implements TrackService {
    @Autowired
    private TrackDao trackDao;

    @Override
    public int batchSaveTrack(Collection<Track> tlist) {
        return trackDao.batchSaveTrack(tlist);
    }


    @Override
    public List<Track> getLatestPointList(RequestVisit visit) {
        return trackDao.getLatestPointList(visit);
    }

    @Override
    public boolean isInRoute(String vid, String id){
        return trackDao.isInRoute(vid,id);
    }

    @Override
    public boolean isInGeofence(String vid,String id){
        return trackDao.isInGeofence(vid,id);
    }
}
