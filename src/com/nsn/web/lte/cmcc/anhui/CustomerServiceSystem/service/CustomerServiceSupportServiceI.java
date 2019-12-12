package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service;

import java.util.List;
import java.util.Map;

public interface CustomerServiceSupportServiceI {
    //   客服支撑 界面部 -- 定界结果
    List<Map<String, Object>> getResult(Map<String, Object> map);

    //  客服支撑 界面部分 -- 异常时间段
    Map<String, Object> getExceptionTime(Map<String, Object> map);

    //  客服支撑 获取问题占比分析图
    List<Map<String, Object>> getResultPieChart(Map<String, Object> map);

}
