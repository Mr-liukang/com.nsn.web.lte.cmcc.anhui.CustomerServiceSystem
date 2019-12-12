package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.nsn.gis.geojson.Feature;
import com.nsn.gis.geojson.FeatureCollection;
import com.nsn.gis.geojson.Geom;
import com.nsn.gis.geojson.JtsUtils;
import com.nsn.gis.geojson.ProjUtils;
import com.nsn.web.lte.action.PageAction;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums.BusinessTypeEnum;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.CustomerServiceSupportServiceI;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.impl.CustomerServiceSupportSecondLineExportImpl;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.DateUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.ResultUtil;
import com.nsn.web.lte.db.DSType;
import com.nsn.web.lte.db.Db;
import com.nsn.web.lte.db.DbDo;
import com.nsn.web.lte.db.Record;
import com.nsn.web.lte.db.SqlMap;
import com.nsn.web.lte.mvc.ReqContext;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class CSSAction {
	private CustomerServiceSupportServiceI cssSecondLineWebservice = new CustomerServiceSupportSecondLineExportImpl();
	public String index(ReqContext rc) {
		return "intelligent_complaint.html";
	}
	
	public void exportData(ReqContext rc) {
		String userSensitive = rc.param("userSensitive");
		PageAction page = new PageAction();
		List<Record> list = page.getData(rc);
		if(!userSensitive.equals("0")){
			for(Record record : list){
				String imsi = record.getStr("IMSI", "");
				String msisdn = record.getStr("MSISDN", "");
				imsi = setxxxxnumber(imsi);
				msisdn = setxxxxnumber(msisdn);
				record.set("IMSI", imsi);
				record.set("MSISDN", msisdn);
			}
		}
		page.exportList(rc, list);
	}
	
	public String setxxxxnumber(String obj){
		System.out.println("运行到这里了");
		if(obj.length()==13){
			return obj.substring(0,5)+"****"+obj.substring(9,13);
		}else if(obj.length()==15){
			return obj.substring(0,5)+"****"+obj.substring(9,15);
		}else if(obj.length()>=4){
			return obj.substring(0,obj.length()-4)+"****";
		}else if(obj.length()==4){
			return "****";
		}else{
			return obj;
		}
	}
	
	public List<Record> setTest(ReqContext rc){
		String phone = rc.param("phone");
		String r1 = rc.param("r1");
		String date_d = rc.param("date_d");
		String date_h = rc.param("date_h");
		//System.out.println("phone "+phone+" r1 "+r1+" date_d "+date_d+" date_h "+date_h );
		
		List<Record> list =Db.use(DSType.MAIN).query(SqlMap.getSql("testFromMysql"));
		//System.out.println("list: "+list);
		return list;
	}
	/**
	 * 定界方法入口
	 * @param rc
	 * @return
	 * @throws ParseException
	 */
	public Object secondLine(ReqContext rc) throws ParseException{
		String phone = rc.param("phone");
		String r1 = rc.param("r1");
		String date_d = rc.param("date_d");
		String date_h = rc.param("date_h");
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
	 *  问题定界占比查询
	 * @param rc
	 * @return
	 * @throws ParseException
	 */
	public Object getResultPieChart(ReqContext rc) throws ParseException{
		String phone = rc.param("phone");
		String r1 = rc.param("r1");
		String date_d = rc.param("date_d");
		String date_h = rc.param("date_h");
		String  sdate ="";
		if("day".equals(r1)) {
			sdate = date_d;
		}else {
			sdate = date_h.substring(0, 10);
		}
		System.out.println("phone "+phone+" r1 "+r1+" date_d "+date_d+" date_h "+date_h+" sdate "+sdate );
		List<Map<String, Object>> result = null;
		Map<String, Object> map = requestParamProcessSecondline(sdate,sdate,phone,"0","","");
		map.put("type", BusinessTypeEnum.DOLOCAL.getCode());
		System.out.println("map "+map);
        result = cssSecondLineWebservice.getResultPieChart(map);
	    System.out.println("secondLine-->result"+result);
		//List<Record> list =Db.use(DSType.MAIN).query(SqlMap.getSql("testFromMysql"));
		//System.out.println("ResultUtil.getSuccessResult(result): "+ResultUtil.getSuccessResult(result).toString());
		return result;
	}
	/******************GIS begin*************************/
    public Map<String, Object> loadGisCell(ReqContext rc){
    	Map<String, Object> map = new HashMap<String, Object>(2);
		/*Map<String,Object> date = DbDo.findFirstCache("5min", SqlMap.getSql("getMaxMinDateGis"));
		String sdate = "";
		if(date.get("sdate")!=null){
			sdate = date.get("sdate").toString();
		}
		Map<String, Object> param = param(rc);
		param.put("sdate", sdate);*/
		String sql = SqlMap.getSql("loadGisCell");	
		List<Record> kpi_list =Db.use(DSType.MAIN).query(SqlMap.getSql("loadGisCell"));
//		List<Record> kpi_list = DbDo.query(sql);
		int max = 0;
		String cellname=null;
		
		List<Feature> listFtr = new ArrayList<Feature>();
		if (null != kpi_list) {
			for (int i = 0, len = kpi_list.size(); i < len; i++) {
				Record record = kpi_list.get(i);
//				Geometry ply = JtsUtils.wkt2Geometry(record.get("cell_wkt").toString());
				Geometry ply = JtsUtils.wkt2Geometry(record.get("cell_wkt").toString());
			    //Polygon polygon = JtsUtils.cellSector(pt.getX(), pt.getY(), 100, record.getInt("azimuth"), 60, 20);
				Feature ftr = new Feature(ply);
				Map<String, Object> prts = new LinkedHashMap<String, Object>();
				/*for(String key : record.getColumnNames()){
					prts.put(key, record.get(key));
				}*/
				//prts.remove("cell_wkt");
				ftr.setProperties(prts);
				listFtr.add(ftr);
			}
			FeatureCollection ftrc = new FeatureCollection(listFtr);
			//map.put("mm", maxMinMap);
			map.put("ftrc", ftrc);
			//map.put("cellname", cellname);
		}
		return map;
    }
    //渲染气泡
    public List<Map<String, Object>> loadGisPop(ReqContext rc){
    	Map map = new HashMap<String, Object>();
    	map.put("jd", "31.859079");
    	map.put("wd", "119.818699");
    	map.put("cs", "8");
    	Map map2 = new HashMap<String, Object>();
    	map2.put("jd", "31.859065");
    	map2.put("wd", "119.825699");
    	map2.put("cs", "3");
    	Map map1 = new HashMap<String, Object>();
    	map1.put("jd", "31.859662");
    	map1.put("wd", "119.825096");
    	map1.put("cs", "10");
    	List list = new ArrayList<Map<String, Object>>();
    	list.add(map);
    	list.add(map2);
    	list.add(map1);
    	System.out.println("排序前"+list);
    	Collections.sort(list, new Comparator<Map<String, Object>>(){  
    		  
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {  
            	Integer cs1 = Integer.parseInt( (String) o1.get("cs")); 
            	Integer cs2= Integer.parseInt( (String) o2.get("cs"));      
             return cs2-cs1;    
        }  
              
           }); 
    	System.out.println("list "+list);
    	return list;
    }
    public Map<String, Object> loadBadLine(ReqContext rc){
    	List l1  = new ArrayList();
    	List l2  = new ArrayList();
    	List l3  = new ArrayList();
    	List l4  = new ArrayList();
    	l1.add("2019-10-20 18:00"); 
    	l1.add("2019-10-20 18:05");
    	l1.add("2019-10-20 18:10");
    	l1.add("2019-10-20 18:15");
    	l1.add("2019-10-20 18:20");
    	l1.add("2019-10-20 18:25");
    	l1.add("2019-10-20 18:30");
    	l1.add("2019-10-20 18:35");
    	l1.add("2019-10-20 18:40"); 
    	l1.add("2019-10-20 18:45"); 
    	l1.add("2019-10-20 18:50");
    	l1.add("2019-10-20 18:55");
    	
    	l2.add("1");
    	l2.add("2");
    	l2.add("3");
    	l2.add("4");
    	l2.add("0");
    	l2.add("0");
    	l2.add("7");
    	l2.add("7");
    	l2.add("9");
    	l2.add("10");
    	l2.add("11");
    	l2.add("12");
    	
    	l3.add("13");
    	l3.add("14");
    	l3.add("15");
    	l3.add("16");
    	l3.add("17");
    	l3.add("18");
    	l3.add("19");
    	l3.add("20");
    	l3.add("21");
    	l3.add("22");
    	l3.add("23");
    	l3.add("24");
    	
    	l4.add("25");
    	l4.add("26");
    	l4.add("27");
    	l4.add("28");
    	l4.add("29");
    	l4.add("30");
    	l4.add("31");
    	l4.add("32");
    	l4.add("33");
    	l4.add("34");
    	l4.add("35");
    	l4.add("36");
    	
    	Map map = new HashMap<>();
    	
    	map.put("m1", l1);
    	map.put("m2", l2);
    	map.put("m3", l3);
    	map.put("m4", l4);
    			
    	
    	return map;
    }
    /**
     * 当时间选择为天为颗粒度的时候横坐标
     */
    public List getXArrForDay(String sdate){
    	List list = new ArrayList();
    	for(int i = 0; i<24; i++){
			if(i<10){
				list.add(sdate+" 0"+i);
			} else {
				list.add(sdate+" "+i);
			}
		}
    	return list;
    }
    /**
     * 当时间选择为小时颗粒度的时候横坐标
     * 
     */
    public List getXArrForHours(String sdate){
    	List list = new ArrayList();
    	for(int i = 0; i<12; i++){
			if(i*5<10){
				list.add(sdate+":0"+i*5);
			} else {
				list.add(sdate+":"+i*5);
			}
		}
    	return list;
    }
	/**
	 * 公用的参数组装
	 * @param rc
	 * @return
	 */
	private Map<String, Object> param(ReqContext rc) {
		String cityname = rc.param("cityname");
		String scene = rc.param("scene");
		String scene_type = rc.param("scene_type");
		Map<String, Object> p = new HashMap<>();
		p.put("cityname", cityname);
		p.put("scene", scene);
		p.put("scene_type", scene_type);
		return p;
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
