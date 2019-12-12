package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.impl;



import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.DnsDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.HttpDetail;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.RPT_CUS_SER_SIGNALING_SPEC;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.service.CustomerHbaseServiceI;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.DateUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.HBaseUtil;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.MapTransformBean;
import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils.StringUtil;


public class CustomerHbaseServiceImpl implements CustomerHbaseServiceI {


   // @Autowired
   // private CustomerServiceSupportMapper mapper;
	private static final String TABLESPLITFLAG="1";
    private static List<String> signaKeyList;//信令面 汇总表
    private static List<String> singaSpecList;//信令面 详单表
    private static List<String> httpKeyList;//http详单表
    private static List<String> dnsKeyList;//dns详单表
    private static List<String> speedLimitKeyList;//限速表
    private static List<String> cfgblindspothKeyList;//盲点库表
    private static List<String> rptcusserupnor15httpList;
    private static List<String> frequentlyTableNameKeyList;//信令侧小区表字段日期，小区号码，手机号

    //  小区配置表
//    private static List<String> cfgWirelessKeyList = Arrays.asList(new String[]{"ci"});


    //信令面 汇聚表
    //private static final String singaTableName = "rpt_cus_ser_problem_delimit_5min";
    private static final String tablePreix = "npodo:";
   // private static final String tablePreix = "";
    private static final String singaTableName = tablePreix+"rpt_cus_ser_problem_delimit_s1mme";
    //    限速用户表名
//    private static final String limitSpeedTableName = "rpt_only_limit_rate_user_h";
    //    信令面详单表  top3 存在 输出 不存在输出详单表
//    private static final String singaSpecTableName = "rpt_cus_ser_signaling_spec";
    private static final String singaSpecTableName = tablePreix+"rpt_cus_ser_signaling_spec";

    //无理由申诉
//    private static final String noReasonTableName = "rpt_no_reason_user_d";
    //   无线-盲点库配置表
    private static final String cfg_kh_nowireless_ciTableName = tablePreix+"cfg_kh_nowireless_ci";
    //    频繁事件表
    private static final String frequentlyTableName = tablePreix+"rpt_fre_evt_5min";

    //http详单表
    private static final String httpTableName = tablePreix+"rpt_cus_ser_http_spec";
    //dns详单表
    private static final String dnsTableName = tablePreix+"rpt_cus_ser_dns_spec";

    //限速表
    private static final String speedLimitTableName = tablePreix+"rpt_only_limit_rate_user_h";
    //限速表
    private static final String cfgblindspothTableName = tablePreix+"cfg_blindspot_h";
    //----
    private static final String rptcusserupnor15httpTableName = tablePreix+"rpt_cus_ser_up_nor_http";
    //日期比较器
    public static final Comparator<Map> comparator;
    //ecgi数量比较器
    public static final Comparator<Map> comparator_num;


    static {

        comparator = (a, b) -> a.get("sdate").toString().compareTo(b.get("sdate").toString());
        comparator_num = (a, b) -> a.get("num").toString().compareTo(b.get("num").toString());

//        String[] rpt_only_limit_rate_user_h = {};

//
//        String[] cfg_kh_nowireless_ci = {};

//       信令面详单表
        String[] rpt_cus_ser_signaling_spec = {
                "iface",
                "imsi",
                "imei_sv",
                "msisdn",
                "home_province",
                "proc_type",
                "start_time",
                "end_time",
                "status",
                "req_cause",
                "req_cause_group",
                "fail_cause",
                "fail_cause_group",
                "keyword",
                "mme_ue_s1ap_id",
                "target_enb_id",
                "user_ipv4",
                "user_ipv6",
                "mme_ip",
                "enb_ip",
                "tai",
                "cell",
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
                "city"
                 // 1 无线问题-计数desc排序  超过5次无线原因
        };

//        信令面 汇总表   rpt_cus_ser_problem_delimit_5min
        String[] rpt_cus_ser_problem_delimit_5min = {
                "sdate",
                "msisdn",
                "cp_user_cause",
                "cp_corenet_cause",
                "cp_radio_cause",
                "cp_normal_cnt",
                "cp_abnormal_cnt",
                "attach_failure_cnt",
                "attach_corenet_cause",
                "attach_user_cause",
                "detach_cnt",
                "detach_user_cnt",
                "detach_corenet_cnt",
                "bearer_failure_cnt",
                "bearer_corenet_cause",
                "bearer_radio_cause",
                "bearer_user_cause",
                "pdn_failure_cnt",
                "pdn_corenet_cause",
                "pdn_user_cause",
                "service_request_failure_cnt",
                "service_request_corenet_cause",
                "service_request_user_cause",
                "tau_failure_cnt",
                "tau_corenet_cause",
                "tau_user_cause",
                "drop_cnt",
                "drop_corenet_cause",
                "drop_radio_cause",
                "drop_user_cause",
                "paging_failure_cnt",
                "tau_frequently_flag",
                "handover_frequently_flag",
                "detach_frequently_flag",
                "attach_flag",
                "detach_flag",
                "pdn_flag",
                "bearer_flag",
                "service_flag",
                "attach_status",
                "plane_flag",
                "pdn_ms_cause",
                "bearer_ms_cause",
                "abnormal_period_flag"};

        //        http 汇总表
        String[] rpt_cus_ser_http_spec = {
//                "row",
                "inter",
                "imsi",
                "msisdn",
                "imei",
                "dest_ip",
                "dest_port",
                "src_ip",
                "src_port",
                "sgw_ip_addr",
                "mme_ip_addr",
                "pgw_ip_addr",
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
                "mme_ue_s1ap_id",
                "enb_ue_s1ap_id",
                "home_province",
                "l4",
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
                "host",
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
                 "city"};
        //        dns汇总表
        String[] rpt_cus_ser_dns_spec = {
                "inter",
                "imsi",
                "msisdn",
                "imei",
                "dest_ip",
                "dest_port",
                "src_ip",
                "src_port",
                "ecgi",
                "start_time",
                "end_time",
                "duration",
                "mme_ue_s1ap_id",
                "enb_ue_s1ap_id",
                "home_province",
                "sessionstopflag",
                "queryresult",
                "replycode",
                "querytime",
                "responsetime",
                "requestnumber",
                "responsenumber",
                "authnumber",
                "additionalnumber",
                "flowid",
                "querydomainname",
                "dns_delimiting",
                "dns_delay_abnormal"};
        //        限速表
        String[] rpt_only_limit_rate_user_h = {
                "sdate",
                "msisdn",
                "apn_ambr_dl"};
        String[] cfg_blindspot_h = {
                "sdate",
                "eci",
                "enb_id",
                "cell_id",
                "busy_prb",
                "busy_traffic",
                "busy_user",
                "poor_coverage"};
        //add by liukang
        String[] rpt_cus_ser_up_nor_http = {
                "data_type",
                "sdate",
                "msisdn",
                "dns_delimiting_dns_cause",
                "dns_delimiting_user_cause",
                "page_failure_delimiting_user_cause",
                "page_failure_delimiting_sp_cause",
                "first_page_delay_delimiting_corenet_cause",
                "first_page_delay_delimiting_sp_cause",
                "first_page_delay_delimiting_user_cause",
                "first_page_delay_delimiting_radio_cause",
                "first_page_delay_delimiting_terminal_cause",
                "dns_delay_delimiting_dns_cause",
                "dns_delay_delimiting_user_cause",
                "tcp_delay_upper_interf_corenet_cause",
                "tcp_delay_upper_interf_sp_cause",
                "tcp_delay_lower_interf_user_cause",
                "tcp_delay_lower_interf_radio_cause",
                "tcp_delay_lower_interf_terminal_cause",
                "page_throughput_delimiting_corenet_cause",
                "page_throughput_delimiting_sp_cause",
                "page_throughput_delimiting_user_cause",
                "page_throughput_delimiting_radio_cause",
                "page_throughput_delimiting_terminal_cause",
                "video_tcp_delay_upper_interf_corenet_cause",
                "video_tcp_delay_upper_interf_sp_cause",
                "video_tcp_delay_lower_interf_user_cause",
                "video_tcp_delay_lower_interf_radio_cause",
                "video_tcp_delay_lower_interf_terminal_cause",
                "video_throughput_delimiting_corenet_cause",
                "video_throughput_delimiting_sp_cause",
                "video_throughput_delimiting_user_cause",
                "video_throughput_delimiting_radio_cause",
                "video_throughput_delimiting_terminal_cause",
                "abnormal_period_flag"
        };
        //top 3
        String[] rpt_fre_evt_5min = {
                "iface",
                "imsi ",
                "imei_sv",
                "msisdn",
                "home_province",
                "proc_type",
                "start_time",
                "end_time",
                "status",
                "req_cause",
                "req_cause_group",
                "fail_cause",
                "fail_cause_group",
                "keyword",
                "mme_ue_s1ap_id",
                "target_enb_id",
                "user_ipv4",
                "user_ipv6",
                "mme_ip",
                "enb_ip",
                "tai",
                "cell",
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
                "city"};

        frequentlyTableNameKeyList = Arrays.asList(rpt_fre_evt_5min);
        signaKeyList = Arrays.asList(rpt_cus_ser_problem_delimit_5min);
        singaSpecList = Arrays.asList(rpt_cus_ser_signaling_spec);
        httpKeyList = Arrays.asList(rpt_cus_ser_http_spec);
        dnsKeyList = Arrays.asList(rpt_cus_ser_dns_spec);
        speedLimitKeyList = Arrays.asList(rpt_only_limit_rate_user_h);
        cfgblindspothKeyList = Arrays.asList(cfg_blindspot_h);
        rptcusserupnor15httpList=Arrays.asList(rpt_cus_ser_up_nor_http);


    }


