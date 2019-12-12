package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.impl;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.config.ScheduledTasks;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums.CacheKeyEnum;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums.JumpResultEnum;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.CfgNatPgw;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.DnsDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.HttpDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.RptCusSerAbnormalAll;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.VideoDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.CustomerHbaseServiceI;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.CustomerServiceSupportServiceI;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.ConstantsUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.HBaseUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.JumpResultUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.StringUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class CustomerServiceSupportSecondLineExportImpl implements CustomerServiceSupportServiceI {

    
  
    private CustomerHbaseServiceI hbaseService = new CustomerHbaseServiceImpl();
    
    //导出数据
    private List<Map<String, Object>> exportData = new ArrayList<>();

    //定界结果数据
    private List<Map<String, Object>> resultData = new ArrayList<>();


    public List<Map<String, Object>> getResult(Map<String, Object> map) {
        List<Map<String, Object>> result = new ArrayList<>();
        //根据用户场景判断流程
        Integer module = Integer.valueOf(map.get("module").toString());
        System.out.println("module "+module);
        switch (module) {
//            网速慢
            case 0:
                result = networkSpeedLow(map);
                break;
//                无4G信号
            case 1:
                result = signallingPlaneJump(map);

                break;
//                频繁掉线
            case 2:
                result = frequently(map);
                break;
//                客户端软件
            case 3:
                result = networkSpeedLow(map);
                break;
//                有信号无法登陆
            case 4:
                result = frequently(map);
                break;
//                网页无法正常打开
            case 5:
                result = notOpenNet(map);
                break;
        }
        //如果result 有值直接返回  modfiy by liukang
        if(!result.isEmpty()){
            return result;
        }
        //信令侧后四步PDN、终端、无线判断逻辑移植到用户侧之后
        List<Map<String, Object>> afterUsersSign =signallingPlaneJumpAfterUserPlaneJump(map);
        if (!afterUsersSign.isEmpty()) {
            return afterUsersSign;
        }
        return result;
    }


    /**
     * 网页无法正常打开
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> notOpenNet(Map<String, Object> map) {

        List<Map<String, Object>> result = new ArrayList<>();

//        信令面定界
        List<Map<String, Object>> signall = signallingPlaneJump(map);
        if (!signall.isEmpty()) {
            return signall;
        }
//        无理由
        List<Map<String, Object>> grounds = groundlessJump(map);
        if (!grounds.isEmpty()) {
            return grounds;
        }
//        3:用户面
        List<Integer> userParam = new ArrayList<>();
        userParam.add(0);
        userParam.add(2);
        List<Map<String, Object>> users = userPlaneJump(map);
        if (!users.isEmpty()) {
            return users;
        }

        return result;

    }

    /**
     * 限速判断逻辑，只有网速慢的情景加入限速逻辑
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> speedLimitJump(Map<String, Object> map) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Integer type = Integer.valueOf(map.get("type").toString());
        List<Map<String, Object>> result = new ArrayList<>();
        //限速数据获取时间最近一个小时的数据
        List<Map<String, Object>> speedLimitData = hbaseService.getSpeedLimitData(map);
        //如果数据不为空,且系统时间-当天查询最后的限速时间<=5（因为数据会延迟四个多小时后才生成），定界为限速；不是当天，最后时间的限速时间是23点，那么就是限速，否则不是限速。
        if (!speedLimitData.isEmpty()) {
            //取得时间最近的限速记录
            Map<String, Object> lastMap = speedLimitData.get(speedLimitData.size() - 1);
            //获得最近时间
            String lastTime = lastMap.get("sdate") == null ? "" : lastMap.get("sdate") + "";
            if (!"".equals(lastTime)) {
                String nowDate = sdf.format(new Date());
                //如果是当前时间
                if (lastTime.substring(0, 8).equals(nowDate.substring(0, 8))) {
                    //当前时间转化为日期
                    Date lastDate = null;
                    try {
                        lastDate = sdf.parse(lastTime + "0000");
                        //如果时间间隔》=5小时
                        long nd = 1000 * 24 * 60 * 60;//每天毫秒数
                        long nh = 1000 * 60 * 60;//每小时毫秒数
                        long hours = (new Date().getTime() - lastDate.getTime()) % nd / nh; // 计算差多少小时
                        if (hours <= 5.0) {
                            String speed = lastMap.get("apn_ambr_dl") == "0" ? "" : lastMap.get("apn_ambr_dl") + "";
                            //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SPEED_LIMIT.getCode(), type);
                            List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SPEED_LIMIT.getCode(), type);
                            String jump = jumpResult.get(0).get("jump").toString();
                            jump = jump + speed + "kbps";
                            jumpResult.get(0).put("jump", jump);
                            return jumpResult;
                        }
                    } catch ( Exception e ) {
//                        log.info("日期转化错误");
                    }

                }
                //如果不是当天
                else {
                    //最近的时间是晚上23点，那么就是限速问题
                    String lastTime_23 = lastTime.substring(0, 8) + "23";
                    if (lastTime.equals(lastTime_23)) {
                        String speed = lastMap.get("apn_ambr_dl") == "0" ? "" : lastMap.get("apn_ambr_dl") + "";
                        //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SPEED_LIMIT.getCode(), type);
                        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SPEED_LIMIT.getCode(), type);
                        String jump = jumpResult.get(0).get("jump").toString();
                        jump = jump + speed + "kbps";
                        jumpResult.get(0).put("jump", jump);
                        return jumpResult;
                    }
                }
            }
        }
        /*if (!speedLimitData.isEmpty()&&getLimitDataExistOrNot(speedLimitData, map.get("phone").toString())) {
            return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SPEED_LIMIT.getCode(), type);
        }*/
        return result;
    }

    /**
     * 判断list对象中是否包含手机号码
     *
     * @param list
     * @return
     */
    private boolean getLimitDataExistOrNot(List<Map<String, Object>> list, String phone) {
        for (Map<String, Object> temp : list) {
            String onePhone = temp.get("msisdn") == null ? "" : temp.get("msisdn") + "";
            if (onePhone.equals(phone)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 频繁掉线
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> frequently(Map<String, Object> map) {

        List<Map<String, Object>> result = new ArrayList<>();
//        信令面定界
        List<Map<String, Object>> signall = signallingPlaneJump(map);
        if (!signall.isEmpty()) {
            return signall;
        }
        List<Map<String, Object>> grounds = groundlessJump(map);
        if (!grounds.isEmpty()) {
            return grounds;
        }
//        3:用户面
        List<Integer> userParam = new ArrayList<>();
        userParam.add(0);
        List<Map<String, Object>> users = userPlaneJump(map);
        if (!users.isEmpty()) {
            return users;
        }
        return result;

    }


    /**
     * 网速慢
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> networkSpeedLow(Map<String, Object> map) {

        List<Map<String, Object>> result = new ArrayList<>();
        Integer module = Integer.valueOf(map.get("module").toString());
        //     用户限速，只有网速慢的情况下加入限速判断
        if (0 == module) {
            //北京排除限速表  List<Map<String, Object>> speedLimit = speedLimitJump(map); modfiy by liukang
            List<Map<String, Object>> speedLimit = new ArrayList<>();
            if (!speedLimit.isEmpty()) {
                return speedLimit;
            }
        }
//      2：信令面
        List<Map<String, Object>> signalls = signallingPlaneJump(map);
        if (!signalls.isEmpty()) {
            return signalls;
        }
//        无理由申诉 modfiy by liukang 北京没有这个表
       /* List<Map<String, Object>> grounds = groundlessJump(map);
        if (!grounds.isEmpty()) {
            return grounds;
        }*/
//       3:用户面
        List<Integer> userParam = new ArrayList<>();
        List<Map<String, Object>> users = userPlaneJump(map);
        if (!users.isEmpty()) {
            return users;
        }
        return result;
    }

    @Override
    public Map<String, Object> getExceptionTime(Map<String, Object> map) {

        Map<String, Object> exceptionTime = hbaseService.getExceptionTime(map);

        return exceptionTime;
    }

    /**
     * 获得饼图问题占比
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> getResultPieChart(Map<String, Object> map) {
        {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            // modfiy by liukang  mapper.getUserCasePhoenixPieChart(map);
            Map<String, Object> userCase = hbaseService.getUserCasePhoenixPieChart(map);
            if (userCase == null) {
                return result;
            }
//            log.info("333333-===userCase====333333333 "+userCase);
            Float core = Float.valueOf(userCase.get("CORE") == null ? "0" : userCase.get("CORE").toString());
            Float dns = Float.valueOf(userCase.get("DNS") == null ? "0" : userCase.get("DNS").toString());
            Float terminal = Float.valueOf(userCase.get("TERMINAL") == null ? "0" : userCase.get("TERMINAL").toString
                    ());
            Float wireless = Float.valueOf(userCase.get("WIRELESS") == null ? "0" : userCase.get("WIRELESS").toString
                    ());
            Float sp = Float.valueOf(userCase.get("SP") == null ? "0" : userCase.get("SP").toString());
            Float users = Float.valueOf(userCase.get("USERS") == null ? "0" : userCase.get("USERS").toString());

            Float sp2 = Float.valueOf(userCase.get("SP2") == null ? "0" : userCase.get("SP2").toString());
            Float usersNew = Float.valueOf(userCase.get("USERSNEW") == null ? "0" : userCase.get("USERSNEW").toString());
           Float sum = core+dns+terminal+wireless+sp+users+sp2+usersNew;
           DecimalFormat df2 = new DecimalFormat("###.00");
       

           System.out.println("转换2="+df2.format(1/(float)8));
            Map coreMap = new HashMap<String, Object>();
            coreMap.put("value", df2.format(core/sum));
            coreMap.put("name", "核心网");
            Map dnsMap = new HashMap<String, Object>();
            dnsMap.put("value", df2.format(dns/sum));
            dnsMap.put("name", "DNS");
            Map terminalMap = new HashMap<String, Object>();
            terminalMap.put("value", df2.format(terminal/sum));
            terminalMap.put("name", "终端");
            Map wirelessMap = new HashMap<String, Object>();
            wirelessMap.put("value", df2.format(wireless/sum));
            wirelessMap.put("name", "无线");
            Map spMap = new HashMap<String, Object>();
            spMap.put("value", df2.format((sp+sp2)/sum));
            spMap.put("name", "SP");
            Map usersMap = new HashMap<String, Object>();
            usersMap.put("value", df2.format(users/sum));
            //usersMap.put("name", "用户");
            usersMap.put("name", "无线原因(个别用户)");
            Map userNewMap = new HashMap<String, Object>();
            userNewMap.put("value", df2.format(usersNew/sum));
            userNewMap.put("name", "用户");
            result.add(coreMap);
            result.add(dnsMap);
            result.add(terminalMap);
            result.add(wirelessMap);
            result.add(spMap);
            result.add(usersMap);
            result.add(userNewMap);
//            log.info("__________result_________"+result);
            return result;
        }
    }


    /**
     * 信令面判断方法
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> signallingPlaneJump(Map<String, Object> map) {


//        业务类型
        Integer type = Integer.valueOf(map.get("type").toString());
        List<Map<String, Object>> result = new ArrayList<>();
//        1-核心网问题 最后一次attach_status=1,且attach_flag=2,（核心网原因）失败；最后一次状态attach_status =
// 0，且detach_flag=1；频繁detach（核心网发起），detach次数在投诉时段内>1；service request，PDN被拒绝最后一次且为核心网原因，或在投诉时段内核心网原因导致的拒绝>2
//        List<Map<String, Object>> signaOne = hbaseService.getSignaOne(map);
        //根据最后一条记录判断是否是核心网问题，时间优先，满足条件的是核心网问题，否则不是
        String uuidExcel = (String) map.get("uuidExcel");
        //分表
        exportData = hbaseService.getSignaNew(map);
        //log.info("===signallingPlaneJump==="+exportData);
        //没有分表
        //exportData = hbaseService.getSignaOne(map);
        //获得最后一条记录
        if (!exportData.isEmpty()) {
            Map<String, Object> lastMap = exportData.get(exportData.size() - 1);
            if ((StringUtil.lastChart2Number(lastMap.get("attach_status")) == 1 && StringUtil.lastChart2Number
                    (lastMap.get("attach_flag")) == 2)
                    || (StringUtil.lastChart2Number(lastMap.get("attach_status")) == 0 && StringUtil.lastChart2Number
                    (lastMap.get("detach_flag")) == 1)
                    || (StringUtil.lastChart2Number(lastMap.get("detach_corenet_cnt")) > 10 && (StringUtil
                    .lastChart2Number(lastMap.get("detach_cnt")) - StringUtil.lastChart2Number(lastMap.get
                    ("detach_user_cnt"))) > 1)
                    || (StringUtil.lastChart2Number(lastMap.get("service_flag")) == 1 || StringUtil.lastChart2Number
                    (lastMap.get("service_request_corenet_cause")) > 5)
                    || (StringUtil.lastChart2Number(lastMap.get("pdn_flag")) == 1 || StringUtil.lastChart2Number
                    (lastMap.get("pdn_corenet_cause")) > 5)

                    /*|| (StringUtil.lastChart2Number(lastMap.get("service_flag")) == 1 && StringUtil.lastChart2Number
                    (lastMap.get("service_request_corenet_cause")) > 2)
                    || (StringUtil.lastChart2Number(lastMap.get("pdn_flag")) == 1 && StringUtil.lastChart2Number
                    (lastMap.get("pdn_corenet_cause")) > 2)*/) {
                //          有数据放入缓存
                String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
//                log.info("##########1111111key=======" + key);
                //CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
              //  CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_ONE.getCode(), type);
                return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_ONE.getCode(), type);
            }
        }
