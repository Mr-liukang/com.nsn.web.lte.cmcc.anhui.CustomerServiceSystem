package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;


import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;







public class ConstantsUtil {
    //常量定义为了诊断结果
    public static final String cons_dialogue_result="dialogue_result";
    public static final String cons_order_id="order_id";
    public static final String cons_result="result";
    public static final String cons_jump="jump";
    public static final String cons_advice="advice";
    public static final String cons_dialogue="dialogue";
    public static final String cons_out_code="out_code";
    //常量定义为了阈值
    public static final String cons_threshold_content="threshold_content";
    public static final String cons_first_detail_terminal="first_detail_terminal";
    public static final String cons_first_detail_wireless="first_detail_wireless";
    public static final String cons_first_detail_sp="first_detail_sp";
    public static final String cons_first_detail_users="first_detail_users";

    public static final String cons_detail_terminal = "detail_terminal";
    public static final String cons_detail_wireless="detail_wireless";
    public static final String cons_detail_sp="detail_sp";
    public static final String cons_detail_users="detail_users";

    public static final String cons_section_terminal="section_terminal";
    public static final String cons_section_wireless="section_wireless";
    public static final String cons_section_sp="section_sp";
    public static final String cons_section_users="section_users";

    public static final String cons_radio_and_user="radio_and_user";

    public static  Float first_detail_terminal = 0f;
    public static  Float first_detail_wireless = 0f;
    public static  Float first_detail_sp = 0f;
    public static  Float first_detail_users = 0f;

    public static  Float detail_terminal = 0f;
    public static  Float detail_wireless = 0f;
    public static  Float detail_sp = 0f;
    public static  Float detail_users = 0f;

    public static  Float section_terminal = 0f;
    public static  Float section_wireless = 0f;
    public static  Float section_sp = 0f;
    public static  Float section_users = 0f;

    public static  Float radio_and_user = 0f;

    public static List<Map<String, Object>> resultList=new ArrayList<Map<String, Object>>();
    static {
        try {
            //某一类详单问题>指定值直接输出该问题
            //获得门限值
            Map<String, String> map=XmlUtil.getThresholdMap();
            System.out.println("ConstantsUtil-->map"+map);
            first_detail_terminal = Float.parseFloat(map.get("first_detail_terminal"));
            first_detail_wireless =Float.parseFloat(map.get("first_detail_wireless"));
            first_detail_sp = Float.parseFloat(map.get("first_detail_sp"));
            first_detail_users = Float.parseFloat(map.get("first_detail_users"));
            //#详单判断值
            detail_terminal = Float.parseFloat(map.get("detail_terminal"));
            detail_wireless = Float.parseFloat(map.get("detail_wireless"));
            detail_sp = Float.parseFloat(map.get("detail_sp"));
            detail_users = Float.parseFloat(map.get("detail_users"));

            //#切片判断值
            section_terminal = Float.parseFloat(map.get("section_terminal"));
            section_wireless = Float.parseFloat(map.get("section_wireless"));
            section_sp = Float.parseFloat(map.get("section_sp"));
            section_users = Float.parseFloat(map.get("section_users"));

            radio_and_user = Float.parseFloat(map.get("radio_and_user"));
            resultList=XmlUtil.getCfgOrderIdList();
           

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
    
   
}
