package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;


public class JumpResultUtil {

    /**
     * @param key
     * @return
     * @descript 获取定界结果 一线接口 北京表cfg_order_id_new无数据，去除Phoenix
     * @author liukang
     * @date  20190713
     */
    public static List<Map<String, Object>> getJumpResult( Integer key, Integer type) {
        List<Map<String, Object>> result = new ArrayList<>();
      //  Map<String, Object> jumpResult = mapper.getJumpResult(key + "", type + "");
        Map<String, Object> jumpResult = new HashMap<>();
                //传入变量用来区分是什么定界结果，根据定界结果来做异常时间切片的区分，定界结果JumpResultEnum实际的数据
        jumpResult.put("resultFlag",key);
        result.add(jumpResult);
        return result;
    }
    /**
     * @param key
     * @return
     * @descript 获取定界结果 一线接口
     */
  /*  public static List<Map<String, Object>> getJumpResult(CustomerServiceSupportMapper mapper, Integer key, Integer type) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> jumpResult = mapper.getJumpResult(key + "", type + "");
        //传入变量用来区分是什么定界结果，根据定界结果来做异常时间切片的区分，定界结果JumpResultEnum实际的数据
        jumpResult.put("resultFlag",key);
        result.add(jumpResult);
        return result;
    }*/

    /**
     * @param key
     * @return
     * @descript 获取定界结果 一线接口
     */
    public static List<Map<String, Object>> getJumpResultFromXml( Integer key, Integer type) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> jumpResult = new HashMap<String, Object>();
        //根据key和type查找对应结果对象
//        log.info("---------------------resultList"+ConstantsUtil.resultList);
        /* List<Map<String, Object>> jumpResult_1 =new ArrayList<>();
        CollectionUtils.addAll(jumpResult_1,new Object[ConstantsUtil.resultList.size()]);
        Collections.copy(jumpResult_1,ConstantsUtil.resultList);*/
        for(Map<String, Object> temp:ConstantsUtil.resultList)
        {
            String key_t="";
            if(null!=temp.get(ConstantsUtil.cons_order_id))
            {
                key_t=temp.get(ConstantsUtil.cons_order_id)+"";
            }
            String type_t="";
            if(null!=temp.get(ConstantsUtil.cons_out_code))
            {
                type_t=temp.get(ConstantsUtil.cons_out_code)+"";
            }
            if(key_t.equals(key+"")&&type_t.equals(type+""))
            {
                jumpResult=temp;
            }
        }
        jumpResult.put("resultFlag",key);
        result.add(jumpResult);

        return result;
    }



    /**
     * @return
     * @descript 限速时接口处理结果
     */
    public static List<Map<String, Object>> getXiansuJumpResult() {
        List<Map<String, Object>> result = new ArrayList<>();
        //Map<String, Object> jumpResult = new HashMap<String, Object>();
        //result.add(jumpResult);
        return result;
    }

    /**
     * @param key
     * @return
     * @descript 获取定界结果 二线
     */
  /*  public static Map<String, Object> getJumpResult(CustomerServiceSupportMapper mapper, Integer key, Integer type, List<Map<String, Object>> export) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> jumpResult = mapper.getJumpResult(key + "", type + "");
        result.put("result", jumpResult);
        result.put("data", export);
        return result;
    }*/
}
