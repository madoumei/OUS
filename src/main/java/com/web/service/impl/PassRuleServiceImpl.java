package com.web.service.impl;

import com.client.bean.Gate;
import com.client.service.VisitorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.DaysOffTranslation;
import com.web.bean.Holiday;
import com.web.bean.PassConfig;
import com.web.bean.PassRule;
import com.web.dao.DaysOffTranslationDao;
import com.web.dao.HolidayDao;
import com.web.dao.PassRuleDao;
import com.web.service.AddressService;
import com.web.service.PassRuleService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service("passRuleService")
public class PassRuleServiceImpl implements PassRuleService {
    @Autowired
    private PassRuleDao passRuleDao;

    @Autowired
    private DaysOffTranslationDao daysOffTranslationDao;

    @Autowired
    private HolidayDao holidayDao;

    @Autowired
    private AddressService addressService;

    @Autowired
    private VisitorService visitorService;

    @Override
    public int addPassRule(PassRule pr) {
        // TODO Auto-generated method stub
        return passRuleDao.addPassRule(pr);
    }

    @Override
    public int updatePassRule(PassRule pr) {
        // TODO Auto-generated method stub
        return passRuleDao.updatePassRule(pr);
    }

    @Override
    public List<PassRule> getPassRuleList(PassRule pr) {
        List<PassRule> prList = passRuleDao.getPassRuleList(pr);
        for (PassRule passRule : prList) {
            int rid = passRule.getRid();
            DaysOffTranslation daysOffTranslation = new DaysOffTranslation();
            daysOffTranslation.setRid(rid);
            daysOffTranslation.setUserid(pr.getUserid());
            List<DaysOffTranslation> dts = daysOffTranslationDao.getDaysOffTranslation(daysOffTranslation);
            passRule.setDaysOffTranslations(dts);
        }
        return prList;
    }

    @Override
    public int deletePassRule(PassRule pr) {
        // TODO Auto-generated method stub
        return passRuleDao.deletePassRule(pr);
    }

    @Override
    public List<PassRule> getPassRule(PassRule pr) {
        // TODO Auto-generated method stub
        return passRuleDao.getPassRule(pr);
    }

    @Override
    public List<PassConfig> getPassConfig(PassConfig pc) {
        // TODO Auto-generated method stub
        return passRuleDao.getPassConfig(pc);
    }

    @Override
    public int addPassConfig(PassConfig pc) {
        // TODO Auto-generated method stub
        return passRuleDao.addPassConfig(pc);
    }

    @Override
    public int updatePassConfig(PassConfig pc) {
        // TODO Auto-generated method stub
        return passRuleDao.updatePassConfig(pc);
    }

    @Override
    public int deletePassConfig(PassConfig pc) {
        // TODO Auto-generated method stub
        return passRuleDao.deletePassConfig(pc);
    }

