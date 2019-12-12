package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.enums;

import lombok.Data;

/**
 * 定界结果枚举
 *
 * @author lly
 * @date 2018年08月02日09:22:21
 */

public enum JumpResultEnum {

//  信令面定界
    SIGNA_ONE("信令面-1核心网问题", 1),
    SIGNA_TWO("信令面-2用户欠费", 2),
    LIMIT_SPEED("信令面3-限速", 3),
    SIGNA_FOUR("信令面-4用户关闭4G数据业务或终端问题", 4),
    SIGNA_FIVE("信令面-5用户配置（PDN）", 5),
    SIGNA_SIX("信令面-6终端问题", 6),
    SIGNA_SEVEN("信令面-7无线问题-匹配到盲点库", 7),
    SIGNA_EIGHT("信令面-8无线问题-未匹配到盲点库", 8),
//  用户面定界
    USER_TERMINAL_CODE("终端原因", 9),
    USER_WIRELESS_MATCH_CODE("无线问题-匹配到忙疯Ian库", 10),
    USER_WIRELESS_UNMATCH_CODE("无线问题-未匹配到盲点库", 11),
    USER_CORE_NET_CODE("用户面-核心网问题", 12),
    USER_DNS_CODE("用户面-dns问题", 14),
    GROUNDLESS("无理由申诉", 15),
    USER_SP_CODE("用户面-sp问题", 16),
    USER_USERS_CODE("用户问题", 17),
    IpranA("ipran-a问题", 18),
    IpranB("ipran-b问题", 19),
    //新增Nat和Nat以上问题
    NAT_CODE("Nat问题", 20),
    NAT_UP_CODE("Nat以上问题", 21),
    CHECK_NORMAL("无明显问题", 22),
    SPEED_LIMIT("无明显问题", 23),
    USER_WIRELESS_BUSY("无线问题", 24),
    USER_WIRELESS_POOR_COVER("无线问题", 25),
    USER_WIRELESS_FAULT("无线问题", 26),
    USER_WIRELESS_UNMATCH_MANGDIAN("无线问题", 27),
    //add by liukang 北京用户问题原因新加
    USER_NEW_CODE("用户问题", 28);



    private String info;
    private Integer code;

    JumpResultEnum(String info, Integer code) {
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
