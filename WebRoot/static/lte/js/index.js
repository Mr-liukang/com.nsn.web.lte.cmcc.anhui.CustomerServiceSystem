var sdate = "2016-05-11";
var tabIndex = 0;
var loadFlag = new Array();

$(function() {
  $("#datetimepicker").val(sdate);
  $("#chartWrap").css('height', document.body.clientHeight - 136).css('background', '#ffffff');
  query();
});

function query() {
  sdate = $("#datetimepicker").val();
  loadFlag = new Array();
  queryNum("V_usercnt_Snap", "label1", "", "142090");
  queryNum("V_roaming_usercnt_Snap", "label2", "", "29325");
  queryNum("V_CALL_SETUP_AUDIO_Snap", "label3", "", "628695");
  queryNum("V_CALL_SETUP_VIDEO_Snap", "label4", "", "1951");
  queryNum("V_audio_traffic_Snap", "label5", "G", "428.34");
  queryNum("V_video_traffic_Snap", "label6", "G", "2.91");

  queryLineChart("V_REGISTER_SUCCRATE", "dmChart00", "全网注册成功率(%)", "%", V_REGISTER_SUCCRATE, "business.html");
  queryLineChart("V_MEDIA_TRAFFIC", "dmChart01", "全网呼叫总流量(G)", "G", V_MEDIA_TRAFFIC, "business.html");
  queryLineChart("V_MEDIA_TRAFFIC", "dmChart02", "活跃用户数", "", V_MEDIA_TRAFFIC, "business.html");
  var chartData = [];
  for (var i = 0; i < 6; i++) {
    if (i == 0) {
      var BRAND = data[i]['PRODUCT'];
      if (data[i]['PRODUCT']) {
        BRAND = '-' + BRAND;
      }
      chartData[i] = {
        name: data[i]['BRAND'] + BRAND,
        y: data[i]['NO_OF_USER'],
        sliced: true,
        selected: true
      };
    } else {
      var BRAND = data[i]['PRODUCT'];
      if (data[i]['PRODUCT']) {
        BRAND = '-' + BRAND;
      }
      chartData[i] = [data[i]['BRAND'] + BRAND, data[i]['NO_OF_USER']];
    }
  }
  drawPieChart("dmChart30", "用户数最多的5种终端", "用户数", chartData, "terminal.html");

  if (loadFlag[tabIndex] == null) {
    loadFlag[tabIndex] = true;
    load();
  }
  var str = "";
  if (data.length > 0) {
    str = str + "<thead><tr>";
    str = str + "<th align='center' style='font-weight: bold;'>TOP终端型号</th>";
    str = str + "<th align='center' style='font-weight: bold;'>用户数</th>";
    str = str + "<th align='center' style='font-weight: bold;'>渗透率(%)</th>";
    str = str + "<th align='center' style='font-weight: bold;'>流量(G)</th>";
    str = str + "</tr></thead><tbody>";

    for (var i = 0; i < data.length; i++) {
      str = str + "<tr>";
      var brand = data[i]['BRAND'];
      if (data[i]['BRAND']) {
        brand + "-";
      }
      str = str + "<td align='center'>" + brand + data[i]['PRODUCT'] + "</td>";
      str = str + "<td align='center'>" + data[i]['NO_OF_USER'] + "</td>";
      str = str + "<td align='center'>" + data[i]['PENETRATION_RATE'] + "</td>";
      str = str + "<td align='center'>" + data[i]['TOTAL_TRAFFIC'] + "</td>";
      str = str + "</tr>";
    }
    str = str + "</tbody>";
  }
  $("#t30").html(str);
}

function tabClick(i) {
  tabIndex = i;
  if (loadFlag[tabIndex] == null) {
    loadFlag[tabIndex] = true;
    load();
  }
}

function load() {
  if (tabIndex == 0) {
    setTimeout("load0()", 1);
  } else if (tabIndex == 1) {
    setTimeout("load1()", 1);
  } else if (tabIndex == 2) {
    setTimeout("load2()", 1);
  }
}

function load0() {
  queryLineChart("V_A_CALL_SETUP_SUCCRATE", "dmChart10", "VoLTE接通率(%)", "%", V_A_CALL_SETUP_SUCCRATE, "business.html");
  queryLineChart("V_A_CALL_SETUP_LATENCY", "dmChart11", "VoLTE呼叫建立时延(s)", "s", V_A_CALL_SETUP_LATENCY, "business.html");
  queryLineChart("V_A_CALL_DROP_RATE", "dmChart12", "VoLTE掉话率(%)", "%", V_A_CALL_DROP_RATE, "business.html");
  queryLineChart("V_A_MOS_VALUE", "dmChart13", "MOS值", "", V_A_MOS_VALUE, "business.html");
  queryLineChart("V_A_esrvcc_succrate", "dmChart14", "eSRVCC切换成功率(%)", "%", V_A_esrvcc_succrate, "business.html");
  queryLineChart("V_A_esrvcc_latency", "dmChart15", "eSRVCC切换时长(ms)", "ms", V_A_esrvcc_latency, "business.html");
}

