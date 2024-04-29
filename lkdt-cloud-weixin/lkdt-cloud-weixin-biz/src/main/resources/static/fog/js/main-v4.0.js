//全局变量
let map = null;
let allequ = 0;
//摄像头实时图片获取
let hasClose = 0;
let loadAudioFile_flag = 0;
//是否打开雾霾告警
let fogOpen = true;
//告警信息/告警闪烁点
let alarmInfo = [];
//大风测试标记
let windMarkers = [];
//轨迹描线
let trackLines = [];
//路段能见度趋势
let myChart2 = echarts.init(document.getElementById("routVisibility"));
let myChart25 = echarts.init(document.getElementById("routVisibility25"));

//加载执行
$(function () {
    //固定高度，数据溢出滚动条
    $('#alarmList').css('height',$('#tuanwuyujing').height()*0.90 + 'px');
    $('#eventList').css('height',$('#buhegeshipin').height()*0.90 + 'px');

    //删除加载动画
    $('#load').fadeOut(1000);

    setTimerFunc(function(){
        $('#load').remove();
    }, 3000, false);
    /**********************1****************************/
    //execAlarmEquipList();
    statisticAjax();
    exceptionEquList();
    setTimerFunc(function(){
        //execAlarmEquipList();
        statisticAjax();
        exceptionEquList();
    }, 30000, true);
    /***********************1 end***************************/
});

/**************************************左边 **************************************/
myChart2.setOption({...gonglujiaotongnengjiandu_left_option}, true);
myChart25.setOption({...gonglujiaotongnengjiandu_left_option}, true);
myChart2.getZr().on('click',function(params){
    var pointInPixel= [params.offsetX, params.offsetY];
    if (myChart2.containPixel('grid',pointInPixel)) {
        /*此处添加具体执行代码*/

        var pointInGrid = myChart2.convertFromPixel({seriesIndex: 0}, pointInPixel);
        //X轴序号
        var xIndex = pointInGrid[0];
        let epId = 'undefind';
        let name = 'undefind';
        let equLocation = 'undefind';
        let getDistanceStatistic_data15 = uWeb.getStorage("getDistanceStatistic_data15");
        if (getDistanceStatistic_data15) {
            let epIds = getDistanceStatistic_data15.epIds;
            let equLocations = getDistanceStatistic_data15.xAxisData;
            epId = epIds[xIndex];
            //name = params.name;
            name = equLocations[xIndex];
            equLocation = equLocations[xIndex];
        }
        realImgViewFunc(epId, name, equLocation);//epId,name,equLocation
    }
});
myChart25.getZr().on('click',function(params){
    var pointInPixel= [params.offsetX, params.offsetY];
    if (myChart25.containPixel('grid',pointInPixel)) {
        /*此处添加具体执行代码*/

        var pointInGrid = myChart25.convertFromPixel({seriesIndex: 0}, pointInPixel);
        //X轴序号
        var xIndex = pointInGrid[0];
        let epId = 'undefind';
        let name = 'undefind';
        let equLocation = 'undefind';
        let getDistanceStatistic_data25 = uWeb.getStorage("getDistanceStatistic_data25");
        if (getDistanceStatistic_data25) {
            let epIds = getDistanceStatistic_data25.epIds;
            let equLocations = getDistanceStatistic_data25.xAxisData;
            let names = getDistanceStatistic_data25.title;
            epId = epIds[xIndex];
            //name = params.name;
            name = equLocations[xIndex];
            equLocation = equLocations[xIndex];
        }
        realImgViewFunc(epId, name, equLocation);//epId,name,equLocation
    }
});

/***
 * 统计图
 */
