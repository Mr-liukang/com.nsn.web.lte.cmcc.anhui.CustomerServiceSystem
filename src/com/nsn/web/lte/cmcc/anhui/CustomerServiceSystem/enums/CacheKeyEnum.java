package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums;

/**
 * 业务类型接口 查询参数枚举
 *
 * @author lly
 * @date 2018年08月02日09:22:21
 */
public enum CacheKeyEnum {


    SINGADATA("信令面缓存key","singadata"),
    HTTP("HTTPkey","http"),
    VIDEO("VIDEO缓存key","video"),
    DNS("DNS缓存key","dns"),
    CACAHEKEY("缓存管理器key","ciCache");


    private String info;
    private String code;

    CacheKeyEnum(String info, String code) {
        this.info = info;
        this.code = code;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
