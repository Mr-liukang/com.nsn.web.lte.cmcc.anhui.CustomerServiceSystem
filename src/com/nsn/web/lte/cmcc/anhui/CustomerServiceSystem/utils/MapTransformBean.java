package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;


import org.apache.commons.beanutils.PropertyUtilsBean;

import com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model.RPT_CUS_SER_SIGNALING_SPEC;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapTransformBean {


    /**
     * 实体对象转成Map
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Map转成实体对象
     * @param map map实体对象包含属性
     * @param clazz 实体对象类型
     * @return
     */
    public static Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> params = new LinkedHashMap<String, Object>(0);
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (!"class".equals(name)) {
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;

    }
        public static void main(String[] args) {
        RPT_CUS_SER_SIGNALING_SPEC ss = new RPT_CUS_SER_SIGNALING_SPEC();
//        ss.setMsisdn("111");

//
//        Map<String,Object> map  = new HashMap<>();
//        map.put("msisdn","33");

//        System.out.println(map2Object(map, RPT_CUS_SER_SIGNALING_SPEC.class));
        System.out.println(object2Map(ss));
        System.out.println(beanToMap(ss));

    }
}