//        3-限速 --暂不处理

//        4-用户关闭4G数据业务或终端问题
//       //分表
        // exportData = hbaseService.getSignaNew(map);
        //没有分表
        //exportData = hbaseService.getSignaFour(map);
        if (!exportData.isEmpty()) {
            Map last4GTerMap = exportData.get(exportData.size() - 1);
            if ((StringUtil.lastChart2Number(last4GTerMap.get("attach_status")) == 0 && StringUtil.lastChart2Number
                    (last4GTerMap.get("detach_flag")) == 0)) {
                String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
               // log.info("##########222222key=======" + key);
//                CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
                // CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_FOUR.getCode(), type);
                return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_FOUR.getCode(), type);
            }
        }
        return result;
    }


    /**
     * 获取信令侧小区编号  add by liukang 20190715
     * @param
     * @param
     * @return
     */
    public static String getSignallingArea(List<Map<String, Object>> Data)
    {
        String returnEcgi="";
        for(Map<String, Object> singn :Data)
        {
            String f_ecgi=singn.get("ecgi")+"";
            returnEcgi=returnEcgi+f_ecgi+",";
       }

        if(!StringUtils.isEmpty(returnEcgi))
            returnEcgi=returnEcgi.substring(0,returnEcgi.length()-1);
        else
            returnEcgi="未知小区";
        return returnEcgi;

    }


    /**
     * 信令面判断方法
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> signallingPlaneJumpAfterUserPlaneJump(Map<String, Object> map) {

//        业务类型
        Integer type = Integer.valueOf(map.get("type").toString());
        String uuidExcel = (String) map.get("uuidExcel");
        List<Map<String, Object>> result = new ArrayList<>();
//        5-用户配置（PDN）Five
        //分表
        //exportData = hbaseService.getSignaNew(map);
        //没有分表
        //exportData = hbaseService.getSignaFive(map);
        if (!exportData.isEmpty()) {
            Map<String, Object> lastPdnMap = exportData.get(exportData.size() - 1);
            //   pdn_flag = 0  pdn_user_cause >0
            if (StringUtil.lastChart2Number(lastPdnMap.get("pdn_flag")) == 0
                    && StringUtil.lastChart2Number(lastPdnMap.get("pdn_user_cause")) >= -1) {
                String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
//                log.info("##########33333key=======" + key);
//                CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_FIVE.getCode(), type);
//            log.info("00000000----lastPdnMap");
                return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_FIVE.getCode(), type);
            }
        }
//        6-终端问题
        //分表
        //exportData = hbaseService.getSignaNew(map);
        //没有分表
        //exportData = hbaseService.getSignaSix(map);
        if (!exportData.isEmpty()) {
            Map lastTerMap = exportData.get(exportData.size() - 1);
            Integer attach_flag = StringUtil.lastChart2Number(lastTerMap.get("attach_flag"));
            Integer attach_user_cause = StringUtil.lastChart2Number(lastTerMap.get("attach_user_cause"));
            Integer service_flag = StringUtil.lastChart2Number(lastTerMap.get("service_flag"));
            Integer service_request_user_cause = StringUtil.lastChart2Number(lastTerMap.get
                    ("service_request_user_cause"));
            if (((attach_flag == 1 || attach_flag == 2) && attach_user_cause > 0) || ((service_flag == 1 ||
                    service_flag == 0) && service_request_user_cause > 0)) {
                String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
//                log.info("##########4444444key=======" + key);
//                CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
                //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_SIX.getCode(), type);
                return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_SIX.getCode(), type);
            }
        }
//         无线问题
        //分表
        //exportData = hbaseService.getSignaNew(map);
        //没有分表
        //exportData = hbaseService.getSignaSeven(map);
        resultData = exportData.stream()
                .filter(item -> (StringUtil.lastChart2Number(item.get("drop_radio_cause")) > 5
                        || StringUtil.lastChart2Number(item.get("handover_frequently_flag")) == 1
                        || StringUtil.lastChart2Number(item.get("tau_frequently_flag")) == 1))
                .sorted(CustomerHbaseServiceImpl.comparator.reversed())
                //.limit(1)
                .collect(Collectors.toList());
//        log.info("00000---exportData---- ："+exportData+"-----resultData-----:"+resultData
//        +"exportData.size():"+exportData.size()+"resultData.size() ："+resultData.size());
        //无线问题/总数> begin....
      //  if (!resultData.isEmpty() && resultData.size() / exportData.size() >= 0.05) {
        List<Map<String, Object>> top5MME = hbaseService.getWirelessTop5MME(map);
//        log.info("---------top5MME-------: "+top5MME);
        if (!top5MME.isEmpty()) {
            String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
//            log.info("##########5555555key=======" + key);
//            CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
            //CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
            List<Map<String, Object>> jumpResult = new ArrayList<>();
//        频繁事件查询
            //有分表
           // List<Map<String, Object>> frequently = hbaseService.getFrequently(map);
            //没有分表
            //List<Map<String, Object>> frequently = hbaseService.getFrequently(map);
//            7-匹配到盲点库
          //  List<Map<String, String>> scotoma = hbaseService.getSingbScotoma(map);
            //查询TOP5小区

            //if (!scotoma.isEmpty() || !frequently.isEmpty()) {
                //jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_SEVEN.getCode(), type);
               // jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_SEVEN.getCode(), type);
                // add by liukang begin...
              //  String jump = "";//jumpResult.get(0).get("advice").toString();
               // jump = jump + "小区编号如下:" + getSignallingArea(top5MME);
               // jumpResult.get(0).put("advice", jump);
                // add by liukang end...
                //jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_SEVEN.getCode(), type);
               // log.info("signallingPlaneJumpAfterUserPlaneJump_add_by_liukang"+jump);
            //    return jumpResult;
         //   }
//            未匹配到盲点库
            //jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.SIGNA_EIGHT.getCode(), type);
            jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.SIGNA_EIGHT.getCode(), type);

            // add by liukang begin...

              String jump = "";//jumpResult.get(0).get("advice").toString();
              jump = jump  + getSignallingArea(top5MME);
              jumpResult.get(0).put("advice", jump);

//             log.info("signallingPlaneJumpAfterUserPlaneJump_add_by_liukang"+jump);
            // add by liukang end...
            return jumpResult;
        }
        //无线问题/总数> end....
        //SP 或者是 用户原因begin... add by liukang
        Map<String, Object> judgeMap = hbaseService.getUserCasePhoenixPieChart(map);
        if(!judgeMap.isEmpty()){

          Float sp =   Float.valueOf(judgeMap.get("SP2") == null ? "0" : judgeMap.get("SP2").toString());
          Float user =   Float.valueOf(judgeMap.get("USERSNEW") == null ? "0" : judgeMap.get("USERSNEW").toString());
          if(sp >= user){
              return spQuestion(map);
          }else{
             return  usersNewQuestion(map);
          }
        }
     //SP 或者是 用户原因end...
        return result;
    }

    /**
     * 无理由申诉判断方法
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> groundlessJump(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        List<Map<String, Object>> result = new ArrayList<>();
       // exportData = mapper.getGroundless(map); modfiy by liukang
        exportData = new ArrayList<>();
        String uuidExcel = (String) map.get("uuidExcel");
        if (!exportData.isEmpty()) {
            String key = uuidExcel + "-" + CacheKeyEnum.SINGADATA.getCode();
//            CacheUtil.cacheSet(key, exportData, CacheKeyEnum.CACAHEKEY.getCode());
            //log.info("##########66666666key=======" + key);
            //CacheUtil.cacheSet(CacheKeyEnum.SINGADATA.getCode(), exportData, CacheKeyEnum.CACAHEKEY.getCode());
            //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.GROUNDLESS.getCode(), type);
            return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.GROUNDLESS.getCode(), type);
        }
        return result;
    }

    /**
     * 核心网问题
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> coreQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
//          设置问题类型  缓存数据
        map.put("export_type", "4");
        Map<String, Object> returnData = cacheUserData(map);
        //nat和nat以上问题尚未测试，暂时不开放
        List<HttpDetail> userHttpData = (List<HttpDetail>) returnData.get("userHttpData");
        List<HttpDetail> coreData = new ArrayList<HttpDetail>();
        //从详单中取出核心网的数据
        for (HttpDetail temp : userHttpData) {
            String page_failure_delimiting = StringUtils.isEmpty(temp
                    .getPage_failure_delimiting()) ? "0" : temp.getPage_failure_delimiting();
            String first_page_delay_delimiting = StringUtils.isEmpty(temp
                    .getFirst_page_delay_delimiting()) ? "0" : temp.getFirst_page_delay_delimiting();
            String dns_delay_delimiting = StringUtils.isEmpty(temp.getDns_delay_delimiting()
            ) ? "0" : temp.getDns_delay_delimiting();
            String page_throughput_delimiting = StringUtils.isEmpty(temp
                    .getPage_throughput_delimiting()) ? "0" : temp.getPage_throughput_delimiting();
            String tcp_delay_lower_interf_delimiting = StringUtils.isEmpty(temp
                    .getTcp_delay_lower_interf_delimiting()) ? "0" : temp.getTcp_delay_lower_interf_delimiting();
            String tcp_delay_upper_interf_delimiting = StringUtils.isEmpty(temp
                    .getTcp_delay_upper_interf_delimiting()) ? "0" : temp.getTcp_delay_upper_interf_delimiting();
            if ("4".equals(page_failure_delimiting) || "4".equals(first_page_delay_delimiting) || "4".equals
                    (dns_delay_delimiting) || "4".equals(page_throughput_delimiting) || "4".equals
                    (tcp_delay_lower_interf_delimiting) || "4".equals(tcp_delay_upper_interf_delimiting))
                coreData.add(temp);
        }
        if (!coreData.isEmpty()) {
            Map<String, Object> resultMap = getResultByCoreDataAndNat(coreData, map);
            String returnFlag = resultMap.get("flag").toString();
            String nat_name = resultMap.get("nat_name").toString();
            if ("2".equals(returnFlag))
                //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.NAT_UP_CODE.getCode(), type);
                return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.NAT_UP_CODE.getCode(), type);
            else if ("1".equals(returnFlag)) {
                if (!StringUtils.isEmpty(nat_name))
                    nat_name = nat_name.substring(0, nat_name.length() - 1);
                else
                    nat_name = "未知设备";
                //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.NAT_CODE.getCode(), type);
                List<Map<String, Object>> jumpResult =  JumpResultUtil.getJumpResultFromXml(JumpResultEnum.NAT_CODE.getCode(), type);
                String jump = jumpResult.get(0).get("advice").toString();
                jump = jump + "。Nat名称:" + nat_name;
                jumpResult.get(0).put("advice", jump);
                // 核心网问题
                return jumpResult;
            } else {
                //核心网问题带出pgwip和名称
                //查询配置表
                map.put("abnormal_sort", "4");
                //modfiy by liukang   List<CfgNatPgw> cfgNatPgwData = mapper.getCfgNatPgwData(map);
                List<CfgNatPgw> cfgNatPgwData = new ArrayList<>();
                String pgwips = "";
                for (HttpDetail temp : coreData) {
                    for (CfgNatPgw temp_d : cfgNatPgwData) {
                        if (temp.getPgw_ip_addr().equals(temp_d.getPgw_ip())) {
                            pgwips = pgwips + temp_d.getPgw_name() + "(" + temp_d.getPgw_ip() + ")" + ",";
                        }
                    }
                }
                if (!StringUtils.isEmpty(pgwips))
                    pgwips = pgwips.substring(0, pgwips.length() - 1);
                else
                    pgwips = "未知IP";
                //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_CORE_NET_CODE.getCode(), type);
                List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_CORE_NET_CODE.getCode(), type);
                String jump = jumpResult.get(0).get("advice").toString();
                jump = jump + "。pgw信息:" + pgwips;
                jumpResult.get(0).put("advice", jump);
                // 核心网问题
                return jumpResult;
            }
        } else {
           // return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_CORE_NET_CODE.getCode(), type);
            return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_CORE_NET_CODE.getCode(), type);
        }
    }

    /**
     * dns问题
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> dnsQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "2");
        Map<String, Object> returnData = cacheUserData(map);
        List<DnsDetail> userDnsData = (List<DnsDetail>) returnData.get("userDnsData");
        String dest_ips = "";
        Map<String, String> destIpMap = new HashMap<String, String>();
        //去除重复数据
        for (DnsDetail temp : userDnsData) {
            if (!StringUtils.isEmpty(temp.getDest_ip())) {
                destIpMap.put(temp.getDest_ip(), temp.getDest_ip());
            }
        }
        if (!destIpMap.isEmpty()) {
            Iterator<Map.Entry<String, String>> entries = destIpMap.entrySet().iterator();
            {
                Map.Entry<String, String> entry = entries.next();
                dest_ips = dest_ips + entry.getKey() + ",";
            }
        }
        //去掉最后一个，
        if (!StringUtils.isEmpty(dest_ips))
            dest_ips = dest_ips.substring(0, dest_ips.length() - 1);
        if (StringUtils.isEmpty(dest_ips))
            dest_ips = "未知IP";
        //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_DNS_CODE.getCode(), type);
        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_DNS_CODE.getCode(), type);
        String jump = jumpResult.get(0).get("advice").toString();
        jump = jump + "。DNS服务器地址为:" + dest_ips;
        jumpResult.get(0).put("advice", jump);
        return jumpResult;
    }


    /**
     * 终端问题问题
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> terminalQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "9");
        cacheUserData(map);
        //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_TERMINAL_CODE.getCode(), type);
        return JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_TERMINAL_CODE.getCode(), type);
    }
    /**
     * 无线问题
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> wirelessQuestion(Map<String, Object> map, boolean flag, List<Map<String,
            Object>> ciList) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "1");
        cacheUserData(map);
        // 无线问题 匹配到
        String cis = "";
        //拼接所有的小区ID ，供sql in条件使用
        // List<String> cell_id_list = new ArrayList<String>();
        Map<String, String> cell_id_map = new LinkedHashMap<String, String>();
//                将小区信息带出来
        //分表
        List<Map<String, Object>> wirelessTop10ci = hbaseService.getWirelessTop10(map);
//        log.info("222222222222222222=====wirelessTop10ci"+wirelessTop10ci.size());
        //如果小区多余3，去前三的小区
        if (!wirelessTop10ci.isEmpty() && wirelessTop10ci.size() > 5)
            // 如果想变为TOP3则可以放开这段代码 wirelessTop10ci = wirelessTop10ci.subList(0, 3);
            wirelessTop10ci = wirelessTop10ci.subList(0, 5);
        //List<Map<String, Object>> wirelessTop10ci = mapper.getWirelessTop10(map);
        for (Map<String, Object> s : wirelessTop10ci) {
           // long cell_id_value = Long.valueOf(s.get("ecgi").toString());// modfiy by liukang - 460110000000000L;
            String cell_id_value = s.get("ecgi").toString();
        	cell_id_map.put(cell_id_value + "", cell_id_value + "");
        }
        if (!cell_id_map.isEmpty()) {
            List<Map<String, Object>> areaMap = new ArrayList<Map<String, Object>>();
            if (ScheduledTasks.allCiData.isEmpty()) {
                long t1 = System.currentTimeMillis();
                List<String> cell_id_list = new ArrayList<String>();
                Iterator<Map.Entry<String, String>> entries = cell_id_map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = entries.next();
                    String cell_id = entry.getKey();
                    cell_id_list.add(cell_id);
                }
                // modfiy by liukang  areaMap = mapper.getCiByCellidFromHbase(cell_id_list); 代码删除
              //  log.info("------------------cell_id_list------------------"+cell_id_list);
                for(int i=0;i<cell_id_list.size();i++){
                    Map<String,Object> mapTmp = new HashMap<>();
                    mapTmp.put( "cell_id", cell_id_list.get(i).toString());
                    mapTmp.put( "site_name", cell_id_list.get(i).toString());
                    areaMap.add(mapTmp);

                }
            //    log.info("------------------areaMap------------------"+areaMap.toString());
                long t2 = System.currentTimeMillis();
//                log.info("@@@@@@@@@@@@@@@@@@@@@ time_old=" + (t2 - t1) + "   areaMap.size()=" + areaMap.size());
            } else {
                long t3 = System.currentTimeMillis();
                Iterator<Map.Entry<String, String>> entries = cell_id_map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = entries.next();
                    String cell_id = entry.getKey();
                   /* for (Map<String, Object> temp : ScheduledTasks.allCiData) {
                        String ci = temp.get("cell_id") + "";
                        if (cell_id.equals(ci)) {
                            areaMap.add(temp);
                        }
                    }*/
                    List<String> cell_id_list = new ArrayList<String>();
                    Iterator<Map.Entry<String, String>> entries_1 = cell_id_map.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, String> entry_1 = entries_1.next();
                        String cell_id_1 = entry.getKey();
                        cell_id_list.add(cell_id_1);
                    }
