package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model;


public class HttpDetail {
    /*// @Excel(name = "rowkey")
    private String rowkey;*/
    //// @Excel(name = "inter")
    private String inter;
   // // @Excel(name = "imsi")
    private String imsi;
   // // @Excel(name = "msisdn")
    private String msisdn;
   // // @Excel(name = "imei")
    private String imei;
   // // @Excel(name = "dest_ip")
    private String dest_ip;
   // // @Excel(name = "dest_port")
    private String dest_port;
    // @Excel(name = "src_ip")
    private String src_ip;
    // @Excel(name = "src_port")
    private String src_port;
    // @Excel(name = "sgw_ip_addr")
    private String sgw_ip_addr;
    // @Excel(name = "mme_ip_addr")
    private String mme_ip_addr;
    // @Excel(name = "pgw_ip_addr")
    private String pgw_ip_addr;
    // @Excel(name = "ecgi")
    private String ecgi;
    // @Excel(name = "rat")
    private String rat;
    // @Excel(name = "protocol_id")
    private String protocol_id;
    // @Excel(name = "service_type")
    private String service_type;
    // @Excel(name = "start_time")
    private String start_time;
    // @Excel(name = "end_time")
    private String end_time;
    // @Excel(name = "duration")
    private String duration;
    // @Excel(name = "inputoctets")
    private String inputoctets;
    // @Excel(name = "output_octets")
    private String output_octets;
    // @Excel(name = "recordclosecause")
    private String recordclosecause;
    // @Excel(name = "mme_ue_s1ap_id")
    private String mme_ue_s1ap_id;
    // @Excel(name = "enb_ue_s1ap_id")
    private String enb_ue_s1ap_id;
    // @Excel(name = "home_province")
    private String home_province;
    // @Excel(name = "l4")
    private String l4;
    // @Excel(name = "tcp_ooo_ul_packets")
    private String tcp_ooo_ul_packets;
    // @Excel(name = "tcp_ooo_dl_packets")
    private String tcp_ooo_dl_packets;
    // @Excel(name = "tcp_retrans_ul_packets")
    private String tcp_retrans_ul_packets;
    // @Excel(name = "tcp_retrans_dl_packets")
    private String tcp_retrans_dl_packets;
    // @Excel(name = "tcpsetupresponsedelay")
    private String tcpsetupresponsedelay;
    // @Excel(name = "tcpsetupackdelay")
    private String tcpsetupackdelay;
    // @Excel(name = "delay_setup_firsttransaction")
    private String delay_setup_firsttransaction;
    // @Excel(name = "delay_firsttransaction_firstrespackt")
    private String delay_firsttransaction_firstrespackt;
    // @Excel(name = "tcp_connetstate")
    private String tcp_syn_number;
    // @Excel(name = "tcp_connetstate")
    private String tcp_connetstate;
    // @Excel(name = "sessionstopflag")
    private String sessionstopflag;
    // @Excel(name = "host")
    private String host;
    // @Excel(name = "http_action")
    private String http_action;
    // @Excel(name = "httpstatus")
    private String httpstatus;
    // @Excel(name = "resp_delay")
    private String resp_delay;
    // @Excel(name = "tcp_syn_time")
    private String tcp_syn_time;
    // @Excel(name = "tcp_synack_time")
    private String tcp_synack_time;
    // @Excel(name = "tcp_ack_time")
    private String tcp_ack_time;
    // @Excel(name = "actiontime")
    private String actiontime;
    // @Excel(name = "firstpackettime")
    private String firstpackettime;
    // @Excel(name = "pageopentime")
    private String pageopentime;
    // @Excel(name = "lastpacktime")
    private String lastpacktime;
    // @Excel(name = "pagevolume")
    private String pagevolume;
    // @Excel(name = "last_ack_time")
    private String last_ack_time;
    // @Excel(name = "title")
    private String title;
    // @Excel(name = "firstfinacktime")
    private String firstfinacktime;
    // @Excel(name = "dnsquerytime")
    private String dnsquerytime;
    // @Excel(name = "dnsresponsetime")
    private String dnsresponsetime;
    // @Excel(name = "dnsflowid")
    private String dnsflowid;
    // @Excel(name = "firstscreenfintime")
    private String firstscreenfintime;
    // @Excel(name = "abnormal_sort")
    private String abnormal_sort;
    // @Excel(name = "page_failure_delimiting")
    private String page_failure_delimiting;
    // @Excel(name = "first_page_delay_delimiting")
    private String first_page_delay_delimiting;
    // @Excel(name = "dns_delay_delimiting")
    private String dns_delay_delimiting;
    // @Excel(name = "tcp_delay_upper_interf_delimiting")
    private String tcp_delay_upper_interf_delimiting;
    // @Excel(name = "tcp_delay_lower_interf_delimiting")
    private String tcp_delay_lower_interf_delimiting;
    // @Excel(name = "page_throughput_delimiting")
    private String page_throughput_delimiting;
    // @Excel(name = "app_sub_type")
    private String app_sub_type;
	public String getInter() {
		return inter;
	}
	public void setInter(String inter) {
		this.inter = inter;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getDest_ip() {
		return dest_ip;
	}
	public void setDest_ip(String dest_ip) {
		this.dest_ip = dest_ip;
	}
	public String getDest_port() {
		return dest_port;
	}
	public void setDest_port(String dest_port) {
		this.dest_port = dest_port;
	}
	public String getSrc_ip() {
		return src_ip;
	}
	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}
	public String getSrc_port() {
		return src_port;
	}
	public void setSrc_port(String src_port) {
		this.src_port = src_port;
	}
	public String getSgw_ip_addr() {
		return sgw_ip_addr;
	}
	public void setSgw_ip_addr(String sgw_ip_addr) {
		this.sgw_ip_addr = sgw_ip_addr;
	}
	public String getMme_ip_addr() {
		return mme_ip_addr;
	}
	public void setMme_ip_addr(String mme_ip_addr) {
		this.mme_ip_addr = mme_ip_addr;
	}
	public String getPgw_ip_addr() {
		return pgw_ip_addr;
	}
	public void setPgw_ip_addr(String pgw_ip_addr) {
		this.pgw_ip_addr = pgw_ip_addr;
	}
	public String getEcgi() {
		return ecgi;
	}
	public void setEcgi(String ecgi) {
		this.ecgi = ecgi;
	}
	public String getRat() {
		return rat;
	}
	public void setRat(String rat) {
		this.rat = rat;
	}
	public String getProtocol_id() {
		return protocol_id;
	}
	public void setProtocol_id(String protocol_id) {
		this.protocol_id = protocol_id;
	}
	public String getService_type() {
		return service_type;
	}
	public void setService_type(String service_type) {
		this.service_type = service_type;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getInputoctets() {
		return inputoctets;
	}
	public void setInputoctets(String inputoctets) {
		this.inputoctets = inputoctets;
	}
	public String getOutput_octets() {
		return output_octets;
	}
	public void setOutput_octets(String output_octets) {
		this.output_octets = output_octets;
	}
	public String getRecordclosecause() {
		return recordclosecause;
	}
	public void setRecordclosecause(String recordclosecause) {
		this.recordclosecause = recordclosecause;
	}
	public String getMme_ue_s1ap_id() {
		return mme_ue_s1ap_id;
	}
	public void setMme_ue_s1ap_id(String mme_ue_s1ap_id) {
		this.mme_ue_s1ap_id = mme_ue_s1ap_id;
	}
	public String getEnb_ue_s1ap_id() {
		return enb_ue_s1ap_id;
	}
	public void setEnb_ue_s1ap_id(String enb_ue_s1ap_id) {
		this.enb_ue_s1ap_id = enb_ue_s1ap_id;
	}
	public String getHome_province() {
		return home_province;
	}
	public void setHome_province(String home_province) {
		this.home_province = home_province;
	}
	public String getL4() {
		return l4;
	}
	public void setL4(String l4) {
		this.l4 = l4;
	}
	public String getTcp_ooo_ul_packets() {
		return tcp_ooo_ul_packets;
	}
	public void setTcp_ooo_ul_packets(String tcp_ooo_ul_packets) {
		this.tcp_ooo_ul_packets = tcp_ooo_ul_packets;
	}
	public String getTcp_ooo_dl_packets() {
		return tcp_ooo_dl_packets;
	}
	public void setTcp_ooo_dl_packets(String tcp_ooo_dl_packets) {
		this.tcp_ooo_dl_packets = tcp_ooo_dl_packets;
	}
	public String getTcp_retrans_ul_packets() {
		return tcp_retrans_ul_packets;
	}
	public void setTcp_retrans_ul_packets(String tcp_retrans_ul_packets) {
		this.tcp_retrans_ul_packets = tcp_retrans_ul_packets;
	}
	public String getTcp_retrans_dl_packets() {
		return tcp_retrans_dl_packets;
	}
	public void setTcp_retrans_dl_packets(String tcp_retrans_dl_packets) {
		this.tcp_retrans_dl_packets = tcp_retrans_dl_packets;
	}
	public String getTcpsetupresponsedelay() {
		return tcpsetupresponsedelay;
	}
	public void setTcpsetupresponsedelay(String tcpsetupresponsedelay) {
		this.tcpsetupresponsedelay = tcpsetupresponsedelay;
	}
	public String getTcpsetupackdelay() {
		return tcpsetupackdelay;
	}
	public void setTcpsetupackdelay(String tcpsetupackdelay) {
		this.tcpsetupackdelay = tcpsetupackdelay;
	}
	public String getDelay_setup_firsttransaction() {
		return delay_setup_firsttransaction;
	}
	public void setDelay_setup_firsttransaction(String delay_setup_firsttransaction) {
		this.delay_setup_firsttransaction = delay_setup_firsttransaction;
	}
	public String getDelay_firsttransaction_firstrespackt() {
		return delay_firsttransaction_firstrespackt;
	}
	public void setDelay_firsttransaction_firstrespackt(String delay_firsttransaction_firstrespackt) {
		this.delay_firsttransaction_firstrespackt = delay_firsttransaction_firstrespackt;
	}
	public String getTcp_syn_number() {
		return tcp_syn_number;
	}
	public void setTcp_syn_number(String tcp_syn_number) {
		this.tcp_syn_number = tcp_syn_number;
	}
	public String getTcp_connetstate() {
		return tcp_connetstate;
	}
	public void setTcp_connetstate(String tcp_connetstate) {
		this.tcp_connetstate = tcp_connetstate;
	}
	public String getSessionstopflag() {
		return sessionstopflag;
	}
	public void setSessionstopflag(String sessionstopflag) {
		this.sessionstopflag = sessionstopflag;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getHttp_action() {
		return http_action;
	}
	public void setHttp_action(String http_action) {
		this.http_action = http_action;
	}
	public String getHttpstatus() {
		return httpstatus;
	}
	public void setHttpstatus(String httpstatus) {
		this.httpstatus = httpstatus;
	}
	public String getResp_delay() {
		return resp_delay;
	}
	public void setResp_delay(String resp_delay) {
		this.resp_delay = resp_delay;
	}
	public String getTcp_syn_time() {
		return tcp_syn_time;
	}
	public void setTcp_syn_time(String tcp_syn_time) {
		this.tcp_syn_time = tcp_syn_time;
	}
	public String getTcp_synack_time() {
		return tcp_synack_time;
	}
	public void setTcp_synack_time(String tcp_synack_time) {
		this.tcp_synack_time = tcp_synack_time;
	}
	public String getTcp_ack_time() {
		return tcp_ack_time;
	}
	public void setTcp_ack_time(String tcp_ack_time) {
		this.tcp_ack_time = tcp_ack_time;
	}
	public String getActiontime() {
		return actiontime;
	}
	public void setActiontime(String actiontime) {
		this.actiontime = actiontime;
	}
	public String getFirstpackettime() {
		return firstpackettime;
	}
	public void setFirstpackettime(String firstpackettime) {
		this.firstpackettime = firstpackettime;
	}
	public String getPageopentime() {
		return pageopentime;
	}
	public void setPageopentime(String pageopentime) {
		this.pageopentime = pageopentime;
	}
	public String getLastpacktime() {
		return lastpacktime;
	}
	public void setLastpacktime(String lastpacktime) {
		this.lastpacktime = lastpacktime;
	}
	public String getPagevolume() {
		return pagevolume;
	}
	public void setPagevolume(String pagevolume) {
		this.pagevolume = pagevolume;
	}
	public String getLast_ack_time() {
		return last_ack_time;
	}
	public void setLast_ack_time(String last_ack_time) {
		this.last_ack_time = last_ack_time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstfinacktime() {
		return firstfinacktime;
	}
	public void setFirstfinacktime(String firstfinacktime) {
		this.firstfinacktime = firstfinacktime;
	}
	public String getDnsquerytime() {
		return dnsquerytime;
	}
	public void setDnsquerytime(String dnsquerytime) {
		this.dnsquerytime = dnsquerytime;
	}
	public String getDnsresponsetime() {
		return dnsresponsetime;
	}
	public void setDnsresponsetime(String dnsresponsetime) {
		this.dnsresponsetime = dnsresponsetime;
	}
	public String getDnsflowid() {
		return dnsflowid;
	}
	public void setDnsflowid(String dnsflowid) {
		this.dnsflowid = dnsflowid;
	}
	public String getFirstscreenfintime() {
		return firstscreenfintime;
	}
	public void setFirstscreenfintime(String firstscreenfintime) {
		this.firstscreenfintime = firstscreenfintime;
	}
	public String getAbnormal_sort() {
		return abnormal_sort;
	}
	public void setAbnormal_sort(String abnormal_sort) {
		this.abnormal_sort = abnormal_sort;
	}
	public String getPage_failure_delimiting() {
		return page_failure_delimiting;
	}
	public void setPage_failure_delimiting(String page_failure_delimiting) {
		this.page_failure_delimiting = page_failure_delimiting;
	}
	public String getFirst_page_delay_delimiting() {
		return first_page_delay_delimiting;
	}
	public void setFirst_page_delay_delimiting(String first_page_delay_delimiting) {
		this.first_page_delay_delimiting = first_page_delay_delimiting;
	}
	public String getDns_delay_delimiting() {
		return dns_delay_delimiting;
	}
	public void setDns_delay_delimiting(String dns_delay_delimiting) {
		this.dns_delay_delimiting = dns_delay_delimiting;
	}
	public String getTcp_delay_upper_interf_delimiting() {
		return tcp_delay_upper_interf_delimiting;
	}
	public void setTcp_delay_upper_interf_delimiting(String tcp_delay_upper_interf_delimiting) {
		this.tcp_delay_upper_interf_delimiting = tcp_delay_upper_interf_delimiting;
	}
	public String getTcp_delay_lower_interf_delimiting() {
		return tcp_delay_lower_interf_delimiting;
	}
	public void setTcp_delay_lower_interf_delimiting(String tcp_delay_lower_interf_delimiting) {
		this.tcp_delay_lower_interf_delimiting = tcp_delay_lower_interf_delimiting;
	}
	public String getPage_throughput_delimiting() {
		return page_throughput_delimiting;
	}
	public void setPage_throughput_delimiting(String page_throughput_delimiting) {
		this.page_throughput_delimiting = page_throughput_delimiting;
	}
	public String getApp_sub_type() {
		return app_sub_type;
	}
	public void setApp_sub_type(String app_sub_type) {
		this.app_sub_type = app_sub_type;
	}


}
