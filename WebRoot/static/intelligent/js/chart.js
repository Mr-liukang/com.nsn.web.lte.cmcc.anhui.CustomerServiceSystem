N.Chart = N.Class.extend({
    colors: ['#2ec7c9', '#b6a2de', '#5ab1ef', '#ffb980', '#d87a80', '#8d98b3', '#e5cf0d', '#97b552', '#95706d'],
    drawCause: function(htmlid, data) { //根因判断染色列圆环
        var me = this;
        echarts.dispose(document.getElementById(htmlid)); //销毁实例
        var myChart = echarts.init(document.getElementById(htmlid));
        var chartOption = {
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c}% ({d}%)",
                extraCssText:'width:100px;white-space:pre-wrap;'
            },
            color: me.colors,
            series: [{
                name: '',
                type: 'pie',
                radius: ['60%', '85%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        }
                    },
                    emphasis: {
                        label: {
                            show: true,
                            position: 'center',
                            textStyle: {
                                fontSize: '12',
                                fontWeight: 'bold'
                            }
                        }
                    }
                },
                data: data[1]
            }]
        };
        myChart.setOption(chartOption, true);
    },
    drawLineChart: function(options) {
        var me = this;
        echarts.dispose(document.getElementById(options.id)); //销毁实例
        var myChart = echarts.init(document.getElementById(options.id));
        var option = {
            tooltip: {
                trigger: 'axis',
                position: function(pt) {
                    return [pt[0], '14%'];
                }
            },

            legend: {
                data: options.legend,
                x: 'center',
                y: 'bottom'
            },

            xAxis: {
                type: 'category',
                axisLine: {
                    show: false,
                },
                axisTick: {
                    show: false,
                },
                data: options.xArr
            },
            yAxis: [{
                type: 'value',
                axisLine: {
                    show: false,
                },
                axisTick: {
                    show: false,
                },
                splitLine: {
                    lineStyle: {
                        type: 'dotted',
                    }
                },
                splitArea: { //背景条纹
                    show: true,
                    areaStyle: {
                        color: [
                            'rgba(255,255,255,0)',
                            'rgba(242,243,248,1)'
                        ]
                    }
                },
                min: 0,
                splitNumber: 5
            }],
            grid: {
                left: '1rem',
                right: '4%',
                bottom: '10%',
                top: '14%',
                containLabel: true,
                borderWidth: '0'
            },
            series: options.series
        };
        myChart.setOption(option, true);
    },
    drawColumn: function(options) {
        var me = this;
        echarts.dispose(document.getElementById(options.id)); //销毁实例
        var myChart = echarts.init(document.getElementById(options.id));
        var option = {
            backgroundColor: "#fff",
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross' //
                }
            },
            legend: {
                data: options.legend,
                x: 'center',
                y: 'bottom'
            },
            grid: {
                left: '1rem',
                right: '4%',
                bottom: '10%',
                top: '14%',
                containLabel: true,
                borderWidth: '0'
            },
            xAxis: [{
                type: 'category',
                data: options.xArr,
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle: {
                        color: 'rgb(205,205,205)'
                    }
                },
                splitLine: {
                    lineStyle: {
                        type: 'dotted',
                    }
                },
                axisLabel: {
                    show: true,
                    color: 'rgb(170,170,170)',
                    fontSize: 16
                }
            }],
            yAxis: [{
                name: '个数',
                nameTextStyle: {
                    color: 'rgb(68,68,68)',
                    align: 'left',
                    padding: [0, 26, 0, 0]
                },
                type: 'value',
                splitLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: true,
                    lineStyle: {
                        color: 'rgb(205,205,205)'
                    }
                },
                axisLabel: {
                    color: 'rgb(68,68,68)'
                },
                splitArea: { //背景条纹
                    show: true,
                    areaStyle: {
                        color: [
                            'rgba(255,255,255,0)',
                            'rgba(242,243,248,1)'
                        ]
                    }
                },
            }],
            series: options.series
        };
        myChart.setOption(option, true);
    }

});
var chart = new N.Chart();