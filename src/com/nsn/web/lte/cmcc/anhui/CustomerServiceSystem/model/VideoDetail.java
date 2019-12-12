package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.model;

public class VideoDetail {
    // @Excel(name = "rowkey")
    private String rowkey;
    // @Excel(name = "rowkey")
    private String inter;
    // @Excel(name = "IMSI")
    private String IMSI;
    // @Excel(name = "MSISDN")
    private String MSISDN;
    // @Excel(name = "IMEI")
    private String IMEI;
    // @Excel(name = "Dest_IP")
    private String Dest_IP;
    // @Excel(name = "Dest_Port")
    private String Dest_Port;
    // @Excel(name = "Src_IP")
    private String Src_IP;
    // @Excel(name = "Src_Port")
    private String Src_Port;
    // @Excel(name = "SGW_IP_Addr")
    private String SGW_IP_Addr;
    // @Excel(name = "MME_IP_Addr")
    private String MME_IP_Addr;
    // @Excel(name = "PGW_IP_Addr")
    private String PGW_IP_Addr;
    // @Excel(name = "ECGI")
    private String ECGI;
    // @Excel(name = "RAT")
    private String RAT;
    // @Excel(name = "Protocol_ID")
    private String Protocol_ID;
    // @Excel(name = "Service_Type")
    private String Service_Type;
    // @Excel(name = "start_time")
    private String start_time;
    // @Excel(name = "End_Time")
    private String End_Time;
    // @Excel(name = "Duration")
    private String Duration;
    // @Excel(name = "Input_Octets")
    private String Input_Octets;
    // @Excel(name = "Output_Octets")
    private String Output_Octets;
    // @Excel(name = "Record_Close_Cause")
    private String Record_Close_Cause;
    // @Excel(name = "MME_UE_S1AP_ID")
    private String MME_UE_S1AP_ID;
    // @Excel(name = "ENB_UE_S1AP_ID")
    private String ENB_UE_S1AP_ID;
    // @Excel(name = "Home_Province")
    private String Home_Province;
    // @Excel(name = "L4")
    private String L4;
    // @Excel(name = "TCP_OOO_UL_Packets")
    private String TCP_OOO_UL_Packets;
    // @Excel(name = "TCP_OOO_DL_Packets")
    private String TCP_OOO_DL_Packets;
    // @Excel(name = "TCP_Retrans_UL_Packets")
    private String TCP_Retrans_UL_Packets;
    // @Excel(name = "TCP_Retrans_DL_Packets")
    private String TCP_Retrans_DL_Packets;
    // @Excel(name = "TCPSetupACKDelay")
    private String TCPSetupResponseDelay;
    // @Excel(name = "TCPSetupACKDelay")
    private String TCPSetupACKDelay;
    // @Excel(name = "Delay_Setup_FirstTransaction")
    private String Delay_Setup_FirstTransaction;
    // @Excel(name = "Delay_FirstTransaction_FirstResPackt")
    private String Delay_FirstTransaction_FirstResPackt;
    // @Excel(name = "TCP_Syn_Number")
    private String TCP_Syn_Number;
    // @Excel(name = "TCP_ConnetState")
    private String TCP_ConnetState;
    // @Excel(name = "SessionStopFlag")
    private String SessionStopFlag;
    // @Excel(name = "VideoPlaySuccess")
    private String VideoPlaySuccess;
    // @Excel(name = "VideoPlayWaitTime")
    private String VideoPlayWaitTime;
    // @Excel(name = "VideoPlayHaltCount")
    private String VideoPlayHaltCount;
    // @Excel(name = "VideoRecoverTime")
    private String VideoRecoverTime;
    // @Excel(name = "FirstDataPkgTime")
    private String FirstDataPkgTime;
    // @Excel(name = "LastDataPkgTime")
    private String LastDataPkgTime;
    // @Excel(name = "VideoApp")
    private String VideoApp;
    // @Excel(name = "VideoThroughput")
    private String VideoThroughput;
    // @Excel(name = "VideoType")
    private String VideoType;
    // @Excel(name = "VideoBitRate")
    private String VideoBitRate;
    // @Excel(name = "VideoCacheThroughput")
    private String VideoCacheThroughput;
    // @Excel(name = "tcp_delay_abnormal")
    private String tcp_delay_abnormal;
    // @Excel(name = "page_throughput_abnormal")
    private String page_throughput_abnormal;
    // @Excel(name = "tcp_latency_upper_interf")
    private String tcp_latency_upper_interf;
    // @Excel(name = "tcp_latency_lower_interf")
    private String tcp_latency_lower_interf;
    // @Excel(name = "abnormal_sort")
    private String abnormal_sort;
    // @Excel(name = "tcp_delay_upper_interf_delimiting")
    private String tcp_delay_upper_interf_delimiting;
    // @Excel(name = "tcp_delay_lower_interf_delimiting")
    private String tcp_delay_lower_interf_delimiting;
    // @Excel(name = "video_throughput_delimiting")
    private String video_throughput_delimiting;
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public String getInter() {
		return inter;
	}
	public void setInter(String inter) {
		this.inter = inter;
	}
	public String getIMSI() {
		return IMSI;
	}
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}
	public String getMSISDN() {
		return MSISDN;
	}
	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getDest_IP() {
		return Dest_IP;
	}
	public void setDest_IP(String dest_IP) {
		Dest_IP = dest_IP;
	}
	public String getDest_Port() {
		return Dest_Port;
	}
	public void setDest_Port(String dest_Port) {
		Dest_Port = dest_Port;
	}
	public String getSrc_IP() {
		return Src_IP;
	}
	public void setSrc_IP(String src_IP) {
		Src_IP = src_IP;
	}
	public String getSrc_Port() {
		return Src_Port;
	}
	public void setSrc_Port(String src_Port) {
		Src_Port = src_Port;
	}
	public String getSGW_IP_Addr() {
		return SGW_IP_Addr;
	}
	public void setSGW_IP_Addr(String sGW_IP_Addr) {
		SGW_IP_Addr = sGW_IP_Addr;
	}
	public String getMME_IP_Addr() {
		return MME_IP_Addr;
	}
	public void setMME_IP_Addr(String mME_IP_Addr) {
		MME_IP_Addr = mME_IP_Addr;
	}
	public String getPGW_IP_Addr() {
		return PGW_IP_Addr;
	}
	public void setPGW_IP_Addr(String pGW_IP_Addr) {
		PGW_IP_Addr = pGW_IP_Addr;
	}
	public String getECGI() {
		return ECGI;
	}
	public void setECGI(String eCGI) {
		ECGI = eCGI;
	}
	public String getRAT() {
		return RAT;
	}
	public void setRAT(String rAT) {
		RAT = rAT;
	}
	public String getProtocol_ID() {
		return Protocol_ID;
	}
	public void setProtocol_ID(String protocol_ID) {
		Protocol_ID = protocol_ID;
	}
	public String getService_Type() {
		return Service_Type;
	}
	public void setService_Type(String service_Type) {
		Service_Type = service_Type;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_Time() {
		return End_Time;
	}
	public void setEnd_Time(String end_Time) {
		End_Time = end_Time;
	}
	public String getDuration() {
		return Duration;
	}
	public void setDuration(String duration) {
		Duration = duration;
	}
	public String getInput_Octets() {
		return Input_Octets;
	}
	public void setInput_Octets(String input_Octets) {
		Input_Octets = input_Octets;
	}
	public String getOutput_Octets() {
		return Output_Octets;
	}
	public void setOutput_Octets(String output_Octets) {
		Output_Octets = output_Octets;
	}
	public String getRecord_Close_Cause() {
		return Record_Close_Cause;
	}
	public void setRecord_Close_Cause(String record_Close_Cause) {
		Record_Close_Cause = record_Close_Cause;
	}
	public String getMME_UE_S1AP_ID() {
		return MME_UE_S1AP_ID;
	}
	public void setMME_UE_S1AP_ID(String mME_UE_S1AP_ID) {
		MME_UE_S1AP_ID = mME_UE_S1AP_ID;
	}
	public String getENB_UE_S1AP_ID() {
		return ENB_UE_S1AP_ID;
	}
	public void setENB_UE_S1AP_ID(String eNB_UE_S1AP_ID) {
		ENB_UE_S1AP_ID = eNB_UE_S1AP_ID;
	}
	public String getHome_Province() {
		return Home_Province;
	}
	public void setHome_Province(String home_Province) {
		Home_Province = home_Province;
	}
	public String getL4() {
		return L4;
	}
	public void setL4(String l4) {
		L4 = l4;
	}
	public String getTCP_OOO_UL_Packets() {
		return TCP_OOO_UL_Packets;
	}
	public void setTCP_OOO_UL_Packets(String tCP_OOO_UL_Packets) {
		TCP_OOO_UL_Packets = tCP_OOO_UL_Packets;
	}
	public String getTCP_OOO_DL_Packets() {
		return TCP_OOO_DL_Packets;
	}
	public void setTCP_OOO_DL_Packets(String tCP_OOO_DL_Packets) {
		TCP_OOO_DL_Packets = tCP_OOO_DL_Packets;
	}
	public String getTCP_Retrans_UL_Packets() {
		return TCP_Retrans_UL_Packets;
	}
	public void setTCP_Retrans_UL_Packets(String tCP_Retrans_UL_Packets) {
		TCP_Retrans_UL_Packets = tCP_Retrans_UL_Packets;
	}
	public String getTCP_Retrans_DL_Packets() {
		return TCP_Retrans_DL_Packets;
	}
	public void setTCP_Retrans_DL_Packets(String tCP_Retrans_DL_Packets) {
		TCP_Retrans_DL_Packets = tCP_Retrans_DL_Packets;
	}
	public String getTCPSetupResponseDelay() {
		return TCPSetupResponseDelay;
	}
	public void setTCPSetupResponseDelay(String tCPSetupResponseDelay) {
		TCPSetupResponseDelay = tCPSetupResponseDelay;
	}
	public String getTCPSetupACKDelay() {
		return TCPSetupACKDelay;
	}
	public void setTCPSetupACKDelay(String tCPSetupACKDelay) {
		TCPSetupACKDelay = tCPSetupACKDelay;
	}
	public String getDelay_Setup_FirstTransaction() {
		return Delay_Setup_FirstTransaction;
	}
	public void setDelay_Setup_FirstTransaction(String delay_Setup_FirstTransaction) {
		Delay_Setup_FirstTransaction = delay_Setup_FirstTransaction;
	}
	public String getDelay_FirstTransaction_FirstResPackt() {
		return Delay_FirstTransaction_FirstResPackt;
	}
	public void setDelay_FirstTransaction_FirstResPackt(String delay_FirstTransaction_FirstResPackt) {
		Delay_FirstTransaction_FirstResPackt = delay_FirstTransaction_FirstResPackt;
	}
	public String getTCP_Syn_Number() {
		return TCP_Syn_Number;
	}
	public void setTCP_Syn_Number(String tCP_Syn_Number) {
		TCP_Syn_Number = tCP_Syn_Number;
	}
	public String getTCP_ConnetState() {
		return TCP_ConnetState;
	}
	public void setTCP_ConnetState(String tCP_ConnetState) {
		TCP_ConnetState = tCP_ConnetState;
	}
	public String getSessionStopFlag() {
		return SessionStopFlag;
	}
	public void setSessionStopFlag(String sessionStopFlag) {
		SessionStopFlag = sessionStopFlag;
	}
	public String getVideoPlaySuccess() {
		return VideoPlaySuccess;
	}
	public void setVideoPlaySuccess(String videoPlaySuccess) {
		VideoPlaySuccess = videoPlaySuccess;
	}
	public String getVideoPlayWaitTime() {
		return VideoPlayWaitTime;
	}
	public void setVideoPlayWaitTime(String videoPlayWaitTime) {
		VideoPlayWaitTime = videoPlayWaitTime;
	}
	public String getVideoPlayHaltCount() {
		return VideoPlayHaltCount;
	}
	public void setVideoPlayHaltCount(String videoPlayHaltCount) {
		VideoPlayHaltCount = videoPlayHaltCount;
	}
	public String getVideoRecoverTime() {
		return VideoRecoverTime;
	}
	public void setVideoRecoverTime(String videoRecoverTime) {
		VideoRecoverTime = videoRecoverTime;
	}
	public String getFirstDataPkgTime() {
		return FirstDataPkgTime;
	}
	public void setFirstDataPkgTime(String firstDataPkgTime) {
		FirstDataPkgTime = firstDataPkgTime;
	}
	public String getLastDataPkgTime() {
		return LastDataPkgTime;
	}
	public void setLastDataPkgTime(String lastDataPkgTime) {
		LastDataPkgTime = lastDataPkgTime;
	}
	public String getVideoApp() {
		return VideoApp;
	}
	public void setVideoApp(String videoApp) {
		VideoApp = videoApp;
	}
	public String getVideoThroughput() {
		return VideoThroughput;
	}
	public void setVideoThroughput(String videoThroughput) {
		VideoThroughput = videoThroughput;
	}
	public String getVideoType() {
		return VideoType;
	}
	public void setVideoType(String videoType) {
		VideoType = videoType;
	}
	public String getVideoBitRate() {
		return VideoBitRate;
	}
	public void setVideoBitRate(String videoBitRate) {
		VideoBitRate = videoBitRate;
	}
	public String getVideoCacheThroughput() {
		return VideoCacheThroughput;
	}
	public void setVideoCacheThroughput(String videoCacheThroughput) {
		VideoCacheThroughput = videoCacheThroughput;
	}
	public String getTcp_delay_abnormal() {
		return tcp_delay_abnormal;
	}
	public void setTcp_delay_abnormal(String tcp_delay_abnormal) {
		this.tcp_delay_abnormal = tcp_delay_abnormal;
	}
	public String getPage_throughput_abnormal() {
		return page_throughput_abnormal;
	}
	public void setPage_throughput_abnormal(String page_throughput_abnormal) {
		this.page_throughput_abnormal = page_throughput_abnormal;
	}
	public String getTcp_latency_upper_interf() {
		return tcp_latency_upper_interf;
	}
	public void setTcp_latency_upper_interf(String tcp_latency_upper_interf) {
		this.tcp_latency_upper_interf = tcp_latency_upper_interf;
	}
	public String getTcp_latency_lower_interf() {
		return tcp_latency_lower_interf;
	}
	public void setTcp_latency_lower_interf(String tcp_latency_lower_interf) {
		this.tcp_latency_lower_interf = tcp_latency_lower_interf;
	}
	public String getAbnormal_sort() {
		return abnormal_sort;
	}
	public void setAbnormal_sort(String abnormal_sort) {
		this.abnormal_sort = abnormal_sort;
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
	public String getVideo_throughput_delimiting() {
		return video_throughput_delimiting;
	}
	public void setVideo_throughput_delimiting(String video_throughput_delimiting) {
		this.video_throughput_delimiting = video_throughput_delimiting;
	}
    
}