    /**
     * 没有定界结果或者定界结果失败的情况下，异常时间切片数据全部为0
     *
     * @param map
     * @return
     */
    public Map<String, Object> getExceptionTimeByZero(Map<String, Object> map) {
        Map rMap = new HashMap();
        //取得异常时间段横坐标异常切片时间，以小时为刻度
        List<String> dates = (List) map.get("dates");
        //categroy 分类  series 数据
        List<String> categroy = new ArrayList();
        List<Integer> series = new ArrayList();
        //      处理信令面数据 补全
        for (String date : dates) {
            categroy.add(date);
            series.add(0);
        }
        rMap.put("categoryData", categroy);
        rMap.put("series", series);
        return rMap;
    }


    /**
     * 信令侧异常时间段处理
     *
     * @param map
     * @return
     */
    public Map<String, Object> getExceptionTimeBySign(Map<String, Object> map) {
        Map rMap = new HashMap();
        //取得异常时间段横坐标异常切片时间，以小时为刻度
        List<String> dates = (List) map.get("dates");
        //categroy 分类  series 数据
        List<String> categroy = new ArrayList();
        List<Integer> series = new ArrayList();
        //信令面数据输出
        String resultFlag = map.get("resultFlag").toString();
        List<Map<String, Object>> hbaseData = new ArrayList();
        List<Map<String, Object>> resultData = new ArrayList();
        List<Map<String, Object>> hbaseDataFilter = new ArrayList();
        //获取信令详单是否分表头
        hbaseData = getSignaNew(map);
        // 1-核心网问题 信令面
        if ("1".equals(resultFlag)) {
            for (Map<String, Object> lastMap : hbaseData) {
                if ((StringUtil.lastChart2Number(lastMap.get("attach_status")) == 1 && StringUtil.lastChart2Number
                        (lastMap.get("attach_flag")) == 2)
                        || (StringUtil.lastChart2Number(lastMap.get("attach_status")) == 0 && StringUtil
                        .lastChart2Number
                                (lastMap.get("detach_flag")) == 1)
                        || (StringUtil.lastChart2Number(lastMap.get("detach_corenet_cnt")) > 10 && (StringUtil
                        .lastChart2Number(lastMap.get("detach_cnt")) - StringUtil.lastChart2Number(lastMap.get
                        ("detach_user_cnt"))) > 1)
                        || (StringUtil.lastChart2Number(lastMap.get("service_flag")) == 1 && StringUtil.lastChart2Number
                        (lastMap.get("service_request_corenet_cause")) > 2)
                        || (StringUtil.lastChart2Number(lastMap.get("pdn_flag")) == 1 && StringUtil.lastChart2Number
                        (lastMap.get("pdn_corenet_cause")) > 2))
                    resultData.add(lastMap);
            }
        }
        //4-用户关闭4G数据业务或终端问题 信令面
        if ("4".equals(resultFlag)) {
            for (Map<String, Object> lastMap : hbaseData) {
                if ((StringUtil.lastChart2Number(lastMap.get("attach_status")) == 0 && StringUtil.lastChart2Number
                        (lastMap.get("detach_flag")) == 0))
                    resultData.add(lastMap);
            }
        }

        // 5-用户配置（PDN）Five 信令面
        if ("5".equals(resultFlag)) {
            for (Map<String, Object> lastMap : hbaseData) {
                if (StringUtil.lastChart2Number(lastMap.get("pdn_flag")) == 0
                        && StringUtil.lastChart2Number(lastMap.get("pdn_user_cause")) > 0)
                    resultData.add(lastMap);
            }
        }

        //        6-终端问题 信令面
        if ("6".equals(resultFlag)) {
            for (Map<String, Object> lastMap : hbaseData) {
                Integer attach_flag = StringUtil.lastChart2Number(lastMap.get("attach_flag"));
                Integer attach_user_cause = StringUtil.lastChart2Number(lastMap.get("attach_user_cause"));
                Integer service_flag = StringUtil.lastChart2Number(lastMap.get("service_flag"));
                Integer service_request_user_cause = StringUtil.lastChart2Number(lastMap.get
                        ("service_request_user_cause"));
                if (((attach_flag == 1 || attach_flag == 2) && attach_user_cause > 0) || ((service_flag == 1 ||
                        service_flag == 0) && service_request_user_cause > 0))
                    resultData.add(lastMap);
            }
        }

        //       7,8-无线问题 信令面
        if ("7".equals(resultFlag) || "8".equals(resultFlag)) {
            for (Map<String, Object> lastMap : hbaseData) {
                if ((StringUtil.lastChart2Number(lastMap.get("drop_radio_cause")) > 5
                        || StringUtil.lastChart2Number(lastMap.get("handover_frequently_flag")) == 1
                        || StringUtil.lastChart2Number(lastMap.get("tau_frequently_flag")) == 1))
                    resultData.add(lastMap);
            }
        }
        //      处理信令面数据 补全
        for (String date : dates) {
            if (resultData.size() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("sdate", date);
                item.put("abnormal_period_flag", 0);
                hbaseDataFilter.add(item);
            } else {
                for (Map<String, Object> sing : resultData) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("sdate", date);
                    Object sdate = sing.get("sdate");
                    if (!date.equals(sdate)) {
//                  不存在 按照0处理
                        item.put("abnormal_period_flag", 0);
                    } else {
//                  存在 取出实际的值
                        item.put("abnormal_period_flag", sing.get("abnormal_period_flag"));
                    }
                    if (!hbaseDataFilter.contains(item)) {
                        hbaseDataFilter.add(item);
                    }

                }
            }
        }

