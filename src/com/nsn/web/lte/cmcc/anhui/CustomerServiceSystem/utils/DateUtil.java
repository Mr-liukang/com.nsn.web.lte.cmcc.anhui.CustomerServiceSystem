package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class DateUtil {


    /**
     * 获取指定时间段内的整点时间
     *
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public static List<String> findDates(String start, String end) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date dBegin = sdf.parse(start);
        Date dEnd = sdf.parse(end);
        List<Date> lDate = new ArrayList();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
// 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
// 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
// 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.HOUR_OF_DAY, 1);
            lDate.add(calBegin.getTime());
        }
        List<String> collect = lDate.stream().map(date -> {
            String format = sdf.format(date);
            return format.substring(0, format.lastIndexOf(":") - 2)
                    .replaceAll("-", "").replace(" ", "").replaceAll(":", "") + "0000";

        }).collect(Collectors.toList());

        if (collect.size() > 2) {
            collect = collect.subList(0, collect.size() - 2);
        } else {
            collect = collect.subList(0, collect.size() - 1);
        }
        return collect;
    }

    public static List<String> getBetweenDate(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 声明保存日期集合
        List<String> list = new ArrayList<String>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime() <= endDate.getTime()) {
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate = calendar.getTime();
            }
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return list;
    }



    /**
     *
     * @param map
     * @param flag 1:到分钟，其它到秒
     * @return
     */
    public static List<String> getAllDayRowKey(Map<String, Object> map,String flag) {
        String sdate= (String) map.get("sdate");
        String edate= (String) map.get("edate");
        String phone= (String) map.get("phone");
        String startTime = sdate.substring(0, 8);
        String endTime = edate.substring(0, 8);
        List<String> dateList = getBetweenDate(startTime, endTime);
        List<String> rowKeyList = new ArrayList<String>();
        int size = dateList.size();
        for (int i = 0; i < size; i++) {
            String rowkeyStr = "";
            String srowkey = "";
            String erowkey = "";
            String temp=dateList.get(i);
            if(flag.equals("1"))
            {
                if (size <= 1) {
                    srowkey = phone + "_" + sdate.substring(0,12);
                    erowkey = phone + "_" + edate.substring(0,12);
                } else {
                    //第一天
                    if (i == 0) {
                        srowkey = phone + "_" + sdate.substring(0,12);
                        erowkey = phone + "_" + temp+ "2359";
                    }
                    //最后一天
                    else if (i == size - 1) {
                        srowkey = phone + "_" + temp+ "0000";
                        erowkey = phone + "_" + edate.substring(0,12);
                    }
                    //中间的天数
                    else {
                        srowkey = phone + "_" + temp + "0000";
                        erowkey = phone + "_" + temp + "2359";
                    }
                }
                //开始rowKey和结束rowKey以#分割
                rowKeyList.add(srowkey+"#"+erowkey+"#"+temp);
            }
            else
            {
                if (size <= 1) {
                    srowkey = phone + "_" + sdate;
                    erowkey = phone + "_" + edate;
                } else {
                    //第一天
                    if (i == 0) {
                        srowkey = phone + "_" + sdate;
                        erowkey = phone + "_" + temp+ "235959";
                    }
                    //最后一天
                    else if (i == size - 1) {
                        srowkey = phone + "_" + temp+ "000000";
                        erowkey = phone + "_" + edate;
                    }
                    //中间的天数
                    else {
                        srowkey = phone + "_" + temp + "000000";
                        erowkey = phone + "_" + temp + "235959";
                    }
                }
                //开始rowKey和结束rowKey以#分割
                rowKeyList.add(srowkey+"#"+erowkey+"#"+temp);
            }

        }
        return rowKeyList;
    }

    /**
     * 判断时间的先后
     * @param date
     * @param splitDate
     * @return
     */
    public static String jugeTimeBeforOrAfter(Date date,String splitDate)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String startDate=new SimpleDateFormat("yyyyMMddhhmmss").format(date);
        String flag="0";
        try {
            long diff =  dateFormat.parse(startDate).getTime()-dateFormat.parse(splitDate).getTime();
            if (diff >= 0) {
                flag="1";
            }
        } catch ( Exception e ) {
            flag="0";
        }
        return flag;
    }




    /**
     *当前时间的前几天的日期
     * @param d
     * @param day
     * @return
     */
    public  static String getDateBefore(Date d, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - day);
        return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 实现日期的加减x分钟
     * @param day
     * @param x
     * @return
     */

    public static String dateMinut(String day, int x){

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, x);// 24小时制
        date = cal.getTime();
        return format.format(date);
    }

    /**
     * 0:相同 小于0:a<b  大于0:a></b>b
     * @param a
     * @param b
     * @return
     */
    public static int compareStringDate(String a, String b){

        int result=a.compareTo(b);
        return result;
    }



    public static void main(String[] args) {

        //System.out.println(getHour(new Date()));

    }

}
