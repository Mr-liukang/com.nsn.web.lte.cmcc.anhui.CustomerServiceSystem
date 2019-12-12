package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service;



import java.util.List;
import java.util.Map;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.DnsDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.HttpDetail;

public interface CustomerHbaseServiceI {



//    异常时间段查询
    Map<String,Object> getExceptionTime(Map<String, Object> map);

    //   信令面-步骤一
    List<Map<String, Object>> getSignaOne(Map<String, Object> map);

    //List<Map<String, Object>> getSignaOneForExPorts(Map<String, Object> map);

    List<Map<String, Object>> getSignaFour(Map<String, Object> map);

    List<Map<String, Object>> getSignaFive(Map<String, Object> map);

    List<Map<String, Object>> getSignaSix(Map<String, Object> map);

    List<Map<String, Object>> getSignaSeven(Map<String, Object> map);
    //    频繁事件界定
    List<Map<String, Object>> getFrequently(Map<String, Object> map);

    //    匹配盲点库  信令面7-1
    List<Map<String, String>> getSingbScotoma(Map<String, Object> map);

//    查询无线侧top 10 小区ci
    List<String> getWirelessTop10ci(Map<String, Object> map);

    //   http导出详单分表查询
    List<HttpDetail> getUserHttpData(Map<String, Object> map);

    //   dns导出详单分表查询
    List<DnsDetail> getUserDnsData(Map<String, Object> map);

    /**
     * 无表头分区表获取信令侧详单信息
     * @param map
     * @return
     */
    List<Map<String, Object>> getSignaNew(Map<String, Object> map);


    //   无表头获取小区信息
    List<Map<String, Object>> getWirelessTop10(Map<String, Object> map);
    //  包括时间和ecgi
    List<Map<String, Object>> getWirelessTop3ForTime(Map<String, Object> map);
    //  根据rowkey获取盲点库的数据
    List<Map<String, Object>> getCfgblindspothData(String startRowkey,String endRowkey);
    //   无表头获取盲点库小区信息
    List<Map<String, Object>> getUserScotoma(Map<String, Object> map);

    //   无表头限速数据查询
    List<Map<String, Object>> getSpeedLimitData(Map<String, Object> map);
   //    用户侧
    Map<String, Object> getUserCasePhoenix(Map<String, Object> map);
    //用户面原始数据，现在用于展示各种问题饼图占比
    Map<String,Object>getUserCasePhoenixPieChart (Map<String, Object> map);
    //   用户侧异常时间段
    List<Map<String,Object>> getUserCaseExceptionPhoenix (Map<String, Object> map);
    //   TOP5详单表
    public List<Map<String, Object>> getWirelessTop5MME(Map<String, Object> map);
    // 用户面无线问题小区的TOP5   add by liukang
    public List<Map<String, Object>> getUserWirelessTop5(Map<String, Object> map);
  // add by liukang 在MME层面判断是SP问题还是用户问题
    public  Map<String,Object> judgeMmeSpOrUser (Map<String, Object> map);
    // add by liukang 在MME层面判断是SP问题还是用户问题
    public List<Map<String, Object>> getSpQuestionTop5(Map<String, Object> map);


}