function load1() {
  queryLineChart("V_V_CALL_SETUP_SUCCRATE", "dmChart20", "VoLTE接通率(%)", "%", V_V_CALL_SETUP_SUCCRATE, "business.html");
  queryLineChart("V_V_CALL_SETUP_LATENCY", "dmChart21", "VoLTE呼叫建立时延(s)", "s", V_V_CALL_SETUP_LATENCY, "business.html");
  queryLineChart("V_V_CALL_DROP_RATE", "dmChart22", "VoLTE掉话率(%)", "%", V_V_CALL_DROP_RATE, "business.html");
  queryLineChart("V_V_JITTER_VALUE", "dmChart23", "抖动", "", V_V_JITTER_VALUE, "business.html");
  queryLineChart("V_V_esrvcc_succrate", "dmChart24", "eSRVCC切换成功率(%)", "%", V_V_esrvcc_succrate, "business.html");
  queryLineChart("V_V_esrvcc_latency", "dmChart25", "eSRVCC切换时长(ms)", "ms", V_V_esrvcc_latency, "business.html");
}

function load2() {
  //queryLineChart("V_V_esrvcc_latency1", "dmChart26", "eSRVCC切换时长(ms)", "ms", V_V_esrvcc_latency, "business.html");
  var chart = echarts.init(document.getElementById('dmChart26'),'macarons');
  chart.setOption(option, true)
}

function queryNum(table, htmlid, unit, data) {
  if (data.length > 0) {
    for (var key in data[0]) {
      if (key != "SDATE") {
        $('#' + htmlid).html(data + unit);
      }
    }
  }
}

function queryLineChart(table, htmlid, title, unit, data, url) {
  drawLineChart(htmlid, title, unit, parseLineChartSrcData(data), url);
}

function drawLineChart(htmlid, title, unit, data, url) {
  var colors = ['#32A88C', '#4F81BD', '#E46C0A'];
  $('#' + htmlid).highcharts({
    chart: {
      events: {
        click: function(e) {
          if (url) {
            window.open(url, '_blank');
          };
        }
      }
    },
    title: {
      text: title,
      style: {
        fontFamily: "微软雅黑"
      }
    },
    xAxis: {
      tickInterval: 2,
      categories: data[0],
      labels: {
        style: {
          fontFamily: "微软雅黑"
        }
      }
    },
    yAxis: [{
      title: null,
      max: data[4],
      min: data[5],
      labels: {
        style: {
          fontFamily: "微软雅黑"
        },
        formatter: function() {
          return this.value + unit
        }
      }
    }],
    legend: {
      enabled: false,
      itemStyle: {
        fontFamily: "微软雅黑"
      }
    },
    tooltip: {
      headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
      pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:.2f} ' + unit + '</b></td></tr>',
      footerFormat: '</table>',
      shared: true,
      useHTML: true,
      style: {
        fontFamily: "微软雅黑"
      }
    },
    plotOptions: {
      series: {
        marker: {
          enabled: false,
        }
      }
    },
    series: [{
      name: '当天',
      color: colors[0],
      data: data[1]
    }, {
      name: '一周平均',
      color: colors[1],
      data: data[2]
    }, {
      name: '预警值',
      color: colors[2],
      dashStyle: 'dash',
      data: data[3]
    }]
  });
}

function drawPieChart(htmlid, title, serie, data, url) {
  $('#' + htmlid).highcharts({
    chart: {
      plotBackgroundColor: null,
      plotBorderWidth: null,
      plotShadow: false,
      events: {
        click: function(e) {
          if (url) {
            window.open(url, '_blank');
          };
        }
      }
    },
    title: {
      text: title,
      style: {
        fontFamily: "微软雅黑"
      }
    },
    legend: {
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'middle',
      borderWidth: 0,
      itemStyle: {
        fontFamily: "微软雅黑"
      }
    },
    tooltip: {
      //pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>',
      pointFormat: '{series.name}: <b>{point.y}</b>',
      style: {
        fontFamily: "微软雅黑"
      }
    },
    plotOptions: {
      pie: {
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: false
        },
        showInLegend: true
      }
    },
    series: [{
      type: 'pie',
      name: serie,
      dataLabels: {
        formatter: function() {
          return this.y > 1 ? '<b>' + this.point.name + ': ' + this.point.percentage.toFixed(2) + ' %</b>' : null;
        }
      },
      data: data
    }]
  });
}

function parseLineChartSrcData(data) {
  var xArr = [];
  var y1Arr = [];
  var y2Arr = [];
  var y3Arr = [];
  var colList = [];
  var max = null;
  var min = null;
  if (data.length > 0) {
    for (var key in data[0]) {
      colList[colList.length] = key;
    }
    for (var i = 0; i < data.length; i++) {
      xArr[xArr.length] = data[i][colList[1]];
      y1Arr[y1Arr.length] = data[i][colList[2]];
      y2Arr[y2Arr.length] = data[i][colList[3]];
      y3Arr[y3Arr.length] = data[i][colList[4]];
    }
  }
  for (var i = 0; i < y1Arr.length; i++) {
    if (max == null) {
      max = y1Arr[i];
    }
    if (min == null) {
      min = y1Arr[i];
    }
    if (max < y1Arr[i]) {
      max = y1Arr[i];
    }
    if (min > y1Arr[i]) {
      min = y1Arr[i];
    }
  }
  for (var i = 0; i < y2Arr.length; i++) {
    if (max == null) {
      max = y2Arr[i];
    }
    if (min == null) {
      min = y2Arr[i];
    }
    if (max < y2Arr[i]) {
      max = y2Arr[i];
    }
    if (min > y2Arr[i]) {
      min = y2Arr[i];
    }
  }
  for (var i = 0; i < y3Arr.length; i++) {
    if (max == null) {
      max = y3Arr[i];
    }
    if (min == null) {
      min = y3Arr[i];
    }
    if (max < y3Arr[i]) {
      max = y3Arr[i];
    }
    if (min > y3Arr[i]) {
      min = y3Arr[i];
    }
  }
  var result = [xArr, y1Arr, y2Arr, y3Arr, max, min];
  return result;
}
