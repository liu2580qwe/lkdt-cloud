/*公路交通能见度option*/
let gonglujiaotongnengjiandu_left_option = {
    title: {
        bottom: '5%',
        left: 'center',
        textStyle:{
            fontSize: 15,
            color:'#fff'
        }
    },
    /*tooltip: {//鼠标指上时的标线
        trigger: 'axis',
        showContent: false,
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        }
    },*/
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'line',        // 默认为直线，可选为：'line' | 'shadow' | 'cross'
            label: {
                backgroundColor: '#283b56'
            }
        },
        textStyle: {
            fontSize:10
        },
        position: function (pos, params, el, elRect, size) {
            var obj = {top: 10};
            obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
            return obj;
        },
    },
    visualMap: {
        show: false,
        top: 'top',
        left:'middle',
        color:['#2BB949'],
        // color:['#2BB949'],
        textStyle:{
            color:'#fff'
        },
        pieces: [{
            gt: 0,
            lte: 30,
            color: '#9a1200'
        }, {
            gt: 30,
            lte: 50,
            color: '#d81e06'
        }, {
            gt: 50,
            lte: 100,
            color: '#ffa500'
        }, {
            gt: 100,
            lte: 200,
            color: '#f4ea2a'
        }, {
            gt: 200,
            color: '#48ef8b'
        }]
    },
    grid:{
        x:25,
        y:8,
        x2:5,
        y2:5,
    },
    xAxis: {
        type: 'category',
        //name: 'HH:ss',
        data: [],//['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        boundaryGap: false,
        axisLine: {
            lineStyle: {
                color: '#57617B'
            }
        },
        axisLabel: {
            show: false,
            textStyle: {
                color:'#fff',
            },
        },
        nameTextStyle:{
            color: '#ffffff'
        }
    },
    yAxis: {
        type: 'value',
        scale: true,
        //name: '可见距离（米）',
        max: 300,
        min: 0,
        axisTick: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#57617B',

            }
        },
        axisLabel: {
            margin: 2,
            textStyle: {
                fontSize: 5
            },
            textStyle: {
                color:'#fff',
            },
        },
        splitLine: {
            lineStyle: {
                color: 'rgba(255,255,255,.2)',
                type:'dotted',
            }
        },
        nameTextStyle:{
            color: '#ffffff'
        }
    },
    series: [{
        name: '可见度',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: {
            normal: {
                width: 2
            }
        },
        markLine: {
            silent: true,
            symbol:'none',
            lineStyle:{
                color:'#42515d',
                width:2,
                type :'dotted'
            },
            data: [
            	{
            		yAxis: 200,
	            	label:{
                        position:'end',
                        formatter:"",
                        textStyle: {
                            fontSize: 10
                        },
                        distance:0
                    },
                    lineStyle:{               //警戒线的样式  ，虚实  颜色
                        type:"solid",
                        color:"#3398DB",
                    }
	            }
            ],
            label:{
                position :'start',
                color:'#fff'
            }
        },
        /*areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#48ef8b'
                },{
                    offset: 0.5,
                    color: '#007eff'
                },{
                    offset: 0.75,
                    color: '#fd4a05'
                }, {
                    offset: 0.875,
                    color: '#ff0000'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
            }
        },*/
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(21, 107, 204, 0.7)'
                }, {
                    offset: 0.8,
                    color: 'rgba(21, 107, 204, 0.3)'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
            }
        },
        data: []//[97.3,99.2,99.3,100.0,99.6,90.6,80.0,91.5,69.8,67.5,90.4,84.9]
    }]
};

function convertDataTo(sorceValue){
    let value = 0;
    if(sorceValue <= 100){
        value = parseInt(sorceValue*0.3);
    }else if(sorceValue <= 200){
        value = parseInt(sorceValue*0.2+10);
    }else if(sorceValue <= 300){
        value = parseInt(sorceValue*0.5-50);
    }else if(sorceValue <= 400){
        value = parseInt(sorceValue-200);
    }else if(sorceValue <= 700){
        value = parseInt((parseInt(sorceValue))-200);
    }
    return value;
}
function convertToData(sorceValue){
    let value = 0;
    if(sorceValue <= 30){
        value = parseInt(parseInt(sorceValue)/0.3);
    }else if(sorceValue <= 50){
        value = parseInt((parseInt(sorceValue)-10)/0.2);
    }else if(sorceValue <= 100){
        value = parseInt((parseInt(sorceValue)+50)/0.5);
    }else if(sorceValue <= 200){
        value = parseInt(sorceValue)+200;
    }else if(sorceValue <= 500){
        value = parseInt(sorceValue)+200;
    }
    return value;
}

