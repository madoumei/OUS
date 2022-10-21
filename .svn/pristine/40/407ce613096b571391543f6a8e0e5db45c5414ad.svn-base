package com.web.controller;

import com.web.bean.ProcessArea;
import com.web.bean.ProcessRule;
import com.web.bean.RespInfo;
import com.web.service.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "ProcessController", tags = "API_流程管理", hidden = true)
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @ApiOperation(value = "/getProcessArea 获取审批区域", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getProcessArea")
    @ResponseBody
    public RespInfo getProcessArea(@ApiParam("ProcessArea 审批区域Bean")@Validated @RequestBody ProcessArea pa) {
        List<ProcessArea> palist=processService.getProcessArea(pa);
        ProcessRule pru=new ProcessRule();
        for(int i=0;i<palist.size();i++){
            pru.setAid(palist.get(i).getAid());
            pru.setrType(-1);
            List<ProcessRule> prlist=processService.getProcessRule(pru);
            palist.get(i).setPrlist(prlist);
        }

        return new RespInfo(0, "success",palist);
    }

    @ApiOperation(value = "/addProcessArea 增加审批区域", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addProcessArea")
    @ResponseBody
    public RespInfo addProcessArea(@RequestBody ProcessArea pa) {
        ProcessArea p=processService.getProcessAreaByName(pa);
        if(null==p){
            processService.addProcessArea(pa);
        }else{
            return new RespInfo(3, "already exist");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delProcessArea 删除审批区域", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delProcessArea")
    @ResponseBody
    public RespInfo delProcessArea(@RequestBody ProcessArea pa) {
        int i=processService.deleteProcessArea(pa);
        if(i>0){
            ProcessRule pr=new ProcessRule();
            pr.setAid(pa.getAid());
            processService.deleteProcessRule(pr);
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateProcessArea 更新审批区域", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateProcessArea")
    @ResponseBody
    public RespInfo updateProcessArea(@RequestBody ProcessArea pa) {
        ProcessArea p=processService.getProcessAreaByName(pa);
        if(null==p||p.getAid()==pa.getAid()){
            processService.updateProcessArea(pa);
        }else{
            return new RespInfo(3, "already exist");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addProcessRule 添加审批规则", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addProcessRule")
    @ResponseBody
    public RespInfo addProcessRule(@RequestBody List<ProcessRule> prlist) {
        processService.deleteProcessRule(prlist.get(0));
        processService.addProcessRule(prlist);

        return new RespInfo(0, "success");
    }
}