function statisticAjax(){
    let option2 = {...gonglujiaotongnengjiandu_left_option};
    let option25 = {...gonglujiaotongnengjiandu_left_option}
    //option25 = JSON.parse(option25);
    $.ajax({url:'/fog/vef/getfogStat',type:'get',data:{},dataType:'json',
        success:function(data){
            if(data.length > 0){
                if(data[0]){
                    //初始化1
                    uWeb.setStorage("getDistanceStatistic_data15",data[0]);
                    option2.title.text = data[0].title;
                    option2.xAxis.data = data[0].xAxisData;
                    option2.series[0].data = data[0].data;
                    myChart2.setOption(option2, true);
                }else {
                	option2.title.text = "暂未接入数据";
                	option2.xAxis.data = [];
                    option2.series[0].data = [];
                	myChart2.setOption(option2, true);
                }
                if(data[1]){
                    //初始化2
                    uWeb.setStorage("getDistanceStatistic_data25",data[1]);
                    option25.title.text = data[1].title;
                    option25.xAxis.data = data[1].xAxisData;
                    option25.series[0].data = data[1].data;
                    myChart25.setOption(option25, true);
                }else{
                	option25.title.text = "暂未接入数据";
                    option25.xAxis.data = [];
                    option25.series[0].data = [];
                	myChart25.setOption(option25, true);
                }
            }
            
        }
    });
}

window.onresize = function(){
	myChart2.resize();
	myChart25.resize();    //若有多个图表变动，可多写
}
// 7-9 统计图 end
/**************************************左边 end **************************************/
/**************************************中间地图**************************************/
//自定义图层
setTimerFunc(function(){
    mapInit();
}, 100, false);

function mapInit(){
    //江苏：{zoom:7,center:[119.167147,32.952384]}
    //东部高速：{zoom:9,center:[119.178133,34.596076]}
    let liveAddress = $('#liveAddress').val();
    let liveAddressJSON = {zoom:9,center:[119.178133,34.596076]};
    try{
        let liveAddressJSON$temp = eval('('+liveAddress+')');
        if(liveAddressJSON$temp) {
            liveAddressJSON = liveAddressJSON$temp;
        }
    } catch (e) {
        console.log(e);
        liveAddressJSON = {zoom:9,center:[119.178133,34.596076]};
    }

	let map$zoom = liveAddressJSON.zoom;
	let map$center = liveAddressJSON.center;//初始地图中心点 江苏[119.117709,33.136569]  连云港[119.178133,34.596076 ]
	//创建地图
	map = new AMap.Map('allmap', {
	    cursor: 'default',
	    resizeEnable: true,
	    zoom: map$zoom,
	    center: map$center,
	    mapStyle: "amap://styles/1de1ab4a6f3d6b4a31d558771f073fa2"
	});
	AMapUI.loadUI(['geo/DistrictExplorer'], function(DistrictExplorer) {
        initPage(DistrictExplorer,map);
    });
	
	map.on('zoomchange', function(e) {
        let zoom = map.getZoom(); //获取当前地图级别
        let vs = $(".camimg");
        if(zoom > 10) {
            vs.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
        } else {
            vs.css({ height: '20px', width: '20px' });
        }
    });

    //摄像头初始化
    setTimerFunc(function(){
        mapEquipmentsInit();
    },500,false);
    //大风测试初始化
    windTestInit();
    setTimerFunc(function(){
        windTestInit();
    }, 2*60*1000, true);
}

function initPage(DistrictExplorer,map) {
    //创建一个实例
    let districtExplorer = new DistrictExplorer({
        map: map
    });
    let countryCode = 320000, provCodes = [], cityCodes = []; //全国
    districtExplorer.loadMultiAreaNodes([countryCode].concat(cityCodes),//只需加载全国和市，全国的节点包含省级
        function(error, areaNodes) {
            let countryNode = areaNodes[0], cityNodes = areaNodes.slice(1);
            let path = [];
            //首先放置背景区域，这里是大陆的边界
            path.push(getLongestRing(countryNode.getParentFeature()));

            for (let i = 0, len = provCodes.length; i < len; i++) {
                //逐个放置需要镂空的省级区域
                path.push.apply(path, getAllRings(countryNode.getSubFeatureByAdcode(provCodes[i])));
            }
            for (let i = 0, len = cityNodes.length; i < len; i++) {
                //逐个放置需要镂空的市级区域
                path.push.apply(path, getAllRings(cityNodes[i].getParentFeature()));
            }

            //绘制带环多边形
            //https://lbs.amap.com/api/javascript-api/reference/overlay#Polygon
            let polygon = new AMap.Polygon({
                bubble: true,
                lineJoin: 'round',
                strokeOpacity: 1, //线透明度
                strokeWeight:2, //线宽
                fillColor: '#80d8ff',
                strokeColor: '#0091ea',
                fillOpacity: 0.40, //填充透明度
                map: map,
                path: path
            });
        }
    );
}

