package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums.BusinessTypeEnum;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.CustomerServiceSupportServiceI;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.impl.CustomerServiceSupportSecondLineExportImpl;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.DateUtil;
import com.nsn.web.lte.mvc.ReqContext;

public class SencodLineImp {
	private CustomerServiceSupportServiceI cssSecondLineWebservice = new CustomerServiceSupportSecondLineExportImpl();
	/**
	 * 定界方法入口
	 * @param rc
	 * @return
	 * @throws ParseException
	 */
	public Object secondLine(String phone,String r1,String date_d,String date_h) throws ParseException{
		/*String phone = rc.param("phone");
		String r1 = rc.param("r1");
		String date_d = rc.param("date_d");
		String date_h = rc.param("date_h");*/
		String sdate = "";
		if("day".equals(r1)) {
			sdate = date_d;
		}else {
			sdate = date_h.substring(0, 10);
		}
		System.out.println("phone "+phone+" r1 "+r1+" date_d "+date_d+" date_h "+date_h );
		List<Map<String, Object>> result = null;
		Map<String, Object> map = requestParamProcessSecondline(sdate,sdate,phone,"0","","");
		map.put("type", BusinessTypeEnum.DOLOCAL.getCode());
        result = cssSecondLineWebservice.getResult(map);
	    System.out.println("secondLine-->result"+result);
		//List<Record> list =Db.use(DSType.MAIN).query(SqlMap.getSql("testFromMysql"));
		
		return result;
	}
	/**
     * 请求参数处理..
     *
     * @param sdate
     * @param edate
     * @param phone
     * @param custom 自定义参数
     * @param <T>
     * @return
     * @throws ParseException
     */
    private Map<String, Object> requestParamProcessSecondline(String sdate, String edate, String phone, String module, String complaint, String uuidExcel) throws ParseException {
        Map<String, Object> map = new HashMap<>();

        sdate = sdate.trim()+" 00:00:00";;
        edate = edate.trim()+" 23:59:59";
       /* sdate = sdate.trim();
        edate = edate.trim();*/
        map.put("sdate", sdate.replaceAll("-","").replaceAll(" ","").replaceAll(":",""));
        map.put("edate", edate.replaceAll("-","").replaceAll(" ","").replaceAll(":",""));
        phone = phone.trim();
        phone= new StringBuilder(phone).reverse().toString();
       
    
        map.put("phone", phone);
        map.put("complaint", complaint);
        map.put("module", module);
        map.put("uuidExcel", uuidExcel);
//            计算时间差值 传入控制变量
        long limit = 70;//阈值 70小时
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long diff = (dateFormat.parse(edate).getTime() - dateFormat.parse(sdate).getTime()) / (1000 * 3600);
        boolean is_exception = diff > limit ? true : false;//时间间隔大于70小时 启用异常事件筛选逻辑
        map.put("is_exception", is_exception);
//            获取时间段内的整点时间
        List<String> dates = DateUtil.findDates(sdate, edate);
        map.put("dates", dates);
        map.put("startRowKey_t", phone + "_" + sdate.replaceAll("-", "").replace(" ", "").replaceAll(":", ""));
        map.put("endRowKey_t", phone + "_" + edate.replaceAll("-", "").replace(" ", "").replaceAll(":", ""));
        sdate = sdate.substring(0, sdate.lastIndexOf(":")).replaceAll("-", "").replace(" ", "").replaceAll(":", "");
        edate = edate.substring(0, edate.lastIndexOf(":")).replaceAll("-", "").replace(" ", "").replaceAll(":", "");
//            分钟类型参数
        map.put("startRowKey", phone + "_" + sdate);
        map.put("endRowKey", phone + "_" + edate);
//            秒级 类型参数
        map.put("startRowKey_sec", phone + "_" + sdate + "00");
        map.put("endRowKey_sec", phone + "_" + edate + "00");
//            用户名个性化参数
 /*       map.put("startRowKey_sec_type_zero", phone + "_" + sdate + "00_0");
        map.put("endRowKey_sec_type_zero", phone + "_" + edate + "00_0");
        map.put("startRowKey_sec_type_one", phone + "_" + sdate + "00_1");
        map.put("endRowKey_sec_type_one", phone + "_" + edate + "00_1");
        map.put("startRowKey_sec_type_two", phone + "_" + sdate + "00_2");
        map.put("endRowKey_sec_type_two", phone + "_" + edate + "00_2");*/
        map.put("startRowKey_sec_type_zero", phone + "_" + sdate + "00");
        map.put("endRowKey_sec_type_zero", phone + "_" + edate + "00");
        map.put("startRowKey_sec_type_one", phone + "_" + sdate + "00");
        map.put("endRowKey_sec_type_one", phone + "_" + edate + "00");
        map.put("startRowKey_sec_type_two", phone + "_" + sdate + "00");
        map.put("endRowKey_sec_type_two", phone + "_" + edate + "00");
        return map;
    }
}
