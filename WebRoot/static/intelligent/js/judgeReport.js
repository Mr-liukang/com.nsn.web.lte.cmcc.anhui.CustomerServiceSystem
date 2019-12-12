N.JudgeReport = N.Class.extend({
	
    /************质差诊断书begin********************/
	cate_sense:['页面打开时延','首屏时延','视频下载','视频卡顿','即时通信','游戏交互'],
	cate_kpi:['无线连接成功率','rrc连接建立成功率','e-rab建立成功率','lte系统内切换成功率','cqi≥7占比','上行prb资源利用率','下行prb资源利用率','pdcch信道cce占用率','prach信道占用率','平均激活用户数','最大激活用户数','最大rrc连接用户数','用户面下行平均时延','rrc连接重建比例','erab掉线率%','ue上下文掉线率','寻呼拥塞率','enodeb内切换成功率','x2切换成功率','s1接口切换成功率','s1信令建立成功率','ue响应超时','传输层问题','无线层问题','无线资源不足','其他原因','上行重传比例','下行重传比例','lte重定向到3g的次数','pdcp层上行用户面流量mb','pdcp层下行用户面流量mb','小区可用率','lte重定向到3g比例','上行drb数据调度时长','下行drb数据调度时长','rach负荷','用户体验上行平均速率','用户体验下行平均速率'],
   
    report : function(eci,site_name,report_date,advise){ //诊断书
    	var me = this;
    	$("radio").unbind("click");
        $("input[name='r_kpi']").click(function(){//优良率呈现颗粒度radio点击事件 月/天
			if($(this).val() == 'day'){
				$("#r_kpi_div").html("");
				me.getKpi(0,eci);
			}
			if($(this).val() == 'hour'){
				var html1 = "";
				html1+="<a href='javascript:judgeReport.getKpi(1,\""+eci+"\");' type='button' class='btn btn-primary btn-sm kpi_class' >10-11</a>";
				html1+="<a href='javascript:judgeReport.getKpi(2,\""+eci+"\");' type='button' class='btn btn-primary btn-sm kpi_class' >10-12</a>";
				html1+="<a href='javascript:judgeReport.getKpi(3,\""+eci+"\");' type='button' class='btn btn-primary btn-sm kpi_class' >10-13</a>";
				html1+="<a href='javascript:judgeReport.getKpi(4,\""+eci+"\");' type='button' class='btn btn-primary btn-sm kpi_class' >10-14</a>";
				html1+="<a href='javascript:judgeReport.getKpi(5,\""+eci+"\");' type='button' class='btn btn-primary btn-sm kpi_class' >10-15</a>";
				$("#r_kpi_div").html(html1);
			}
		});

        //告警
        if(eci!='46011019104750'){
        	$("#bad_cell_alarm").hide();
        	$("#bad_cell_alarm_n").show();
        } else {
        	$("#bad_cell_alarm").show();
        	$("#bad_cell_alarm_n").hide();
        }


		$("#bad_cell_business").html("");
		$("#cause_ring").html("");
		$("#cause_table").html("");
		$("#bad_cell_contrast").html("");
		$("#bad_cell_conclusion").html(advise);
		// $("#bad_cell_advice").html("");
		
		$("#report_id div").find("span").eq(0).text(site_name); //小区中文名
		$("#report_id div").find("span").eq(1).text(eci); //eci
		$("#report_id div").find("span").eq(2).text(report_date);

		//gis地图图片
		if(eci=='46011019104750' || eci=='46011019123250' || eci=='46011019108749'){
			$("#report_gis").attr('src','static/intelligent/img/'+eci+'.jpg');
		}

		//建议调整项
		var paramHtml = "";
		paramHtml+="<table class='table table-bordered bg-currency' id='para_table' style='width: 500px;'>";
		paramHtml+="<tr>";
		paramHtml+="<td>建议调整项</td>";
		if(eci=='46011019104750'){
			paramHtml+="<td>2</td>";
			paramHtml+="<td><a href='static/intelligent/template/参数优化建议表46011019104750.xlsx' type='button' class='btn btn-primary btn-sm' style='margin-bottom:5px;'>详情</a></td>";
		} else {
			paramHtml+="<td>1</td>";
			paramHtml+="<td><a href='static/intelligent/template/参数优化建议表46011019123250.xlsx' type='button' class='btn btn-primary btn-sm' style='margin-bottom:5px;'>详情</a></td>";
		}
		paramHtml+="</tr>";
		paramHtml+="</table>";
		$("#bad_cell_para").html(paramHtml);


		//邻区信息
		var adjustHtml = "";
		adjustHtml+="<table class='table table-bordered bg-currency'>";
		adjustHtml+="<tr><td>"+site_name+"</td><td>缺失</td><td>定义</td></tr>";
		adjustHtml+="<tr><td>同站邻区</td><td>无</td><td>同站小区</td></tr>";
		if(eci=='46011019104750'){
			adjustHtml+="<tr><td>第一圈邻区</td><td>2</td><td>Voronoi泰森多边形相邻小区</td></tr>";
		} else {
			adjustHtml+="<tr><td>第一圈邻区</td><td>无</td><td>Voronoi泰森多边形相邻小区</td></tr>";
		}
		adjustHtml+="<tr><td>第二圈邻区</td><td>无</td><td>第一圈邻区的同站小区及其Voronoi相邻小区</td></tr>";
		adjustHtml+="</table>";
		$("#adjustTable").html(adjustHtml);

		var badCellBusiness = {};
		if(eci=='46011019104750' || eci=='46011019123250'){
			badCellBusiness = eval("badCellBusiness"+eci);
		} else {
			badCellBusiness = badCellBusinessOther;
		}

		me.drawWaterFall("bad_cell_business",me.parseWaterData(badCellBusiness));


		var badCellCause = [{ "name": "无线", "value": 0.2 },
			    { "name": "核心网", "value": 0.1 },
			    { "name": "DNS服务器", "value": 0.1 },
			    { "name": "DNS用户", "value": 0.1 },
			    { "name": "终端", "value": 0.1 },
			    { "name": "SP", "value": 0.1 },
			    { "name": "无线原因（个别用户）", "value": 0.1 },
			    { "name": "用户", "value": 0.1 }
			];
        chart.drawCause("cause_ring", intelligent.parsePieData(badCellCause));
        intelligent.drawCauseTable("cause_table", badCellCause);

		
		var badCellContrast = {};
		if(eci=='46011019104750' || eci=='46011019123250'){
			badCellContrast = eval("badCellContrast"+eci);
		} else {
			badCellContrast = badCellContrastOther;
		}

		var result = [];
		var ci = badCellContrast.ci;
		var total = badCellContrast.total;
		result[0] = [ci.page_rate,ci.first_rate,ci.video_rate,ci.video_halt_rate,ci.im_rate,ci.game_rate];
		result[1] = [total.page_rate,total.first_rate,total.video_rate,total.video_halt_rate,total.im_rate,total.game_rate];

		var bad_cell_contrast = {
    		id:'bad_cell_contrast',
    		legend:['小区', '全网'],
    		xArr:me.cate_sense,
    		series: [{
                    name: '小区',
                    yAxisIndex: 0,
                    type: 'bar',
                    barWidth: '12',
                    itemStyle: {
                        normal: {
                            barBorderRadius: [30, 30, 0, 0],
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1, [{
                                        offset: 0,
                                        color: 'rgb(66,201,79)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(66,221,94)'
                                    }
                                ]
                            )
                        }
                    },
                    data: result[0],
                    zlevel: 1
                },{
                    name: '全网',
                    yAxisIndex: 0,
                    type: 'bar',
                    barWidth: '12',
                    itemStyle: {
                        normal: {
                            barBorderRadius: [30, 30, 0, 0],
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1, [{
                                        offset: 0,
                                        color: 'rgb(255,159,14)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(255,186,57)'
                                    }
                                ]
                            )
                        }
                    },
                    data: result[1],
                    zlevel: 1
                }
            ]
    	}
    	chart.drawColumn(bad_cell_contrast);

		var ci = badCellKpi.ci;
		var total = badCellKpi.total;
		var ciResult = [];
		var totalResult = [];
		$.each(ci,function(i,obj){
			if(i!='sdate' && i!='area' && i!='cell_id'){
				ciResult.push(obj);
			}
		});
		$.each(total,function(i,obj){
			if(i!='sdate' && i!='area'){
				totalResult.push(obj);
			}
		});

		var bad_cell_kpi = {
    		id:'bad_cell_kpi',
    		legend:['小区', '全网'],
    		xArr:me.cate_kpi,
    		series: [{
                    name: '小区',
                    yAxisIndex: 0,
                    type: 'bar',
                    barWidth: '12',
                    itemStyle: {
                        normal: {
                            barBorderRadius: [30, 30, 0, 0],
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1, [{
                                        offset: 0,
                                        color: 'rgb(66,201,79)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(66,221,94)'
                                    }
                                ]
                            )
                        }
                    },
                    data: ciResult,
                    zlevel: 1
                },{
                    name: '全网',
                    yAxisIndex: 0,
                    type: 'bar',
                    barWidth: '12',
                    itemStyle: {
                        normal: {
                            barBorderRadius: [30, 30, 0, 0],
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1, [{
                                        offset: 0,
                                        color: 'rgb(255,159,14)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(255,186,57)'
                                    }
                                ]
                            )
                        }
                    },
                    data: totalResult,
                    zlevel: 1
                }
            ]
    	}
    	chart.drawColumn(bad_cell_kpi);

		//工参信息
		var columnsCfg = [];
		for(var prop in cfgHead){
			columnsCfg.push({title:cfgHead[prop],"data":cfgHead[prop], "orderable":false});
		}

		$('#cfg_table').html("");
    	if ($('#cfg_table').hasClass('dataTable')) {
            var dttable = $('#cfg_table').dataTable();
            dttable.fnClearTable(); //清空一下table
            dttable.fnDestroy(); //还原初始化了的datatable
            $("#cfg_table").html("");
        }
        var cfgData = [];
        if(eci=='46011019104750' || eci=='46011019123250' || eci=='46011019108749'){
        	cfgData = eval("cfgData"+eci);
        } else {
        	cfgData = cfgDataOther;
        }

		$("#cfg_table").DataTable({
			"bFilter": false, // 过滤功能
			"iDisplayLength": 5,
			"bLengthChange": false, // 改变每页显示数据数量
			"scrollX": true,
			"data":cfgData,
			"columns":  columnsCfg			
		});
		
		
	},
	parseWaterData : function(data){ //质差业务分布 瀑布图数据
		var me = this;
		var result = [];
//		result[0] = [data.page_goood,data.first_good,data.video_down_good,data.video_caton_good,data.im_good,data.game_good];
//		result[1] = [data.page,data.first,data.video_down,data.video_caton,data.im,data.game];
		result = [{
            name: '页面打开时延',
            y: data.page,
            color:'#b5c334'
        },{
            name: '首屏时延',
            y: data.first,
            color:'#b5c334'
        },{
            name: '视频下载',
            y: data.video_down,
            color:'#b5c334'
        },{
            name: '视频卡顿',
            y: data.video_caton,
            color:'#b5c334'
        },{
            name: '即时通信',
            y: data.im,
            color:'#b5c334'
        },{
            name: '游戏交互',
            y: data.game,
            color:'#b5c334'
        }, {
            name: '总质差',
            isSum: true,
            color: '#c42d35'
        }];
		return result;
	},
	drawWaterFall : function(htmlid,data){ //质差业务分布 瀑布图
		var me = this;

	    $('#'+htmlid).highcharts({
	        chart: {
	            type: 'waterfall'
	        },
	        title: {
	            text: ''
	        },
	        xAxis: {
	            type: 'category'
	        },
	        yAxis: {
	            title: {
	                text: ''
	            }
	        },
	        legend: {
	            enabled: false
	        },
	        tooltip: {
	            pointFormat: '<b>{point.y}</b> '
	        },
	        series: [{
	            data: data,
	            dataLabels: {
	                enabled: true,
	                style: {
	                    color: '#FFFFFF',
	                    fontWeight: 'bold',
	                    textShadow: '0px 0px 3px black'
	                }
	            },
	            pointPadding: 0
	        }]
	    });
	},
	drawColumn : function(htmlid,cate,data){ //小区感知指标以及全网均值对比柱状图
		var me = this;
		var startnum = 0;
		var endnum = 100;
		if(htmlid=='bad_cell_kpi'){
			endnum = 20;
		} 
		var chartOption = {
		    title : {
		        text: ''
		    },
		    color:me.colors,
		    tooltip : {
		        trigger: 'axis'
		    },
		    legend: {
		        data:['小区','全网']
		    },
		    
		    xAxis : [
		        {
		            type : 'category',
		            data : cate
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value'
		        }
		    ],
		    series : [
		        {
		            name:'小区',
		            type:'bar',
		            label: {
		                normal: {
		                    show: true,
		                    position: 'inside'
		                }
		            },
		            data:data[0]
		        },
		        {
		            name:'全网',
		            type:'bar',
		            itemStyle : {
						normal : {
							label: {
						          show: true,
						          position: 'top'
						      }
						 }
					},
		            data:data[1]
		        }
		    ]
		};
		var chart = echarts.init(document.getElementById(htmlid),'macarons');
		chart.setOption(chartOption, true);
			
	}
	 /************质差诊断书end********************/
	
});
var judgeReport = new N.JudgeReport();
$(function() {
	
});