function getAllRings(feature) {
    let coords = feature.geometry.coordinates,
        rings = [];
    for (let i = 0, len = coords.length; i < len; i++) {
        rings.push(coords[i][0]);
    }
    return rings;
}

function getLongestRing(feature) {
    let rings = getAllRings(feature);
    rings.sort(function(a, b) {
        return b.length - a.length;
    });
    return rings[0];
}

function windTestInit(){
    $.ajax({url:'/fog/fogTrack/windTest',type:'post',data:{hwId:'1'},dataType:'json',
        success:function(data){
            try{
                if (document.createElement('canvas').getContext) {  // 判断当前浏览器是否支持绘制海量点
                    for(let i = 0; i < windMarkers.length; i++){
                        map.remove(windMarkers.shift());
                    }
                    let html = '';
                    for(let i = 0; i < data.length; i++){
                        //告警
                        data[i].epId=data[i].epId.replace('+','_');
                        if(data[i].windLevel != -1){
                            html += "<ul>";
                            let param__ = null;
                            if(data[i].lng && data[i].lat){
                                param__= data[i].lng+","+data[i].lat;
                            } else {
                                param__= "-1,-1";
                            }
                            html += "<li><a href='#' style='color: #95C1EE;' title='" +data[i].equLocation+ "' onclick='"+"locationFunc("+param__+")"+"'>" + data[i].equLocation.substr(0,5) + "</a></li>";
                            html += "<li>" + data[i].winds + "</li>";
                            html += "<li>"+getWindd(data[i].windd)+"</li>";
                            html += "<li>"+data[i].time?data[i].time.substring(10):'-'+"</li>";
                            html += "</ul>";
                        }
                        let icon = null;
                        switch(parseInt(data[i].windLevel)){
                            case -1: icon = '/fog/windImg/wind-blue.png'; break;
                            case 0: icon = '/fog/windImg/wind-blue.png'; break;
                            case 1: icon = '/fog/windImg/wind-yellow.png'; break;
                            case 2: icon = '/fog/windImg/wind-yellow.png'; break;
                            case 3: icon = '/fog/windImg/wind-red.png'; break;
                            case 4: icon = '/fog/windImg/wind-red.png'; break;
                        }
                        windTest(new AMap.LngLat(data[i].lng,data[i].lat),icon,data[i].epId,data[i].epId,data[i].equLocation);
                    }
                    //大风预警
                    $("#contentUl_wind").html(html);
                } else {
                    alert('请在chrome、safari、IE8+以上浏览器查看');
                }
            } catch (e) {
                console.log(e);
            }
        }
    });
}

//获取风向
function getWindd(windd){
    try{
        if(windd>337.5||windd<=22.5){
            return "北";
        }else if(windd>22.5&&windd<=67.5){
            return "东北";
        }else if(windd>67.5&&windd<=112.5){
            return "东";
        }else if(windd>112.5&&windd<=157.5){
            return "东南";
        }else if(windd>157.5&windd<=202.5){
            return "南";
        }else if(windd>202.5&&windd<=247.5){
            return "西南";
        }else if(windd>247.5&&windd<=292.5){
            return "西";
        }else if(windd>292.5&&windd<=337.5){
            return "西北";
        }
    } catch (e) {
        console.log(e);
    }
    return "无";
}

/**
 * 摄像头实时图片获取
 * */
function realWindTestFunc(epId,name,equLocation){
    layer.open({
        type : 2,
        title : "ID："+epId+" >> 位置："+equLocation,
        maxmin : true,
        shadeClose : false, //点击遮罩关闭层
        area : [ '60%', '95%' ],
        zIndex:99999999,
        content : '/system/equipment/home_lookup_wind/' + epId
    });
}

/**
 * 渲染标记
 * @param map
 * @param point
 * @param icon
 * @param equLocation
 */
