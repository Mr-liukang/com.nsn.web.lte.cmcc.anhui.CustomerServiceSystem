package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model;



/**
 * 盲点库 匹配表 实体类
 */

public class RPT_CUS_SER_SIGNALING_SPEC {

    private String   interface_type; // interface
    private String   imsi;
    private String   imei; //IMEI(SV)
    private String   msisdn;
    private String   home_province;
    private String   procedure_type;
    private String   start_time;
    private String   end_time;
    private String   status;
    private String   request_cause;

    private String   failure_cause;
    private String   keyword;
    private String   mme_ue_s1ap_id;
    private String   target_enb_id;
    private String   user_ipv4;
    private String   user_ipv6;
    private String   mme_ip_addr;
    private String   enb_ip_addr;
    private String   tai;
    private String   ecgi;

    private String   apn;
    private String   enb_ue_s1ap_id;
    private String   eps_bearer_number;
    private String   bearer_id_1;
    private String   bearer_type_1;
    private String   bearer_qci_1;
    private String   bearer_status_1;
    private String   bearer_id_2;
    private String   bearer_type_2;
    private String   bearer_qci_2;

    private String   bearer_status_2;
    private String   bearer_id_3;
    private String   bearer_type_3;
    private String   bearer_qci_3;
    private String   bearer_status_3;
    private String   problem_delimit ;
	public String getInterface_type() {
		return interface_type;
	}
	public void setInterface_type(String interface_type) {
		this.interface_type = interface_type;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getHome_province() {
		return home_province;
	}
	public void setHome_province(String home_province) {
		this.home_province = home_province;
	}
	public String getProcedure_type() {
		return procedure_type;
	}
	public void setProcedure_type(String procedure_type) {
		this.procedure_type = procedure_type;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRequest_cause() {
		return request_cause;
	}
	public void setRequest_cause(String request_cause) {
		this.request_cause = request_cause;
	}
	public String getFailure_cause() {
		return failure_cause;
	}
	public void setFailure_cause(String failure_cause) {
		this.failure_cause = failure_cause;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getMme_ue_s1ap_id() {
		return mme_ue_s1ap_id;
	}
	public void setMme_ue_s1ap_id(String mme_ue_s1ap_id) {
		this.mme_ue_s1ap_id = mme_ue_s1ap_id;
	}
	public String getTarget_enb_id() {
		return target_enb_id;
	}
	public void setTarget_enb_id(String target_enb_id) {
		this.target_enb_id = target_enb_id;
	}
	public String getUser_ipv4() {
		return user_ipv4;
	}
	public void setUser_ipv4(String user_ipv4) {
		this.user_ipv4 = user_ipv4;
	}
	public String getUser_ipv6() {
		return user_ipv6;
	}
	public void setUser_ipv6(String user_ipv6) {
		this.user_ipv6 = user_ipv6;
	}
	public String getMme_ip_addr() {
		return mme_ip_addr;
	}
	public void setMme_ip_addr(String mme_ip_addr) {
		this.mme_ip_addr = mme_ip_addr;
	}
	public String getEnb_ip_addr() {
		return enb_ip_addr;
	}
	public void setEnb_ip_addr(String enb_ip_addr) {
		this.enb_ip_addr = enb_ip_addr;
	}
	public String getTai() {
		return tai;
	}
	public void setTai(String tai) {
		this.tai = tai;
	}
	public String getEcgi() {
		return ecgi;
	}
	public void setEcgi(String ecgi) {
		this.ecgi = ecgi;
	}
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	public String getEnb_ue_s1ap_id() {
		return enb_ue_s1ap_id;
	}
	public void setEnb_ue_s1ap_id(String enb_ue_s1ap_id) {
		this.enb_ue_s1ap_id = enb_ue_s1ap_id;
	}
	public String getEps_bearer_number() {
		return eps_bearer_number;
	}
	public void setEps_bearer_number(String eps_bearer_number) {
		this.eps_bearer_number = eps_bearer_number;
	}
	public String getBearer_id_1() {
		return bearer_id_1;
	}
	public void setBearer_id_1(String bearer_id_1) {
		this.bearer_id_1 = bearer_id_1;
	}
	public String getBearer_type_1() {
		return bearer_type_1;
	}
	public void setBearer_type_1(String bearer_type_1) {
		this.bearer_type_1 = bearer_type_1;
	}
	public String getBearer_qci_1() {
		return bearer_qci_1;
	}
	public void setBearer_qci_1(String bearer_qci_1) {
		this.bearer_qci_1 = bearer_qci_1;
	}
	public String getBearer_status_1() {
		return bearer_status_1;
	}
	public void setBearer_status_1(String bearer_status_1) {
		this.bearer_status_1 = bearer_status_1;
	}
	public String getBearer_id_2() {
		return bearer_id_2;
	}
	public void setBearer_id_2(String bearer_id_2) {
		this.bearer_id_2 = bearer_id_2;
	}
	public String getBearer_type_2() {
		return bearer_type_2;
	}
	public void setBearer_type_2(String bearer_type_2) {
		this.bearer_type_2 = bearer_type_2;
	}
	public String getBearer_qci_2() {
		return bearer_qci_2;
	}
	public void setBearer_qci_2(String bearer_qci_2) {
		this.bearer_qci_2 = bearer_qci_2;
	}
	public String getBearer_status_2() {
		return bearer_status_2;
	}
	public void setBearer_status_2(String bearer_status_2) {
		this.bearer_status_2 = bearer_status_2;
	}
	public String getBearer_id_3() {
		return bearer_id_3;
	}
	public void setBearer_id_3(String bearer_id_3) {
		this.bearer_id_3 = bearer_id_3;
	}
	public String getBearer_type_3() {
		return bearer_type_3;
	}
	public void setBearer_type_3(String bearer_type_3) {
		this.bearer_type_3 = bearer_type_3;
	}
	public String getBearer_qci_3() {
		return bearer_qci_3;
	}
	public void setBearer_qci_3(String bearer_qci_3) {
		this.bearer_qci_3 = bearer_qci_3;
	}
	public String getBearer_status_3() {
		return bearer_status_3;
	}
	public void setBearer_status_3(String bearer_status_3) {
		this.bearer_status_3 = bearer_status_3;
	}
	public String getProblem_delimit() {
		return problem_delimit;
	}
	public void setProblem_delimit(String problem_delimit) {
		this.problem_delimit = problem_delimit;
	}
    
}