    @Override
    public int getSendCardStatus(Integer userid, Date date, String[] gNames) {
        ObjectMapper objectMapper = new ObjectMapper();
        int floorStatus = 1;
        // TODO: 2020/6/20 对入参数据进行解析 `T1-08/09F&9` => T1-8,T1-9
        List<String> floorList = new ArrayList<>();
        for (int i = 0; i < gNames.length; i++) {
            //["T3-35/37M&80","T1-11/12/13F&80","T1-08/09F&9","T1-34M"]
            if (gNames[i].contains("-")) {
                gNames[i] = gNames[i].replaceAll("-", "_");
            }
            String gateName = gNames[i].split("_")[0];
            String floors = gNames[i].split("_")[1];
            if (floors.contains("F")) {
                floors = floors.split("F")[0];
            } else if (floors.contains("M")) {
                floors = floors.split("M")[0];
            }
            //08/09
            String[] floorArr = floors.split("/");
            for (String s : floorArr) {
                floorList.add(gateName + "_" + s);
            }
        }
        for (String gName : floorList) {
            String gateName = gName.split("_")[0];
            String floorName = gName.split("_")[1];
            while (!Character.isDigit(floorName.charAt(floorName.length() - 1))) {
                floorName = floorName.substring(0, floorName.length() - 1);
            }
            Gate gate = new Gate();
            gate.setUserid(userid);
            switch (gateName) {
                case "T1":
                    gate.setGname("1号楼");
                    break;
                case "T2":
                    gate.setGname("2号楼");
                    break;
                case "T3":
                    gate.setGname("3号楼");
                    break;
            }
            Gate gateByName = addressService.getGateByName(gate);
            if (gateByName != null) {
                int gid = Integer.parseInt(gateByName.getGids());
                PassConfig pc = new PassConfig();
                pc.setUserid(userid);
                List<PassConfig> passConfig = passRuleDao.getPassConfig(pc);
                if (passConfig.size() > 0) {

                    /**
                     * 过滤掉过期配置
                     */
                    List<PassConfig> send_cards = filterTimeOutPassRule(passConfig, date);

                    /**
                     * 过滤出该塔楼，该楼层的配置
                     */
                    List<PassConfig> passConfigList = new ArrayList<>();
                    for (int i = 0; i < send_cards.size(); i++) {
                        PassConfig passC = send_cards.get(i);
                        if (StringUtils.isNotBlank(passC.getConditions())) {
                            ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
                            try {
                                JsonNode jsonNode = mapperInstance.readValue(passC.getConditions(), JsonNode.class);
                                int conf_gid = jsonNode.path("gid").asInt();
                                String conf_floor = jsonNode.path("floor").asText();
                                if (StringUtils.isNotBlank(conf_floor)) {
                                    String[] conf_foorArr = conf_floor.split(",");
                                    for (int j = 0; j < conf_foorArr.length; j++) {
                                        int conf_foor = Integer.parseInt(conf_foorArr[j]);
                                        int req_foor = Integer.parseInt(floorName);
                                        if (conf_foor == req_foor && gid == conf_gid) {
                                            passConfigList.add(passC);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 规则判断
                     */
                    if (null != passConfigList && passConfigList.size() > 0) {
                        for (PassConfig config : passConfigList) {
                            try {
                                JsonNode jsonNode = objectMapper.readTree(config.getConditions());
                                if (StringUtils.isNotBlank(jsonNode.path("floor").asText())) {
                                    PassRule pr = config.getPr();
                                    pr.setRid(config.getRid());
                                    pr.setUserid(config.getUserid());
                                    List<PassRule> passRuleList = passRuleDao.getPassRuleList(pr);
                                    if (passRuleList != null && passRuleList.size() > 0) {
                                        //判断是否调休日
                                        boolean isDaysOffTranslation = false;
                                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                                        List<PassRule> passRules = passRuleDao.getPassRuleListByDaysOffTranslation(config.getPr());
                                        if (passRules != null && passRules.size() > 0) {
                                            for (PassRule passRule : passRules) {
                                                // 判断是否是调休日上班  ->  节假日  ->   工作日 通行判断
                                                String visitdateStr = dateFormat1.format(date);
                                                List<DaysOffTranslation> dots = passRule.getDaysOffTranslations();
                                                if (dots != null && dots.size() > 0) {
                                                    List<String> list = dots.stream()
                                                            .filter(daysOffTranslation -> daysOffTranslation.getDate() != null)
                                                            .map(d -> dateFormat1.format(d.getDate())).collect(Collectors.toList());
                                                    if (!list.contains(visitdateStr)) {
                                                        isDaysOffTranslation = false;
                                                    }else {
                                                        isDaysOffTranslation = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (isDaysOffTranslation) {
                                            //当天是调休日
                                            for (PassRule passRule : passRuleList) {
                                                if (StringUtils.isNotEmpty(passRule.getDaysOff())) {
                                                    if (Confirm_today(date, passRule.getDaysOff()) == -1) {
                                                        floorStatus = -1;
                                                    }
                                                }
                                            }
                                        } else {
                                            //当天不是调休日
                                            for (PassRule passRule : passRuleList) {
                                                if (Confirm_Holiday(date, passRule.gethList()) == 1) {
                                                    //节假日通行时间判断
                                                    if (Confirm_today(date, pr.getHol()) == -1) {
                                                        floorStatus = -1;
                                                    }
                                                } else {
                                                    //工作日通行判断
                                                    if (Confirm_Day_Of_Week(date, pr) == -1) {
                                                        floorStatus = -1;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //未设置楼层，全部发卡；
                                    return 1;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //todo 没有该门岗的通行策略。或通行测量过期了；
                        return 1;
                    }
                }
            } else {
                return -1;
            }
        }
        return floorStatus;
    }


    /**
     * 按门岗id对请求门岗进行分组
     *
     * @param passConfigList
     * @param gid
     * @return
     */
    private Map<Integer, List<PassConfig>> passConfigGroupByGid(List<PassConfig> passConfigList, Integer gid) {
        Map<Integer, List<PassConfig>> resultMap = new HashMap<>();
        if (null != passConfigList && passConfigList.size() > 0) {
            List<PassConfig> temp = new ArrayList<>();
            for (PassConfig send_card : passConfigList) {
                String conditions = send_card.getConditions();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = null;
                try {
                    jsonNode = objectMapper.readTree(conditions);
                    if (jsonNode != null && jsonNode.has("gid")) {
                        int gid1 = Integer.parseInt(jsonNode.get("gid").toString());
                        if (gid == gid1) {
                            temp.add(send_card);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resultMap.put(gid, temp);
            return resultMap;
        } else {
            return null;
        }
    }

    /**
     * 过滤掉过期的通行时间策略
     *
     * @param passConfigs
     * @return
     */
    private List<PassConfig> filterTimeOutPassRule(List<PassConfig> passConfigs, Date date) {
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        List<PassConfig> passConfigList = new ArrayList<>();
        //过滤掉过期配置
        if (null != passConfigs && passConfigs.size() > 0) {
            Iterator<PassConfig> it = passConfigs.iterator();
            while (it.hasNext()) {
                PassConfig passConfig = it.next();
                if (passConfig.getCname().equals("send_card")) {
                    int s = Integer.parseInt(passConfig.getPr().getStartDate().replace("-", ""));
                    int e = Integer.parseInt(passConfig.getPr().getEndDate().replace("-", ""));
                    int time = Integer.parseInt(dateFormat2.format(date).replace("-", ""));
                    if (s < time && time < e) {
                        passConfigList.add(passConfig);
                    }
                }
            }
            return passConfigList;
        } else {
            return null;
        }
    }

    /**
     * 根据门禁组获取楼层
     *
     * @param orgEgName "gNames":["17F女卫+东行政卫"]
     * @return 17
     */
    private String getFloorByEgname(String orgEgName) {
        Pattern pattern = Pattern.compile("[0-9]+");
        char c = orgEgName.charAt(orgEgName.length() - 1);
        if ('A' == c) {
            return orgEgName;
        }
        if (!pattern.matcher(String.valueOf(c)).matches()) {
            orgEgName = orgEgName.substring(0, orgEgName.length() - 1);
            return getFloorByEgname(orgEgName);
        } else {
            return String.valueOf(Integer.parseInt(orgEgName));
        }
    }

    /**
     * 根据工作日判断当天可否通行
     *
     * @param date
     * @return
     */
    public static int Confirm_Day_Of_Week(Date date, PassRule passRules) {
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        String passTime = "";
        int status = -1;
        switch (weekDay) {
            case 1:
                passTime = passRules.getSun();
                break;
            case 2:
                passTime = passRules.getMon();
                break;
            case 3:
                passTime = passRules.getTues();
                break;
            case 4:
                passTime = passRules.getWed();
                break;
            case 5:
                passTime = passRules.getThur();
                break;
            case 6:
                passTime = passRules.getFri();
                break;
            case 7:
                passTime = passRules.getSat();
                break;
        }

        try {
            if (StringUtils.isNotEmpty(passTime)) {
                JSONArray jsonArray = new JSONArray(passTime);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String startTime = jsonObject.get("startTime").toString();
                    String endTime = jsonObject.get("endTime").toString();
                    Date startDate = dateFormat2.parse(year + ":" + month + ":" + day + " " + startTime);
                    Date endDate = dateFormat2.parse(year + ":" + month + ":" + day + " " + endTime);
                    if (date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
                        status = 1;
                        return status;
                    }
                }
            } else {
                status = 1;
                return status;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 根据时间范围判断是否可通行
     * 判断date是否在passtime区间内
     * @param date
     * @param passtime [{"startTime":"02:00","endTime":"12:00"}]
     * @return 1=在范围内 -1=不在范围内
     */
    public static int Confirm_today(Date date, String passtime) {
        int status = -1;
        if(StringUtils.isEmpty(passtime)){
            return status;
        }
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
            JSONArray jsonArray = new JSONArray(passtime);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String startTime = jsonObject.get("startTime").toString();
                String endTime = jsonObject.get("endTime").toString();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                Date sdate = dateFormat2.parse(year + ":" + month + ":" + day + " " + startTime);
                Date edate = dateFormat2.parse(year + ":" + month + ":" + day + " " + endTime);
                long reqdate = date.getTime();
                if (reqdate >= sdate.getTime() && reqdate <= edate.getTime()) {
                    status = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 确认是否为节假日
     *
     * @param date
     * @param hDates
     * @return
     */
    public static int Confirm_Holiday(Date date, List<Holiday> hDates) {
        int status = -1;
        if (hDates.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String reqDate = dateFormat.format(date);
            int reqDateInteger = Integer.parseInt(reqDate.replaceAll("-", ""));
            for (Holiday hDate : hDates) {
                int hDateInteger = Integer.parseInt(dateFormat.format(hDate.getHdate()).replaceAll("-", ""));
                if (reqDateInteger == hDateInteger) {
                    return 1;
                }
            }
        }
        return status;
    }

    /**
     * 是否是周末时间段拜访
     *
     * @param visitDate
     * @param gid
     * @param userid
     * @return
     */
    @Override
    public Boolean isWeekendPass(Date visitDate, int gid, int userid) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        try {

            // TODO: 2020/6/23 获取门岗通行数据
            PassConfig pc = new PassConfig();
            pc.setUserid(userid);
            List<PassConfig> passConfig = passRuleDao.getPassConfig(pc);

            List<PassConfig> send_cards = new ArrayList<>();
            //过滤掉过期配置
            for (PassConfig config : passConfig) {
                if (config.getCname().equals("send_card")) {
                    int s = Integer.parseInt(config.getPr().getStartDate().replace("-", ""));
                    int e = Integer.parseInt(config.getPr().getEndDate().replace("-", ""));
                    int time = Integer.parseInt(dateFormat1.format(visitDate).replace("-", ""));
                    if (s < time && time < e) {
                        send_cards.add(config);
                    }
                }
            }
            if (send_cards.size() > 0) {
                for (PassConfig config : send_cards) {
                    String conditions = config.getConditions();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(conditions);
                    if (jsonNode.has("gid")) {
                        int gid1 = Integer.parseInt(jsonNode.get("gid").toString());
                        if (gid == gid1) {
                            PassRule pr = config.getPr();
                            pr.setRid(config.getRid());
                            pr.setUserid(config.getUserid());
                            List<PassRule> passRuleList = passRuleDao.getPassRuleList(pr);
                            if (passRuleList != null && passRuleList.size() > 0) {
                                for (PassRule passRules : passRuleList) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(visitDate.getTime());
                                    int year = calendar.get(Calendar.YEAR);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                                    String passTime = "";
                                    if (weekDay > 1 && weekDay < 6) {
                                        return false;
                                    } else {
                                        switch (weekDay) {
                                            case 1:
                                                passTime = passRules.getSun();
                                                break;
                                            case 2:
                                                passTime = passRules.getMon();
                                                break;
                                            case 3:
                                                passTime = passRules.getTues();
                                                break;
                                            case 4:
                                                passTime = passRules.getWed();
                                                break;
                                            case 5:
                                                passTime = passRules.getThur();
                                                break;
                                            case 6:
                                                passTime = passRules.getFri();
                                                break;
                                            case 7:
                                                passTime = passRules.getSat();
                                                break;
                                        }
                                        if (StringUtils.isNotBlank(passTime)) {
                                            JSONArray jsonArray = new JSONArray(passTime);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                String startTime = jsonObject.get("startTime").toString();
                                                String endTime = jsonObject.get("endTime").toString();
                                                Date startDate = dateFormat2.parse(year + ":" + month + ":" + day + " " + startTime);
                                                Date endDate = dateFormat2.parse(year + ":" + month + ":" + day + " " + endTime);
                                                if (visitDate.getTime() >= startDate.getTime() && visitDate.getTime() <= endDate.getTime()) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否节假日拜访
     *
     * @param visitDate
     * @param gid
     * @param userid
     * @return
     */
    @Override
    public Boolean isHolidayPass(Date visitDate, int gid, int userid) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        try {

            // TODO: 2020/6/23 获取门岗通行数据
            PassConfig pc = new PassConfig();
            pc.setUserid(userid);
            List<PassConfig> passConfig = passRuleDao.getPassConfig(pc);

            List<PassConfig> send_cards = new ArrayList<>();
            //过滤掉过期配置
            for (PassConfig config : passConfig) {
                if (config.getCname().equals("send_card")) {
                    int s = Integer.parseInt(config.getPr().getStartDate().replace("-", ""));
                    int e = Integer.parseInt(config.getPr().getEndDate().replace("-", ""));
                    int time = Integer.parseInt(dateFormat1.format(visitDate).replace("-", ""));
                    if (s < time && time < e) {
                        send_cards.add(config);
                    }
                }
            }
            if (send_cards.size() > 0) {
                for (PassConfig config : send_cards) {
                    String conditions = config.getConditions();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(conditions);
                    if (jsonNode.has("gid")) {
                        int gid1 = Integer.parseInt(jsonNode.get("gid").toString());
                        if (gid == gid1) {
                            PassRule pr = config.getPr();
                            pr.setRid(config.getRid());
                            pr.setUserid(config.getUserid());
                            List<PassRule> passRuleList = passRuleDao.getPassRuleList(pr);
                            List<String> holidayList = new ArrayList<>();
                            if (passRuleList != null && passRuleList.size() > 0) {
                                for (PassRule passRules : passRuleList) {
                                    List<Holiday> holidays = passRules.gethList();
                                    if (holidays != null && holidays.size() > 0) {
                                        holidayList = holidays.stream().map(holiday -> dateFormat1.format(holiday.getHdate()).replaceAll("-", "")).collect(Collectors.toList());
                                        if (holidayList.size() > 0) {
                                            boolean contains = holidayList.contains(dateFormat1.format(visitDate).replaceAll("-", ""));
                                            if (contains) {
                                                JSONArray jsonArray = new JSONArray(passRules.getHol());
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                String startTime = jsonObject.get("startTime").toString();
                                                String endTime = jsonObject.get("endTime").toString();
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(visitDate);
                                                int year = calendar.get(Calendar.YEAR);
                                                int month = calendar.get(Calendar.MONTH) + 1;
                                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                                Date sdate = dateFormat2.parse(year + ":" + month + ":" + day + " " + startTime);
                                                Date edate = dateFormat2.parse(year + ":" + month + ":" + day + " " + endTime);
                                                long reqdate = visitDate.getTime();
                                                if (reqdate >= sdate.getTime() && reqdate <= edate.getTime()) {
                                                    return true;
                                                }
                                            }
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 是否调休日拜访
     *
     * @param visitDate
     * @param gid
     * @param userid
     * @return
     */
    @Override
    public Boolean isDaysOffTranslation(Date visitDate, int gid,List<String> reqFloors, int userid) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> floors = new ArrayList<>();
        for (int i = 0; i < reqFloors.size(); i++) {
            if (reqFloors.get(i).contains("-")) {
                String floorName = reqFloors.get(i).replaceAll("-", "_");
                floorName = reqFloors.get(i).split("-")[1];
                while (!Character.isDigit(floorName.charAt(floorName.length() - 1))) {
                    floorName = floorName.substring(0, floorName.length() - 1);
                }
                floors.add(floorName);
            }else {
                String floorName = reqFloors.get(i).split("_")[1];
                while (!Character.isDigit(floorName.charAt(floorName.length() - 1))) {
                    floorName = floorName.substring(0, floorName.length() - 1);
                }
                floors.add(floorName);
            }
        }

        boolean b = true;
        // TODO: 2020/6/23 获取门岗通行数据
        PassConfig pc = new PassConfig();
        pc.setUserid(userid);
        List<PassConfig> passConfig = passRuleDao.getPassConfig(pc);
        if (null == passConfig || passConfig.size()==0){
            return  false;
        }
        //TODO: 2020/6/23 过滤掉过期配置
        List<PassConfig> send_cards = filterTimeOutPassRule(passConfig, visitDate);

        /**
         * 过滤出该塔楼的配置
         */
        List<PassConfig> passConfigs = new ArrayList<>();
        for (int i = 0; i < send_cards.size(); i++) {
            PassConfig passC = send_cards.get(i);
            if (StringUtils.isNotBlank(passC.getConditions())) {
                ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
                try {
                    JsonNode jsonNode = mapperInstance.readValue(passC.getConditions(), JsonNode.class);
                    int conf_gid = jsonNode.path("gid").asInt();
                    String conf_floor = jsonNode.path("floor").asText();
                    if (StringUtils.isNotBlank(conf_floor)) {
                        for (String reqFloor : floors) {
                            if (conf_floor.indexOf(String.valueOf(reqFloor)) > 0 && gid == conf_gid) {
                                passConfigs.add(passC);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 配置判断
         */
        if (passConfigs.size() > 0) {
            for (PassConfig config : passConfigs) {
                String conditions = config.getConditions();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = null;
                try {
                    jsonNode = objectMapper.readTree(conditions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (jsonNode != null && jsonNode.has("gid")) {
                    int gid1 = Integer.parseInt(jsonNode.get("gid").toString());
                    if (gid == gid1) {
                        PassRule pr = config.getPr();
                        // TODO: 2020/6/19 通行时刻表策略判断

                        // 通行有效期判断
                        int s = Integer.parseInt(pr.getStartDate().replace("-", ""));
                        int e = Integer.parseInt(pr.getEndDate().replace("-", ""));
                        int time = Integer.parseInt(dateFormat1.format(visitDate).replace("-", ""));
                        if (time < s || time > e) {
                            b = false;
                            return b;
                        }

                        pr.setRid(config.getRid());
                        pr.setUserid(config.getUserid());
                        List<PassRule> passRuleList = passRuleDao.getPassRuleListByDaysOffTranslation(pr);
                        if (passRuleList != null && passRuleList.size() > 0) {
                            for (PassRule passRule : passRuleList) {
                                // 判断是否是调休日上班  ->  节假日  ->   工作日 通行判断
                                String visitdateStr = dateFormat1.format(visitDate);
                                List<DaysOffTranslation> dots = passRule.getDaysOffTranslations();
                                if (dots != null && dots.size() > 0) {
                                    List<String> list = dots.stream()
                                            .filter(daysOffTranslation -> daysOffTranslation.getDate() != null)
                                            .map(d -> dateFormat1.format(d.getDate())).collect(Collectors.toList());
                                    if (!list.contains(visitdateStr)) {
                                        b = false;
                                        return b;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            b = true;
        }
        return b;
    }

    @Override
    public PassRule getPassRuleByRname(PassRule passRule) {
        return passRuleDao.getPassRuleByRname(passRule);
    }
}