        for (Map<String, Object> sing : hbaseDataFilter) {
            categroy.add(sing.get("sdate").toString());
            String sing_flag_pre = "0";
            if (null != sing.get("abnormal_period_flag")) {
                sing_flag_pre = sing.get("abnormal_period_flag").toString();
            }
            Integer sing_flag = StringUtil.lastChart2Number(sing_flag_pre);
            Integer r_flag = sing_flag >= 1 ? 1 : 0;
            series.add(r_flag);
        }
        rMap.put("categoryData", categroy);
        rMap.put("series", series);
        return rMap;
    }


    /**
     * 用户侧根据时间切片具体日期求和六类结果的数据,因为数据时间不准确，以小时为粒度需要合并数据
     *
     * @param oldList
     * @return
     */
    public Map<String, Object> getMapDataBySDate(List<Map<String, Object>> oldList, String sdate) {
        Integer num = 0;
        Map<String, Object> finalMap = new HashMap();
        for (Map<String, Object> temp : oldList) {
            //时间格式如下20181024112324，截取到小时级别的时间进行比较
            if (sdate.substring(0, 10).equals(temp.get("sdate").toString().substring(0, 10))) {
                num = num + Integer.valueOf(temp.get("all").toString());
            }
        }
        finalMap.put("sdate", sdate);
        finalMap.put("all", num);
        return finalMap;
    }

    /**
     * 用户面异常切片处理
     *
     * @param map
     * @return
     */
    public Map<String, Object> getExceptionTimeByUser(Map<String, Object> map) {
        Map rMap = new HashMap();
        List<String> dates = (List) map.get("dates");
        //categroy 分类  series 数据
        List<String> categroy = new ArrayList();
        List<Integer> series = new ArrayList();
        //信令面数据输出
        String resultFlag = map.get("resultFlag").toString();
        //用户面结果
        List<Map<String, Object>> userCasePhoenix = new ArrayList();
        List<Map<String, Object>> userCasePhoenixFilter = new ArrayList();
        //add by liukang
        userCasePhoenix = getUserCaseExceptionPhoenix(map);
        //根据定界结果取得异常切片数据
        List<Map<String, Object>> userResultData = getUserExceptionData(resultFlag, userCasePhoenix);
        //补全用户数据
        for (String date : dates) {
            if (userResultData.size() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("sdate", date);
                item.put("all", 0);
                userCasePhoenixFilter.add(item);
            } else {
                Map<String, Object> item = getMapDataBySDate(userResultData, date);
                if (!userCasePhoenixFilter.contains(item)) {
                    userCasePhoenixFilter.add(item);
                }
            }
        }
        for (Map<String, Object> user : userCasePhoenixFilter) {
            categroy.add(user.get("sdate").toString());
            Integer r_flag = user.get("all").toString() == null ? 0 : Integer.valueOf(user.get("all").toString());
            series.add(r_flag);
        }
        rMap.put("categoryData", categroy);
        rMap.put("series", series);
        return rMap;
    }


    /**
     * 新逻辑根据定界结果resultFlag来去异常时间切片，resultFlag取值来自于JumpResultEnum
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> getExceptionTime(Map<String, Object> map) {
        Map rMap = new HashMap();
        String resultFlag = map.get("resultFlag").toString();
        //resultFlag=0没有异常或者定界结果查询失败的情况所有数据初始化0
        if ("0".equals(resultFlag)) {
            rMap = getExceptionTimeByZero(map);
        }
        if ("1".equals(resultFlag) || "4".equals(resultFlag) || "5".equals(resultFlag) || "6".equals(resultFlag) ||
                "7".equals(resultFlag) || "8".equals(resultFlag)) {
            rMap = getExceptionTimeBySign(map);
        } else if ("9".equals(resultFlag) || "10".equals(resultFlag) || "11".equals(resultFlag) || "12".equals
                (resultFlag) || "14".equals(resultFlag) || "15".equals(resultFlag) || "16".equals(resultFlag) || "17"
                .equals(resultFlag) || "20".equals(resultFlag) || "21".equals(resultFlag)|| "23".equals(resultFlag)) {
            rMap = getExceptionTimeByUser(map);
        }

        //处理显示时间去掉分秒只是显示小时开始
        List<String> categoryData = new ArrayList<String>();
        List<String> new_categoryData = new ArrayList<String>();
        categoryData = (List<String>) rMap.get("categoryData");
        for (String date : categoryData) {

            new_categoryData.add(date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8) + " "
                    + date.substring(8, 10) + ":00");
        }
        rMap.put("categoryData", new_categoryData);
        //处理显示时间去掉分秒只是显示小时结束
        return rMap;
    }


    /**
     * 处理用户侧异常时间切片数据
     *
     * @param resultFlag
     * @param list
     * @return
     */
    public List<Map<String, Object>> getUserExceptionData(String resultFlag, List<Map<String, Object>> list) {
        List<Map<String, Object>> returnList = new ArrayList();
        //9-用户侧终端问题
        if ("9".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {    //9-用户侧终端问题
                Integer first_page_delay_delimiting_terminal_cause = temp.get
                        ("first_page_delay_delimiting_terminal_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("first_page_delay_delimiting_terminal_cause").toString());
                Integer tcp_delay_lower_interf_terminal_cause = temp.get("tcp_delay_lower_interf_terminal_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("tcp_delay_lower_interf_terminal_cause").toString());
                Integer page_throughput_delimiting_terminal_cause = temp.get
                        ("page_throughput_delimiting_terminal_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("page_throughput_delimiting_terminal_cause").toString());
                Integer video_tcp_delay_lower_interf_terminal_cause = temp.get
                        ("video_tcp_delay_lower_interf_terminal_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("video_tcp_delay_lower_interf_terminal_cause").toString());
                Integer video_throughput_delimiting_terminal_cause = temp.get
                        ("video_throughput_delimiting_terminal_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("video_throughput_delimiting_terminal_cause").toString());
                Integer all = first_page_delay_delimiting_terminal_cause + tcp_delay_lower_interf_terminal_cause +
                        page_throughput_delimiting_terminal_cause + video_tcp_delay_lower_interf_terminal_cause +
                        video_throughput_delimiting_terminal_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }
        //10,11-用户侧无线问题
        if ("10".equals(resultFlag) || "11".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {
                Integer first_page_delay_delimiting_radio_cause = temp.get("first_page_delay_delimiting_radio_cause")
                        == null ? 0 : Integer.valueOf(temp.get("first_page_delay_delimiting_radio_cause").toString());
                Integer tcp_delay_lower_interf_radio_cause = temp.get("tcp_delay_lower_interf_radio_cause") == null ?
                        0 : Integer.valueOf(temp.get("tcp_delay_lower_interf_radio_cause").toString());
                Integer page_throughput_delimiting_radio_cause = temp.get("page_throughput_delimiting_radio_cause")
                        == null ? 0 : Integer.valueOf(temp.get("page_throughput_delimiting_radio_cause").toString());
                Integer video_tcp_delay_lower_interf_radio_cause = temp.get
                        ("video_tcp_delay_lower_interf_radio_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("video_tcp_delay_lower_interf_radio_cause").toString());
                Integer video_throughput_delimiting_radio_cause = temp.get("video_throughput_delimiting_radio_cause")
                        == null ? 0 : Integer.valueOf(temp.get("video_throughput_delimiting_radio_cause").toString());
                Integer all = first_page_delay_delimiting_radio_cause + tcp_delay_lower_interf_radio_cause +
                        page_throughput_delimiting_radio_cause + video_tcp_delay_lower_interf_radio_cause +
                        video_throughput_delimiting_radio_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }

        //12-用户侧核心网问题
        if ("12".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {
                Integer first_page_delay_delimiting_Corenet_cause = temp.get
                        ("first_page_delay_delimiting_corenet_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("first_page_delay_delimiting_corenet_cause").toString());
                Integer tcp_delay_upper_interf_Corenet_cause = temp.get("tcp_delay_upper_interf_corenet_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("tcp_delay_upper_interf_corenet_cause").toString());
                Integer page_throughput_delimiting_Corenet_cause = temp.get
                        ("page_throughput_delimiting_corenet_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("page_throughput_delimiting_corenet_cause").toString());
                Integer video_tcp_delay_upper_interf_Corenet_cause = temp.get
                        ("video_tcp_delay_upper_interf_corenet_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("video_tcp_delay_upper_interf_corenet_cause").toString());
                Integer video_throughput_delimiting_Corenet_cause = temp.get
                        ("video_throughput_delimiting_corenet_cause") == null ? 0 : Integer.valueOf(temp.get
                        ("video_throughput_delimiting_corenet_cause").toString());
                Integer all = first_page_delay_delimiting_Corenet_cause + tcp_delay_upper_interf_Corenet_cause +
                        page_throughput_delimiting_Corenet_cause + video_tcp_delay_upper_interf_Corenet_cause +
                        video_throughput_delimiting_Corenet_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }
        //14-用户侧DNS问题
        if ("14".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {
                Integer DNS_delimiting_dns_cause = temp.get("dns_delimiting_dns_cause") == null ? 0 : Integer.valueOf
                        (temp.get("dns_delimiting_dns_cause").toString());
                Integer dns_delay_delimiting_dns_cause = temp.get("dns_delay_delimiting_dns_cause") == null ? 0 :
                        Integer.valueOf(temp.get("dns_delay_delimiting_dns_cause").toString());
                Integer all = DNS_delimiting_dns_cause + dns_delay_delimiting_dns_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }
        //16-用户侧SP问题
        if ("16".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {
                Integer page_failure_delimiting_sp_cause = temp.get("page_failure_delimiting_sp_cause") == null ? 0 :
                        Integer.valueOf(temp.get("page_failure_delimiting_sp_cause").toString());
                Integer first_page_delay_delimiting_sp_cause = temp.get("first_page_delay_delimiting_sp_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("first_page_delay_delimiting_sp_cause").toString());
                Integer tcp_delay_upper_interf_sp_cause = temp.get("tcp_delay_upper_interf_sp_cause") == null ? 0 :
                        Integer.valueOf(temp.get("tcp_delay_upper_interf_sp_cause").toString());
                Integer page_throughput_delimiting_sp_cause = temp.get("page_throughput_delimiting_sp_cause") == null
                        ? 0 : Integer.valueOf(temp.get("page_throughput_delimiting_sp_cause").toString());
                Integer video_tcp_delay_upper_interf_sp_cause = temp.get("video_tcp_delay_upper_interf_sp_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("video_tcp_delay_upper_interf_sp_cause").toString());
                Integer video_throughput_delimiting_sp_cause = temp.get("video_throughput_delimiting_sp_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("video_throughput_delimiting_sp_cause").toString());
                Integer all = page_failure_delimiting_sp_cause + first_page_delay_delimiting_sp_cause +
                        tcp_delay_upper_interf_sp_cause + page_throughput_delimiting_sp_cause +
                        video_tcp_delay_upper_interf_sp_cause + video_throughput_delimiting_sp_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }
        //16-用户侧用户问题
        if ("17".equals(resultFlag)) {
            for (Map<String, Object> temp : list) {
                Integer dns_delimiting_user_cause = temp.get("dns_delimiting_user_cause") == null ? 0 : Integer
                        .valueOf(temp.get("dns_delimiting_user_cause").toString());
                Integer page_failure_delimiting_user_cause = temp.get("page_failure_delimiting_user_cause") == null ?
                        0 : Integer.valueOf(temp.get("page_failure_delimiting_user_cause").toString());
                Integer first_page_delay_delimiting_user_cause = temp.get("first_page_delay_delimiting_user_cause")
                        == null ? 0 : Integer.valueOf(temp.get("first_page_delay_delimiting_user_cause").toString());
                Integer dns_delay_delimiting_user_cause = temp.get("dns_delay_delimiting_user_cause") == null ? 0 :
                        Integer.valueOf(temp.get("dns_delay_delimiting_user_cause").toString());
                Integer tcp_delay_lower_interf_user_cause = temp.get("tcp_delay_lower_interf_user_cause") == null ? 0
                        : Integer.valueOf(temp.get("tcp_delay_lower_interf_user_cause").toString());
                Integer page_throughput_delimiting_user_cause = temp.get("page_throughput_delimiting_user_cause") ==
                        null ? 0 : Integer.valueOf(temp.get("page_throughput_delimiting_user_cause").toString());
                Integer video_tcp_delay_lower_interf_user_cause = temp.get("video_tcp_delay_lower_interf_user_cause")
                        == null ? 0 : Integer.valueOf(temp.get("video_tcp_delay_lower_interf_user_cause").toString());
                Integer video_throughput_delimiting_user_cause = temp.get("video_throughput_delimiting_user_cause")
                        == null ? 0 : Integer.valueOf(temp.get("video_throughput_delimiting_user_cause").toString());
                Integer all = dns_delimiting_user_cause + page_failure_delimiting_user_cause +
                        first_page_delay_delimiting_user_cause + dns_delay_delimiting_user_cause +
                        tcp_delay_lower_interf_user_cause + page_throughput_delimiting_user_cause +
                        video_tcp_delay_lower_interf_user_cause + video_throughput_delimiting_user_cause;
                temp.put("all", all);
                returnList.add(temp);
            }
        }
        return returnList;
    }

    /**
     * 原来的方法和定界结果同步查询，实现信令侧和用户侧统一的异常时间切片逻辑
     *
     * @param map
     * @return
     */
   /* @Override
    public Map<String, Object> getExceptionTime(Map<String, Object> map) {
        Map rMap = new HashMap();
        //信令面数据输出
        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
        //用户面数据输出
        List<Map<String, Object>> userCasePhoenix = mapper.getUserCaseExceptionPhoenix(map);
//        categroy 分类  series 数据
        List<String> categroy = new ArrayList();
        List<Integer> series = new ArrayList();


        List<Map<String, Object>> hbaseDataFilter = new ArrayList();
        List<Map<String, Object>> userCasePhoenixFilter = new ArrayList();
        List<String> dates = (List) map.get("dates");

//      处理信令面数据 补全
        for (String date : dates) {
            if (hbaseData.size() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("sdate", date);
                item.put("abnormal_period_flag", 0);
                hbaseDataFilter.add(item);
            } else {
                for (Map<String, Object> sing : hbaseData) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("sdate", date);
                    Object sdate = sing.get("sdate");
                    if (!date.equals(sdate)) {
//                  不存在 按照0处理
                        item.put("abnormal_period_flag", 0);
                    } else {
//                  存在 取出实际的值
                        item.put("abnormal_period_flag", sing.get("abnormal_period_flag"));
                    }
                    if (!hbaseDataFilter.contains(item)) {
                        hbaseDataFilter.add(item);
                    }

                }
            }
        }
        //      处理用户面数据 补全
        for (String date : dates) {
            if (userCasePhoenix.size() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("sdate", date);
                item.put("abnormal_period_flag", 0);
                userCasePhoenixFilter.add(item);
            } else {
                for (Map<String, Object> user : userCasePhoenix) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("sdate", date);
                    Object sdate = user.get("sdate");
                    if (!date.equals(sdate)) {
//                  不存在 按照0处理
                        item.put("abnormal_period_flag", 0);
                    } else {
//                  存在 取出实际的值
                        item.put("abnormal_period_flag", user.get("abnormal_period_flag"));
                    }
                    if (!userCasePhoenixFilter.contains(item)) {
                        userCasePhoenixFilter.add(item);
                    }

                }
            }
        }
        for (Map<String, Object> sing : hbaseDataFilter) {
            for (Map<String, Object> user : userCasePhoenixFilter) {
                if (sing.get("sdate").equals(user.get("sdate"))) {
                    categroy.add(sing.get("sdate").toString());
                    String sing_flag_pre = "0";
                    String user_flag_pre = "0";
                    if (null != sing.get("abnormal_period_flag")) {
                        sing_flag_pre = sing.get("abnormal_period_flag").toString();
                    }
                    if (null != user.get("abnormal_period_flag")) {
                        user_flag_pre = (String) user.get("abnormal_period_flag").toString();
                    }
                    Integer sing_flag = StringUtil.lastChart2Number(sing_flag_pre);
                    Integer user_flag = StringUtil.lastChart2Number(user_flag_pre);
                    Integer r_flag = sing_flag + user_flag >= 1 ? 1 : 0;
                    series.add(r_flag);
                }
            }
        }
        rMap.put("categoryData", categroy);
        rMap.put("series", series);
        return rMap;
    }*/
    @Override
    public List<Map<String, Object>> getSignaOne(Map<String, Object> map) {
        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
//        信令面步骤一:算法
        /*List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> (StringUtil.lastChart2Number(item.get("attach_status")) == 1 && StringUtil
                .lastChart2Number(item.get("attach_flag")) == 2)
                        || (StringUtil.lastChart2Number(item.get("attach_status")) == 0 && StringUtil
                        .lastChart2Number(item.get("detach_flag")) == 1)
                        || (StringUtil.lastChart2Number(item.get("detach_corenet_cnt")) > 10 && (StringUtil
                        .lastChart2Number(item.get("detach_cnt")) - StringUtil.lastChart2Number(item.get
                        ("detach_user_cnt"))) > 1)
                        || (StringUtil.lastChart2Number(item.get("service_flag")) == 1 && StringUtil.lastChart2Number
                        (item.get("service_request_corenet_cause")) > 2)
                        || (StringUtil.lastChart2Number(item.get("pdn_flag")) == 1 && StringUtil.lastChart2Number
                        (item.get("pdn_corenet_cause")) > 2))
                .sorted(comparator.reversed())
                .limit(10)
                .collect(Collectors.toList());
        return rData;*/
        return hbaseData;
    }

    @Override
    public List<Map<String, Object>> getSignaFour(Map<String, Object> map) {

        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
        /*List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> (StringUtil.lastChart2Number(item.get("attach_status")) == 0 && StringUtil
                .lastChart2Number(item.get("detach_flag")) == 0))
                .sorted(comparator.reversed())
                .limit(1)
                .collect(Collectors.toList());
        return rData;*/
        return hbaseData;
    }

    @Override
    public List<Map<String, Object>> getSignaFive(Map<String, Object> map) {
        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
       /* List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> ((StringUtil.lastChart2Number(item.get("pdn_flag")) == 1 || StringUtil
                .lastChart2Number(item.get("pdn_flag")) == 0)
                        && StringUtil.lastChart2Number(item.get("pdn_user_cause")) == 0))
                .sorted(comparator.reversed())
                .limit(1)
                .collect(Collectors.toList());
        return rData;*/
        return hbaseData;
    }

    /**
     * 无表头分区表限速信息查询
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getSpeedLimitData(Map<String, Object> map) {
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断201900313之前没有数据表直接返回
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190311235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                return finalHbaseData;
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
            return finalHbaseData;
        }
        //没有分表分表
        else {
            finalHbaseData=getUserSpeedLimitData(map);
            /* List<Map<String, Object>> allHbaseData = getUserSpeedLimitData(map);
            //如果数据不为空
           if(!allHbaseData.isEmpty())
            {
                //取得最近的时间
                Map<String, Object> lastMap=allHbaseData.get(allHbaseData.size()-1);
                String lastTime=lastMap.get("sdate") == null ? "" : lastMap.get("sdate") + "";
                //根据最近的时间获取最近时间的所有数据
                for(Map<String, Object> temp : allHbaseData)
                {
                    String oneTime=temp.get("sdate") == null ? "" : temp.get("sdate") + "";
                    if(lastTime.equals(oneTime))
                    {
                        finalHbaseData.add(temp);
                    }
                }
            }*/
        }
        return finalHbaseData;
    }

    /**
     * 无表头分区表获取限速数据
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> getUserSpeedLimitData(Map<String, Object> map) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String[] rowkeys=rowkey.split("#");
            String[] rowkeys_start=rowkeys[0].split("_");
            String startRowkey = rowkeys_start[0]+"_"+rowkeys_start[1];
            String[] rowkeys_end=rowkeys[1].split("_");
            String endRowkey = rowkeys_end[0]+"_"+rowkeys_end[1];
            //分表
            String tableName = speedLimitTableName + "_" +rowkeys[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, speedLimitKeyList);
            allHbaseData.addAll(hbaseData);
        }
        return allHbaseData;
    }


    /**
     * 无表头盲点库数据查询
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getCfgblindspothData(String startRowkey,String endRowkey) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        try
        {
           /* String tableName=cfgblindspothTableName+"_"+startRowkey.substring(0,8);
            allHbaseData=getHbaseData(tableName, startRowkey, endRowkey, cfgblindspothKeyList);*/
        }
        catch (Exception e)
        {
//            log.info("获取盲点库数据异常："+e);
        }
        return allHbaseData;
    }




    /**
     * 无表头分区表获取信令侧详单信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getSignaNew(Map<String, Object> map) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        //是否分表 0，没有分表，1 分表
        if ("0".equals(TABLESPLITFLAG))
            allHbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                    ("endRowKey").toString(), signaKeyList);
        else {
            List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "1");
            for (String rowkey : rowkeyList) {
                String startRowkey = rowkey.split("#")[0];
                String endRowkey = rowkey.split("#")[1];
                //分表
                String tableName = singaTableName + "_" + rowkey.split("#")[2];
                List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, signaKeyList);
                allHbaseData.addAll(hbaseData);
            }
        }
        return allHbaseData;
    }

    /**
     * 无表头分区表获取信令侧详单信息包括时间信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getWirelessTop3ForTime(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap=new HashMap<String, Object>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
               // finalHbaseData =mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
           // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            for (Map<String, Object> temp : allHbaseData) {
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "0" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("1".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5)  // 一线接口无线问题小区返回TOP（N）在次设置
                .collect(Collectors.toList());
        for( Map<String, Object> temp10:finalHbaseData)
        {
            temp10.put("time",getTimeStrForHour(tempData,temp10.get("ecgi")+""));
        }
        return finalHbaseData;
    }


    /**
     * 获取所有的日期对象到小时
     * @param tempData
     * @param ecgi
     * @return
     */

    public static Map<String,String> getTimeStrForHour(List<Map<String, Object>> tempData ,String ecgi)
    {
        Map<String,String> map=new HashMap<String,String>();
        for (Map<String, Object> temp : tempData) {
            String ci = temp.get("ecgi")+"";
            if(ecgi.equals(ci))
            {
                String time_h=temp.get("start_time").toString().replaceAll("/", "").replaceAll("-", "").replaceAll(" ", "")
                        .replaceAll(":", "");
                map.put(time_h,time_h);
            }
        }
         return map;
    }
    /**
     *   add by liukang
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getWirelessTop5MME(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
            // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getSingaSpecTableNameForMap(map);
//             log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
            for (Map<String, Object> temp : allHbaseData) {
                String page_failure_delimiting = temp.get("problem_delimit") == null ? "0" : temp.get
                        ("problem_delimit").toString();

                //过滤符合条件的数据
                if ("1".equals(page_failure_delimiting)) {
                    tempData.add(temp);
                }
            }
//            log.info("tempData0000000000"+tempData.size());
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("cell") == null ? "0" : temp.get
                        ("cell").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
//            log.info("***********tempMap********* "+tempMap);
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }

//        log.info("====ecgiData====== "+ecgiData);
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .filter(item->Long.valueOf(item.get("num").toString()) >= 5)
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());
        if(!finalHbaseData.isEmpty()){
            /*if(Float.valueOf(finalHbaseData.get(0).get("num").toString())>=5){*/
//                log.info("-----getWirelessTop5MME---"+finalHbaseData);
                 return  finalHbaseData ;
          /*}else{
                log.info("-----getWirelessTop5Frequently---"+finalHbaseData);
               return  getWirelessTop5Frequently(map);

            }*/
        }else{
//            log.info("-----getWirelessTop5Frequently---"+finalHbaseData);
            return  getWirelessTop5Frequently(map);
        }

    }
    /**
     *   add by liukang
     *
     * @param map
     * @return
     */

    public List<Map<String, Object>> getWirelessTop5Frequently(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
            // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getFrequentlyTableNameForMap(map);
            // log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
//           log.info("hhhhhhhhh---allHbaseData------hhhhh"+tempData.size());
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : allHbaseData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());

        return finalHbaseData;
    }
    /**
     * 无表头分区表获取http数据Map
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> getFrequentlyTableNameForMap(Map<String, Object> map) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = frequentlyTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, singaSpecList);
            allHbaseData.addAll(hbaseData);
        }
        return allHbaseData;
    }
    /**
     * 无表头分区表获取http数据Map
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> getSingaSpecTableNameForMap(Map<String, Object> map) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = singaSpecTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, singaSpecList);
            allHbaseData.addAll(hbaseData);
        }
        return allHbaseData;
    }
    /**
     * 无表头分区表获取详单信息  add by liukang 一线接口SP问题取TOP5小区
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getSpQuestionTop5(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
            // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            // log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
            for (Map<String, Object> temp : allHbaseData) {
                // 修改 page_failure_delimiting 判断为 0
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "9" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("3".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
            //log.info("tempData0000000000"+tempData.size());
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());
        return finalHbaseData;
    }
    /**
     * 无表头分区表获取信令侧详单信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getWirelessTop10(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
               // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
           // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
           // log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
            for (Map<String, Object> temp : allHbaseData) {
                // 修改 page_failure_delimiting 判断为 0
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "0" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("1".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
            //log.info("tempData0000000000"+tempData.size());
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());
        return finalHbaseData;
    }
    /**
     * 无表头分区表获取信令侧详单信息
     *
     * @param map
     * @return
     */
    /*@Override
       public List<Map<String, Object>> getWirelessTop10(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(CustomerServiceSupportController.TABLESPLITFLAG)) {
            // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            // log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
            for (Map<String, Object> temp : allHbaseData) {
                // 修改 page_failure_delimiting 判断为 0
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "0" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("1".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
            //log.info("tempData0000000000"+tempData.size());
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());
        return finalHbaseData;
    }*/
    /**
     * 无表头分区表获取信令侧详单信息
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserWirelessTop5(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
                // return mapper.getWirelessTop10(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
            // finalHbaseData = mapper.getWirelessTop10(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            // log.info("wwwwwwwww==allHbaseData"+allHbaseData.size()+"  "+allHbaseData);
            for (Map<String, Object> temp : allHbaseData) {
                // 修改 page_failure_delimiting 判断为 0
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "9" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("0".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
//            log.info("tempData0000000000 "+tempData.size()+" tempData  "+ tempData);
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {
                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(5) // modfiy by liukang  limit(10)
                .collect(Collectors.toList());
        return finalHbaseData;
    }

    @Override
    public List<Map<String, Object>> getUserScotoma(Map<String, Object> map) {
        List<Map<String, Object>> ecgiData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> tempData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> finalHbaseData = new ArrayList<Map<String, Object>>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
               // return mapper.getUserScotomaPhoenix(map);
            }
        } catch ( Exception e ) {
        }
        if ("0".equals(TABLESPLITFLAG)) {
          //  finalHbaseData = mapper.getUserScotomaPhoenix(map);
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            for (Map<String, Object> temp : allHbaseData) {
                String page_failure_delimiting = temp.get("page_failure_delimiting") == null ? "0" : temp.get
                        ("page_failure_delimiting").toString();
                String first_page_delay_delimiting = temp.get("first_page_delay_delimiting") == null ? "0" : temp.get
                        ("first_page_delay_delimiting").toString();
                String dns_delay_delimiting = temp.get("dns_delay_delimiting") == null ? "0" : temp.get
                        ("dns_delay_delimiting").toString();
                String tcp_delay_upper_interf_delimiting = temp.get("tcp_delay_upper_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_upper_interf_delimiting").toString();
                String tcp_delay_lower_interf_delimiting = temp.get("tcp_delay_lower_interf_delimiting") == null ? "0" :
                        temp.get("tcp_delay_lower_interf_delimiting").toString();
                String page_throughput_delimiting = temp.get("page_throughput_delimiting") == null ? "0" : temp.get
                        ("page_throughput_delimiting").toString();
                //过滤符合条件的数据
                if ("1".equals(page_failure_delimiting) || "1".equals(first_page_delay_delimiting) || "1".equals
                        (dns_delay_delimiting) || "1".equals(tcp_delay_upper_interf_delimiting) || "1".equals
                        (tcp_delay_lower_interf_delimiting) || "1".equals(page_throughput_delimiting)) {
                    tempData.add(temp);
                }
            }
            //计算每个ecgi的数量,key为ecgi,value为ecgi出现的次数
            Map<String, Integer> tempMap = new HashMap<String, Integer>();
            for (Map<String, Object> temp : tempData) {
                String ecgi = temp.get("ecgi") == null ? "0" : temp.get
                        ("ecgi").toString();
                if (null != tempMap.get(ecgi)) {
                    tempMap.put(ecgi, tempMap.get(ecgi) + 1);
                } else {
                    tempMap.put(ecgi, 1);
                }
            }
            //去掉空数据
            if (!tempMap.isEmpty())
                tempMap.remove("0");
            //构造ecgi和对应数量 List<Map<String, Object>>为排序做准备
            if (!tempMap.isEmpty()) {

                Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }


                /*Iterator<Map.Entry<String, Integer>> entries = tempMap.entrySet().iterator();
                {
                    Map.Entry<String, Integer> entry = entries.next();
                    String ecgi = entry.getKey();
                    Integer num = entry.getValue();
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("ecgi", ecgi);
                    temp.put("num", num);
                    ecgiData.add(temp);
                }*/
            }
        }
        finalHbaseData = ecgiData.stream()
                .sorted(comparator_num.reversed())
                .limit(10)
                .collect(Collectors.toList());
        return finalHbaseData;
    }


    /**
     * 无表头分区表获取http数据Map
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> getUserHttpDataForMap(Map<String, Object> map) {
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = httpTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, httpKeyList);
            allHbaseData.addAll(hbaseData);
        }
        return allHbaseData;
    }

    /**
     * 无表头分区表获取http数据 list
     *
     * @param map
     * @return
     */
    @Override
    public List<HttpDetail> getUserHttpData(Map<String, Object> map) {
        List<HttpDetail> userHttpData = new ArrayList<HttpDetail>();
        //加入日期判断20190129之前的数据没有分表
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
               // return mapper.getUserHttpData(map);
                List<Map<String, Object>> hbaseData = getHbaseData(httpTableName, map.get("startRowKey_sec").toString(), map.get("endRowKey_sec").toString(), httpKeyList);
                List<Object> userHttpData_1 = hbaseData.stream().map(e -> MapTransformBean.map2Object(e, HttpDetail
                        .class)).collect(Collectors.toList());
                for (Object temp : userHttpData_1) {
                    userHttpData.add((HttpDetail) temp);
                }
                return userHttpData;
            }
        } catch ( Exception e ) {
        }
        //没有分表
        if ("0".equals(TABLESPLITFLAG)) {
          //  userHttpData = mapper.getUserHttpData(map);
            List<Map<String, Object>> hbaseData = getHbaseData(httpTableName, map.get("startRowKey_sec").toString(), map.get("endRowKey_sec").toString(), httpKeyList);
            List<Object> userHttpData_1 = hbaseData.stream().map(e -> MapTransformBean.map2Object(e, HttpDetail
                    .class)).collect(Collectors.toList());
            for (Object temp : userHttpData_1) {
                userHttpData.add((HttpDetail) temp);
            }
        }
        //没有分表分表
        else {
            List<Map<String, Object>> allHbaseData = getUserHttpDataForMap(map);
            List<Object> userHttpData_1 = allHbaseData.stream().map(e -> MapTransformBean.map2Object(e, HttpDetail
                    .class)).collect(Collectors.toList());
            for (Object temp : userHttpData_1) {
                userHttpData.add((HttpDetail) temp);
            }
        }
       // log.info("&&&&&&&&&====userHttpData=======&&&&&&&&&"+userHttpData);
        return userHttpData;
    }

    /**
     * 无表头分区表获取dns数据
     *
     * @param map
     * @return
     */
    @Override
    public List<DnsDetail> getUserDnsData(Map<String, Object> map) {
        List<DnsDetail> userDnsData = new ArrayList<DnsDetail>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String edate = map.get("edate").toString();
        String splitDate = "20190128235959";
        try {
            long diff = dateFormat.parse(splitDate).getTime() - dateFormat.parse(edate).getTime();
            if (diff >= 0) {
               // return mapper.getUserDnsData(map);
            }
        } catch ( Exception e ) {
        }
        //没有分表
        if ("0".equals(TABLESPLITFLAG)) {
           // userDnsData = mapper.getUserDnsData(map);
        } else {
            List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
            List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
            for (String rowkey : rowkeyList) {
                String startRowkey = rowkey.split("#")[0];
                String endRowkey = rowkey.split("#")[1];
                //分表
                String tableName = dnsTableName + "_" + rowkey.split("#")[2];
                List<Map<String, Object>> hbaseData = getHbaseData(tableName, startRowkey, endRowkey, dnsKeyList);
                allHbaseData.addAll(hbaseData);
            }
            List<Object> userDnsData_1 = allHbaseData.stream().map(e -> MapTransformBean.map2Object(e, DnsDetail
                    .class)).collect(Collectors.toList());
            for (Object temp : userDnsData_1) {
                userDnsData.add((DnsDetail) temp);
            }
        }
        return userDnsData;
    }

    @Override
    public List<Map<String, Object>> getSignaSix(Map<String, Object> map) {

        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
       /* List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> (
                        (StringUtil.lastChart2Number(item.get("attach_flag")) == 1 && StringUtil.lastChart2Number
                        (item.get("attach_user_cause")) > 0)
                                || (StringUtil.lastChart2Number(item.get("service_flag")) == 1 && StringUtil
                                .lastChart2Number(item.get("service_request_user_cause")) > 0)))
                .sorted(comparator.reversed())
                .limit(1)
                .collect(Collectors.toList());
        return rData;*/
        return hbaseData;
    }

    @Override
    public List<Map<String, Object>> getSignaSeven(Map<String, Object> map) {

        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
       /* List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> (StringUtil.lastChart2Number(item.get("drop_radio_cause")) > 5
                        || StringUtil.lastChart2Number(item.get("handover_frequently_flag")) == 1
                        || StringUtil.lastChart2Number(item.get("tau_frequently_flag")) == 1))
                .sorted(comparator.reversed())
                .limit(1)
                .collect(Collectors.toList());
        return rData;*/
        return hbaseData;
    }

    /**
     * 没有分表
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getFrequently(Map<String, Object> map) {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Map<String, Object>> hbaseData = getSignaOne(map);
        List<Map<String, Object>> rData = hbaseData.stream()
                .filter(item -> StringUtil.lastChart2Number(item.get("tau_frequently_flag")) == 1
                        || StringUtil.lastChart2Number(item.get("handover_frequently_flag")) == 1
                        || StringUtil.lastChart2Number(item.get("drop_radio_cause")) > 1)
//                .sorted(comparator.reversed())
//                .limit(10)
                .collect(Collectors.toList());
        //msisdn+ci
        if (rData.size() > 0) {
            Map<String, Object> stringObjectMap = rData.get(0);
            String msisdn = stringObjectMap.get("msisdn").toString();
            String sdate = stringObjectMap.get("sdate").toString();

            Map<String, Object> endStringObjectMap = rData.get(rData.size() - 1);

            String edate = endStringObjectMap.get("sdate").toString();
            Map<String, Object> param = new HashMap<>();
            param.put("startRowKey", msisdn + "_" + sdate);
            param.put("endRowKey", msisdn + "_" + edate);
            data = getHbaseData(frequentlyTableName, param.get("startRowKey").toString(), param.get("endRowKey")
                    .toString(), frequentlyTableNameKeyList)
                    .stream().sorted(comparator.reversed()).limit(10).collect(Collectors.toList());
            if (data.size() > 0) {
                return data;
            }
            data = getHbaseData(singaSpecTableName, param.get("startRowKey").toString(), param.get("endRowKey")
                    .toString(), singaSpecList)
                    .stream().filter(item -> StringUtil.lastChart2Number(item.get("problem delimiting")) == 2)
                    .sorted(comparator.reversed()).limit(10).collect(Collectors.toList());

            if (data.size() > 0) {
                return data;
            }
        }
        return data;
    }

    @Override
    public List<Map<String, String>> getSingbScotoma(Map<String, Object> map) {

        List<Map<String, String>> dataList = blindDataMatch(singaSpecTableName, map.get("startRowKey_sec").toString()
                , map.get("endRowKey_sec").toString(), singaSpecList);

//        查询配置表

        ResultScanner scanner = HBaseUtil.getAllRecord(cfg_kh_nowireless_ciTableName);
        List<String> ciList = new ArrayList<>();
        List<Map<String, String>> rlist = new ArrayList();
        if (null != scanner) {
            for (Result r : scanner) {
                for (KeyValue kv : r.raw()) {
//                    log.info("@@@@@@@@@@@@@@@@@@scanner=====" + new String(kv.getValue()));
                    String s = new String(kv.getValue());
                    ciList.add(s);
                }
            }
            //        关闭流
            scanner.close();
            dataList.forEach(e -> {
                ciList.forEach(t -> {
                    if (e.get("ecgi").equals(t)) {
                        rlist.add(e);
                    }
                });
            });
        }
        return rlist;
    }

    @Override
    public List<String> getWirelessTop10ci(Map<String, Object> map) {


        List<Map<String, Object>> hbaseData = getHbaseData(singaTableName, map.get("startRowKey").toString(), map.get
                ("endRowKey").toString(), signaKeyList);
        List<String> collect = hbaseData.stream()
                .filter(item -> StringUtil.lastChart2Number(item.get("page_failure_delimiting=")) == 1
                        || StringUtil.lastChart2Number(item.get("first_page_delay_delimiting")) == 1
                        || StringUtil.lastChart2Number(item.get("dns_delay_delimiting")) == 1
                        || StringUtil.lastChart2Number(item.get("tcp_delay_upper_interf_delimiting")) == 1
                        || StringUtil.lastChart2Number(item.get("tcp_delay_lower_interf_delimiting")) == 1
                        || StringUtil.lastChart2Number(item.get("page_throughput_delimiting")) == 1
                )
                .sorted(comparator.reversed())
                .limit(10)
                .collect(Collectors.toList()).stream()
                .map(e -> {

                    return e.get("ecgi").toString();
                }).collect(Collectors.toList());


        return collect;

    }
    /*
     *Phoenix 改造成原生Hbase
     * add by liukang
     */
    @Override
    public  Map<String,Object>getUserCasePhoenix (Map<String, Object> map) {
      // 对rpt_cus_ser_up_nor_15http表进行三次查询，生成三个list
        Map<String,Object> resultMap=new HashMap<String,Object>();
      //查询第一张表
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = rptcusserupnor15httpTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseDataOne = getHbaseData(tableName, map.get("startRowKey_sec_type_one").toString(), map.get
                    ("endRowKey_sec_type_one").toString(), rptcusserupnor15httpList);
           /* List<Map<String, Object>> hbaseDataTwo = getHbaseData(tableName, map.get("startRowKey_sec_type_two").toString(), map.get
                    ("endRowKey_sec_type_two").toString(), rptcusserupnor15httpList);
            List<Map<String, Object>> hbaseDataZero = getHbaseData(tableName, map.get("startRowKey_sec_type_zero").toString(), map.get
                    ("endRowKey_sec_type_zero").toString(), rptcusserupnor15httpList);*/
            allHbaseData.addAll(hbaseDataOne);
           /* allHbaseData.addAll(hbaseDataTwo);
            allHbaseData.addAll(hbaseDataZero);*/
        }
     //   log.info("cccccccc+allHbaseData+ccccccc"+allHbaseData);
       /* List<Map<String, Object>> collectOne = allHbaseData.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
               .collect(Collectors.toList());*/

       /* List<Map<String, Object>> hbaseDataTwo = getHbaseData(rptcusserupnor15httpTableName, map.get("startRowKey_sec_type_two").toString(), map.get
                ("endRowKey_sec_type_two").toString(), rptcusserupnor15httpList);
        List<Map<String, Object>> collectTwo = hbaseDataOne.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
                .collect(Collectors.toList());


        List<Map<String, Object>> hbaseDataZero = getHbaseData(rptcusserupnor15httpTableName, map.get("startRowKey_sec_type_zero").toString(), map.get
                ("endRowKey_sec_type_zero").toString(), rptcusserupnor15httpList);*/
        List<Map<String, Object>> collectZero = allHbaseData.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
                .collect(Collectors.toList());
       // 将三次查询的结果合并成一个list
      /*  hbaseDataOne.addAll(collectTwo);
        hbaseDataOne.addAll(hbaseDataZero);*/
      //  log.info("===============================================15Leng"+collectZero.size()+"list.toString"+collectZero.toString());
        //log.info("ccccccc+collectZero+ccccccc"+collectZero);
        //计算切片总和
        Float dns =new Float(0);
        Float core =new Float(0);
        Float terminal =new Float(0);
        Float wireless =new Float(0);
        Float sp =new Float(0);
        Float users=new Float(0);

        //计算总和 begin...
        Float oldDns =new Float(0);
        Float oldCore =new Float(0);
        Float oldTerminal =new Float(0);
        Float oldWireless =new Float(0);
        Float oldSp =new Float(0);
        Float oldUsers=new Float(0);
        Float sp2 =new Float(0);
        Float usersNew=new Float(0);
        //计算总和 end...


        String dnsAr[]={
             "dns_delimiting_dns_cause","dns_delay_delimiting_dns_cause"
        };

        String coreAr[]={
                "first_page_delay_delimiting_corenet_cause",
                "tcp_delay_upper_interf_corenet_cause",
                "page_throughput_delimiting_corenet_cause",
                "video_tcp_delay_upper_interf_corenet_cause"
        };
        String terminalAr[]={
                "first_page_delay_delimiting_terminal_cause",
                "tcp_delay_lower_interf_terminal_cause",
                "page_throughput_delimiting_terminal_cause",
                "video_tcp_delay_lower_interf_terminal_cause",
                "video_throughput_delimiting_terminal_cause"
        };
        String wirelessAr[]={
                "first_page_delay_delimiting_radio_cause",
                "tcp_delay_lower_interf_radio_cause",
                "page_throughput_delimiting_radio_cause",
                "video_tcp_delay_lower_interf_radio_cause",
                "video_throughput_delimiting_radio_cause"
        };
        String spAr[]={
               // "page_failure_delimiting_sp_cause",
                "first_page_delay_delimiting_sp_cause",
                "tcp_delay_upper_interf_sp_cause",
                "page_throughput_delimiting_sp_cause",
                "video_tcp_delay_upper_interf_sp_cause",
                "video_throughput_delimiting_sp_cause"
        };
        String usersAr[]={
                "dns_delimiting_user_cause",
              //  "page_failure_delimiting_user_cause",
                "first_page_delay_delimiting_user_cause",
                "dns_delay_delimiting_user_cause",
                "tcp_delay_lower_interf_user_cause",
                "page_throughput_delimiting_user_cause",
                "video_tcp_delay_lower_interf_user_cause",
                "video_throughput_delimiting_user_cause"
        };
        String sp2Ar[]={
                "page_failure_delimiting_sp_cause"//   > SP
        };
        String usersNewAr[]={
                "page_failure_delimiting_user_cause" // >用户
        };
        /*hbaseDataOne.forEach(map1->{
            Float dnsFt = translateUtile(map1,dnsAr);
            if(dnsFt>1){
                dns =dns+ dnsFt;
            }
          }
        );*/
        Float tmp = new Float(1);
           for (Map<String, Object> temp : collectZero) {
            if(translateUtile(temp,dnsAr)>=1){
               // dns = Float.sum(dns,translateUtile(temp,dnsAr));
            }
            if(translateUtile(temp,coreAr)>=1){
              //  core = Float.sum(core,translateUtile(temp,coreAr));
                core= Float.sum(core,tmp);
            }
            if(translateUtile(temp,terminalAr)>=1){
               // terminal = Float.sum(terminal,translateUtile(temp,terminalAr));
                terminal= Float.sum(terminal,tmp);
            }
            if(translateUtile(temp,wirelessAr)>=1){
                //wireless = Float.sum(wireless,translateUtile(temp,wirelessAr));
                wireless= Float.sum(wireless,tmp);
            }
            if(translateUtile(temp,spAr)>=1){
               // sp = Float.sum(sp,translateUtile(temp,spAr));
                sp= Float.sum(sp,tmp);
            }
            if(translateUtile(temp,usersAr)>=1){
              //  users = Float.sum(users,translateUtile(temp,usersAr));
                users= Float.sum(users,tmp);
            }
            //计算总和 begin ...
               oldDns = Float.sum(oldDns,translateUtile(temp,dnsAr));
               oldCore = Float.sum(oldCore,translateUtile(temp,coreAr));
               oldTerminal = Float.sum(oldTerminal,translateUtile(temp,terminalAr));
               oldWireless = Float.sum(oldWireless,translateUtile(temp,wirelessAr));
               oldSp = Float.sum(oldSp,translateUtile(temp,spAr));
               oldUsers = Float.sum(oldUsers,translateUtile(temp,usersAr));
               sp2 = Float.sum(sp2,translateUtile(temp,sp2Ar));
               usersNew = Float.sum(usersNew,translateUtile(temp,usersNewAr));
               // 计算总和 end..
        }
        /*resultMap.put("DNS",dns.toString());
        resultMap.put("CORE",core.toString());
        resultMap.put("TERMINAL",terminal.toString());
        resultMap.put("WIRELESS",wireless.toString());
        resultMap.put("SP",sp.toString());
        resultMap.put("USERS",users.toString());*/
        if(dns > 0){
            resultMap.put("DNS",dns.toString()) ;
        }
        if(core > 0) {
            resultMap.put("CORE", core.toString());
        }
        if(terminal > 0) {
            resultMap.put("TERMINAL", terminal.toString());
        }
        if(wireless > 0) {
            resultMap.put("WIRELESS",wireless.toString());
        }
        if(sp > 0) {
            resultMap.put("SP", sp.toString());
        }
        if(users > 0) {
            resultMap.put("USERS", users.toString());
        }
        //计算总和 begin...
        if(oldDns > 0){
            resultMap.put("OLDDNS",oldDns.toString()) ;
        }
        if(oldCore > 0) {
            resultMap.put("OLDCORE", oldCore.toString());
        }
        if(oldTerminal > 0) {
            resultMap.put("OLDTERMINAL", oldTerminal.toString());
        }
        if(oldWireless > 0) {
            resultMap.put("OLDWIRELESS",oldWireless.toString());
        }
        if(oldSp > 0) {
            resultMap.put("OLDSP", oldSp.toString());
        }
        if(oldUsers > 0) {
            resultMap.put("OLDUSERS", oldUsers.toString());
        }
        if(sp2 > 0) {
            resultMap.put("SP2", sp2.toString());
        }
        if(usersNew > 0) {
            resultMap.put("USERSNEW", usersNew.toString());
        }
        //计算总和 end...
      /*  log.info("CCCCCCCCCCCC+collectZero+CCCCCCCCCCCC "+" dns: "+dns+" core: "
                +core+" terminal: "+terminal+" wireless "+wireless+"  sp "+ sp+" users "+users);*/


        return resultMap;

    }

    /*
     *Phoenix 改造成原生Hbase
     */
    @Override
    public  Map<String,Object>getUserCasePhoenixPieChart (Map<String, Object> map) {
        // 对rpt_cus_ser_up_nor_15min表进行三次查询，生成三个list
        Map<String,Object> resultMap=new HashMap<String,Object>();
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = rptcusserupnor15httpTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseDataOne = getHbaseData(tableName, map.get("startRowKey_sec_type_one").toString(), map.get
                    ("endRowKey_sec_type_one").toString(), rptcusserupnor15httpList);
            /*List<Map<String, Object>> hbaseDataTwo = getHbaseData(tableName, map.get("startRowKey_sec_type_two").toString(), map.get
                    ("endRowKey_sec_type_two").toString(), rptcusserupnor15httpList);
            List<Map<String, Object>> hbaseDataZero = getHbaseData(tableName, map.get("startRowKey_sec_type_zero").toString(), map.get
                    ("endRowKey_sec_type_zero").toString(), rptcusserupnor15httpList);*/
            allHbaseData.addAll(hbaseDataOne);
            /*allHbaseData.addAll(hbaseDataTwo);
            allHbaseData.addAll(hbaseDataZero);*/
        }
      //  log.info("DDDDD+allHbaseData+DDDDD"+allHbaseData);
        List<Map<String, Object>> collectZero = allHbaseData.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
                .collect(Collectors.toList());
        // 将三次查询的结果合并成一个list
      /*  hbaseDataOne.addAll(collectTwo);
        hbaseDataOne.addAll(hbaseDataZero);*/
       // log.info("DDDDDDDDDDDD+collectZero+DDDDDDDDDDDD"+collectZero);
        //计算总和
        Float dns =new Float(0);
        Float core =new Float(0);
        Float terminal =new Float(0);
        Float wireless =new Float(0);
        Float sp =new Float(0);
        Float users=new Float(0);
        Float sp2 =new Float(0);
        Float usersNew=new Float(0);


        String dnsAr[]={
                "dns_delimiting_dns_cause","dns_delay_delimiting_dns_cause"
        };

        String coreAr[]={
                "first_page_delay_delimiting_corenet_cause",
                "tcp_delay_upper_interf_corenet_cause",
                "page_throughput_delimiting_corenet_cause",
                "video_tcp_delay_upper_interf_corenet_cause"
        };
        String terminalAr[]={
                "first_page_delay_delimiting_terminal_cause",
                "tcp_delay_lower_interf_terminal_cause",
                "page_throughput_delimiting_terminal_cause",
                "video_tcp_delay_lower_interf_terminal_cause",
                "video_throughput_delimiting_terminal_cause"
        };
        String wirelessAr[]={
                "first_page_delay_delimiting_radio_cause",
                "tcp_delay_lower_interf_radio_cause",
                "page_throughput_delimiting_radio_cause",
                "video_tcp_delay_lower_interf_radio_cause",
                "video_throughput_delimiting_radio_cause"
        };
        String spAr[]={
               // "page_failure_delimiting_sp_cause",   > SP
                "first_page_delay_delimiting_sp_cause",
                "tcp_delay_upper_interf_sp_cause",
                "page_throughput_delimiting_sp_cause",
                "video_tcp_delay_upper_interf_sp_cause",
                "video_throughput_delimiting_sp_cause"
        };
        String usersAr[]={
                "dns_delimiting_user_cause",
                //"page_failure_delimiting_user_cause",  >用户
                "first_page_delay_delimiting_user_cause",
                "dns_delay_delimiting_user_cause",
                "tcp_delay_lower_interf_user_cause",
                "page_throughput_delimiting_user_cause",
                "video_tcp_delay_lower_interf_user_cause",
                "video_throughput_delimiting_user_cause"
        };
        String sp2Ar[]={
                 "page_failure_delimiting_sp_cause"//   > SP
        };
        String usersNewAr[]={
                 "page_failure_delimiting_user_cause" // >用户
        };
        /*hbaseDataOne.forEach(map1->{
            Float dnsFt = translateUtile(map1,dnsAr);
            if(dnsFt>1){
                dns =dns+ dnsFt;
            }
          }
        );*/
        for (Map<String, Object> temp : collectZero) {
              dns = Float.sum(dns,translateUtile(temp,dnsAr));
              core = Float.sum(core,translateUtile(temp,coreAr));
              terminal = Float.sum(terminal,translateUtile(temp,terminalAr));
              wireless = Float.sum(wireless,translateUtile(temp,wirelessAr));
              sp = Float.sum(sp,translateUtile(temp,spAr));
              users = Float.sum(users,translateUtile(temp,usersAr));
              sp2 = Float.sum(sp2,translateUtile(temp,sp2Ar));
              usersNew = Float.sum(usersNew,translateUtile(temp,usersNewAr));

        }
       if(dns > 0){
        resultMap.put("DNS",dns.toString()) ;
       }
        if(core > 0) {
            resultMap.put("CORE", core.toString());
        }
        if(terminal > 0) {
            resultMap.put("TERMINAL", terminal.toString());
        }
        if(wireless > 0) {
        resultMap.put("WIRELESS",wireless.toString());
        }
        if(sp > 0) {
            resultMap.put("SP", sp.toString());
        }
        if(users > 0) {
            resultMap.put("USERS", users.toString());
        }
        if(sp2 > 0) {
            resultMap.put("SP2", sp2.toString());
        }
        if(usersNew > 0) {
            resultMap.put("USERSNEW", usersNew.toString());
        }
        /*log.info("DDDDDDDDDDDD+collectZero+DDDDDDDDDDDD "+" dns: "+dns+" core: "
                +core+" terminal: "+terminal+" wireless "+wireless
                +"  sp "+ sp+" users "+users+" sp2 "+sp2+" usersNew "+usersNew);*/


        return resultMap;

    }
    /*
     *合计
     */
    public Float translateUtile(Map<String, Object> map,String[] str){
        float temp=0;
        for(int i=0 ; i<str.length;i++){
            if(map.get(str[i]) != null){
                temp += Float.valueOf(map.get(str[i]).toString());
//                log.info(str[i]+":"+map.get(str[i]).toString());
            }
        }
      return temp;
    }
    /*
     *在MME层进行判断 是SP还是用户问题   这个方法不用了
     */
    @Override
    public  Map<String,Object> judgeMmeSpOrUser (Map<String, Object> map) {
        // 对rpt_cus_ser_up_nor_15min表进行三次查询，生成三个list
        Map<String,Object> resultMap=new HashMap<String,Object>();
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = rptcusserupnor15httpTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseDataOne = getHbaseData(tableName, map.get("startRowKey_sec_type_one").toString(), map.get
                    ("endRowKey_sec_type_one").toString(), rptcusserupnor15httpList);
           allHbaseData.addAll(hbaseDataOne);

        }
//        log.info("DDDDD+allHbaseData+DDDDD"+allHbaseData);
        List<Map<String, Object>> collectZero = allHbaseData.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
                .collect(Collectors.toList());

     //   log.info("DDDDDDDDDDDD+collectZero+DDDDDDDDDDDD"+collectZero);
        //计算总和
        Float dns =new Float(0);
        Float core =new Float(0);
        Float terminal =new Float(0);
        Float wireless =new Float(0);
        Float sp =new Float(0);
        Float users=new Float(0);

        String spAr[]={
                 "page_failure_delimiting_sp_cause"   // 0 SP

        };
        String usersAr[]={
               "page_failure_delimiting_user_cause" //  用户
    };
        /*hbaseDataOne.forEach(map1->{
            Float dnsFt = translateUtile(map1,dnsAr);
            if(dnsFt>1){
                dns =dns+ dnsFt;
            }
          }
        );*/
        for (Map<String, Object> temp : collectZero) {

            sp = Float.sum(sp,translateUtile(temp,spAr));
            users = Float.sum(users,translateUtile(temp,usersAr));

        }
//       log.info("FFFFFFFFF+collectZero+FFFFFFFFFFF "+"  sp "+ sp+" users "+users);
        if(sp > 0) {
            resultMap.put("SP", sp.toString());
        }
        if(users > 0) {
            resultMap.put("USERS", users.toString());
        }

        return resultMap;

    }
    /*
     *Phoenix 改造成原生Hbase
     * add by liukang
     */
    @Override
    public  List<Map<String,Object>> getUserCaseExceptionPhoenix (Map<String, Object> map) {
        // 对rpt_cus_ser_up_nor_http表进行三次查询，生成三个list
        Map<String,Object> resultMap=new HashMap<String,Object>();
        List<Map<String, Object>> allHbaseData = new ArrayList<Map<String, Object>>();
        List<String> rowkeyList = DateUtil.getAllDayRowKey(map, "");
        for (String rowkey : rowkeyList) {
            String startRowkey = rowkey.split("#")[0];
            String endRowkey = rowkey.split("#")[1];
            //分表
            String tableName = rptcusserupnor15httpTableName + "_" + rowkey.split("#")[2];
            List<Map<String, Object>> hbaseDataOne = getHbaseData(tableName, map.get("startRowKey_sec_type_one").toString(), map.get
                    ("endRowKey_sec_type_one").toString(), rptcusserupnor15httpList);
           /* List<Map<String, Object>> hbaseDataTwo = getHbaseData(tableName, map.get("startRowKey_sec_type_two").toString(), map.get
                    ("endRowKey_sec_type_two").toString(), rptcusserupnor15httpList);
            List<Map<String, Object>> hbaseDataZero = getHbaseData(tableName, map.get("startRowKey_sec_type_zero").toString(), map.get
                    ("endRowKey_sec_type_zero").toString(), rptcusserupnor15httpList);*/
            allHbaseData.addAll(hbaseDataOne);
            /*allHbaseData.addAll(hbaseDataTwo);
            allHbaseData.addAll(hbaseDataZero);*/
        }
       // log.info("EEEEEEEEE+allHbaseData+EEEEEEEEEE"+allHbaseData);
        List<Map<String, Object>> collectZero = allHbaseData.stream()
                .filter(item ->!"dns_delimiting_dns_cause".equals(item.get("dns_delimiting_dns_cause").toString()) )
                .sorted(comparator.reversed())
                .collect(Collectors.toList());

      //  log.info("EEEEEEEEEEE+collectZero+EEEEEEEEEEE"+collectZero);
        return collectZero;

    }


    /**
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @param keyList     传入null时候 ，随机生成key
     * @return
     */
    private List<Map<String, Object>> getHbaseData(String tableName, String startRowKey, String endRowKey,
                                                   List<String> keyList) {
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
                    valueList = Arrays.asList(split);
                }
                Map<String, Object> data = new HashMap<>();
                if (null != keyList && keyList.size() > 0) {
                    if (valueList.size() > 0) {
                        for (int i = 0; i < valueList.size(); i++) {
                            for (int j = 0; j < keyList.size(); j++) {
                                if (i == j) {
                                    data.put(keyList.get(j), valueList.get(i));
                                    continue;
                                }
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

            /* log.info(
                     "555555*****************5555555555+rList+"+rList.size()
                             +"数据查询表：table=" + tableName
                             + "  startRowKey=" + startRowKey
                             + "  endRowKey=" + endRowKey);*/
            return rList;
        } catch ( Exception e ) {
             /*log.info("没有表头的数据查询异常：table=" + tableName + "  startRowKey=" + startRowKey + "  endRowKey=" + endRowKey
                     +"555555*****************5555555555+rList"+rList);*/
        } finally {
            if (null != scanner)
                scanner.close();
        }
        return rList;
    }

    // 盲点库匹配
    public static List<Map<String, String>> blindDataMatch(String tableName, String startRowKey, String endRowKey,
                                                           List<String> keyList) {
        ResultScanner scanner = HBaseUtil.getScanner(tableName, startRowKey, endRowKey);
        List<Map<String, Object>> rList = new ArrayList<>();
        for (Result result : scanner) {
            List valueList = new ArrayList();
            for (Cell cell : result.rawCells()) {
               // log.info("value值得：" + Bytes.toString(cell.getValue()));
                String[] split = Bytes.toString(cell.getValue()).split(",");
                valueList = Arrays.asList(split);
            }
            Map<String, Object> keyMap = MapTransformBean.object2Map(new RPT_CUS_SER_SIGNALING_SPEC());
            Map<String, Object> data = new HashMap<>();
            if (valueList.size() > 0) {
                for (int i = 0; i < valueList.size(); i++) {
                    for (int j = 0; j < keyList.size(); j++) {
                        if (i == j) {
                            data.put(keyList.get(j), valueList.get(i));
                            continue;
                        }
                    }
                }
            }
            rList.add(data);
        }

        // 释放链接
        scanner.close();

        List<RPT_CUS_SER_SIGNALING_SPEC> beanList = new ArrayList();
        rList.forEach(e -> {
            RPT_CUS_SER_SIGNALING_SPEC o = (RPT_CUS_SER_SIGNALING_SPEC) MapTransformBean.map2Object(e,
                    RPT_CUS_SER_SIGNALING_SPEC.class);
            beanList.add(o);
        });
        //条件过滤
        List<RPT_CUS_SER_SIGNALING_SPEC> collect = beanList.stream().filter(e -> StringUtil.lastChart2Number(e.getProblem_delimit()) == 1
                && StringUtil.lastChart2Number(e.getProcedure_type()) == 20)
                .collect(Collectors.toList());
//      分组统计数量
        Map<String, Long> result = collect.stream().collect(Collectors.groupingBy
                (RPT_CUS_SER_SIGNALING_SPEC::getEcgi, Collectors.counting()));
        System.out.println("1.1=" + result);
        //1.3根据分组的key值对结果进行排序、放进另一个map中并输出 有序输出
        Map<String, Long> xMap = new LinkedHashMap<>();
        result.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue()) //reversed不生效
                .forEachOrdered(x -> xMap.put(x.getKey(), x.getValue()));
        System.out.println("1.3" + xMap);
//        取出前十个数据
        List<Map<String, String>> limit10Data = new ArrayList<>();
        xMap.entrySet().forEach(e -> {
            String key = e.getKey();
            Long value = e.getValue();
            Map<String, String> map = new HashMap<>();
            map.put("ecgi", key);
            map.put("count", value.toString());
            if (limit10Data.size() < 10) {
                limit10Data.add(map);
            }
        });
//        查询配合表 去匹配
        return limit10Data;

    }  //    频繁时间表匹配

}
