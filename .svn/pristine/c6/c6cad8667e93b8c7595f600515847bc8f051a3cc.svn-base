package com.client.controller;

import com.client.bean.*;
import com.client.service.QuestionnaireService;
import com.client.service.SatisfactionService;
import com.client.service.TrainingService;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.UtilTools;
import com.web.bean.AuthToken;
import com.web.bean.RespInfo;
import com.web.bean.UserInfo;
import com.web.bean.VisitorType;
import com.web.service.TokenServer;
import com.web.service.UserService;
import com.web.service.VisitorTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "QuestionnaireController", tags = "API_调查问卷管理", hidden = true)
public class QuestionnaireController {

    private final static Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private TrainingService trainingService;
    
    @Autowired
    private SatisfactionService satisfactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "问卷管理_添加试卷设置 /getQuestionnaire ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addQuestionnaire")
    @ResponseBody
    public RespInfo addQuestionnaire(@ApiParam(value = "Questionnaire 调查问卷Bean", required = true) @Validated @RequestBody Questionnaire que,
                                     HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != que.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        RespInfo respInfo = new RespInfo(0, "success", que);
        if (StringUtils.isBlank(que.getTitle()) || (null == que.getFdesc())) {
            // 题目内容，选项，答案不可以为空
            logger.info("addQuestionnaire：title is null or fdesc is null");
            respInfo.setStatus(-1);
            respInfo.setResult("试卷标题或描述为空，请后重试！");
            return respInfo;
        }

        int num = -1;
        try {
            long qid = System.currentTimeMillis();
            que.setQid(String.valueOf(qid));
            num = questionnaireService.addQuestionnaire(que);
            if (num < 1) {
                respInfo.setStatus(-1);
                respInfo.setResult("设置试卷失败");
                return respInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }


    @ApiOperation(value = "问卷管理_获取问卷列表 /getQuestionnaire ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getQuestionnaire")
    @ResponseBody
    public RespInfo getQuestionnaire(@ApiParam(value = "ReqQuestion 请求问卷Bean", required = true) @Validated @RequestBody ReqQuestion rque,
                                     HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != rque.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        RespInfo respInfo = new RespInfo(0, "success");
        if (null == rque) {
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
            return respInfo;
        }

        try {
            Page<Questionnaire> rpage = new Page<Questionnaire>(rque.getStartIndex() / rque.getRequestedCount() + 1, rque.getRequestedCount(), 0);
            rque.setPage(rpage);
            List<Questionnaire> quelist = questionnaireService.getQuestionnaire(rque);
            rpage.setList(quelist);
            respInfo.setResult(rpage);
        } catch (Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }


    @ApiOperation(value = "问卷管理_获取答题结果 /getAnswerResult  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"identity\":\"13515512375\",\n" +
                    "    \"userid\":\"2147483647\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAnswerResult")
    @ResponseBody
    public RespInfo getAnswerResult(@RequestBody VisitorAnswer va,HttpServletRequest request, BindingResult result)  {

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, va.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        va=questionnaireService.getAnswerByIdentity(va);
        return new RespInfo(0,"success",va);
    }

    /**
     * 根据试卷ID，获取该试卷下的所有题目
     *
     * @param rque
     * @return
     */
    @ApiOperation(value = "问卷管理_获取考试题库 /getQueDetailList  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getQueDetailList")
    @ResponseBody
    public RespInfo getQueDetailList(@RequestBody ReqQuestionDetail rque) {
        RespInfo resInfo = new RespInfo(0, "success", null);
        if (StringUtils.isBlank(rque.getQid())) {
            resInfo.setStatus(-1);
            resInfo.setReason("fail");
            return resInfo;
        }

        Page<QuestionnaireDetail> rpage = new Page<QuestionnaireDetail>(rque.getStartIndex() / rque.getRequestedCount() + 1,
                rque.getRequestedCount(), 0);
        rque.setPage(rpage);
        List<QuestionnaireDetail> quelist = questionnaireService.getQuestionnaireDetail(rque);
        rpage.setList(quelist);
        return new RespInfo(0, "success", rpage);
    }

    /**
     * 试卷更新
     *
     * @param que
     * @return
     */
    @ApiOperation(value = "问卷管理_更新题库设置 /updateQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateQuestionnaire")
    @ResponseBody
    public RespInfo  updateQuestionnaire(@RequestBody Questionnaire que) {
        RespInfo respInfo = new RespInfo(0, "success");
        if (StringUtils.isBlank(que.getQid())) {
            logger.info("updateQuestionnaire qid is null");
            respInfo.setStatus(-1);
            respInfo.setResult("参数有误，请后重试！");
            return respInfo;
        }

        int num = -1;
        try {
            num = questionnaireService.updateQuestionnaire(que);
            if(num < 1) {
                respInfo.setStatus(-1);
                respInfo.setResult("更新试卷失败");
                return respInfo;
            }
        }catch(Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }


    @ApiOperation(value = "问卷管理_获取培训资料 /getTrainingVideo  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getTrainingVideo")
    @ResponseBody
    public RespInfo getTrainingVideo(@RequestBody  Training t)  {
        Page<Training> rpage = new Page<Training>(t.getStartIndex()/t.getRequestedCount()+1, t.getRequestedCount(), 0);
        t.setPage(rpage);
        List<Training> tlist=trainingService.getTrainingVideo(t);
        rpage.setList(tlist);
        return new RespInfo(0,"success",rpage);
    }

    /**
     *  试卷删除
     * @param que
     * @return
     */
    @ApiOperation(value = "问卷管理_删除题库 /delQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delQuestionnaire")
    @ResponseBody
    public RespInfo delQuestionnaire(@RequestBody Questionnaire que) {
        int i=questionnaireService.delQuestionnaire(que);
        if(i>0){
            questionnaireService.delQuestionnaireByQid(que);
            VisitorType vt=new VisitorType();
            vt.setQid(que.getQid());;
            visitorTypeService.updateVisitorTypeQid(vt);

        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "问卷管理_上传培训视频 /addTrainingVideo  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addTrainingVideo")
    @ResponseBody
    public RespInfo addTrainingVideo(@RequestBody  Training t)  {
        t.setTvid(String.valueOf(System.currentTimeMillis()));
        if(StringUtils.isEmpty(t.getQid())){
            return new RespInfo(-1,"qid is null");
        }
        trainingService.addTrainingVideo(t);
        return new RespInfo(0,"success");
    }

    @ApiOperation(value = "问卷管理_删除培训视频 /delTrainingVideo  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delTrainingVideo")
    @ResponseBody
    public RespInfo delTrainingVideo(@RequestBody  Training t)  {
        trainingService.delTrainingVideo(t);
        return new RespInfo(0,"success");
    }

    /**
     * 添加题目接口
     *
     * @param queDetailBean 题干明细实体类
     * @return
     */
    @ApiOperation(value = "问卷管理_添加题目接口 /addQuestionnaireDetail  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addQuestionnaireDetail")
    @ResponseBody
    public RespInfo addQuestionnaireDetail(@RequestBody QuestionnaireDetail queDetailBean) {
        RespInfo respInfo = new RespInfo(0, "success");
        if (StringUtils.isBlank(queDetailBean.getQid())
                || StringUtils.isBlank(queDetailBean.getQuestion())
                || (null == queDetailBean.getFoption())
                || StringUtils.isBlank(queDetailBean.getAnswer())) {
            logger.info("addQuestionnaireDetail question is null or option is null");
            // 题目内容，选项，答案不可以为空
            respInfo.setStatus(-1);
            respInfo.setResult("参数有误！");
            return respInfo;
        }

        int num = -1;
        try {
            num = questionnaireService.addQuestionnaireDetail(queDetailBean);
            if(num < 1) {
                respInfo.setStatus(-1);
                respInfo.setResult("添加题目失败");
                return respInfo;
            }
        }catch(Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }

    /**
     * 修改题目
     * @param que
     * @return
     */
    @ApiOperation(value = "问卷管理_更新题目接口 /updateQuestionnaireDetail  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateQuestionnaireDetail")
    @ResponseBody
    public RespInfo updateQuestionnaireDetail(@RequestBody QuestionnaireDetail que) {
        RespInfo respInfo = new RespInfo(0, "success");
        if (0 == que.getQdid()) {
            logger.info("updateQuestionnaireDetail qid is null");
            respInfo.setStatus(-1);
            respInfo.setResult("参数有误，请后重试！");
            return respInfo;
        }

        int num = -1;
        try {
            num = questionnaireService.updateQuestionnaireDetail(que);
            if(num < 1) {
                respInfo.setStatus(-1);
                respInfo.setResult("更新题目失败");
                return respInfo;
            }
        }catch(Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }

    /**
     *  题目删除
     * @param que
     * @return
     */
    /**
     * 修改题目
     * @param que
     * @return
     */
    @ApiOperation(value = "问卷管理_删除题目接口 /delQuestionnaireDetail  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"qdid\":17,\"userid\":2147483647}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delQuestionnaireDetail")
    @ResponseBody
    public RespInfo delQuestionnaireDetail(@RequestBody QuestionnaireDetail que) {
        questionnaireService.delQuestionnaireDetail(que);
        return new RespInfo(0, "success");
    }

    /**
     * 根据试卷ID，获取本次考试题目
     *
     * @param rque
     * @return
     */
    @ApiOperation(value = "问卷管理_随机获取考题 /getRandomTopicList  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRandomTopicList")
    @ResponseBody
    public RespInfo getRandomTopicList(@RequestBody ReqQuestionDetail rque) {
        RespInfo resInfo = new RespInfo(0, "success", null);
        if (StringUtils.isBlank(rque.getQid())) {
            resInfo.setStatus(-1);
            resInfo.setReason("fail");
            return resInfo;
        }

        ReqQuestion rq =new ReqQuestion();
        rq.setQid(rque.getQid());
        rq.setUserid(rque.getUserid());

        Questionnaire que=questionnaireService.getQueByQid(rq);
        String choice[]=que.getTopicNum().split(",");

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("qid", rque.getQid());
        map.put("userid", rque.getUserid());
        map.put("rtype", 0);
        List<QuestionnaireDetail> singlelist = questionnaireService.getRandomTopic(map);
        Set<Integer> set=new HashSet<Integer>();
        List<QuestionnaireDetail> result=new ArrayList<QuestionnaireDetail>();
        Random rand = new Random(System.currentTimeMillis());
        if(singlelist.size()>0){
            int single= Integer.parseInt(choice[0]);
            if(single>singlelist.size()){
                single=singlelist.size();
            }
            for(int i=0;i<single;i++){
                while(true){
                    int s=rand.nextInt(singlelist.size());
                    if(!set.contains(s)){
                        result.add(singlelist.get(s));
                        set.add(s);
                        break;
                    }
                }
            }
        }


        map.put("rtype", 1);
        int multi= Integer.parseInt(choice[1]);
        List<QuestionnaireDetail> multilist= questionnaireService.getRandomTopic(map);
        if(multilist.size()>0){
            if(multi>multilist.size()){
                multi=multilist.size();
            }
            set.clear();
            for(int a=0;a<multi;a++){
                while(true){
                    int m=rand.nextInt(multilist.size());
                    if(!set.contains(m)){
                        result.add(multilist.get(m));
                        set.add(m);
                        break;
                    }
                }
            }
        }

        return new RespInfo(0, "success", result);
    }

    /**
     * 访客提交试卷
     *
     * @param examBean
     * @return
     */
    @ApiOperation(value = "问卷管理_访客提交试卷 /examSubmit  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/examSubmit")
    @ResponseBody
    public RespInfo examSubmit(@RequestBody ReqExamBean examBean) {
        RespInfo respInfo = new RespInfo(0, "success");
        if (StringUtils.isBlank(examBean.getQid())) {
            logger.info("examSubmit: qid is null");
            respInfo.setStatus(-1);
            respInfo.setResult("参数有误，请稍后重试！");
            return respInfo;
        }

        int num = -1;
        int grade = 0;
        try {
//			UserInfo userinfo=userService.getExtendsInfo(examBean.getUserId());
//			int permission=userinfo.getPermissionSwitch();
//
//			//判断访客是否是邀请的客户，如果不在，不可以答卷
//			Map<String, Object> conditions = new HashMap<String,Object>();
//			conditions.put("cardId", examBean.getIdcard());
//			conditions.put("vphone", examBean.getVphone());
//			conditions.put("vemail", examBean.getVemail());
//			conditions.put("userid", examBean.getUserId());
//			if(permission==1){
//				conditions.put("permission", permission);
//			}else{
//				conditions.put("permission", "0");
//			}
//			List<RespVisitor> getVisList = visitorService.getExamVisitList(conditions);
//			if(CollectionUtils.isEmpty(getVisList)) {
//				respInfo.setStatus(-1);
//				respInfo.setResult("该访客信息有误，请确认后重试 ！");
//				return respInfo;
//			}

//			//获取访客资料明细
//			RespVisitor visitor = getVisList.get(0);
//			if(null == visitor) {
//				respInfo.setStatus(-1);
//				respInfo.setResult("未获取到该访客的受邀信息 ！");
//				return respInfo;
//			}

            //获取访客资料明细
            VisitorType visType=new VisitorType();
            visType.setTid(examBean.getTid());
            visType.setUserid(examBean.getUserId());

            //通过tid查询用户访客类型，以及有效期周期
            visType = visitorTypeService.getVisitorTypeByTid(visType);
            if(null == visType) {
                respInfo.setStatus(-1);
                respInfo.setResult("未获取到访客的访客类型！");
                return respInfo;
            }

            //通过试卷id查询到试卷明细
            ReqQuestion rque =new ReqQuestion();
            rque.setQid(examBean.getQid());
            rque.setUserid(examBean.getUserId());

            Questionnaire ques  =  questionnaireService.getQueByQid(rque);
            if(null == ques) {
                respInfo.setStatus(-1);
                respInfo.setResult("参数有误，请稍后重试！");
                return respInfo;
            }

            //通过试卷id，查询出该试卷下面所有题目对象
            ReqQuestionDetail reqQue = new ReqQuestionDetail();
            reqQue.setQid(examBean.getQid());
            reqQue.setUserid(examBean.getUserId());
            List<QuestionnaireDetail> queListOr = questionnaireService.getQuestionnaireDetail(reqQue);

            //访客回答的结果
            List<QuestionnaireDetail> queListRes = examBean.getReqDetList();
            int i=0;
            if(!CollectionUtils.isEmpty(queListOr) && !CollectionUtils.isEmpty(queListRes)) {
                //遍历题目，然后计算出总分数
                for(int s=0;s<queListOr.size();s++) {
                    for(int k=0;k<queListRes.size();k++) {
                        if(queListRes.get(k).getQdid() == queListOr.get(s).getQdid()
                                &&queListRes.get(k).getAnswer().equals(queListOr.get(s).getAnswer())) {
                            grade +=  queListOr.get(s).getScore();
                            i=i+1;
                        }
                        continue;
                    }
                }
            }

            VisitorAnswer visAns = new VisitorAnswer();
            visAns.setCorrect(grade);//考试分数
            visAns.setRcount(i);
            //是否及格
            if(grade >= ques.getTopicRigNum()) {
                visAns.setIfPass(1);
            }else {
                visAns.setIfPass(0);
            }
            if(null!=examBean.getVphone()){
                visAns.setIdentity(examBean.getVphone()); //手机号
            }else if(null!=examBean.getVemail()){
                visAns.setIdentity(examBean.getVemail());//邮箱
            }else if(null!=examBean.getIdcard()){
                visAns.setIdentity(examBean.getIdcard());//身份证号码
            }

            if(grade >=  ques.getTopicRigNum()) {
                visAns.setPassDate(new Date());// 考试通过的时间
                visAns.setEndDate(addDate(new Date(), visType.getPovDays()));//有效期结束时间

                visAns.setName(examBean.getName());
                visAns.setUserid(examBean.getUserId());//公司id
                VisitorAnswer va=questionnaireService.getAnswerByIdentity(visAns);
                if(null==va){
                    num = questionnaireService.addExamResult(visAns);
                }else{
                    num = questionnaireService.updateExamResult(visAns);
                }

                if(num < 1) {
                    respInfo.setStatus(-1);
                    respInfo.setResult("提交试卷失败");
                    return respInfo;
                }
            }

            respInfo.setResult(visAns);
        }catch(Exception e) {
            e.printStackTrace();
            respInfo.setStatus(-1);
            respInfo.setReason("fail");
        }
        return respInfo;
    }

    public Date addDate(Date date, int x)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, x);
        return cal.getTime();
    }
    
    //=====================================================================================================

    @ApiOperation(value = "问卷管理_问卷设置添加/更新 /addSatisfaction  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"sid\":1,\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"title\":\"标题：你阿吃过啦\",\n" +
                    "    \"description\":\"问卷说明\",\n" +
                    "    \"scoreDesc\":\"评分说明\",\n" +
                    "    \"expirydate\":\"1\",\n" +
                    "    \"createTime\":\"1623203256197\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addSatisfaction")
    @ResponseBody
    public RespInfo addSatisfaction(@RequestBody Satisfaction sa, HttpServletRequest request) {
        if (StringUtils.isBlank(sa.getTitle())
                || StringUtils.isBlank(sa.getDescription())
                || StringUtils.isBlank(sa.getScoreDesc())
                || sa.getExpirydate() <= 0) {
            return new RespInfo(-1, "Fail");
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());
        int i = -1;
        if (sa.getSid() != 0) {
            satisfactionService.updateSatisfaction(sa);
        } else {
            i = satisfactionService.addSatisfaction(sa);
            if (i == -1) {
                return new RespInfo(-1, "Fail");
            }
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "问卷管理_获取问卷设置 /getSatisfaction  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSatisfaction")
    @ResponseBody
    public RespInfo getSatisfaction(@RequestBody Satisfaction sa, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());
        Satisfaction satisfaction = satisfactionService.getSatisfactionByUserid(sa);
        return new RespInfo(0, "success", satisfaction);
    }


    @ApiOperation(value = "问卷管理_添加调查表题目 /addSatisfactionQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"sqid\":0,\n" +
                    "    \"qtype\":1,\n" +
                    "    \"question\":\"题目：你阿吃过啦\",\n" +
                    "    \"option\":\"A,b,c,d\",\n" +
                    "    \"answer\":\"答案A\",\n" +
                    "    \"feedback\":\"好吃的很咯\",\n" +
                    "    \"sorder\":0\n" +
                    "}\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addSatisfactionQuestionnaire")
    @ResponseBody
    public RespInfo addSatisfactionQuestionnaire(@RequestBody SatisfactionQuestionnaire sq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        int i = -1;
        sq.setUserid(authToken.getUserid());
        sq.setCreateTime(new Date());
        UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
        if ("0".equals(userInfo.getSatisfactionQuestionnaire()) || StringUtils.isBlank(sq.getQuestion())
                || StringUtils.isBlank(sq.getOption()) || StringUtils.isBlank(sq.getFeedback())) {
            return new RespInfo(-1, "Fail");
        }
        i = satisfactionService.addSatisfactionQuestionnaire(sq);
        if (i == -1) {
            return new RespInfo(-1, "Fail");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "问卷管理_删除调查表题目 /deleteSatisfactionQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/deleteSatisfactionQuestionnaire")
    @ResponseBody
    public RespInfo deleteSatisfactionQuestionnaire(@RequestBody SatisfactionQuestionnaire sq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        int i = -1;
        sq.setUserid(authToken.getUserid());
        if (sq.getSqid() <= 0) {
            return new RespInfo(-1, "Fail delete");
        }
        i = satisfactionService.deleteSatisfactionQuestionnaire(sq);
        if (i == -1) {
            return new RespInfo(-1, "Fail delete");
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "问卷管理_根据userid获取调查项目列表 /getSatisfactionQuestionnaireByUserid  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSatisfactionQuestionnaireByUserid")
    @ResponseBody
    public RespInfo getSatisfactionQuestionnaireByUserid(@RequestBody SatisfactionQuestionnaire sq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        int i = -1;
        sq.setUserid(authToken.getUserid());
        List<SatisfactionQuestionnaire> questionnaires = satisfactionService.getSatisfactionQuestionnaireByUserid(sq);
        return new RespInfo(0, "success", questionnaires);
    }

    @ApiOperation(value = "问卷管理_获取调查表题目详情 /getSatisfactionQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"sqid\":1\n" +
                    "}\n" +
                    "    "
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSatisfactionQuestionnaire")
    @ResponseBody
    public RespInfo getSatisfactionQuestionnaire(@RequestBody SatisfactionQuestionnaire sq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (sq.getSqid() <= 0) {
            return new RespInfo(-1, "fail");
        }
        sq.setUserid(authToken.getUserid());
        SatisfactionQuestionnaire questionnaire = satisfactionService.getSatisfactionQuestionnaire(sq);
        return new RespInfo(0, "success", questionnaire);
    }

    @ApiOperation(value = "问卷管理_更新调查表题目详情 /updateSatisfactionQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSatisfactionQuestionnaire")
    @ResponseBody
    public RespInfo updateSatisfactionQuestionnaire(@RequestBody SatisfactionQuestionnaire sq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (sq.getSqid() <= 0) {
            return new RespInfo(-1, "fail");
        }
        sq.setUserid(authToken.getUserid());
        int i = satisfactionService.updateSatisfactionQuestionnaire(sq);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "问卷管理_完成调查问卷 /finishQuestionnaire  ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"secid\":\"服务器返回的加密串\",\n" +
                    "    \"overallScore\":90,\n" +
                    "    \"scoreDetail\":\"问卷详细信息\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/finishQuestionnaire")
    @ResponseBody
    public RespInfo finishQuestionnaire(@RequestBody RequestVisit requestVisit) {
        String secid = requestVisit.getSecid();
        if (StringUtils.isBlank(secid)) {
            return new RespInfo(-1, "invalid vid");
        }
        String vid = AESUtil.decode(secid, Constant.AES_KEY);
        Visitor visitor = visitorService.getVisitorById(Integer.parseInt(vid));
        if (null != visitor) {
            Visitor v = new Visitor();
            v.setVid(visitor.getVid());
            v.setOverallScore(requestVisit.getOverallScore());
            v.setScoreDetail(requestVisit.getScoreDetail());
            //完成调查问卷
            visitorService.finishQuestionnaire(v);
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "invalid vid");
        }
    }


}