//                    log.info("------------------cell_id_list1------------------"+cell_id_list);
                    for(int i=0;i<cell_id_list.size();i++){
                        Map<String,Object> mapTmp = new HashMap<>();
                        mapTmp.put( "cell_id", cell_id_list.get(i).toString());
                        mapTmp.put( "site_name", cell_id_list.get(i).toString());
                        areaMap.add(mapTmp);

                    }
                }
                long t4 = System.currentTimeMillis();
//                log.info("@@@@@@@@@@@@@@@@@@@@@ time_new=" + (t4 - t3) + "   areaMap.size()=" + areaMap.size());
            }
            for (Map<String, Object> temp : areaMap) {
                String ci = temp.get("cell_id").toString();
                String wirelessname = temp.get("site_name").toString();
                //cis = cis + wirelessname + "(" + ci + ")" + ",";
                cis = cis + ci + ",";
            }
        }
        if (!StringUtils.isEmpty(cis))
            cis = cis.substring(0, cis.length() - 1);
        if (StringUtils.isEmpty(cis))
            cis = "未知小区";
//               匹配盲点库
        //List<Map<String, Object>> scotoma = mapper.getUserScotomaPhoenix(map);
        List<Map<String, Object>> scotoma = hbaseService.getUserScotoma(map);
        if (!scotoma.isEmpty()) {
//                  查询 配置表
            List<Map<String, Object>> mtach = new ArrayList<>();
            if (scotoma.size() > 0) {
                for (Map<String, Object> item : scotoma) {
                    //long ci = Long.valueOf(item.get("ecgi").toString());
                    String ci = item.get("ecgi").toString();
                   // Map<String, Object> md = mapper.getMd(ci); modfiy by liukang
                    Map<String, Object> md = new HashMap<>();
                    if (md != null) {
                        mtach.add(md);
                    }
                }
            }
            List<Map<String, Object>> filterMatch = new ArrayList<>();
            if (flag) {
                for (Map<String, Object> item : mtach) {
                    for (Map<String, Object> e : ciList) {
                        Long ci = Long.valueOf(e.get("cell_id").toString()) + 460110000000000L;
                        if (item.get("ecgi").equals(ci)) {
                            filterMatch.add(item);
                        }
                    }
                }
            }

            if (!filterMatch.isEmpty()) {
                //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_WIRELESS_MATCH_CODE.getCode(), type);
                List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_WIRELESS_MATCH_CODE.getCode(), type);
//                        无线问题 未匹配到
                if (jumpResult.size() > 0) {
                    String jump = jumpResult.get(0).get("advice").toString();
                    jump = jump + "" + cis;
                    jumpResult.get(0).put("advice", jump);
                }
                return jumpResult;
            }
        }
