package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;



import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XmlUtil {


    public static List<Map<String, Object>> getCfgOrderIdList() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        try {
            DocumentBuilderFactory bdf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = bdf.newDocumentBuilder();
            String path = XmlUtil.class.getResource("/cfg_order_id.xml").getPath();
            System.out.println("path"+path);
           // Document document = db.parse(new File(path));
            Document document = db.parse(XmlUtil.class.getResourceAsStream("/cfg_order_id.xml"));
            NodeList list = document.getElementsByTagName(ConstantsUtil.cons_dialogue_result);
      
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                Map<String, Object> map = new HashMap<String, Object>();
                String order_id = getContentByTag(ConstantsUtil.cons_order_id,element);
                map.put("order_id", order_id);
                String result = getContentByTag(ConstantsUtil.cons_result,element);
                map.put("result", result);
                String jump = getContentByTag(ConstantsUtil.cons_jump,element);
                map.put("jump", jump);
                String advice = getContentByTag(ConstantsUtil.cons_advice,element);
                map.put("advice", advice);
                String dialogue = getContentByTag(ConstantsUtil.cons_dialogue,element);
                map.put("dialogue", dialogue);
                String out_code = getContentByTag(ConstantsUtil.cons_out_code,element);
                map.put("out_code", out_code);
                returnList.add(map);

            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

//        log.info("######诊断结果======" + returnList.toString());
        return returnList;
    }


    public static Map<String, String> getThresholdMap() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilderFactory bdf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = bdf.newDocumentBuilder();
            String path = XmlUtil.class.getResource("threshold.xml").getPath();
            System.out.println(" threshold path"+path);
           // Document document = db.parse(new File(path));
            Document document = db.parse(XmlUtil.class.getResourceAsStream("/threshold.xml"));
            NodeList list = document.getElementsByTagName(ConstantsUtil.cons_threshold_content);
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String first_detail_terminal =getContentByTag(ConstantsUtil.cons_first_detail_terminal,element);
                if (StringUtils.isEmpty(first_detail_terminal))
                    first_detail_terminal = "0";
                map.put("first_detail_terminal", first_detail_terminal);
                String first_detail_wireless = getContentByTag(ConstantsUtil.cons_first_detail_wireless,element);
                if (StringUtils.isEmpty(first_detail_wireless))
                    first_detail_wireless = "0";
                map.put("first_detail_wireless", first_detail_wireless);
                String first_detail_sp = getContentByTag(ConstantsUtil.cons_first_detail_sp,element);
                if (StringUtils.isEmpty(first_detail_sp))
                    first_detail_sp = "0";
                map.put("first_detail_sp", first_detail_sp);
                String first_detail_users = getContentByTag(ConstantsUtil.cons_first_detail_users,element);
                if (StringUtils.isEmpty(first_detail_users))
                    first_detail_users = "0";
                map.put("first_detail_users", first_detail_users);
                String detail_terminal = getContentByTag(ConstantsUtil.cons_detail_terminal,element);
                if (StringUtils.isEmpty(detail_terminal))
                    detail_terminal = "0";
                map.put("detail_terminal", detail_terminal);
                String detail_wireless = getContentByTag(ConstantsUtil.cons_detail_wireless,element);
                if (StringUtils.isEmpty(detail_wireless))
                    detail_wireless = "0";
                map.put("detail_wireless", detail_wireless);
                String detail_sp = getContentByTag(ConstantsUtil.cons_detail_sp,element);
                if (StringUtils.isEmpty(detail_sp))
                    detail_sp = "0";
                map.put("detail_sp", detail_sp);
                String detail_users = getContentByTag(ConstantsUtil.cons_detail_users,element);
                if (StringUtils.isEmpty(detail_users))
                    detail_users = "0";
                map.put("detail_users", detail_users);
                String section_terminal = getContentByTag(ConstantsUtil.cons_section_terminal,element);
                if (StringUtils.isEmpty(section_terminal))
                    section_terminal = "0";
                map.put("section_terminal", section_terminal);
                String section_wireless = getContentByTag(ConstantsUtil.cons_section_wireless,element);
                if (StringUtils.isEmpty(section_wireless))
                    section_wireless = "0";
                map.put("section_wireless", section_wireless);
                String section_sp = getContentByTag(ConstantsUtil.cons_section_sp,element);
                if (StringUtils.isEmpty(section_sp))
                    section_sp = "0";
                map.put("section_sp", section_sp);
                String section_users = getContentByTag(ConstantsUtil.cons_section_users,element);
                if (StringUtils.isEmpty(section_users))
                    section_users = "0";
                map.put("section_users", section_users);
                String radio_and_user = getContentByTag(ConstantsUtil.cons_radio_and_user,element);
                if (StringUtils.isEmpty(radio_and_user))
                    radio_and_user = "0";
                map.put("radio_and_user", radio_and_user);
            }
        } catch ( Exception e ) {
        	 e.printStackTrace();
        }

        return map;
    }

    public static String getContentByTag(String tag,Element element)
    {
        String returnFlag="";
        if (null != element.getElementsByTagName(tag) && (null != element.getElementsByTagName
                (tag).item(0) && null != element.getElementsByTagName(tag).item(0)
                .getFirstChild() && null != element.getElementsByTagName(tag).item(0).getFirstChild()
                .getNodeValue()))
            returnFlag = element.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
        return returnFlag;
    }


 /*   public static void main(String[] args) {

       Map<String, String> getThresholdMap =getThresholdMap();
 	   System.out.println("getThresholdMap "+getThresholdMap);

    }*/

}