function windTest(point,iconUrl,epId,name,equLocation){
	let marker1 = new AMap.Marker({
        position: point,
        //content: '<div style="background-color: hsla(180, 100%, 50%, 0.4); height: 14px; width: 14px; border: 1px solid hsl(180, 100%, 40%); border-radius: 12px; box-shadow: hsl(180, 100%, 50%) 0px 0px 1px;"></div>',
        content: '<img src="'+iconUrl+'" style="width:20px;height:20px;cursor: pointer;" />',
        offset: new AMap.Pixel(-10, -10),
    });
    map.add(marker1);
    //点击事件无效
    marker1.on('click',function(e){
        realWindTestFunc(epId,name,equLocation);
    });
    windMarkers.push(marker1);
}

/**
 *  地图摄像头初始化
 * */
function mapEquipmentsInit(){
    $.ajax({
        url:'/system/equipment/getAllEquipments',
        type:'post',
        data:{hwId:'26'},
        dataType:'json',
        success:function(data){
//            if(data.code != 200){
//                alert("内部错误，请联系管理员");
//                return;
//            }
            //摄像头经纬度 >>>30m:#C30004,50m:#F87018,100m:#F9C115,200:#F7F940,300:#357A00,500:#357A00,
            data.forEach(function(currValue,index,arr){
                //标记摄像头
                let arrayData = [];
                arrayData.push({lnglat: [currValue.lng,currValue.lat], epId:currValue.epId, name: currValue.equName,
                    equLocation:currValue.equLocation,id: index});
//                currValue.hw_equipments.forEach(function(currData,i_,a_){
//                    arrayData.push({lnglat: [currData.lng,currData.lat], epId:currData.epId, name: currData.equ_name,
//                        equLocation:currData.equ_location,id: index+'-'+i_});
//                });
                //摄像头标记
                mapClusterMarker(arrayData);
                allequ += arrayData.length;
            },this);
            //告警信息刷新
            showInfo();
            setTimerFunc(function(){
                showInfo();
            }, 30000, true);
        }
    });
}

/**大量点聚合标记*/
function mapClusterMarker(arrayData){
    let markers = [];
    for (let i = 0; i < arrayData.length; i++) {
        let marker = new AMap.Marker({
            position: arrayData[i]['lnglat'],
            content: '<div class="camimg"></div>',
            offset: new AMap.Pixel(-7, -7)
        });
        marker.epId = arrayData[i].epId;
        marker.name = arrayData[i].name;
        marker.equLocation = arrayData[i].equLocation;
        marker.on('click',function(e){
            realImgViewFunc(this.epId,this.name,this.equLocation);
        });
        markers.push(marker);
    }
    //最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    map.plugin(["AMap.MarkerClusterer"],function() {
    	let markerClusterer = new AMap.MarkerClusterer(map, markers,{
    	    gridSize: 40,
    	    minClusterSize:6,
    	    averageCenter: true,
    	    renderClusterMarker:function (context) {
    	        let count = markers.length;
    	        let factor = Math.pow(context.count / count, 1 / 18);
    	        let div = document.createElement('div');
    	        let Hue = 180 - factor * 180;
    	        let bgColor = 'hsla(210,72%,76%,0.5)';
    	        let fontColor = 'hsla(0,0%,100%,1)';
    	        let borderColor = 'hsla(210,100%,40%,0.7)';
    	        let shadowColor = 'hsla(' + Hue + ',100%,50%,0.7)';
    	        div.style.backgroundColor = bgColor;
    	        let size = Math.round(5 + Math.pow(context.count / count, 1 / 5) * 20);
    	        div.style.width = div.style.height = size + 'px';
    	        div.style.border = 'solid 1px ' + borderColor;
    	        div.style.borderRadius = size / 2 + 'px';
    	        div.style.boxShadow = '0 0 1px ' + shadowColor;
    	        div.innerHTML = context.count;
    	        div.style.lineHeight = size + 'px';
    	        div.style.color = fontColor;
    	        div.style.fontSize = '12px';
    	        div.style.textAlign = 'center';
    	        context.marker.setOffset(new AMap.Pixel(-size / 2, -size / 2));
    	        context.marker.setContent(div)
    	    }
    	});
    });
}

function realImgViewFunc(epId,name,equLocation){
	hasClose=0;
    layer.open({
        type : 2,
        title : "ID："+epId+" >> 桩号："+name+" >> 位置："+equLocation,
        maxmin : false,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '60%', '90%' ],
        zIndex:99999999,
        content : '/system/equipment/home_lookup/' + epId
    });
}