/**可见距离*/
let common_options1 = {
    title: {
        text: "",//可见距离统计
        textStyle: {
            color: '#ffffff'
        }
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        },
	    formatter:function(a){
	    	//var value = 0;
	    	// if(a[0].value <= 100){
	    	// 	value = parseInt(a[0].value*0.3);
	    	// }else if(a[0].value <= 200){
	    	// 	value = parseInt(a[0].value*0.2+10);
	    	// }else if(a[0].value <= 300){
	    	// 	value = parseInt(a[0].value*0.5-50);
	    	// }else if(a[0].value <= 400){
	    	// 	value = parseInt(a[0].value-200);
	    	// }else if(a[0].value <= 700){
	    	// 	value = parseInt((parseInt(a[0].value))-200);
	    	// }
            let value = convertDataTo(a[0].value);
	    	return  a[0].name + '<br/>'+ a[0].marker + a[0].seriesName + '：' + value;
	    }
    },
    grid:{
        x:45,
        x2:47,
        y:50,
        y2:20
    },
    visualMap: {
        show: false,
        top: 'top',
        left:'middle',
        color:['#16DDFD'],
        textStyle:{
            color:'#fff'
        },
        pieces: [{
            gt: 0,
            lte: 100,
            color: '#ff0000'
        }, {
            gt: 100,
            lte: 200,
            color: '#fd4a05'
        }, {
            gt: 200,
            lte: 300,
            color: '#ffeb3b'
        }, {
            gt: 300,
            lte: 400,
            color: '#007eff'
        }, {
            gt: 400,
            color: '#48ef8b'
        }]
    },
    xAxis: {
        type: 'category',
        name: 'HH:ss',
        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        boundaryGap: false,
        axisLine: {
            lineStyle: {
                color: '#57617B'
            }
        },
        axisLabel: {
        	interval:10,
            textStyle: {
                color:'#fff',
            },
            formatter : function(params) {
                return params.substr(0,2);
            }
        },
        nameTextStyle:{
            color: '#ffffff'
        },
        splitLine:{
            show:false,  //想要不显示网格线，改为false
            lineStyle: {
                color: 'rgba(255,255,255,.2)',
                type:'dotted',
            }
        }
    },
    yAxis: {
        type: 'value',
        scale: true,
        name: '可见距离（米）',
        max: 700,
        min: 0,
        axisTick: {
            show: false
        },
        axisLine: {
        	show:false,
            lineStyle: {
                color: '#57617B',

            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            },
            textStyle: {
                color:'#fff',
            },
            formatter : function(params) {
                if (params == 700) {
                    return '>500';
                }else{
                	if(params == 0){
                		return '';
                	}
                	if(params == 100){
                		return '30';
                	}
                	if(params == 200){
                		return '50';
                	}
                	if(params == 300){
                		return '100';
                	}
                	if(params == 400){
                		return '200';
                	}
                	if(params == 500){
                		return '300';
                	}
                	if(params == 600){
                		return '400';
                	}
                	
                    return params;
                }
            }
        },
        splitLine: {
            show:true,
            lineStyle: {
                color: 'rgba(255,255,255,.2)',
                type:'dotted',
            }
        },
        nameTextStyle:{
            color: '#ffffff'
        },
        axisPointer:{
        	show:false
        }
    },
    series: [{
        data: [1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000],
        type: 'line',
        name:'可见距离曲线',

        smooth: true,
        lineStyle: {
            normal: {
                width: 2
            }
        },
        markLine: {
            silent: true,
            symbol:'none',
            data: [
            	/*{
	                yAxis: 30,
	                label:{
                        formatter:"\n30  ",
                        fontSize:'12'
                    }
	            }, {
	                yAxis: 50,
	                label:{
                        formatter:"50  ",
                        fontSize:'12'
                    }
	            },*/ {
	            	yAxis: 400,
	            	label:{
                        position:'end',
                        formatter:"告警线"
                    },
                    lineStyle:{               //警戒线的样式  ，虚实  颜色
                        type:"solid",
                        color:"#3398DB",
                    }
	            }, {
	            	yAxis: 100,
	            	label:{
                        position:'end',
                        formatter:"特级线"
                    }
	            }
            ],
            lineStyle:{
                color:'#42515d',
                width:2,
                type :'dotted'
            },
            label:{
                position :'start',
                color:'#fff'
                
            }
        },
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#48ef8b'
                },{
                    offset: 0.55,
                    color: '#007eff'
                },{
                    offset: 0.67,
                    color: '#ffeb3b'
                },{
                    offset: 0.79,
                    color: '#fd4a05'
                }, {
                    offset: 0.9,
                    color: '#ff0000'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
            }
        }
    }]
};

