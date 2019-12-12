package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;

import java.util.List;
import java.util.Map;

public class TestUtils {

   public static void main(String[] args) {
	   List<Map<String,Object>> cfgOrderIdList = XmlUtil.getCfgOrderIdList();
	   System.out.println(cfgOrderIdList);
}
   
}