function openFog(epId){
	hasClose=1;
    layer.open({
        type : 2,
        title : "ID："+epId,
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '60%', '90%' ],
        zIndex:99999999,
        content : '/system/equipment/home_lookup/' + epId
    });
}

function gethasClose(){
	return hasClose;
}

/**
 * 告警统计
 */
function alarmCon(){
    layer.open({
        type : 2,
        title : "设备实时在线情况",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '50%', '70%' ],
        zIndex:99999999,
        content : '/system/equipment/alarmCon'
    });
}

/**
 * 告警信息渲染
 * */
function showInfo() {
    $.ajax({
        type: "GET",
        url: "/fog/fogTrack/selectRealAlarmInfoListByUser",
        dataType:'json',
        success: function (data) {
            //告警闪烁点清空
            alarmImgCloseAll(map);
            let alarmHtml_right = "";
            let html = "";
            let unconfirmhtml = "";
            if(data.length > 0){
                data.sort(function(a,b){
                    return a.distance - b.distance;
                });
            }
            for (let i = 0; i < data.length; i++) {
                //渲染告警闪烁
                let tempContent;
                let zIndex = 100;
                if(data[i].distance == -1){
                	//设备异常
                }else if(data[i].distance < 50){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    tempContent = "/img/wumai_red.gif";
                    zIndex = 200;
                } else if(data[i].distance < 100){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    tempContent = "/img/wumai_orange.gif";
                    zIndex = 199;
                } else if(data[i].distance < 200){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    tempContent = "/img/wumai_yellow.gif";
                    zIndex = 198;
                } else {
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    tempContent = "/img/wumai_yellow.gif";
                    zIndex = 198;
                }

                //闪烁点渲染
                if(fogOpen && data[i].distance != -1){
                	if(data[i].lon&&data[i].lat){
                		addCustomMarker(map,tempContent,new AMap.LngLat(parseFloat(data[i].lon), parseFloat(data[i].lat)),data[i].epId,data[i].equName,zIndex,data[i].address);
                	}
                }

                try{
                    //***告警信息---右边start***
                    try{
                        if(data[i].distance != -1){
                            alarmHtml_right +=
                                `<div class="panel panel-default">`;
                            if(data[i].distance == -1){
                                alarmHtml_right += `<div class="panel-heading" style="background-color: lightcoral;">异常设备</div>`;
                            } else {
                                if(data[i].distance >= 200){
                                    alarmHtml_right += `<div class="panel-heading" style="background-color: yellow;">黄色告警(100~200m)</div>`;
                                } else if(data[i].distance >= 100){
                                    alarmHtml_right += `<div class="panel-heading" style="background-color: yellow;">黄色告警(100~200m)</div>`;
                                } else if(data[i].distance >= 50){
                                    alarmHtml_right += `<div class="panel-heading" style="background-color: orange;">橙色告警(50~100m)</div>`;
                                } else if(data[i].distance >= 0){
                                    alarmHtml_right += `<div class="panel-heading" style="background-color: red;">红色告警(0~50m)</div>`;
                                }

                            }
                            date=new Date(data[i].begintime);
            			
	            			var year = date.getFullYear();
	            			var month = date.getMonth()+1;
	            			var day = date.getDate();
	            			var dateStr=year+'-'+month+'-'+day;
                            let $src_src_ = "/system/alarm/showImg?fname=" + data[i].imgfn +
                                "&epId="+ data[i].epId + "&dateStr=" + dateStr + "&_x_=" + new Date().getTime();
                            alarmHtml_right +=
                                `<div class="panel-body">`+
                                    `<div class="expand-popc-width">`+
                                        `<div class="expand-popc-left">`+
                                        `<img src="`+$src_src_+`" alt="告警图片" onclick="expand_popc_click(this);" class="img-rounded">`+
                                    `</div>`+
                                    `<div class="expand-popc-right">`+
                                        `可见距离：<span>`+data[i].distance+`米</span>`+
                                        `<br/>桩号：<span>`+data[i].epId+`</span>`+
                                        `<br/>路段：<span>`+data[i].address+`</span>`+
                                    `</div>`+
                                    `</div>`+
                                        `<label>2020-05-16 01:28:56</label>`+
                                    `</div>`+
                                `</div>`;
                        }
                    } catch (e) {
                        console.log(e);
                    }
                    //***告警信息---右边end***
                    //***异常设备html***下方start
                    try{
                        if(data[i].distance == -1){
                            html += "<ul>";
                            let param__ = null;
                            if(data[i].lon && data[i].lat){
                                param__= data[i].lon+","+data[i].lat;
                            } else {
                                param__= "-1,-1";
                            }
                            html += "<li style='word-wrap:break-word;width:25%;text-align: left;' ><a href='#' style='color: #95C1EE;' " +
                                "title='" +data[i].epId+"："+data[i].equName + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                                data[i].equName+ "</a></li>";
                            html += `<li style="width:25%;text-align: left;">` + "异常设备</li>";
                            let begintime = data[i].begintime.substring(10);
                            html += `<li style="width:25%;text-align: left;" title='" + data[i].begintime + "'>` + (begintime==''?'&nbsp;':begintime) + "</li>";
                            html += `<li style="width:25%;text-align: left;"><a href='#' style='color: #95C1EE;'` +
                                "title='" +data[i].forTime+ "' onclick='openFog((\""+data[i].epId+"\"))'>" +
                                data[i].forTime+ "</a></li>";
                            html += "</ul>";
                        }
                    } catch (e) {
                        console.log(e);
                    }
                    //***异常设备html***下方end
                    //***未确认告警html***start
                    try{
                        if((data[i].cameraType == 1 || data[i].cameraType == 3 || data[i].cameraType == 5)&& data[i].distance > 0){
                            unconfirmhtml += "<ul>";
                            let param__ = null;
                            if(data[i].lon && data[i].lat){
                                param__= data[i].lon+","+data[i].lat;
                            } else {
                                param__= "-1,-1";
                            }
                            unconfirmhtml += "<li style='word-wrap:break-word;' ><a href='#' style='color: #95C1EE;' " +
                                "title='" +data[i].equName + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                                (data[i].equName).substr(0,5)+"..."+ "</a></li>";
                            unconfirmhtml += "<li>" + data[i].distance + "(m)</li>";

                            let begintime = data[i].begintime.substring(10);
                            unconfirmhtml += "<li title='" + data[i].begintime + "'>" + (begintime==''?'&nbsp;':begintime) + "</li>";
                            unconfirmhtml += "<li><a href='#' style='color: #95C1EE;' " +
                                "title='" +"未确认"+ "' onclick='"+"confirm(\""+data[i].epId+"\")"+"'>" +
                                "未确认"+ "</a></li>";
                            unconfirmhtml += "</ul>";
                        }
                    } catch (e) {
                        console.log(e);
                    }
                    //***未确认告警html***end
                } catch (e) {
                    console.log(e);
                }
            }
            $("#expand-popc").html(alarmHtml_right);
            $("#contentUl").html(html);
            $("#contentUl_unconfirm").html(unconfirmhtml);
            //路线三色图
            $.ajax({url: '/fog/fogTrack/getAlarmTrack',
                type: 'post',
                data: {epJson: JSON.stringify(data)},
                dataType: 'json',
                success: function (data) {
                    //清除路径
                    for(;trackLines.length > 0;){
                        trackLines.shift().setMap(null);
                    }
                    //////////////////////////
                    //
                    if(data && data.length > 0){
                        data.forEach(function(curr,index,arr){
                            if(curr.color && curr.track){
                                polylineFunc(curr.track,curr.color);
                            }
                        });
                    }
                }
            });
        }
    });
}