/**可见距离【值班】*/
let common_options1_zhiban = {
    title: {
        text: "",//可见距离统计
        textStyle: {
            color: '#ffffff'
        }
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        },
        formatter:function(a){
            var value = 0;
            if(a[0].value <= 100){
                value = parseInt(a[0].value*0.3);
            }else if(a[0].value <= 200){
                value = parseInt(a[0].value*0.2+10);
            }else if(a[0].value <= 300){
                value = parseInt(a[0].value*0.5-50);
            }else if(a[0].value <= 400){
                value = parseInt(a[0].value-200);
            }else if(a[0].value <= 700){
                value = parseInt((parseInt(a[0].value))-200);
            }
            return  a[0].name + '<br/>'+ a[0].marker + a[0].seriesName + '：' + value;
        }
    },
    grid:{
        x:45,
        x2:47,
        y:50,
        y2:20
    },
    visualMap: {
        show: false,
        top: 'top',
        left:'middle',
        color:['#16DDFD'],
        textStyle:{
            color:'#fff'
        },
        pieces: [{
            gt: 0,
            lte: 100,
            color: '#ff0000'
        }, {
            gt: 100,
            lte: 200,
            color: '#fd4a05'
        }, {
            gt: 200,
            lte: 300,
            color: '#ffeb3b'
        }, {
            gt: 300,
            lte: 400,
            color: '#007eff'
        }, {
            gt: 400,
            color: '#48ef8b'
        }]
    },
    xAxis: {
        type: 'category',
        name: 'HH:ss',
        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
        boundaryGap: false,
        axisLine: {
            show:true,
            lineStyle: {
                color: '#57617B'
            }
        },
        axisLabel: {
            interval:10,
            textStyle: {
                color:'#fff',
            },
            formatter : function(params) {
                return params.substr(0,2);
            }
        },
        nameTextStyle:{
            color: '#ffffff'
        }
    },
    yAxis: {
        type: 'value',
        scale: true,
        name: '可见距离（米）',
        max: 500,
        min: 0,
        axisTick: {
            show: false
        },
        axisLine: {
            show:false,
            lineStyle: {
                color: '#57617B',

            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            },
            textStyle: {
                color:'#fff',
            },
            formatter : function(params) {
                if (params == 700) {
                    return '>500';
                }else{
                    if(params == 0){
                        return '';
                    }
                    if(params == 100){
                        return '30';
                    }
                    if(params == 200){
                        return '50';
                    }
                    if(params == 300){
                        return '100';
                    }
                    if(params == 400){
                        return '200';
                    }
                    if(params == 500){
                        return '300';
                    }
                    if(params == 600){
                        return '400';
                    }

                    return params;
                }
            }
        },
        splitLine: {
            lineStyle: {
                color: 'rgba(255,255,255,.2)',
                type:'dotted',
            }
        },
        nameTextStyle:{
            color: '#ffffff'
        },
        axisPointer:{
            show:false
        }
    },
    series: [{
        data: [1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000],
        type: 'line',
        name:'可见距离【admin】',

        smooth: true,
        lineStyle: {
            normal: {
                width: 2
            }
        },
        markLine: {
            silent: true,
            symbol:'none',
            data: [
                {
                    yAxis: 200,
                    label:{
                        position:'end',
                        formatter:"告警线"
                    },
                    lineStyle:{               //警戒线的样式  ，虚实  颜色
                        type:"solid",
                        color:"#3398DB",
                    }
                }, {
                    yAxis: 30,
                    label:{
                        position:'end',
                        formatter:"特级线"
                    }
                }
            ],
            lineStyle:{
                color:'#42515d',
                width:2,
                type :'dotted'
            },
            label:{
                position :'start',
                color:'#fff'

            }
        },
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: '#48ef8b'
                },{
                    offset: 0.55,
                    color: '#007eff'
                },{
                    offset: 0.67,
                    color: '#ffeb3b'
                },{
                    offset: 0.79,
                    color: '#fd4a05'
                }, {
                    offset: 0.9,
                    color: '#ff0000'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
            }
        }
    },{
        data: [],
        type: 'line',
        name:'可见距离【user】',

        smooth: true,
        lineStyle: {
            normal: {
                width: 2
            }
        },
        markLine: {
            silent: true,
            symbol:'none',
            data: [
                {
                    yAxis: 200,
                    label:{
                        position:'end',
                        formatter:"告警线"
                    },
                    lineStyle:{               //警戒线的样式  ，虚实  颜色
                        type:"solid",
                        color:"#3398DB",
                    }
                }, {
                    yAxis: 30,
                    label:{
                        position:'end',
                        formatter:"特级线"
                    }
                }
            ],
            lineStyle:{
                color:'#42515d',
                width:2,
                type :'dotted'
            },
            label:{
                position :'start',
                color:'#fff'

            }
        },
    }]
};

