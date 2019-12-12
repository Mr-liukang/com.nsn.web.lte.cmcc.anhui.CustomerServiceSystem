package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model;



public class CfgNatPgw {
    // @Excel(name = "rowkey")
    private String rowkey;
    // @Excel(name = "nat_name")
    private String nat_name;
    // @Excel(name = "pgw_name")
    private String pgw_name;
    // @Excel(name = "pgw_ip")
    private String pgw_ip;
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public String getNat_name() {
		return nat_name;
	}
	public void setNat_name(String nat_name) {
		this.nat_name = nat_name;
	}
	public String getPgw_name() {
		return pgw_name;
	}
	public void setPgw_name(String pgw_name) {
		this.pgw_name = pgw_name;
	}
	public String getPgw_ip() {
		return pgw_ip;
	}
	public void setPgw_ip(String pgw_ip) {
		this.pgw_ip = pgw_ip;
	}

}
