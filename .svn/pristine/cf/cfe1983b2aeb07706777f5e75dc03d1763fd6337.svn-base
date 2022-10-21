package com.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.Geofence;
import com.client.bean.RequestVisit;
import com.client.bean.Track;

import java.util.Collection;
import java.util.List;

public interface TrackService extends IService<Track> {
    /**
     * 批量导入轨迹点
     * @param tlist
     * @return
     */
    int batchSaveTrack(Collection<Track> tlist);

    /**
     * 获取某个时间后出现的人员轨迹，根据openid合并
     * @param visit reqDate 时间
     * @return
     */
    List<Track> getLatestPointList(RequestVisit visit);

    /**
     * 检查行动路线是否在推荐路径上
     * @param vid 访客预约记录
     * @param id 推荐route id
     * @return
     */
    boolean isInRoute(String vid,String id);

    /**
     * 是否在电子围栏内
     * @param vid 访客预约记录
     * @param id 电子围栏 id
     * @return
     */
    boolean isInGeofence(String vid,String id);
}