/**微信公众号*/
let common_options2 = JSON.parse(JSON.stringify(common_options1));
common_options2.xAxis = [{
	type: "category",
    name: "\n\n时间(h)",
	nameGap: 5,
    data: ["23:00", "24:00", "01:00", "02:00", "03:00", "04:00", "05:00"],
    boundaryGap: false,
    axisLine: {
        lineStyle: {
            color: '#57617B'
        }
    },
    axisLabel: {
        textStyle: {
            color:'#fff',
        },
        formatter : function(params) {
            return params.substr(0,2);
        }
    },
    nameTextStyle:{
        color: '#ffffff'
    }
}];
common_options2.yAxis = [{
    type : 'value',
    scale: true,
    name:'可见距离(米)',
    max: 700,
    min: 0,
    axisTick: {
        show: false
    },
    axisLine: {
        lineStyle: {
            color: '#57617B',

        }
    },
    axisLabel: {
        margin: 10,
        textStyle: {
            fontSize: 14
        },
        textStyle: {
            color:'#fff',
        },
        formatter : function(params) {
            if (params == 700) {
                return '>500';
            }else{
            	if(params == 0){
            		return '';
            	}
            	if(params == 100){
            		return '30';
            	}
            	if(params == 200){
            		return '50';
            	}
            	if(params == 300){
            		return '100';
            	}
            	if(params == 400){
            		return '200';
            	}
            	if(params == 500){
            		return '300';
            	}
            	if(params == 600){
            		return '400';
            	}
            	
                return params;
            }
        }
    },
    splitLine: {
        lineStyle: {
            color: 'rgba(255,255,255,.2)',
            type:'dotted',
        }
    },
    nameTextStyle:{
        color: '#ffffff'
    },
    axisPointer:{
    	show:false
    }
}];

/**
 * 时间格式化
 * new Date(new Date().getTime()-24*60*60*1000).Format('yyyy-MM-dd hh:mm:ss');
 * @param fmt
 * @returns {Date.Format.props}
 * @constructor
 */
Date.prototype.Format = function(fmt){
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)) {
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
};
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
/**
 * @param func 回调函数
 * @param timer 定时器
 * @param bool 是否间隔执行
 * @private
 */
let setTimerFunc = function(func, timer, bool){
    let time = setTimeout(opa, timer);
    function opa() {
        clearTimeout(time);
        func();
        if(!bool){
            return;
        }
        setTimeout(opa, timer);
    }
};
//定义全局变量u
let uWeb = {};
//设置缓存
uWeb.setStorage = function (key, value) {
    let v = value;
    if (typeof v == 'object') {
        v = JSON.stringify(v);
        v = 'obj-' + v;
    } else {
        v = 'str-' + v;
    }
    let ls = window.localStorage;
    if (ls) {
        ls.setItem(key, v);
    }
};
//获取缓存
uWeb.getStorage = function (key) {
    let ls = window.localStorage;
    if (ls) {
        let v = ls.getItem(key);
        if (!v) {
            return;
        }
        if (v.indexOf('obj-') === 0) {
            v = v.slice(4);
            return JSON.parse(v);
        } else if (v.indexOf('str-') === 0) {
            return v.slice(4);
        }
    }
};
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

function playAudio(file) {
    const fileReader = new FileReader();
    const audioContext = new AudioContext();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = function() {
        audioContext.decodeAudioData(fileReader.result, function(result) {
            //创建播放源
            const source = audioContext.createBufferSource();
            source.buffer = result;
            //连接输出终端
            source.connect(audioContext.destination);
            //开始播放
            source.start();
        });
    }
};

//播放音频
function loadAudioFile(url) {
    let source = null;
    let audioBuffer = null;
    //let context = new window.AudioContext || new window.webkitAudioContext();
    window.AudioContext = window.AudioContext || window.webkitAudioContext || window.mozAudioContext || window.msAudioContext;
    let context=new window.AudioContext();
    let xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(e) {
        context.decodeAudioData(this.response, function(buffer) {
            audioBuffer = buffer;
            source = context.createBufferSource();
            source.buffer = audioBuffer;
            source.loop = false;
            source.connect(context.destination);
            source.start(0);
            //uWeb.setStorage("main-v4.0-source", source);
        }, function(e) {
            console.log('Error decoding file', e);
        });
    };
    xhr.send();
}