function checkChinese(obj_val){
    let reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
    if(reg.test(obj_val)){
        return true;
    }
    return false;
}

function confirm(epId){
	layer.open({
        type : 2,
        title : "告警确认",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '60%', '85%' ],
        zIndex:99999999,
        content : '/system/alarm/confirmPhone/' + epId
    });
}

/**
 * 播放告警声音
 * */
function alarmPlay(url){
    try{
        if(loadAudioFile_flag == 0){
            loadAudioFile(url);
            loadAudioFile_flag = 1;
            setTimerFunc(function(){
                loadAudioFile_flag = 0;
            },7000,false);
        }
    } catch (e) {
        console.log(e);
        layer.msg("EXCEPTION·告警提示音频播放失败");
    }
}

/**
 * 根据坐标列表划线
 * */
function polylineFunc(equipmentLngLat,colorDesc){
    //给定路径划线
    let polyline = new AMap.Polyline({
        path: JSON.parse(equipmentLngLat),            // 设置线覆盖物路径
        strokeColor: colorDesc,   // 线颜色
        strokeOpacity: 1,         // 线透明度
        strokeWeight: 5,          // 线宽
        strokeStyle: 'dashed',     // 线样式
        strokeDasharray: [0,0,0], // 补充线样式
        geodesic: true,            // 绘制大地线
        bubble:true,
        isOutline:true,
        outlineColor:'white',
    });
    polyline.setMap(map);
    trackLines.push(polyline);
}

