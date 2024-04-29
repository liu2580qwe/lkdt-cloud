
var prefix = "/fog/verificationlog"

var epId=$("#epId").val();
var pieoption = {
	    title : {
	        text: '校验统计',
	        subtext: '时间',
	        x:'center',
	        textStyle: {
                color: '#ffffff'
            }
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{b} : {c} ({d}%)"
	    },
	    series : [
	        {
	            name: 'pie',
	            type: 'pie',
	            radius : '60%',
	            center: ['50%', '60%'],
	            data:[],
	            itemStyle: {
	                emphasis: {
	                    shadowBlur: 10,
	                    shadowOffsetX: 0,
	                    shadowColor: 'rgba(0, 0, 0, 0.5)'
	                },
	                normal:{ 
				           label:{ 
				              show: true, 
				              formatter: '{b} : {c} ({d}%)' 
				              }, 
				              labelLine :{show:true} 
				              }
	            }
	        }
	    ]
	};
var baroption={
	title: {
        text: '校验统计',
        subtext: '校验人',
    	textStyle: {
            color: '#ffffff'
        }
    },
    legend: {
        data: ['校验次数', '校验摄像头数'],
        textStyle: {
            color: '#ffffff'
        }
    },
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    xAxis: {
        type: 'category',
        name: '用户名',
        axisLine: {
            lineStyle: {
                color: '#57617B'
            }
        },
        axisLabel: {
            textStyle: {
                color:'#fff',
            },
        },
        nameTextStyle:{
            color: '#ffffff'
        },
        data: []
    },
    yAxis: {
    	name: '数量',
    	axisLine: {
            lineStyle: {
                color: '#57617B'
            }
        },
        axisLabel: {
            textStyle: {
                color:'#fff',
            },
        },
        nameTextStyle:{
            color: '#ffffff'
        },
        type: 'value'
    },
    series: [
		{
	        name: '校验次数',
	        type: 'bar',
	        barGap: 0,
	        data: [],
	        itemStyle: {
				normal: {
					label: {
						show: true, //开启显示
						position: 'top', //在上方显示
						textStyle: { //数值样式
							color: 'white',
							fontSize: 16
						}
					}
				}
			}

	    },
	    {
	        name: '校验摄像头数',
	        type: 'bar',
	        data: [],
	        itemStyle: {
				normal: {
					label: {
						show: true, //开启显示
						position: 'top', //在上方显示
						textStyle: { //数值样式
							color: 'white',
							fontSize: 16
						}
					}
				}
			}
	    }
    ]
};
$(function() {
	$.ajax({
		type : 'get',
		url : prefix + "/logSta",
		success : function(data) {
			
			console.log(data);
			var piedata=data.piedata;
			var wbtj=piedata.all-piedata.one-piedata.two-piedata.thr;
			pieoption.series[0].data=[{value:piedata.one,name:'30天内'},{value:piedata.two,name:'30-60天'},{value:piedata.thr,name:'超过60天'},{value:wbtj,name:'未校验'}]
			var bardata=data.bardata;
			for(var i=0;i<bardata.length;i++){
				baroption.xAxis.data.push(bardata[i].createtor)
				baroption.series[0].data.push(bardata[i].count)
				baroption.series[1].data.push(bardata[i].count2)
			}
			var pieChart = echarts.init(document.getElementById("echarts-pie-chart"));
			pieChart.setOption(pieoption);
			var barChart = echarts.init(document.getElementById("echarts-bar-chart"));
			barChart.setOption(baroption);
			$(window).resize(pieChart.resize);
			$(window).resize(barChart.resize);
			
		}
	});
});

/*var rows=data.rows;
var series=[];
var xAxis=[];
for(var i=0;i<rows.length;i++){
	series.push(rows[i].visibledist);
	xAxis.push(rows[i].createtime);
}
var xAxisObj=[{"type":"category","boundaryGap": false,data:xAxis}]
var obj=[{"name":"可见距离(m)","type": "line",data:series}]
lineoption.series = obj;
lineoption.xAxis = xAxisObj;*/