//              无线问题 未匹配到
        //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_WIRELESS_UNMATCH_CODE.getCode(), type);
        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_WIRELESS_UNMATCH_CODE.getCode(), type);
        if (jumpResult.size() > 0) {
            //String jump = jumpResult.get(0).get("advice").toString();
            String jump = "";
            jump = jump + cis;
            jumpResult.get(0).put("advice", jump);
        }
        return jumpResult;
    }


    /**
     * sp问题
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> spQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "3");
        Map returnData = cacheUserData(map);
        List<HttpDetail> userHttpData = (List<HttpDetail>) returnData.get("userHttpData");
        List<HttpDetail> userHttpDataLast = new ArrayList<HttpDetail>();
        //过滤sp问题
        for (HttpDetail temp : userHttpData) {
            if (StringUtil.lastChart2Number(temp.getTcp_delay_lower_interf_delimiting()) == 3 || StringUtil
                    .lastChart2Number(temp.getTcp_delay_upper_interf_delimiting()) == 3 || StringUtil
                    .lastChart2Number(temp.getPage_failure_delimiting()) == 3 || StringUtil.lastChart2Number(temp
                    .getFirst_page_delay_delimiting()) == 3
                    || StringUtil.lastChart2Number(temp.getDns_delay_delimiting()) == 3 || StringUtil
                    .lastChart2Number(temp.getPage_throughput_delimiting()) == 3) {
                userHttpDataLast.add(temp);
            }
        }
        //查询条件拼接
        List<String> service_type_list = new ArrayList<String>();
        //构造service_type和dest_ip的map对象，key为service_type，value为dest_ip的List<String>对象，取值前三条
        Map destIpListMap = new HashMap<String, List<String>>();
//       log.info("eeeeeeeeee---userHttpDataLast"+userHttpDataLast);
        for (HttpDetail temp : userHttpDataLast) {
            service_type_list.add(temp.getApp_sub_type()+"_"+temp.getService_type());
            if (null == destIpListMap.get(temp.getApp_sub_type()+"_"+temp.getService_type())) {
                List<String> ipList = new ArrayList<String>();
                ipList.add(temp.getDest_ip());
                destIpListMap.put(temp.getApp_sub_type()+"_"+temp.getService_type(), ipList);
            } else {
                List<String> ipList = (List<String>) destIpListMap.get(temp.getApp_sub_type()+"_"+temp.getService_type() );
                //取值前三条数据
                if (ipList.size() < 3) {
                    if (!containsDestIPOrNOt(temp.getDest_ip(), ipList)) {
                        ipList.add(temp.getDest_ip());
                        destIpListMap.put(temp.getApp_sub_type()+"_"+temp.getService_type(), ipList);
                    }
                }
            }
        }
       //根据service_type_list对象获得对应的service_name
//        log.info("=========================service_type_list=========================>"+service_type_list.toString());
        List<Map<String, Object>> listChineseServiceType = new ArrayList();
        if (!service_type_list.isEmpty()) {
            //modfiy by liukang  listChineseServiceType = mapper.getSpRcgRule(service_type_list);
           listChineseServiceType =HBaseUtil.getTable("npodo:cfg_do_app",service_type_list);



        }
        //log.info("spQuestion()--->维表查询的数据 "+listChineseServiceType.toString());
        //log.info("spQuestion()--->维表查询的数据的rowkey "+service_type_list.toString());
        //log.info("spQuestion()--->维表查询的数据的长度 ",listChineseServiceType.size());

        //根据service_name对象滤拼接sp名称和dest_ip，最终形成展示信息
//        log.info("9999999999&&&&&&&&----listChineseServiceType: "+listChineseServiceType);
        String service_name = "";
        for (Map<String, Object> temp : listChineseServiceType) {
            if (destIpListMap.isEmpty()) {
                service_name = service_name + temp.get("service_name") + ",";
            } else {
                List<String> ipList = (List<String>) destIpListMap.get(temp.get("service_type") + "");
                String destIpStr = "";
                for (String ip : ipList) {
                    destIpStr = destIpStr + ip + ",";
                }
                //数据不为空，去掉最后一个,
                if (!StringUtils.isEmpty(destIpStr))
                    destIpStr = destIpStr.substring(0, destIpStr.length() - 1);
                service_name = service_name + temp.get("service_name") + "(" + destIpStr + ")" + ",";
            }
        }
        //查询数据不为空，去掉最后一个，
        if (!StringUtils.isEmpty(service_name))
            service_name = service_name.substring(0, service_name.length() - 1);
        if (StringUtils.isEmpty(service_name))
            service_name = "未知网站";
        //List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_SP_CODE.getCode(), type);
        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_SP_CODE.getCode(), type);


         String jump = jumpResult.get(0).get("advice").toString();
        //String jump ="给出TOP问题SP，建议经后台技术人员确认后，向用户反馈并建议其使用其他SP作为替代或到该SP网站下投诉解决。";
//        log.info("&&&&&&&------&&service_name: "+service_name+" jump "+jump);
        if (!StringUtils.isEmpty(service_name))
            jump = jump + "网站名称:" + service_name;
//        log.info("&&&&&&&------&&service_name: "+service_name+" 拼接后的jump "+jump);
        jumpResult.get(0).put("advice", jump);
//        log.info("*********====jumpResult=======*******: "+jumpResult);
        // sp问题

        return jumpResult;
    }

    /**
     * 判断list对象中是否包含IP对象
     *
     * @param ip
     * @param ipList
     * @return
     */
    public boolean containsDestIPOrNOt(String ip, List<String> ipList) {
        for (String temp : ipList) {
            if (!StringUtils.isEmpty(temp) && temp.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用户问题  江苏的用户问题北京改为了 无线问题（个别用户）
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> usersQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "3");
        cacheUserData(map);
        // add by liukang 用户问题返回小区
        List<Map<String, Object>> wirelessTop10ci = hbaseService.getUserWirelessTop5(map);
//        log.info("====secondLine======usersQuestion+getUserWirelessTop5==="+wirelessTop10ci
//                +"wirelessTop10ci.size()"+wirelessTop10ci.size());
        //如果小区多余3，去前三的小区
        if (!wirelessTop10ci.isEmpty() && wirelessTop10ci.size() > 5)
            // 如果想变为TOP3则可以放开这段代码 wirelessTop10ci = wirelessTop10ci.subList(0, 3);
            wirelessTop10ci = wirelessTop10ci.subList(0, 5);
        //用户问题
        //用户问题输入小区在此修改
        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_USERS_CODE.getCode(), type);
        String jump = "";//jumpResult.get(0).get("advice").toString();
        jump = jump + getSignallingArea(wirelessTop10ci);
        jumpResult.get(0).put("advice", jump);
      //  getSignallingArea(wirelessTop10ci);
        //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_USERS_CODE.getCode(), type);
        return jumpResult ;
    }
    /**
     * 用户问题新的判定方法  不用输入小区
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> usersNewQuestion(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        map.put("export_type", "3");
        cacheUserData(map);
        // add by liukang 用户问题返回小区
       /* List<Map<String, Object>> wirelessTop10ci = hbaseService.getUserWirelessTop5(map);
        log.info("====secondLine======usersQuestion+getUserWirelessTop5==="+wirelessTop10ci
                +"wirelessTop10ci.size()"+wirelessTop10ci.size());
        //如果小区多余3，去前三的小区
        if (!wirelessTop10ci.isEmpty() && wirelessTop10ci.size() > 5)
            // 如果想变为TOP3则可以放开这段代码 wirelessTop10ci = wirelessTop10ci.subList(0, 3);
            wirelessTop10ci = wirelessTop10ci.subList(0, 5);*/
        //用户问题

        List<Map<String, Object>> jumpResult = JumpResultUtil.getJumpResultFromXml(JumpResultEnum.USER_NEW_CODE.getCode(), type);
      //  String jump = "";//jumpResult.get(0).get("advice").toString();
        //jump = jump + getSignallingArea(wirelessTop10ci);
       // jumpResult.get(0).put("advice", jump);
        //return JumpResultUtil.getJumpResult(mapper, JumpResultEnum.USER_USERS_CODE.getCode(), type);
        return jumpResult ;
    }
    /**
     * 用户面面判断方法
     *
     * @param map
     * @param map 忽略掉的步骤
     * @return
     */
    private List<Map<String, Object>> userPlaneJump(Map<String, Object> map) {
        Integer type = Integer.valueOf(map.get("type").toString());
        List<Map<String, Object>> result = new ArrayList<>();
//        用户面
        //新的逻辑，加入门限值判断
        //modfiy by liukang mapper.getUserCasePhoenix(map)
        Map<String, Object> userCase = hbaseService.getUserCasePhoenix(map);
        //原有的逻辑  modfiy by liukang mapper.getUserCasePhoenixPieChart(map);
       /* Map<String, Object> oldUserCase = hbaseService.getUserCasePhoenixPieChart(map);*//*
        if (userCase == null || null == oldUserCase||userCase.isEmpty()||oldUserCase.isEmpty()) {
            return result;
        }*/
        if (userCase == null || userCase.isEmpty()) {
            return result;
        }
        //切片数
        Float core = Float.valueOf(userCase.get("CORE") == null ? "0" : userCase.get("CORE").toString());
        Float dns = Float.valueOf(userCase.get("DNS") == null ? "0" : userCase.get("DNS").toString());
        Float terminal = Float.valueOf(userCase.get("TERMINAL") == null ? "0" : userCase.get("TERMINAL").toString());
        Float wireless = Float.valueOf(userCase.get("WIRELESS") == null ? "0" : userCase.get("WIRELESS").toString());
        Float sp = Float.valueOf(userCase.get("SP") == null ? "0" : userCase.get("SP").toString());
        Float users = Float.valueOf(userCase.get("USERS") == null ? "0" : userCase.get("USERS").toString());
        //详单数
        Float oldCore = Float.valueOf(userCase.get("OLDCORE") == null ? "0" : userCase.get("OLDCORE").toString());
        Float oldDns = Float.valueOf(userCase.get("OLDDNS") == null ? "0" : userCase.get("OLDDNS").toString());
        Float oldTerminal = Float.valueOf(userCase.get("OLDTERMINAL") == null ? "0" : userCase.get("OLDTERMINAL")
                .toString());
        Float oldWireless = Float.valueOf(userCase.get("OLDWIRELESS") == null ? "0" : userCase.get("OLDWIRELESS")
                .toString());
        Float oldSp = Float.valueOf(userCase.get("OLDSP") == null ? "0" : userCase.get("OLDSP").toString());
        Float oldUsers = Float.valueOf(userCase.get("OLDUSERS") == null ? "0" : userCase.get("OLDUSERS").toString());
        /*Float oldCore = Float.valueOf(oldUserCase.get("CORE") == null ? "0" : oldUserCase.get("CORE").toString());
        Float oldDns = Float.valueOf(oldUserCase.get("DNS") == null ? "0" : oldUserCase.get("DNS").toString());
        Float oldTerminal = Float.valueOf(oldUserCase.get("TERMINAL") == null ? "0" : oldUserCase.get("TERMINAL")
                .toString());
        Float oldWireless = Float.valueOf(oldUserCase.get("WIRELESS") == null ? "0" : oldUserCase.get("WIRELESS")
                .toString());
        Float oldSp = Float.valueOf(oldUserCase.get("SP") == null ? "0" : oldUserCase.get("SP").toString());
        Float oldUsers = Float.valueOf(oldUserCase.get("USERS") == null ? "0" : oldUserCase.get("USERS").toString());*/
//        http video  0，9，1，2，3，4，分代表用户问题，终端问题，无线问题，dns服务器问题，sp问题，核心网问题
//        dns 可取值为dns服务器问题 0，用户侧问题  1
//        总数量
        //Float total = core + dns + terminal + wireless + sp;
        Float total = core + dns + terminal + wireless + sp + users;
        System.out.println("core "+core+" dns "+dns+" terminal "+terminal+" wireless "+wireless+" sp "+sp+" users "+users+" total "+total);
        Float oldTotal = oldCore + oldDns + oldTerminal + oldWireless + oldSp + oldUsers;
        System.out.println("oldcore "+oldCore+" oldDns "+oldDns+" oldTerminal "+oldTerminal+" oldWireless "+oldWireless+" oldSp "+oldSp+" oldUsers "+oldUsers+" oldTotal "+oldTotal);
        /*log.info("@@@@@@@@@@@@@@@@@@@@@@@@total=======" + total);*/
//      设置dns类型为1 用户侧
        map.put("export_dns", "1");
//       核心网问题
        if (core > 0) {
            return coreQuestion(map);
        }
//      dns问题
        if (dns > 0) {
            return dnsQuestion(map);
        }
        boolean flag = false;
        if (org.apache.commons.lang.StringUtils.isNotBlank(map.get("complaint").toString())) {
            flag = true;
        }
        List<Map<String, Object>> ciList = new ArrayList<>();
        if (flag) {
          //  ciList = getCiList1000(map);
        }
        //某一类详单问题>55%直接输出该问题
        if (oldTerminal / oldTotal > ConstantsUtil.first_detail_terminal) {
            return terminalQuestion(map);
        }
        if (oldWireless / oldTotal > ConstantsUtil.first_detail_wireless) {
        	System.out.println("进入了wirelessQuestion()");
            return wirelessQuestion(map, flag, ciList);
        }
//        log.info("$$$$$$$ConstantsUtil.first_detail_sp$$$$$: "+ConstantsUtil.first_detail_sp
//                +"oldSp / oldTotal > ConstantsUtil.first_detail_sp"+(oldSp / oldTotal > ConstantsUtil.first_detail_sp));
        if (oldUsers / oldTotal > ConstantsUtil.first_detail_users) {
            return usersQuestion(map);
        }
        if (oldSp / oldTotal > ConstantsUtil.first_detail_sp) {
            return spQuestion(map);
        }

        //      优先级排序 输出  终端问题
        if ((terminal > 0 && terminal / total > ConstantsUtil.section_terminal) || (oldTerminal > 0 && oldTerminal / oldTotal > ConstantsUtil.detail_terminal)) {
            return terminalQuestion(map);
        }
        if (((users+wireless)/total > ConstantsUtil.radio_and_user  && wireless > 0 && wireless / total > ConstantsUtil.section_wireless) || (oldWireless > 0 && oldWireless / oldTotal > ConstantsUtil.detail_wireless)) {
            return wirelessQuestion(map, flag, ciList);
        }
        //用户问题
        if (((users+wireless)/total > ConstantsUtil.radio_and_user && users > 0 && users / total > ConstantsUtil.section_users) || (oldUsers > 0 && oldUsers / oldTotal > ConstantsUtil.detail_users)) {
            return usersQuestion(map);
        }
        if ((sp > 0 && sp / total > ConstantsUtil.section_sp) || (oldSp > 0 && oldSp / oldTotal > ConstantsUtil.detail_sp)) {
            return spQuestion(map);
        }

        return result;
    }

    /**
     * 判断list对象中是否包含元素 1:包含，0：不包含
     *
     * @return
     */
    public String isListContainsItem_2(List<RptCusSerAbnormalAll> list, String ip) {
        String flag = "0";
        for (RptCusSerAbnormalAll temp : list) {
            if (temp.getValue().equals(ip)) {
                return "1";
            }
        }
        return "0";
    }

    /**
     * 根据核心网数据判断结果是核心网问题，还是Nat问题，还是Nat以上问题
     *
     * @return
     */
    private Map<String, Object> getResultByCoreDataAndNat(List<HttpDetail> coreData, Map<String, Object> map) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Map<String, Map<String, Object>> natMap = getNatOrUpNatQuestion(map);
        //所有的nat名称
        String all_nat_name = "";
        //返回结果2：nat以上问题，1：nat问题
        String result_flag = "0";
        for (HttpDetail temp : coreData) {
            String start_time = temp.getStart_time().replaceAll("/", "").replaceAll("-", "").replaceAll(" ", "")
                    .replaceAll(":", "").substring(0, 10);
            String pgw_ip_addr = temp.getPgw_ip_addr();
            if (!natMap.isEmpty()) {
                Iterator<Map.Entry<String, Map<String, Object>>> entries = natMap.entrySet().iterator();
                {
                    Map.Entry<String, Map<String, Object>> entry = entries.next();
                    String sdate = entry.getKey().substring(0, 10);
                    Map<String, Object> resultMap = entry.getValue();
                    String flag = (String) resultMap.get("flag");
                    List<RptCusSerAbnormalAll> oneHourIpList = (List<RptCusSerAbnormalAll>) resultMap.get
                            ("oneHourIpList");
                    //时间相同并且IP在异常IP范围内
                    if (start_time.equals(sdate) && "1".equals(isListContainsItem_2(oneHourIpList, pgw_ip_addr))) {
                        //定界结果为Nat以上问题，直接返回结果
                        if ("2".equals(flag)) {
                            returnMap.put("flag", flag);
                            returnMap.put("nat_name", "");
                            return returnMap;
                        }
                        //定界结果为Nat问题，获得所有的nat_name
                        if ("1".equals(flag)) {
                            all_nat_name = all_nat_name + resultMap.get("nat_name");
                            returnMap.put("nat_name", all_nat_name);
                            result_flag = "1";
                        }
                    }
                }
            }
        }
        //nat问题
        if ("1".equals(result_flag)) {
            returnMap.put("flag", "1");
            returnMap.put("nat_name", all_nat_name);
        }
        //非nat问题和nat以上问题
        else {
            returnMap.put("flag", "0");
            returnMap.put("nat_name", "");
        }
        return returnMap;
    }


    /**
     * 判断是否是nat问题或者nat以上问题，按照时间存放结果值，根据cfg_nat_pgw和rpt_cus_ser_abnormal_all的数据来判断
     * rpt_cus_ser_abnormal_all 按照小时来存储异常IP
     * cfg_nat_pgw存储pgwip和nat的对应关系
     *
     * @param map
     * @return 返回值key存放时间，value存放是否Nat问题和Nat以上问题的诊断结果
     */
    private Map<String, Map<String, Object>> getNatOrUpNatQuestion(Map<String, Object> map) {
        Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
        //abnormal_sort:0，1，2，3，4，9（暂不包含ipran维度，依次为用户，无线，dns，sp，核心网，终端）
        map.put("abnormal_sort", "4");
        //modfiy by liukang  List<RptCusSerAbnormalAll> rptCusSerAbnormalAllData = mapper.getRptCusSerAbnormalAllData(map);
        List<RptCusSerAbnormalAll> rptCusSerAbnormalAllData = new ArrayList<>();
        //首先查询异常表，如果根据时间sdate和abnormal_sort查询的结果为空，直接返回正常结果
        if (rptCusSerAbnormalAllData.isEmpty()) {
            return returnMap;
        }
        //获取每个小时级别的数据
        Map<String, List<RptCusSerAbnormalAll>> questIpMap = getQuestIpMap(rptCusSerAbnormalAllData);
        //查询nat和pgw配置表获得nat和pgw的对应关系
        //modfiy by liukang    List<CfgNatPgw> cfgNatPgwData = mapper.getCfgNatPgwData(map);
        List<CfgNatPgw> cfgNatPgwData = new ArrayList<>();
        if (cfgNatPgwData.isEmpty()) {
            return returnMap;
        }
        //存在一个nat对应多个IP的情况，因此需要合并处理,key值为nat_name,value该nat下所有的pgwip的list对象
        Map<String, List<CfgNatPgw>> cfgNatPgwMap = getNatNameMap(cfgNatPgwData);
        //获得最终结果
        return getNatOrUpNatQuestionResult(questIpMap, cfgNatPgwMap);
    }

    /**
     * 根据数据获得最终结果，是Nat问题，还是Nat以上问题，还是正常问题
     * key值为nat_name+"_"+sdate
     *
     * @param questIpMap
     * @param cfgNatPgwMap
     * @return 返回值存放两个key，一个key值为时间，存储的值为当前小时的Nat诊断结果
     */
    public Map<String, Map<String, Object>> getNatOrUpNatQuestionResult(Map<String, List<RptCusSerAbnormalAll>>
                                                                                questIpMap, Map<String,
            List<CfgNatPgw>> cfgNatPgwMap) {
        Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
        Iterator<Map.Entry<String, List<RptCusSerAbnormalAll>>> entriesIp = questIpMap.entrySet().iterator();
        {
            Map.Entry<String, List<RptCusSerAbnormalAll>> entry = entriesIp.next();
            String sdate = entry.getKey();
            List<RptCusSerAbnormalAll> oneHourIpList = entry.getValue();
            Map<String, Object> tempMap = getOneHourNatOrUpNatQuestionResult(sdate, oneHourIpList, cfgNatPgwMap);
            returnMap.put(sdate, tempMap);
        }
        return returnMap;
    }

    /**
     * 判断list对象中是否包含元素 1:包含，0：不包含
     *
     * @return
     */
    public String isListContainsItem(List<CfgNatPgw> list, String ip) {
        String flag = "0";
        for (CfgNatPgw temp : list) {
            if (temp.getPgw_ip().equals(ip)) {
                return "1";
            }
        }
        return "0";
    }

    /**
     * 获取一个小时粒度的Nat和Nat以上问题诊断结果
     *
     * @param sdate
     * @param oneHourIpList
     * @param cfgNatPgwMap
     * @return返回值存放3个key，一个key值用来存放结果值用 flag表示，0：代表正常，1：代表nat问题，2：代表nat以上问题；
     * 另外一个key值用来存放nat_name,只有在是nat问题时，存放所有的nat名称，多个以,分割
     * 最后一个key值是时间
     */
    public Map<String, Object> getOneHourNatOrUpNatQuestionResult(String sdate, List<RptCusSerAbnormalAll>
            oneHourIpList, Map<String, List<CfgNatPgw>> cfgNatPgwMap) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        //每个nat是否是nat问题的数量
        Integer isNatOrNot = 0;
        String havaQuestNatName = "";
        Iterator<Map.Entry<String, List<CfgNatPgw>>> entries = cfgNatPgwMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<CfgNatPgw>> entry = entries.next();
            String key = entry.getKey();
            List<CfgNatPgw> listTemp = entry.getValue();
            //每个nat有问题的数量
            Integer haveQuestNum = 0;
            if (listTemp.isEmpty()) {
                continue;
            } else {
                for (RptCusSerAbnormalAll rptCusSerAbnormalAll : oneHourIpList) {
                    if ("1".equals(isListContainsItem(listTemp, rptCusSerAbnormalAll.getValue())))
                        haveQuestNum = haveQuestNum + 1;
                }
            }
            //有问题的数据/总数>0.5
            double fz = Double.parseDouble(haveQuestNum.toString());
            double fm = listTemp.size();
            double judgeData = 0.0;
            if (fm > 0)
                judgeData = fz / fm;
            if (judgeData > 0.5)
            //是否net问题加1
            {
                isNatOrNot = isNatOrNot + 1;
                havaQuestNatName = havaQuestNatName + key + ",";
            }
        }
        //最终定界结果
        /*log.info("@@@@@@@@@@@@@@@@@@@@@@@@isNatOrNot===" + isNatOrNot + "     oneHourIpList.size()===" +
                oneHourIpList.size());*/
        if (isNatOrNot > 0) {
            //判断是否是Nat上问题,如果有问题的nat的数量/所有Nat总数>0.5,就是Nat以上问题
            double fz = Double.parseDouble(isNatOrNot.toString());
            double fm = oneHourIpList.size();
            double judgeData = 0.0;
            if (fm > 0)
                judgeData = fz / fm;
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@@judgeData===" + judgeData);
            if (judgeData > 0.5) {
                returnMap.put("flag", "2");
                returnMap.put("nat_name", "");
                returnMap.put("sdate", sdate);
                //本小时的异常IP结果集
                returnMap.put("oneHourIpList", oneHourIpList);
                return returnMap;
            }
            //否则是Nat问题
            else {
                returnMap.put("flag", "1");
                returnMap.put("nat_name", havaQuestNatName);
                returnMap.put("sdate", sdate);
                //本小时的异常IP结果集
                returnMap.put("oneHourIpList", oneHourIpList);
                return returnMap;
            }
        } else {
            returnMap.put("flag", "0");
            returnMap.put("nat_name", "");
            returnMap.put("sdate", sdate);
            return returnMap;
        }
    }

    /**
     * 合并rpt_cus_ser_abnormal_all,因为是小时级数据，存放到按照小时为key值的map对象中
     *
     * @param rptCusSerAbnormalAllData
     * @return
     */
    public Map<String, List<RptCusSerAbnormalAll>> getQuestIpMap(List<RptCusSerAbnormalAll> rptCusSerAbnormalAllData) {
        Map<String, List<RptCusSerAbnormalAll>> listMap = new HashMap<String, List<RptCusSerAbnormalAll>>();
        for (RptCusSerAbnormalAll temp : rptCusSerAbnormalAllData) {
            String sdate = temp.getSdate();
            //没有存放当前小时有问题的IP数据
            if (null == listMap.get(sdate)) {
                List<RptCusSerAbnormalAll> tempList = new ArrayList<RptCusSerAbnormalAll>();
                tempList.add(temp);
                listMap.put(sdate, tempList);
            }
            //有存放当前小时有问题的IP数据
            else {
                List<RptCusSerAbnormalAll> tempList = listMap.get(sdate);
                tempList.add(temp);
                listMap.put(sdate, tempList);
            }
        }
        return listMap;
    }

    /**
     * 合并nat_name map,因为有存在一个nat_name对应多个pgwip的情况
     *
     * @param cfgNatPgwData
     * @return
     */
    public Map<String, List<CfgNatPgw>> getNatNameMap(List<CfgNatPgw> cfgNatPgwData) {
        Map<String, List<CfgNatPgw>> listMap = new HashMap<String, List<CfgNatPgw>>();
        for (CfgNatPgw temp : cfgNatPgwData) {
            String nat_name = temp.getNat_name();
            //没有存放Nat名称的数据
            if (null == listMap.get(nat_name)) {
                List<CfgNatPgw> tempList = new ArrayList<CfgNatPgw>();
                tempList.add(temp);
                listMap.put(nat_name, tempList);
            }
            //有存放Nat名称的数据
            else {
                List<CfgNatPgw> tempList = listMap.get(nat_name);
                tempList.add(temp);
                listMap.put(nat_name, tempList);
            }
        }
        return listMap;
    }


    /**
     * 公用方法，实现http和dns数据查询，无论分表和未分表都支持
     *
     * @param map
     * @return
     */
    private Map<String, Object> cacheUserData(Map<String, Object> map) {
        String export_type = map.get("export_type").toString();
        List<HttpDetail> userHttpData = hbaseService.getUserHttpData(map);
        List<DnsDetail> userDnsData = hbaseService.getUserDnsData(map);
        // modfiy by liukang   List<VideoDetail> userVideoData = mapper.getUserVideoData(map);
        List<VideoDetail> userVideoData = new ArrayList<>();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        //        缓存数据
        /*String uuidExcel = (String) map.get("uuidExcel");
        String http_key = uuidExcel + "-" + CacheKeyEnum.HTTP.getCode();
        String dns_key = uuidExcel + "-" + CacheKeyEnum.DNS.getCode();
        String video_key = uuidExcel + "-" + CacheKeyEnum.VIDEO.getCode();
        //dns问题只导出dns数据
        if ("2".equals(export_type)) {
            CacheUtil.cacheSet(dns_key, userDnsData, CacheKeyEnum.CACAHEKEY.getCode());
        } else {
            CacheUtil.cacheSet(http_key, userHttpData, CacheKeyEnum.CACAHEKEY.getCode());
            CacheUtil.cacheSet(video_key, userVideoData, CacheKeyEnum.CACAHEKEY.getCode());
        }*/
        returnMap.put("userHttpData", userHttpData);
        returnMap.put("userDnsData", userDnsData);
        returnMap.put("userVideoData", userVideoData);
        return returnMap;
    }

}
