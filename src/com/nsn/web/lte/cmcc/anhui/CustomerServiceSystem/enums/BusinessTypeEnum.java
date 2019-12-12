package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums;

/**
 * 业务类型接口 查询参数枚举
 *
 * @author lly
 * @date 2018年08月02日09:22:21
 */
public enum BusinessTypeEnum {


    DOLOCAL("do",0),
    FIRSTLINE("一线客服",1),
    SECONDLINE("二线客服",2);

    private String info;
    private Integer code;

    BusinessTypeEnum(String info, Integer code) {
        this.info = info;
        this.code = code;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
