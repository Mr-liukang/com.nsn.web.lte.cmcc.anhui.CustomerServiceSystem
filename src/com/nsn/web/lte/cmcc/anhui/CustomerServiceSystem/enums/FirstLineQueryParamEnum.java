package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums;

/**
 * 客服一线接口 查询参数枚举
 *
 * @author lly
 * @date 2018年08月02日09:22:21
 */
public enum FirstLineQueryParamEnum {


    IsLimit("限速标识",1);

    private String info;
    private Integer code;

    FirstLineQueryParamEnum(String info, Integer code) {
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