/**定义闪烁点divMarker*/
function addCustomMarker(map,content,position,epId,name,zIndex,equLocation){
    let marker1 = new AMap.Marker({
        //map:map,
        offset:new AMap.Pixel(-10,-10), //相对于基点的偏移位置
        draggable:false,  //是否可拖动
        content:'<img src="'+content+'" style="width:20px;height:20px;cursor: pointer;" />',   //自定义覆盖物内容,
        position:position, //基点位置
        zIndex:zIndex//显示级别
    });
    marker1.on( 'click', function(){
        realImgViewFunc(epId,name,equLocation);
    });
    map.add(marker1);
    alarmInfo.push(marker1);
}

/**
 * 清空所有告警闪烁点
 */
function alarmImgCloseAll(){
    while(alarmInfo.length > 0){
        map.remove(alarmInfo.shift());
    }
}

/**
 * 警告列表摄像头定位
 * */
function locationFunc(lon,lat){
    //异常
    if(lon == -1 || lat == -1){
        layer.msg("ERROR403·经纬度异常");
        return;
    }
    let point = new AMap.LngLat(lon, lat);
    var marker = new AMap.Marker({
    	position:point
        //offset:{width:-8,height:-8},
    });
    marker.setAnimation('AMAP_ANIMATION_BOUNCE');
    marker.setMap(map);
    setTimeout(function(){
        marker.setMap(null);
    }, 3000);
    map.setZoom(14);  // 初始化地图,设置中心点坐标和地图级别
    map.setCenter(point);
    // setTimeout(function (){
    // 	map.remove(marker);
    // },5000);
}


/**************************************中间地图 end **************************************/
/**************************************右边 **************************************/
let swiper = new Swiper('.swiper-container', {
    pagination: {
        el: '.swiper-pagination',
        type: 'fraction',
    },
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
    on:{
        slidePrevTransitionEnd: function(){
            if(this.activeIndex == 0){
                $("#big_wind_title").text("大风预警")
            } else if(this.activeIndex == 1){
                //$("#big_wind_title").text("确认情况")
            }
        },
        slideNextTransitionEnd: function(){
            if(this.activeIndex == 0){
                $("#big_wind_title").text("大风预警")
            } else if(this.activeIndex == 1){
                //$("#big_wind_title").text("确认情况")
            }
        },
        click:function(){}
    }
});

/**
 * 异常摄像头列表
 * system/equipment/getExeptionEqu  #exceptionEqus
 */
function exceptionEquList(){
    $.ajax({
        type: "GET",
        url: "/system/equipment/getExeptionEqu",
        dataType:'json',
        success: function (data) {
            try{
                $('#exceptionEqus').empty();
                let html = "";
                if(allequ>1){
                	$("#percentage").text(parseInt(data.length*100/allequ));
                }
                for (let i = 0; i < data.length; i++) {
                    //***告警信息html***start
                    html += "<ul>";
                    let param__ = null;
                    if(data[i].lng && data[i].lat){
                        param__= data[i].lng+","+data[i].lat;
                    } else {
                        param__= "-1,-1";
                    }
                    html += "<li style='word-wrap:break-word;width: 10%;text-align: left;' ><span href='#' style='color: #95C1EE;' " +
                        "title='" +i+ "' onclick='"+"locationFunc("+param__+")"+"'>" + i + "</a></li>";
                    html += "<li style='width: 30%;text-align: left;word-wrap:break-word;' ><a href='#' style='color: #95C1EE;' " +
                        "title='" +data[i].equName + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                            data[i].equName+ "</a></li>";
                    html += "<li style='word-wrap:break-word;width: 30%;text-align: left;' ><a href='#' style='color: #95C1EE;' " +
                        "title='" +data[i].stateName + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                        (data[i].stateName)+ "</a></li>";
                    html += "<li style='word-wrap:break-word;width: 30%;text-align: left;' >" +
                        "<a href='#' style='color: #95C1EE;' title='" +data[i].equLocation + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                        (data[i].equLocation)+ "</a>" +
                        "</li>";
                    html += "</ul>";
                    //***告警信息html***end
                }
                $("#exceptionEqus").html(html);
            } catch (e) {
                console.log(e);
            }
        }
    });
}
/**************************************右边 end **************************************/

