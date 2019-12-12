package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;




import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


public class HBaseUtil {

//    private static final String TABLE_NAME = "demo_table";

    public static Configuration conf = null;
    public HTable table = null;
    public HBaseAdmin admin = null;

    static {
    	// 这个配置文件主要是记录 kerberos的相关配置信息，例如KDC是哪个IP？默认的realm是哪个？
    	// 如果没有这个配置文件这边认证的时候肯定不知道KDC的路径喽
    	// 这个文件也是从远程服务器上copy下来的
    	//System.setProperty("java.security.krb5.conf","/etc/krb5.conf");
        conf = HBaseConfiguration.create();
       System.out.println(conf.get("hbase.zookeeper.quorum"));
     /*   conf.set("hadoop.security.authentication","Kerberos");
        // 这个hbase.keytab也是从远程服务器上copy下来的, 里面存储的是密码相关信息
        // 这样我们就不需要交互式输入密码了
        conf.set("keytab.file","/weblogic/npodo.keytab");
        // 这个可以理解成用户名信息，也就是Principal
        conf.set("kerberos.principal","npodo");
        UserGroupInformation.setConfiguration(conf);
        try{
        	UserGroupInformation.loginUserFromKeytab("npodo","/weblogic/npodo.keytab");
          }catch(IOException e ){
        // TODO Auto-generated catch block
	      e.printStackTrace();
         }*/
    }


//     public static


    /**
     * 创建表
     *
     * @param tableName
     * @param familys
     * @throws Exception
     */
    public static void creatTable(String tableName, String[] familys)
            throws Exception {
        HBaseAdmin admin = new HBaseAdmin(conf);
        if (admin.tableExists(tableName)) {
            System.out.println("表已经存在!");
        } else {
            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            for (int i = 0; i < familys.length; i++) {
                tableDesc.addFamily(new HColumnDescriptor(familys[i]));
            }
            admin.createTable(tableDesc);
            System.out.println("创建表 " + tableName + " ok.");
        }
    }

    /**
     * 删除表
     *
     * @param tableName
     * @throws Exception
     */
    public static void deleteTable(String tableName) throws Exception {
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            System.out.println("删除表 " + tableName + " ok.");
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增一条记录
     *
     * @param tableName
     * @param rowKey
     * @param family
     * @param qualifier
     * @param value
     * @throws Exception
     */
    public static void addRecord(String tableName, String rowKey,
                                 String family, String qualifier, String value) throws Exception {
        try {
            HTable table = new HTable(conf, tableName);
            Put put = new Put(Bytes.toBytes(rowKey));
            put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
                    Bytes.toBytes(value));
            table.put(put);
            System.out.println("插入数据 " + rowKey + " 到表 "
                    + tableName + " ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     *
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public static void delRecord(String tableName, String rowKey)
            throws IOException {
        HTable table = new HTable(conf, tableName);
        List list = new ArrayList();
        Delete del = new Delete(rowKey.getBytes());
        list.add(del);
        table.delete(list);
        System.out.println("删除表 " + rowKey + " ok.");
    }

    /**
     * 根据rowkey 查询数据
     *
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public static void getOneRecord(String tableName, String rowKey)
            throws IOException {
        HTable table = new HTable(conf, tableName);
        Get get = new Get(rowKey.getBytes());
        org.apache.hadoop.hbase.client.Result rs = table.get(get);
        for (KeyValue kv : rs.raw()) {
            System.out.print(new String(kv.getRow()) + " ");
            System.out.print(new String(kv.getFamily()) + ":");
            System.out.print(new String(kv.getQualifier()) + " ");
            System.out.print(kv.getTimestamp() + " ");
            System.out.println(new String(kv.getValue()));
        }
    }

    /**
     * 全表查询
     *
     * @param tableName
     */
    public static ResultScanner getAllRecord(String tableName) {
        try {
            HTable table = new HTable(conf, tableName);
            Scan s = new Scan();
            ResultScanner ss = table.getScanner(s);
           /* for (Result r : ss) {
                for (KeyValue kv : r.raw()) {
                    System.out.print(new String(kv.getRow()) + " ");
                    System.out.print(new String(kv.getFamily()) + ":");
                    System.out.print(new String(kv.getQualifier()) + " ");
                    System.out.print(kv.getTimestamp() + " ");
                    System.out.println(new String(kv.getValue()));
                }
                return ss;
            }*/
            return ss;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量检索数据.
     *
     * @param tableName   表名
     * @param startRowKey 起始RowKey
     * @param endRowKey   终止RowKey
     * @return ResultScanner实例
     */
    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey) {
        try {
            HTable table = new HTable(conf, tableName);
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRowKey));
            scan.setStopRow(Bytes.toBytes(endRowKey));
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> getTable(String tableName, List<String> startRowKey) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        try {

            HTable table = new HTable(conf, tableName);
            Scan scan = new Scan();
            List<String> list = new ArrayList<>();


            Object[] star = startRowKey.toArray();
//            log.info("$$$$$$$$$$star$$$$$$$$$:"+star.length);
            //统计RowKey重复个数
            Map<String, String> maps = new HashMap<>();
            for (int i = 0; i < startRowKey.toArray().length; i++) {
            	//修改
                if (maps.get(star[i].toString())==null) {
                    maps.put(star[i].toString(), "1");
                } else {
                    maps.put(star[i].toString(), String.valueOf(Long.valueOf(maps.get(star[i])) + 1));
                }
            }
//            log.info("%%%%%%%%%%maps%%%%%%%%%:"+maps+"maps.size(): "+maps.size());
            List<Map.Entry<String, String>> lists = new ArrayList<Map.Entry<String, String>>(maps.entrySet());
            Collections.sort(lists, new Comparator<Map.Entry<String, String>>() {
                //降序排序
                public int compare(Map.Entry<String, String> o1,
                                   Map.Entry<String, String> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            //添加排序后前3个RowKey
            int i = 1;
            List<String> listTop = new ArrayList<>();
            for (Map.Entry<String, String> mapping : lists) {
                listTop.add(mapping.getKey());
                if (i == 3) break;
                i++;
            }
            for (String str : listTop) {
                Get get = new Get(Bytes.toBytes(str));
                org.apache.hadoop.hbase.client.Result result = table.get(get);
                for (Cell cl : result.rawCells()) {
                    String value = Bytes.toString(CellUtil.cloneValue(cl));
                    list.add(value);
                }
            }
            Object[] startRow = listTop.toArray();
            Object[] end = list.toArray();

            for (int j = 0; j < end.length; j++) {
                Map<String, Object> map = new HashMap<>();
                map.put("service_name", end[j].toString().split(",")[6]);
                map.put("service_type", startRow[j]);
                listMap.add(map);
            }

            return listMap;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return listMap;
    }

    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey,
                                           FilterList filterList) {
        try {
            HTable table = new HTable(conf, tableName);
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRowKey));
            scan.setStopRow(Bytes.toBytes(endRowKey));
            scan.setFilter(filterList);
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }


}