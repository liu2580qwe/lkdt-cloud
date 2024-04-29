
var prefix = "/system/alarm"
$(function() {
	laydate.render({
		elem: '#year',
		type: 'year',
		value: new Date()
	});
	selectInfo();
});

function selectInfo() {
	var year = $('#year').val();
	if(year==""){
		var d = new Date();
		year = d.getFullYear();
	}
	var hwId = $('#hwId').val();
	var camcode = $('#epid').val();
	$.ajax({
		type : 'get',
		url : prefix + '/alarmStatistics',
		data:{'year':year, 'hwId':hwId,'cameraId':camcode},
		success : function(data) {
			var myChart = echarts.init(document.getElementById("info"));
			var date = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10','11','12'];
			var option = {
				    title: {
				        text: '告警情况统计', //主标题
				        x: 'center', //标题位置
			        	textStyle: {
                            color: '#ffffff'
                        }
				    }, //图表标题
				    tooltip: {
				        trigger: 'axis'
				    },
				    calculable: true,
				    legend: {
				        data: ['告警', '误报'],
				        x: '50px',
				        y: '5px',
				        textStyle:{
                            fontSize: 18,//字体大小
                            color: '#ffffff'//字体颜色
                        }

				    },
				    xAxis: [{
				            type: 'category',
				            data: date,
				            axisTick: {
				                alignWithLabel: true, //刻度线和标签对齐
				                interval: 0 //间隔
				            },
				            splitLine: {
				                show: false, //网格线开关
				            },
				            axisLabel: {
	                            show: true,
	                            textStyle: {
	                                color: '#ffffff'
	                            }
	                        }
				        },

				    ],
				    yAxis: [{
				        type: 'value',
				        name: '（次）',
				        splitLine: { // 分隔线
				            show: false, // 默认显示，属性show控制显示与否

				        },
				        axisLine: {
				            show: true // 坐标轴是否显示
				        },
				        axisTick: {
				            show: true //坐标轴刻度线是否显示
				        },
				        splitLine: {
				            show: true, //网格线开关
				        },
				        axisLabel : {
                            textStyle: {
                                color: '#ffffff'
                            }
                        }
				    }],
				    series: [

				        {
				            name: '告警',
				            type: 'bar',
				            data: data[0],
				            itemStyle: {
				                normal: {
				                    //barBorderRadius: 15,
				                    color: '#A9DBF6'
				                }
				            }

				        },
				        {
				            name: '误报',
				            type: 'bar',
				            data: data[1],
				            itemStyle: {
				                normal: {
				                    color: '#006EDD'
				                }
				            },
				        }
				    ]
				};
			myChart.setOption(option);
	        $(window).resize(myChart.resize);
 		}
	});
}

 


var openHighway = function(){
	layer.open({
		type:2,
		title:"选择公路",
		area : [ '300px', '450px' ],
		content:"/system/highway/treeView"
	})
}
var hid=null;
function loadHy( node ){
	var	hwId = node.id;
	var name = node.text;
	$("#hwId").val(hwId);
	$("#name").val(name);
	$("#epid").val("");
	$("#camName").val("");
	hid = hwId;
}
function loadCamera( epId,equName){
	$("#epid").val(epId);
	$("#camName").val(equName);
}



var openCamera = function(){
	layer.open({
		type:2,
		title:"选择摄像头",
		area : [ '400px', '450px' ],
		content:"/system/alarm/equipment/"+ hid
	})
}