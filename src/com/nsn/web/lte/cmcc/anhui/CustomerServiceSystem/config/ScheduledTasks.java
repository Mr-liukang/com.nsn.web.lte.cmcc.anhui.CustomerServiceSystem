package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.config;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;



//spring自带的定时调度任务
public class ScheduledTasks {
   // @Autowired
    //private PostgresServiceI postgresServiceI;
    //@Autowired
  //  private CustomerServiceSupportMapper mapper;

    public static List<Map<String, Object>> allCiData=new ArrayList<Map<String, Object>>();
    public static List<Map<String, Object>> currentAlarmData=new ArrayList<Map<String, Object>>();
    public static List<Map<String, Object>> historyAlarmData=new ArrayList<Map<String, Object>>();


    public void reportCurrentTime() {
//        log.info("Scheduling Tasks Examples: The time is now " + dateFormat().format(new Date()));
    }

    //每1分钟执行一次  [秒] [分] [小时] [日] [月] [周] [年]
//    @Scheduled(cron = "0 0 17 * * ?  ")
//    @Scheduled(cron = "0 */1 *  * * * ")
//    public void reportCurrentByCron(){
//        List<Map<String, Object>> ci = postgresServiceI.getCi();
//        log.info("启动定时任务》》》》》 " + dateFormat ().format (new Date ())+"静态表大小》》"+ci.size());
//    }

    public void reportCurrentByCronS() {
        //List<Map<String, Object>> ci = postgresServiceI.getCiCache();
        // modfiy by liukang  mapper.getCiFromHbase();
        allCiData =new ArrayList<>();
//        log.info("启动定时任务》》》》》 " + dateFormat().format(new Date()) + "定时静态表大小》》" + allCiData.size());
    }

    private SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }

}