N.Intelligent = N.Class.extend({
	init:function(){
		var me = this;
		me.day_label = 'day';
		me.initHeight();
		laydate.render({
			elem: '#date_d',
			format: 'yyyy-MM-dd'
		});
		
		laydate.render({
			elem: '#date_h',
			type: 'datetime',
			format: 'yyyy-MM-dd HH'
		});

		$("#date_d").val(N.Util.formatDate(new XDate(),'yyyy-MM-dd'));
		$("#date_h").val(N.Util.formatDate(new XDate(),'yyyy-MM-dd HH'));

		$("input[name='r1']").click(function(){//优良率呈现颗粒度radio点击事件 月/天
			me.day_label = $(this).val();
			if($(this).val() == 'day'){
				$("#day_label").show();
				$("#hour_label").hide();
			}
			if($(this).val() == 'hour'){
				$("#day_label").hide();
				$("#hour_label").show();
			}
		});
		//me.load();
		me.initMap();
	},
	//地图绘制begin...
	initMap : function() {
		var me = this;
		var cfg = N.GIS_CFG;
		me.nsnMap = L.nsnMap('map', cfg);
		me.map = me.nsnMap.getMap();
	//	me.heatmapLayer = L.heatmapOverlay(cfg.heatMapCfg);

		
		//me.loadingControl = L.Control.loading().addTo(me.map);
	//	me.legend = L.control.legend().addTo(me.map); //图例
		
	//	me.infoCtrl = L.control.info().addTo(me.map); // GIS地图下拉
		/*me.infoCtrl.options="topleft";*/
	
		// var attrs = [];
        // attrs.push('<input type="text" id="find"  style="text-align: center;"/>');
        // me.infoCtrl.update(attrs);
		/*me.infoCtrl.update("<select id='selectMap'><option value='user_cn'>1</option>"
				+ "<option value='user_cn1'>2</option></select>");*/
		me.lineLayer = new L.featureGroup();
		me.nsnMap.addOverlay({
			name : '小区图层',
			layer : me.lineLayer,
			show : true
		});
         me.loadingControl = L.Control.loading();
         me.map.addControl(me.loadingControl);
         me.infoCtrl = L.control.info({
        	    position: 'bottomright',
        	    }).addTo(me.map);
        /* var attrs = [];
         attrs.push("<button>搜索</button><select id='selectMap' hidden='hidden'>"+
        		 "<option value='user_cn'>基站</option>"+
        		 "<option value='user_cn1'>投诉地址</option>"+
        		 "<option value='user_cn1' >经纬度</option></select>"+
        		 "<input type='text' id='find' style='text-align: center;'/><button>定位分析</button>)";*/
         me.infoCtrl.update("<button id='ss'>搜索</button><select id='selectMap'  >"+
        		 "<option value='1'>基站</option>"+
        		 "<option value='2'>投诉地址</option>"+
        		 "<option value='3'>经纬度</option></select>"+
        		 "<input type='text' id='find'/><button id='smbt'>定位分析</button>");
         
         
		me.kpiKey='user_cn';
		$('#ss').on("click", function(e) {
			$('#selectMap').toggle();
			$('#find').toggle();
			$('#smbt').toggle();
          });
		$('#smbt').on("click", function(e) {
			console.log($('#selectMap').val());
			console.log($('#find').val());
			$('#selectMap').val();
			$('#find').val();
			
          });
	

	},
	loadGisPop:function(){
		var me = this;
		me.map.on("click",function(e) {
			var popup = L.popup();
            popup.setLatLng(e.latlng).setContent("你点击的位置在 " + e.latlng.toString()).openOn(me.map);
	        /*alert("You clicked the map at " + e.latlng);*/
	    });
		$.ajax({
		 	type : "POST",
		 	url : "/CustomerServiceSystem/CSSAction/loadGisPop",
		 	data : {
		 		cityname:$("#map_city").val(),
				scene:$("#map_scene").val(),
				scene_type:$("#map_scene_type").val(),
		 		"kpis":me.kpi
		 	},
		 	success : function(data) {
		 		if (me.lineLayer) {
		 		     me.lineLayer.clearLayers();
		 		     me.lineLayer.removeLayer();
		 		    }
		 		//&& data.mm
		 		// var attrs = [];
		 		 for (var i in data) {
		 			 var item = data[i];
			    	var circle = L.circle([item.jd, item.wd], item.cs*5, {
					          color: 'red',   //设置圆形边框的颜色
					          fillColor: '#f03',  //设置圆形内填充的颜色
					          fillOpacity: 0.5  //设置填充颜色的透明度
					      })
					      circle.bindPopup("I am a circle.");
						//attrs.push(circle);
						me.lineLayer.addLayer(circle);
						if(i==0){
							 me.map.setView([item.jd, item.wd],15);
						}
			     }
		 	
				
		 	}
		});

	},
    loadGisCell: function(){
    	var me = this;
		me.loading_gis = layer.load(3);
		me.loadingControl.showIndicator();
		$.ajax({
		 	type : "POST",
		 	url : "/CustomerServiceSystem/CSSAction/loadGisCell",
		 	data : {
		 		cityname:$("#map_city").val(),
				scene:$("#map_scene").val(),
				scene_type:$("#map_scene_type").val(),
		 		"kpis":me.kpi
		 	},
		 	success : function(data) {
		 		//&& data.mm
				if (data && 　data.ftrc ) {
			        me.cells = data.ftrc;
			        me.loadCells(me.cells,true);
				}
				me.loadingControl.hideIndicator();
				layer.close(me.loading_gis);
		 	}
		});
    },
    setKpi: function(kpi) {
		var me = this;
		kpi_name =me.fields[kpi];
		me.kpiKey = kpi;
		me.loadCells(me.cells, false);
},
loadCells: function(cells,flag) {
    var me = this;
    me.pts = [];
  /*  me.heatmapLayer.setData({
      'max': 0,
      'min': 0,
      'data': []
    });*/
    if (me.cellLayer) {
      me.cellLayer.clearLayers();
    }
    if (cells && cells.features.length > 0) {
      if (me.cellLayer) {
        me.cellLayer.addData(cells);
      } else {
        me.cellLayer = L.geoJson(cells, {
          style: function(feature) {
           
            var color = me.getColor(2);
           // var color = 'blue';
            console.log(feature);
            //console.log(feature.properties);
            //console.log(feature.properties['cellname']);
           // var alarm = feature.properties['中国移动'];
            if(false){
            	color = 'red';
            }else{
            	color = 'blue';
            }
            return {
              weight: 1.5,
              opacity: 0.8,
              color: color,
              dashArray: '3',
              fillOpacity: 0.3,
              fillColor: color
            };
          },
          onEachFeature: function(feature, layer) {
        	 if (feature.properties) {
              var pContent = me.buildContent(feature.properties);
              layer.bindPopup(pContent.join(''), { maxWidth: 600, maxHeight: 350 });
              }
          }
        });

        me.nsnMap.addOverlay({
          layer: me.cellLayer,
          name: '扇区图层',
          show: true
        });
      }
       
     /* if(flag&&me.cellLayer){
        var bd = me.cellLayer.getBounds();
        console.log(bd);
        if (bd.isValid()) {
          me.map.fitBounds(bd,{maxZoom:16});
        }
      }*/
    }
  },
  getColor: function(d) {
	    return d < 0 ? '#FFFFFF' :
		      d <= 1 ? '#0000FF' :
		      d <= 2 ? '#0000FF' :
		      d <= 3 ? '#0000FF' :
		      d <= 4 ? '#0000FF' :
		      d <= 5 ? '#FF0000' : '#FF0000';
 },
 buildContent: function(data) {
	    var cont = [];
	    if (data) {
	     /* cont.push("<table style = 'width:520px' class='marker-properties'><tr><th>"+data['sdate']+"</th><th colspan='2'>["+data['ci']+"] "+data['cell_name']+"</th></tr>");
	      for (var k in data) {
	        if (!this.fields[k]||k=='scity'||k=='sdate'||k=='cell_name'||k=='ci') {
	          continue;
	        }
	          cont.push("<tr>");
	          cont.push("<td>" + this.fields[k] + "</td><td>" + data[k] + "</td>");
	          cont.push("</tr>");
	      }
	      cont.push("</table>");*/
	    }
	    cont.push("<p>中国移动</p>");
	    return cont;
},
//地图绘制end...
	load:function(){
		var me = this;
		me.loadProblem();
		me.loadCause();
		me.loadAnalysis();
		me.loadBadLine();
		me.loadBadColumn();
		me.loadCiTable('all');
		me.loadIndexLine('RSRP');
		me.loadIndexColumn('RSRP');
		me.loadCdrTable('business');
		
	},
    loadReport:function(){
    
        layer.open({
                type : 1,
                shift : 4,
                title : '质差小区诊断书',
                skin: 'layer-class',
                content : $('#report'),
                area : [ '90%', '90%' ],//宽高 100%
                success: function(layero, index){
                    judgeReport.report('46011019104750','安徽小区_1','2017-10-15','通过关联根因分析，主要原因为弱覆盖。');
                }
        });
    },
	loadProblem:function(){
		var me = this;
		//用户投诉信息begin...
		 var phone = $('#phone').val();
		 if (phone == '') {
	            layer.msg("请输入手机号！");
	            return false;
	        }
		 //做个清空操作
		 $("#complaint_user").val();
		 $("#is_speed_limit").val();
		 $("#terminal").val();
		 $("#traffic").val();
		$.ajax({
			type: "POST",
			url:"/CustomerServiceSystem/CSSAction/setTest",
			data:$('#searchForm').serialize(),// id
			dataType:'json',
		    success: function(data) {
		    	for (var i in data) {
			    		var item = data[i];
			    		$("#complaint_user").val(item.id);
						$("#is_speed_limit").val(item.name);
						$("#terminal").val(item.password);
						$("#traffic").val(item.age);
			     }
		    
		}})
		//用户投诉信息end...
	
		//用户投诉内容begin...
		$("#complaint-detail").html("");
       $.ajax({
			type: "POST",
			url:"/CustomerServiceSystem/CSSAction/setTest",
			data:$('#searchForm').serialize(),// id
			dataType:'json',
		    success: function(data) {
		        var problemLi = '';
		        for (var i in data) {
		    		var item = data[i];
		    		problemLi+="<li><b>问题描述</b>："+item.id+"</li>";
					problemLi+="<li><b>客服查证</b>："+item.name+"</li>";
					problemLi+="<li><b>用户要求</b>："+item.password+"</li>";
					problemLi+="<li><b>备注</b>："+item.age+"</li>";
		     }
				$("#complaint-detail").html(problemLi);
		}})
		//用户投诉内容end...

          //定界结果begin...
		  $.ajax({
			type: "POST",
			url:"/CustomerServiceSystem/CSSAction/secondLine",
			data:$('#searchForm').serialize(),// id
			dataType:'json',
		    success: function(res) {
		    // console.log("res"+res[0]);
		     var boundLi = '';
		     for (var i in res) {
		    		var item = res[i];
		    		boundLi+="<li><b>定界结果</b>："+item.result+"</li>";
					boundLi+="<li><b>投诉处理建议</b>："+item.advice+"</li>";
		     }
		   
				$("#bound-detail").html(boundLi);
		}})
		//定界结果end...

		me.initHeight();
       // me.initMap();
        me.loadGisCell();
        me.loadGisPop();
       
        
	},
	loadCause: function() {
        var me = this;
        $.ajax({
			type: "POST",
			url:"/CustomerServiceSystem/CSSAction/getResultPieChart",
			data:$('#searchForm').serialize(),// id
			dataType:'json',
		    success: function(res) {
		     //console.log("res"+res);
		     me.drawCauseTable("bound_table", res);
		}})
        var badCellCause = [{ "name": "无线", "value": 0.2 },
			    { "name": "核心网", "value": 0.1 },
			    { "name": "DNS服务器", "value": 0.1 },
			    { "name": "DNS用户", "value": 0.1 },
			    { "name": "终端", "value": 0.1 },
			    { "name": "SP", "value": 0.1 },
			    { "name": "无线原因（个别用户）", "value": 0.1 },
			    { "name": "用户", "value": 0.1 }
			];
        /*chart.drawCause("bound_ring", me.parsePieData(badCellCause));*/
       // me.drawCauseTable("bound_table", badCellCause);
    },
    loadAnalysis:function(){
    	//分析定位结果
		var analysisLi = '';
		analysisLi+="<li><b>原因分析</b>：经分析占用-XXXXX-L-51，25日利用率突增峰值利用率80%，综上判断为容量问题导致</li>";
		analysisLi+="<li><b>优化措施</b>：扩容优化，做好用户沟通解释工作</li>";
		analysisLi+="<li><b>依小区维度，输出圈选小区质差及异常事件产生主要原因；及针对主要原因，给出优化建议</b></li>";
		$("#analysis-detail").html(analysisLi);
    },
    badCiMap: {'ci_name':'小区名称','bad_call':'质差话单数','alarm':'告警','down_weak':'下行弱覆盖','down_disturb':'下行干扰','up_disturb':'上行干扰','up_weak':'上行弱覆盖','load':'负荷','often_event':'频繁事件'},
    loadCiTable:function(tab){
    	var me = this;
    	console.log(tab);
	    $('#bad_ci_table').html("");
	    if ($('#bad_ci_table').hasClass('dataTable')) {
	        var dttable = $('#bad_ci_table').dataTable();
	        dttable.fnClearTable(); //清空一下table
	        dttable.fnDestroy(); //还原初始化了的datatable
	    }

	    var columns = [];
	    for(var prop in me.badCiMap){
	      columns.push({title:me.badCiMap[prop],"data":prop});
	    }
	    var data = [];
	    for(var i = 1; i<=15; i++){
	    	data.push({
	    		'ci_name':'小区'+i,
				'bad_call':me.random(40,50),
				'alarm':me.random(10,20),
				'down_weak':me.random(1,10),
				'down_disturb':me.random(1,30),
				'up_disturb':me.random(10,20),
				'up_weak':me.random(0,5),
				'load':me.random(0,10),
				'often_event':me.random(0,3)
	    	});
	    }

	    $("#bad_ci_table").DataTable({
	      	"bFilter": false, // 过滤功能
	        "iDisplayLength": 5,
	        "ordering": false,
	        "bLengthChange": false, // 改变每页显示数据数量
	        'scrollX': true,
	        "data":data,
	        "columns":  columns
	    });
    },
    cdrBusinessMap: {'ci_name':'占用小区','business_map':'业务类型','app':'业务APP','begin_time':'业务开始时间','end_time':'业务结束时间','cause':'定位原因'},
    cdrExceptMap: {'ci_name':'占用小区','type':'事件类型','status':'事件状态','begin_time':'事件开始时间','end_time':'事件结束时间','cause':'定位原因'},
    loadCdrTable:function(tab){
    	var me = this;
	    $('#cdr_table').html("");
	    if ($('#cdr_table').hasClass('dataTable')) {
	        var dttable = $('#cdr_table').dataTable();
	        dttable.fnClearTable(); //清空一下table
	        dttable.fnDestroy(); //还原初始化了的datatable
	    }

	    var titles = [];
	    if(tab=='business'){
	    	titles = me.cdrBusinessMap;
	    } else {
	    	titles = me.cdrExceptMap;
	    }

	    var columns = [];
	    for(var prop in titles){
	      columns.push({title:titles[prop],"data":prop});
	    }
	    var data = [];
	    if(tab=='business'){
	    	for(var i = 1; i<=15; i++){
		    	data.push({
		    		'ci_name':'小区'+i,
					'business_map':me.random(40,50),
					'app':me.random(10,20),
					'begin_time':me.random(1,10),
					'end_time':me.random(0,5),
					'cause':'cause'+i,
		    	});
		    }
	    } else {
	    	for(var i = 1; i<=15; i++){
		    	data.push({
		    		'ci_name':'小区'+i,
					'type':me.random(40,50),
					'status':me.random(10,20),
					'begin_time':me.random(1,10),
					'end_time':me.random(0,5),
					'cause':'cause'+i,
		    	});
		    }
	    }
	    
	    $("#cdr_table").DataTable({
	      	"bFilter": false, // 过滤功能
	        "iDisplayLength": 5,
	        "ordering": false,
	        "bLengthChange": false, // 改变每页显示数据数量
	        "data":data,
	        "columns":  columns
	    });
    },
    //无线质差话单趋势
    loadBadLine:function(){
    	var me = this;
    	var xArr = [];
    	var allArr = [];
    	var badArr = [];
    	var exceptArr = [];
    	$.ajax({
			type: "POST",
			url:"/CustomerServiceSystem/CSSAction/loadBadLine",
			data:$('#searchForm').serialize(),// id
			dataType:'json',
		    success: function(data) {
		    	/*for (var i in data) {
			    		var item = data[i];
			    	console.log(item);
			     }*/
		    	/*xArr=data.m1;
		    	allArr=data.m2;
		    	badArr=data.m3;
		    	exceptArr=data.m4;
		    	console.log(data.m1);*/
		    	var badLine = {
		        		id:'bad_line',
		        		legend:['全部', '业务质差话单', '异常事件'],
//		        		xArr:xArr,
		        		xArr:data.m1,
		        		series:[{
		                        name: "全部",
		                        type: "line",
		                        smooth: true,
		                        symbol: 'circle',
		                        symbolSize: 10,
		                        //data: allArr,
		                        data: data.m2,
		                        itemStyle: {
		                            normal: {
		                                color: '#5fbdff',
		                                lineStyle: {
		                                    width: 3
		                                }
		                            }
		                        }
		                    },{
		                        name: "业务质差话单",
		                        type: "line",
		                        smooth: true,
		                        symbol: 'circle',
		                        symbolSize: 10,
		                       // data: badArr,
		                        data: data.m3,
		                        itemStyle: {
		                            normal: {
		                                color: '#ff975f',
		                                lineStyle: {
		                                    width: 3,
		                                    type: 'dotted',
		                                }
		                            }
		                        }
		                    },{
		                        name: "异常事件",
		                        type: "line",
		                        smooth: true,
		                        symbol: 'circle',
		                        symbolSize: 10,
		                        //data: exceptArr,
		                        data: data.m4,
		                        itemStyle: {
		                            normal: {
		                                color: '#86ce80',
		                                lineStyle: {
		                                    width: 3,
		                                    type: 'dotted',
		                                }
		                            }
		                        }
		                    }
		                ]
		        	}
		        	chart.drawLineChart(badLine);
		    
		}})
    	
    	/*if(me.day_label=='day'){
    		var date = $("#date_d").val();
    		for(var i = 0; i<24; i++){
    			if(i<10){
    				xArr[i] = date+" 0"+i;
    			} else {
    				xArr[i] = date+" "+i;
    			}
    			allArr[i] = me.random(30,40);
    			badArr[i] = me.random(20,30);
    			exceptArr[i] = me.random(10,20);
    		}
    	}
    	if(me.day_label=='hour'){
    		var date = $("#date_h").val();
    		for(var i = 0; i<12; i++){
    			if(i*5<10){
    				xArr[i] = date+":0"+i*5;
    			} else {
    				xArr[i] = date+":"+i*5;
    			}
    			allArr[i] = me.random(20,30);
    			badArr[i] = me.random(10,20);
    			exceptArr[i] = me.random(1,10);
    		}
    	}*/
    	console.log(xArr);
    	console.log(allArr);
    	console.log(badArr);
    	console.log(exceptArr);

    },
    loadBadColumn:function(){
    	var me = this;
    	var xArr = [];
    	var allArr = [];
    	var badArr = [];
    	var exceptArr = [];
		for(var i = 0; i<6; i++){
			xArr[i] = "话单"+(i+1);
			allArr[i] = me.random(30,40);
			badArr[i] = me.random(20,30);
			exceptArr[i] = me.random(10,20);
		}
    	var badColumn = {
    		id:'bad_column',
    		legend:['全部', '业务质差话单', '异常事件'],
    		xArr:xArr,
    		series: [{
                    name: '全部',
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
                    data: allArr,
                    zlevel: 1
                },{
                    name: '业务质差话单',
                    yAxisIndex: 0,
                    type: 'bar',
                    barWidth: '12',
                    itemStyle: {
                        normal: {
                            barBorderRadius: [30, 30, 0, 0],
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1, [{
                                        offset: 0,
                                        color: 'rgb(55,144,241)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(51,194,253)'
                                    }
                                ]
                            )
                        }
                    },
                    data: badArr,
                    zlevel: 1
                }, {
                    name: '异常事件',
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
                    data: exceptArr,
                    zlevel: 1
                }
            ]
    	}
    	chart.drawColumn(badColumn);
    },
    loadIndexLine:function(tab){
    	var me = this;
    	var xArr = [];
    	var numArr = [];
    	var avgArr = [];
    	if(me.day_label=='day'){
    		var date = $("#date_d").val();
    		for(var i = 0; i<24; i++){
    			if(i<10){
    				xArr[i] = date+" 0"+i;
    			} else {
    				xArr[i] = date+" "+i;
    			}
    			numArr[i] = me.random(30,40);
    			avgArr[i] = me.random(20,30);
    		}
    	}
    	if(me.day_label=='hour'){
    		var date = $("#date_h").val();
    		for(var i = 0; i<12; i++){
    			if(i*5<10){
    				xArr[i] = date+":0"+i*5;
    			} else {
    				xArr[i] = date+":"+i*5;
    			}
    			numArr[i] = me.random(30,40);
    			avgArr[i] = me.random(20,30);
    		}
    	}
    	var indexLine = {
    		id:'index_line',
    		legend:['比例', '均值'],
    		xArr:xArr,
    		series:[{
                    name: "比例",
                    type: "line",
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 10,
                    data: numArr,
                    itemStyle: {
                        normal: {
                            color: '#86ce80',
                            lineStyle: {
                                width: 3
                            }
                        }
                    }
                },{
                    name: "均值",
                    type: "line",
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 10,
                    data: avgArr,
                    itemStyle: {
                        normal: {
                            color: '#ff975f',
                            lineStyle: {
                                width: 3,
                                type: 'dotted',
                            }
                        }
                    }
                }
            ]
    	}
    	chart.drawLineChart(indexLine);
    },
    loadIndexColumn:function(tab){
    	var me = this;
    	var xArr = [];
    	var numArr = [];
    	var avgArr = [];
		for(var i = 0; i<6; i++){
			xArr[i] = "小区"+(i+1);
			numArr[i] = me.random(30,40);
			avgArr[i] = me.random(20,30);
		}
    	var indexColumn = {
    		id:'index_column',
    		legend:['比例', '均值'],
    		xArr:xArr,
    		series: [{
                    name: '比例',
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
                    data: numArr,
                    zlevel: 1
                },{
                    name: '均值',
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
                    data: avgArr,
                    zlevel: 1
                }
            ]
    	}
    	chart.drawColumn(indexColumn);
    },
    random:function(min,max){
    	return Math.floor(Math.random()*(max-min+1)+min);
    },
    parsePieData: function(data) { //圆环数据
        var me = this;
        var lagend = [];
        var serieData = [];
        $.each(data, function(i, obj) {
            lagend[i] = obj.name;
            serieData[i] = { name: obj.name, value: me.formatNum(obj.value * 100, 0) };
        });
        return [lagend, serieData];
    },
    drawCauseTable: function(htmlid, data) { //根因判断染色列table
        var me = this;
        $("#" + htmlid).html("");
        var table = "<table class='table a_style' style='border:2px solid #f4f4f4;'>";
        table += "<tr>";
        $.each(data, function(i, obj) {
            table += "<td>" + obj.name + "</td>";
        });
        table += "</tr>";
        table += "<tr id='" + htmlid + "'>";
        $.each(data, function(i, obj) {
            table += "<td style='background-color:" + chart.colors[i] + "'>" + me.formatNum(obj.value * 100, 0) + '%' + "</a></td>";
        });
        table += "</tr>";
        table += "</table>";
        $("#" + htmlid).html(table);
    },
    
	loadBound:function(){

	},
	initHeight:function(){
		$("#right-height").height($("#left-height").height()-10);
	},
	formatNum: function(num, digits) { //数字格式化
        var me = this;
        if (!num) {
            return 0;
        } else if (typeof num == "number") {
            return num.toFixed(digits);
        } else {
            return 0;
        }
    }
});
var intelligent = new N.Intelligent();
$(function() {
	intelligent.init();
	$(window).resize(function () {
		intelligent.initHeight();
	});
});