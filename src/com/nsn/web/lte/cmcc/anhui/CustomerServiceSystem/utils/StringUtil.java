package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;


public class StringUtil {


    public static Integer lastChart2Number(Object o) {
        String s = o.toString();
        if(s.equals("-1")){

            return  Integer.valueOf(o.toString());
        }
       // add by liukang 判断字段的值为空的话变为 65535
        if("".equals(s)|| null==s){
            return Integer.valueOf("65535");
        }else {
            String substring = s.substring(s.length() - 1, s.length());
            Integer integer = Integer.valueOf(substring);
            return integer;
        }


    }

    public static void main(String[] args) {

        System.out.println(StringUtil.lastChart2Number("2234343"));
    }
}
