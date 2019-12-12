package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums.BusinessTypeEnum;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.DateUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.HBaseUtil;

public class DateFromHbase {
	 private static List<List<String>> list = new ArrayList();
	 private static final String tablePreix = "npodo:";
	 //private static final String tablePreix = "";
	 private static final String singaTableName = tablePreix+"rpt_cus_ser_problem_delimit_s1mme";
	 private static List<String> httpSpecKeyList;
	 private static List<String> S1mmSpecKeyList;
	 private static List<String> ltehourKeyList;
	 static {
		 String[] lte_wxdw_xdrmr_hour  = {
				 "cell_id",
				 "source_flag",
				 "procedure_start_time",
				 "procedure_end_time",
				 "procedure_end_ms_time",
				 "procedure_start_ms_time",
				 "local_province",
				 "local_city",
				 "owner_province",
				 "location_longitude",
				 "location_latitude",
				 "ulsinr",
				 "duration",
				 "owner_city",
				 "roaming_type",
				 "imsi",
				 "imei",
				 "phr",
				 "msisdn",
				 "rsrp",
				 "rsrq",
				 "ltescsinrul",
				 "flow_firfail_time",
				 "p_day",
				 "hs",
				 "filvetime",
				 "enb_received_ind",
				 "poor_coverage_ind",
				 "dl_interfe_ind",
				 "ul_poor_sinr_ind",
				 "overlap_ind",
				 "cross_ind",
				 "uepoorphr_ind",
				 "uephr",
				 "prb_highusage_ind",
				 "cellrbpayload_id",
				 "prach_highusage_ind",
				 "cellprachpayload_id",
				 "pdcch_highusage_ind",
				 "alarm_ind",
				 "nalarm_ind",
				 "mod3_interfe_ind",
				 "neigh_problem_ind",
				 "ul_interfe_ind",
				 "cellpdcchpayload_id",
				 "high_conusers_ind",
				 "avg_cell_eta",
				 "ready1",
				 "ready2",
				 "ready3",
				 "ready4",
				 "ready5",
				 "ready6",
				 "ready7"
	          };
	 String[] rpt_cus_ser_http_spec_detail  = {
			 "imsi",
			 "msisdn",
			 "imei",
			 "ecgi",
			 "rat",
			 "protocol_id",
			 "service_type",
			 "start_time",
			 "end_time",
			 "duration",
			 "inputoctets",
			 "output_octets",
			 "recordclosecause",
			 "tcp_ooo_ul_packets",
			 "tcp_ooo_dl_packets",
			 "tcp_retrans_ul_packets",
			 "tcp_retrans_dl_packets",
			 "tcpsetupresponsedelay",
			 "tcpsetupackdelay",
			 "delay_setup_firsttransaction",
			 "delay_firsttransaction_firstrespackt",
			 "tcp_syn_number",
			 "tcp_connetstate",
			 "sessionstopflag",
			 "http_action",
			 "httpstatus",
			 "resp_delay",
			 "tcp_syn_time",
			 "tcp_synack_time",
			 "tcp_ack_time",
			 "actiontime",
			 "firstpackettime",
			 "pageopentime",
			 "lastpacktime",
			 "pagevolume",
			 "last_ack_time",
			 "title",
			 "firstfinacktime",
			 "dnsquerytime",
			 "dnsresponsetime",
			 "dnsflowid",
			 "firstscreenfintime",
			 "app_sub_type",
			 "window_size",
			 "abnormal_sort",
			 "page_failure_delimiting",
			 "first_page_delay_delimiting",
			 "dns_delay_delimiting",
			 "tcp_delay_upper_interf_delimiting",
			 "tcp_delay_lower_interf_delimiting",
			 "page_throughput_delimiting",
			 "city",
			 "cell_name",
			 "app_name",
			 "app_sub_name",
			 "rsrp",
			 "rsrq",
			 "ulsinr",
			 "wireless_postion_result"
          };
	 String[] rpt_cus_ser_signaling_detail  = {
			 "interface",
			 "imsi",
			 "imei",
			 "msisdn",
			 "home_province",
			 "procedure_type",
			 "start_time",
			 "end_time",
			 "status",
			 "request_cause",
			 "req_cause_group",
			 "failure_cause",
			 "fail_cause_group",
			 "keyword",
			 "mme_ue_s1ap_id",
			 "target_enb_id",
			 "user_ipv4",
			 "user_ipv6",
			 "mme_ip",
			 "enb_ip",
			 "tai",
			 "ecgi",
			 "apn",
			 "enb_ue_s1ap_id",
			 "eps_bearer_number",
			 "bearer_id_1",
			 "bearer_type_1",
			 "bearer_qci_1",
			 "bearer_status_1",
			 "bearer_id_2",
			 "bearer_type_2",
			 "bearer_qci_2",
			 "bearer_status_2",
			 "bearer_id_3",
			 "bearer_type_3",
			 "bearer_qci_3",
			 "bearer_status_3",
			 "problem_delimit",
			 "city",
			 "cell_name",
			 "proc_type_name",
			 "rsrp",
			 "rsrq",
			 "ulsinr",
			 "wireless_postion_result"

       };
	 httpSpecKeyList=Arrays.asList(rpt_cus_ser_http_spec_detail);
	 S1mmSpecKeyList=Arrays.asList(rpt_cus_ser_signaling_detail);
	 ltehourKeyList=Arrays.asList(lte_wxdw_xdrmr_hour);

	 list.add(ltehourKeyList);
	 }
	    /**
	     * @param map
	     * @return
	     */
	    public List<Map<String, Object>> getDataFromHbaseDetail(String sdate,String phone,String table,int index) {
	        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
	        Map<String, Object> map = new HashMap<>();
			try {
				map = requestParamProcessSecondline(sdate,sdate,phone,"0","","");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put("type", BusinessTypeEnum.DOLOCAL.getCode());
	        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
	        System.out.println("开始从"+table+"查询数据 ...");
	        for (String rowkey : rowkeyList) {
	            String[] rowkeys=rowkey.split("#");
	            String[] rowkeys_start=rowkeys[0].split("_");
	            String startRowkey = rowkeys_start[0]+"_"+rowkeys_start[1];
	            String[] rowkeys_end=rowkeys[1].split("_");
	            String endRowkey = rowkeys_end[0]+"_"+rowkeys_end[1];
	            //分表
	            String tableName = table + "_" +rowkeys[2];
	            List<Map<String, Object>> hbaseData = getHbaseData(tablePreix+tableName, startRowkey, endRowkey, list.get(index));
	            allHbaseData.addAll(hbaseData);
	        }
	        System.out.println("从"+table+"查询出的数据 "+allHbaseData);
	        return allHbaseData;
	    }
	/**
     * @param map
     * @return
     */
    public List<Map<String, Object>> getHttpSpecDetail(String sdate,String phone,String table) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
		try {
			map = requestParamProcessSecondline(sdate,sdate,phone,"0","","");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("type", BusinessTypeEnum.DOLOCAL.getCode());
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String[] rowkeys=rowkey.split("#");
            String[] rowkeys_start=rowkeys[0].split("_");
            String startRowkey = rowkeys_start[0]+"_"+rowkeys_start[1];
            String[] rowkeys_end=rowkeys[1].split("_");
            String endRowkey = rowkeys_end[0]+"_"+rowkeys_end[1];
            //分表
            String tableName = table + "_" +rowkeys[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tablePreix+tableName, startRowkey, endRowkey, httpSpecKeyList);
            allHbaseData.addAll(hbaseData);
        }
        System.out.println("从"+table+"查询出的数据 "+allHbaseData);
        return allHbaseData;
    }
    /**
     * @param map
     * @return
     */
    public List<Map<String, Object>> getS1mmSpecDetail(String sdate,String phone,String table) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
		try {
			map = requestParamProcessSecondline(sdate,sdate,phone,"0","","");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("type", BusinessTypeEnum.DOLOCAL.getCode());
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String[] rowkeys=rowkey.split("#");
            String[] rowkeys_start=rowkeys[0].split("_");
            String startRowkey = rowkeys_start[0]+"_"+rowkeys_start[1];
            String[] rowkeys_end=rowkeys[1].split("_");
            String endRowkey = rowkeys_end[0]+"_"+rowkeys_end[1];
            //分表
            String tableName = table + "_" +rowkeys[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tablePreix+tableName, startRowkey, endRowkey, S1mmSpecKeyList);
            allHbaseData.addAll(hbaseData);
        }
        System.out.println("从"+table+"查询出的数据 "+allHbaseData);
        return allHbaseData;
    }
	
	 /**
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @param keyList     传入null时候 ，随机生成key
     * @return
     */
    private List<Map<String, Object>> getHbaseData(String tableName, String startRowKey, String endRowKey,List<String> keyList) {
        List<Map<String, Object>> rList = new ArrayList<>();
        ResultScanner scanner = HBaseUtil.getScanner(tableName, startRowKey, endRowKey);
      //  log.info("==================================>1");
         try {
            for (Result result : scanner) {
             //   log.info("==================================>1");
                List valueList = new ArrayList();
                for (Cell cell : result.rawCells()) {
                   // log.info("value值得：" + Bytes.toString(cell.getValue()));
                    String[] split = Bytes.toString(cell.getValue()).split(",");
                    System.out.println("split "+split.toString());
                    valueList = Arrays.asList(split);
                    System.out.println("valueList "+valueList);
                }
                Map<String, Object> data = new HashMap<>();
                if (null != keyList && keyList.size() > 0) {
                    if (valueList.size() > 0) {
                       /* for (int i = 0; i < valueList.size(); i++) {
                            for (int j = 0; j < keyList.size(); j++) {
                                if (i == j) {
                                    data.put(keyList.get(j), valueList.get(i));
                                    continue;
                                }
                            }
                           
                        }*/
                    	for (int i = 0; i < keyList.size(); i++) {
                    		for (int j = 0; j < valueList.size(); j++) {
                                if (i == j) {
                                    data.put(keyList.get(i), valueList.get(j));
                                    continue;
                                }
                            }
                             if(i>valueList.size()-1) {
                            	 data.put(keyList.get(i),"");
                    		}
                           
                        }
                    }
                } else {
                    if (valueList.size() > 0) {
                        for (int i = 0; i < valueList.size(); i++) {
                            for (int j = 0; j < keyList.size(); j++) {
                                if (i == j) {
                                    data.put(j + "", valueList.get(i));
                                    continue;
                                }
                            }
                        }
                    }
                }
                rList.add(data);
            }

            System.out.println(
                     "555555*****************5555555555+rList "+rList.size()
                             +"数据查询表：table=" + tableName
                             + "  startRowKey=" + startRowKey
                             + "  endRowKey=" + endRowKey);
            return rList;
        } catch ( Exception e ) {
        	 System.out.println("没有表头的数据查询异常：table=" + tableName + "  startRowKey=" + startRowKey + "  endRowKey=" + endRowKey
                     +"555555*****************5555555555+rList"+rList);
        } finally {
            if (null != scanner)
                scanner.close();
        }
        return rList;
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