/****************************************告警********************************************/
/**路段告警获取*/
let queryAlarmRoadByUser = () => {
    $.ajax({url:'/fog/alarmRoad/queryAlarmRoadByUser',type:'get',data:{},dataType:'json',
        success:function(data){
            if(data.length > 0){
                let html = ``;
                for(let i = 0; i < data.length; i++){
                    html += `<li>`+data[i].hwName+`当前最小可视距离`+data[i].mindistanceNow+`米。</li>`;
                }
                $('#scroll-wrap-id').html(html);
            }
        }
    });
};
setTimerFunc(function(){
    queryAlarmRoadByUser();
    jqScroll();
},1000,false);
//路段告警
setTimerFunc(function(){
    queryAlarmRoadByUser();
}, 30000, true);

//警情滚动
function jqScroll(){
    try{
        let textRoll=function(){
            $(".scroll-wrap").find(".scroll-con").animate({
                marginTop : "-31px"
            },1000,function(){
                $(this).css({marginTop : "0px"}).find("li:first").appendTo(this);
            });
        };
        let roll= setInterval(textRoll,2000);
        $(".scroll-con li").mouseenter(function() {
            clearInterval(roll);
        }).mouseout(function(){
            clearInterval(roll);
            roll= setInterval(textRoll,2000);
        });
    } catch (e) {
        console.log(e);
    }
}

//告警输出
setTimerFunc(function(){
    if($('#expand-popc')[0].childNodes.length > 0){
        if(new Date().getSeconds() % 2 == 1){
            $("#alarm_icon img")[0].src = "/img/alarm_red.png";
            $(".visual_con").css("background-color","red");
            $(".mapAlarmSlim").show();
        } else {
            $("#alarm_icon img")[0].src = "/img/alarm_red.png";
            $(".visual_con").css("background-color","");
            $(".mapAlarmSlim").hide();
        }
    } else {
        $("#alarm_icon img")[0].src = "/img/alarm_black.png";
        $(".visual_con").css("background-color","");
        $(".mapAlarmSlim").hide();
    }
}, 1000, true);

function closeAll_bottom(){
    $("#buhegeshipin").hide();
    $("#tuanwuyujing").hide();
}

function buhegeshipin_click(){
    closeAll_bottom();
    $("#buhegeshipin").show(500);
    $("#left_bottom_btn").animate({bottom:($("#allmap").height()/3+5)+"px"});
}

function tuanwuyujing_click(){
    closeAll_bottom();
    $("#tuanwuyujing").show(500);
    $("#left_bottom_btn").animate({bottom:($("#allmap").height()/3+5)+"px"});
}

$('.close_out').click(()=>{
    $("#buhegeshipin").hide(500);
    $("#tuanwuyujing").hide(500);
    $("#left_bottom_btn").animate({bottom:"10px"});
});

$('#expand').click(()=>{
    $('#expand').hide();
    $('#expand-popc').hide(500);
    setTimerFunc(function(){
        $("#alarm_icon").show(500);
    },250,false);
});

$('#alarm_icon').click(()=>{
    $('#alarm_icon').hide(250);
    setTimerFunc(function(){
        $('#expand').show(500);
        $('#expand-popc').show(500);
    },100,false);
});

$('.img_close_out').click(()=>{
    $('#expand-popc-preview').hide(250);
});
function expand_popc_click(ele){
    document.getElementById("expand-popc-preview-img").src = ele.src;
    $('#expand-popc-preview').show(250);
}
/****************************************告警end********************************************/
