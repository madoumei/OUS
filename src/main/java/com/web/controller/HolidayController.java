package com.web.controller;

import com.config.exception.ErrorEnum;
import com.web.bean.*;
import com.web.service.DaysOffTranslationServer;
import com.web.service.HolidayService;
import com.web.service.PassRuleService;
import com.web.service.TokenServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "/**")
@Api(value = "HolidayController", tags = "API_节假日管理", hidden = true)
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private PassRuleService passRuleService;

    @Autowired
    private DaysOffTranslationServer daysOffTranslationServer;

    @Autowired
    private TokenServer tokenServer;

    @ApiOperation(value = "/addHoliday 添加节假日", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addHoliday")
    @ResponseBody
    public RespInfo addHoliday(@ApiParam(value = "Holiday 节假日Bean", required = true) @Validated @RequestBody Holiday hd,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (authToken.getUserid() != hd.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ReqHd rhd = new ReqHd();
        rhd.setSdate(hd.getHdate());
        rhd.setUserid(hd.getUserid());
        List<Holiday> lhd = holidayService.getHoliday(rhd);
        if (lhd.size() == 0) {
            hd.setHid(String.valueOf(hd.getHdate().getTime()));
            holidayService.addHoliday(hd);
        } else {
            return new RespInfo(223, "date exists");
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateHoliday 更新节假日", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateHoliday")
	@ResponseBody
	public RespInfo updateHoliday(@RequestBody Holiday hd) {
		ReqHd rhd=new ReqHd();
		rhd.setSdate(hd.getHdate());
		rhd.setUserid(hd.getUserid());
		List<Holiday>  lhd=holidayService.getHoliday(rhd);
		if(lhd.size()==0||lhd.get(0).getHid().equals(hd.getHid())){
			holidayService.updateHoliday(hd);
		}else{
			return new RespInfo(223, "date exists");
		}
		return new RespInfo(0, "success");
	}


    @ApiOperation(value = "/delHoliday 删除节假日", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
	@RequestMapping(method = RequestMethod.POST, value = "/delHoliday")
	@ResponseBody
	public RespInfo delHoliday(@RequestBody Holiday hd) {
		holidayService.delHoliday(hd);
		return new RespInfo(0, "success");
	}



    @ApiOperation(value = "/getHoliday 获取节假日", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getHoliday")
    @ResponseBody
    public RespInfo getHoliday(@ApiParam("ReqHd 请求节假日Bean") @RequestBody ReqHd rhd) {
        if(rhd.getRid()==0){
            PassRule searchPassRule = new PassRule();
            searchPassRule.setUserid(rhd.getUserid());
            List<PassRule> passRuleList = passRuleService.getPassRuleList(searchPassRule);
            for(PassRule passRule:passRuleList){
                rhd.setRid(passRule.getRid());
                List<Holiday> lhd=holidayService.getHoliday(rhd);

                if(lhd != null && lhd.size()>0){
                    if(passRule.getDaysOffTranslations() != null) {
                        for (DaysOffTranslation translation:passRule.getDaysOffTranslations()) {
                            if(translation.getDate().getYear() != rhd.getHdate().getYear()){
                                continue;
                            }
                            Holiday h = new Holiday();
                            h.setPassFlag(1);
                            h.setUserid(translation.getUserid());
                            h.setHdate(translation.getDate());
                            h.setRemark(translation.getRemark());
                            lhd.add(h);
                        }
                    }
                    return new RespInfo(0, "success",lhd);
                }
            }

        }else{
            List<Holiday> lhd=holidayService.getHoliday(rhd);
            return new RespInfo(0, "success",lhd);
        }

        return new RespInfo(0, "success");
    }

//	@RequestMapping(method = RequestMethod.POST, value = "/getHolidayFromTP")
//	@ResponseBody
//	public RespInfo  getHolidayFromTP(@RequestBody Holiday hod)   throws ParseException {
//		Calendar date = Calendar.getInstance();
//        String year = String.valueOf(date.get(Calendar.YEAR));
//		Map<String,String> params=new HashMap<String,String>();
//
//		params.put("year", year);
//		params.put("key", "c6b395073ca23e3d4c5f723aa9778ebd");
//		String response= HttpClientUtil.invokeGet("http://v.juhe.cn/calendar/year", params,"UTF-8",5000);
//		ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
//		JsonNode rootNode = null;
//		try {
//			rootNode = mapper.readValue(response, JsonNode.class);
//
//			if(rootNode.path("reason").asText().equals("Success"))
//			{
//				JsonNode  data = rootNode.path("result");
//				data=data.path("data");
//				data=data.path("holiday_list");
//				Iterator<JsonNode> it=data.iterator();
//				while(it.hasNext()){
//					JsonNode jn=it.next();
//					String name=jn.path("name").asText();
//					SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd");
//					String startday=jn.path("startday").asText();
//					Date d=time.parse(startday);
//					int passFlag=1;
//					if("元旦".equals(name)||"清明节".equals(name)||"劳动节".equals(name)||"端午节".equals(name)||"中秋节".equals(name)){
//						for(int i=0;i<3;i++){
//							Holiday hd=new Holiday();
//							long h=d.getTime()+i*1000*60*60*24L;
//							hd.setHdate(new Date(h));
//							hd.setHid(String.valueOf(h));
//							hd.setRemark(name);
//							hd.setPassFlag(passFlag);
//							hd.setUserid(hod.getUserid());
//							ReqHd rhd=new ReqHd();
//							rhd.setHdate(hd.getHdate());
//							rhd.setSdate(hd.getHdate());
//							rhd.setUserid(hod.getUserid());
//							List<Holiday>  lhd=holidayService.getHoliday(rhd);
//							if(lhd.size()==0){
//								holidayService.addHoliday(hd);
//							}
//						}
//
//					}else if("国庆节".equals(name)){
//						for(int i=0;i<7;i++){
//							Holiday hd=new Holiday();
//							long h=d.getTime()+i*1000*60*60*24L;
//							hd.setHdate(new Date(h));
//							hd.setHid(String.valueOf(h));
//							hd.setRemark(name);
//							hd.setPassFlag(passFlag);
//							hd.setUserid(hod.getUserid());
//							ReqHd rhd=new ReqHd();
//							rhd.setHdate(hd.getHdate());
//							rhd.setSdate(hd.getHdate());
//							rhd.setUserid(hod.getUserid());
//							List<Holiday>  lhd=holidayService.getHoliday(rhd);
//							if(lhd.size()==0){
//								holidayService.addHoliday(hd);
//							}
//						}
//					}else if("春节".equals(name)){
//						for(int i=0;i<6;i++){
//							Holiday hd=new Holiday();
//							long h=d.getTime()+i*1000*60*60*24L;
//							hd.setHdate(new Date(h));
//							hd.setHid(String.valueOf(h));
//							hd.setRemark(name);
//							hd.setPassFlag(passFlag);
//							hd.setUserid(hod.getUserid());
//							ReqHd rhd=new ReqHd();
//							rhd.setHdate(hd.getHdate());
//							rhd.setSdate(hd.getHdate());
//							rhd.setUserid(hod.getUserid());
//							List<Holiday>  lhd=holidayService.getHoliday(rhd);
//							if(lhd.size()==0){
//								holidayService.addHoliday(hd);
//							}
//						}
//					}else{
//							Holiday hd=new Holiday();
//							long h=d.getTime();
//							hd.setHdate(new Date(h));
//							hd.setHid(String.valueOf(h));
//							hd.setRemark(name);
//							hd.setPassFlag(passFlag);
//							hd.setUserid(hod.getUserid());
//							ReqHd rhd=new ReqHd();
//							rhd.setHdate(hd.getHdate());
//							rhd.setSdate(hd.getHdate());
//							rhd.setUserid(hod.getUserid());
//							List<Holiday>  lhd=holidayService.getHoliday(rhd);
//							if(lhd.size()==0){
//								holidayService.addHoliday(hd);
//							}
//					}
//				}
//			}
//
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return new RespInfo(0, "success");
//	}

    @RequestMapping(method = RequestMethod.POST, value = "/addPassRule")
    @ResponseBody
    public RespInfo addPassRule(@RequestBody PassRule pr){
        passRuleService.addPassRule(pr);
        for (int i = 0; i < pr.gethList().size(); i++) {
            pr.gethList().get(i).setHid(String.valueOf(System.currentTimeMillis()));
            pr.gethList().get(i).setRid(pr.getRid());
            holidayService.addHoliday(pr.gethList().get(i));
        }
        if (pr.getDaysOffTranslations() != null && pr.getDaysOffTranslations().size() > 0) {
            for (int i = 0; i < pr.getDaysOffTranslations().size(); i++) {
                pr.getDaysOffTranslations().get(i).setDid(String.valueOf(System.currentTimeMillis()));
                pr.getDaysOffTranslations().get(i).setRid(pr.getRid());
                daysOffTranslationServer.addDaysOffTranslation(pr.getDaysOffTranslations().get(i));
            }
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addPassConfig 添加通行策略", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addPassConfig")
    @ResponseBody
    public RespInfo addPassConfig(@ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassConfig pc,
                                  HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != pc.getUserid() || !"6".equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        passRuleService.addPassConfig(pc);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updatePassRule 更新通行时刻表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"rname\":\"塔1发卡时间\",\n" +
                    "    \"startDate\":\"2020-06-01\",\n" +
                    "    \"endDate\":\"2021-06-30\",\n" +
                    "    \"mon\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"tues\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"wed\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"thur\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"fri\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"sun\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"sat\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"13:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"hol\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"hList\":[\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"端午节\",\n" +
                    "            \"hdate\":\"2020-06-25\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"1980-10-01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-02\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-04\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-05\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆\",\n" +
                    "            \"hdate\":\"2020-10-08\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-11\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-12\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-13\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-15\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-16\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-17\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"daysOff\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"daysOffTranslations\":[\n" +
                    "\n" +
                    "    ],\n" +
                    "    \"rid\":25\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePassRule")
    @ResponseBody
    public RespInfo updatePassRule(@ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassRule pr,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (authToken.getUserid() != pr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        passRuleService.updatePassRule(pr);
        Holiday hd = new Holiday();
        hd.setRid(pr.getRid());
        hd.setUserid(pr.getUserid());
        holidayService.delHoliday(hd);
        DaysOffTranslation daysOffTranslation = new DaysOffTranslation();
        daysOffTranslation.setRid(pr.getRid());
        daysOffTranslation.setUserid(pr.getUserid());
        daysOffTranslationServer.delDaysOffTranslation(daysOffTranslation);

        for (int i = 0; i < pr.gethList().size(); i++) {
            pr.gethList().get(i).setHid(String.valueOf(System.currentTimeMillis())+i);
            pr.gethList().get(i).setRid(pr.getRid());
            holidayService.addHoliday(pr.gethList().get(i));
        }
        for (int i = 0; i < pr.getDaysOffTranslations().size(); i++) {
            pr.getDaysOffTranslations().get(i).setDid(String.valueOf(System.currentTimeMillis()));
            pr.getDaysOffTranslations().get(i).setRid(pr.getRid());
            daysOffTranslationServer.addDaysOffTranslation(pr.getDaysOffTranslations().get(i));
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updatePassConfig 更新通行策略", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"rname\":\"塔1发卡时间\",\n" +
                    "    \"startDate\":\"2020-06-01\",\n" +
                    "    \"endDate\":\"2021-06-30\",\n" +
                    "    \"mon\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"tues\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"wed\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"thur\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"fri\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"sun\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"sat\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"13:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"hol\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"hList\":[\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"端午节\",\n" +
                    "            \"hdate\":\"2020-06-25\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"1980-10-01\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-02\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-04\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆节\",\n" +
                    "            \"hdate\":\"2020-10-05\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"国庆\",\n" +
                    "            \"hdate\":\"2020-10-08\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-11\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-12\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-13\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-15\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-16\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"userid\":2147483647,\n" +
                    "            \"remark\":\"春节\",\n" +
                    "            \"hdate\":\"2021-02-17\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"daysOff\":\"[{\\\"startTime\\\":\\\"00:00\\\",\\\"endTime\\\":\\\"07:59\\\"},{\\\"startTime\\\":\\\"19:00\\\",\\\"endTime\\\":\\\"23:59\\\"}]\",\n" +
                    "    \"daysOffTranslations\":[\n" +
                    "\n" +
                    "    ],\n" +
                    "    \"rid\":25\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePassConfig")
    @ResponseBody
    public RespInfo updatePassConfig(@ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassConfig pc,
                                     HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        passRuleService.updatePassConfig(pc);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delPassConfig 删除通行策略", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delPassConfig")
    @ResponseBody
    public RespInfo delPassConfig(@ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassConfig pc,
                                  HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        passRuleService.deletePassConfig(pc);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getPassConfigList 获取通行策略", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"userid\":2147483647,\"ctype\":\"\"}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getPassConfigList")
    @ResponseBody
    public RespInfo getPassConfigList(
            @ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassConfig pc,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (authToken.getUserid() != pc.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<PassConfig> pcList = passRuleService.getPassConfig(pc);
        for (int i = 0; i < pcList.size(); i++) {
            ReqHd rhd = new ReqHd();
            rhd.setUserid(pcList.get(i).getUserid());
            rhd.setRid(pcList.get(i).getRid());
            List<Holiday> hList = holidayService.getHoliday(rhd);
            if (null != hList && hList.size() > 0) {
                pcList.get(i).getPr().sethList(hList);
            }
            DaysOffTranslation dot = new DaysOffTranslation();
            dot.setUserid(pcList.get(i).getUserid());
            dot.setRid(pcList.get(i).getRid());
            List<DaysOffTranslation> respDot = daysOffTranslationServer.getDaysOffTranslation(dot);
            if (null != respDot && respDot.size() > 0) {
                pcList.get(i).getPr().setDaysOffTranslations(respDot);
            }
        }
        return new RespInfo(0, "success", pcList);
    }


    @ApiOperation(value = "/delPassRule 删除通行时刻表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"rid\":25\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delPassRule")
    @ResponseBody
    public RespInfo delPassRule(@ApiParam(value = "PassConfig 通行时刻表配置Bean", required = true) @Validated @RequestBody PassRule pr,
                                HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (authToken.getUserid() != pr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        passRuleService.deletePassRule(pr);
        Holiday hl = new Holiday();
        hl.setRid(pr.getRid());
        hl.setUserid(pr.getUserid());
        holidayService.delHoliday(hl);

        DaysOffTranslation dot = new DaysOffTranslation();
        dot.setRid(pr.getRid());
        dot.setUserid(pr.getUserid());
        daysOffTranslationServer.delDaysOffTranslation(dot);
        return new RespInfo(0, "success");
    }


}
