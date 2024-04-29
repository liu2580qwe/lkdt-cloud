$(function () {

    //固定高度，数据溢出显示滚动条
    $('#alarmList').css('height',$('#tuanwuyujing').height()*0.90 + 'px');
    $('#eventList').css('height',$('#buhegeshipin').height()*0.90 + 'px');

    /**********************1****************************/
    //execAlarmEquipList();

    statisticAjax();

    //获取告警数量
    getAlarmCount();
    //获取路段总告警数并排序
    getAlarmCountGroupHwId();
    //获取摄像头数量
    getEquCounts();
    //不合规摄像头
    exceptionEquList();
    setTimerFunc(function(){
        windTestInit();
    },500,false);
    setTimerFunc(function(){
        statisticAjax();
        getAlarmCount();
        getEquCounts();
        exceptionEquList();
        windTestInit();
    }, 60000, true);
    //按月统计告警数
    monthReport();
    yearReport();

    /***********************1 end***************************/
});

// 能见度曲线图
let myChart2 = echarts.init(document.getElementById("routVisibility"));
let myChart25 = echarts.init(document.getElementById("routVisibility25"));
let myChart35 = echarts.init(document.getElementById("routVisibility35"));

/**************************************左边 **************************************/
myChart2.setOption(JSON.parse(JSON.stringify(gonglujiaotongnengjiandu_left_option)), true);
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

myChart35.getZr().on('click',function(params){
    var pointInPixel= [params.offsetX, params.offsetY];
    if (myChart35.containPixel('grid',pointInPixel)) {
        /*此处添加具体执行代码*/

        var pointInGrid = myChart35.convertFromPixel({seriesIndex: 0}, pointInPixel);
        //X轴序号
        var xIndex = pointInGrid[0];
        let epId = 'undefind';
        let name = 'undefind';
        let equLocation = 'undefind';
        let getDistanceStatistic_data35 = uWeb.getStorage("getDistanceStatistic_data35");
        if (getDistanceStatistic_data35) {
            let epIds = getDistanceStatistic_data35.epIds;
            let equLocations = getDistanceStatistic_data35.xAxisData;
            let names = getDistanceStatistic_data35.title;
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
    let option2 = JSON.parse(JSON.stringify(gonglujiaotongnengjiandu_left_option));
    let option25 = JSON.parse(JSON.stringify(gonglujiaotongnengjiandu_left_option));
    let option35 = JSON.parse(JSON.stringify(gonglujiaotongnengjiandu_left_option));
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

                if(data[2]){
                    //初始化2
                    uWeb.setStorage("getDistanceStatistic_data35",data[2]);
                    option35.title.text = data[2].title;
                    option35.xAxis.data = data[2].xAxisData;
                    option35.series[0].data = data[2].data;
                    myChart35.setOption(option35, true);
                }else{
                    option35.title.text = "暂未接入数据";
                    option35.xAxis.data = [];
                    option35.series[0].data = [];
                    myChart35.setOption(option35, true);
                }
            }

        }
    });
}

window.onresize = function(){
    myChart2.resize();
    myChart25.resize();    //若有多个图表变动，可多写
}

//获取告警数量
function getAlarmCount() {
    $.ajax({
        url: '/system/alarm/getAlarmCount', type: 'get', data: {}, dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#todayAlarm").html(data.todayCount);
                $("#monthAlarm").html(data.monthCount);
                if(data.unBindCount){
                    popRightBottom('告警绑定提示', '本月累计告警数量：' + data.roadMonth + ',未绑定事件告警数量：' + data.unBindCount
                        + '</br><a mce_href="/fog/alarmRoad" onClick="menuItem(this)" title="路段管制" data-index="9999" href="#"  ' +
                        'style="font-size: 15px;text-decoration:underline;color: #0e9aef">点击前往告警绑定</a>', 60000);
                }

            }
        }
    });
}

function menuItem(obj){
    parent.menuItemeq(obj);
}

function popRightBottom(title,text,time){

    if(title==undefined)
        title = "消息提示";

    if(text==undefined)
        text = "修改成功";

    if(time==undefined)
        time = 2000;


    $("#popHead span").html(title);
    $("#popTxt").html(text);

    $("#rightBottomPop").show(100,"linear",function(){
        setTimeout("closeRightBottomPop()",time);
    });
}

function closeRightBottomPop(){
    $("#rightBottomPop").hide();
}

//获取摄像头数量
function getEquCounts() {
    $.ajax({
        url: '/system/equipment/getEquCounts', type: 'get', data: {}, dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#yijieru").html(data.yijieru);
                $("#gaojing").html(data.gaojing);
                $("#buhegui").html(data.buhegui);
                $("#yichang").html(data.yichang);
            }
        }
    });
}

//获取路段总告警数并排序
function getAlarmCountGroupHwId() {
    $.ajax({
        url: '/system/alarm/getAlarmCountGroupEpId', type: 'get', data: {}, dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                let hwIdCountsHtml = '';
                for (let i = 0; i < data.counts.length; i++) {
                    hwIdCountsHtml += '<div class="media" onclick="alarmDetailsByEpId(\'' + data.counts[i].epId + '\',\'' + data.counts[i].name + '\')">' +
                        '<div class="pull-right">' +
                        '<div class="counts-lv' + (i + 1) + '">' + data.counts[i].count + '</div>' +
                        '</div>' +
                        '<div class="media-body">' +
                        '<h6>' + data.counts[i].name + '</h6>' +
                        '</div>' +
                        '</div>';
                }
                $("#hwIdCounts").html(hwIdCountsHtml);

            }
        }
    });
}

function alarmDetailsByEpId(epId,equName){

    layer.open({
        type : 2,
        title : equName+" 雾情详细：",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '100%', '100%' ],
        zIndex:99999999,
        content : "/system/alarm/alarmListByEpId?epId="+epId
    });
}
/***雾情详细**/
function alarmDetails(hwId,hwName,alarmDate){

    layer.open({
        type : 2,
        title : hwName+" 雾情详细：",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '80%', '80%' ],
        zIndex:99999999,
        content : "/report/alarmReport/alarmRoadReportDetails?hwId="+hwId+"&alarmDate="+alarmDate
    });
}

function openCalendar(){
    layer.open({
        type : 2,
        title : "告警日历",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '95%', '100%' ],
        zIndex:99999999,
        content : '/system/alarm/calendar'
    });
}

function openAlarmLog(hwId){
    layer.open({
        type : 2,
        title : "告警日历",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '95%', '95%' ],
        zIndex:99999999,
        content : '/system/alarm/calendar'
    });
}

/**************************************中间地图**************************************/
//全局变量
let map = null;
//摄像头计数
let allequ = 0;
//大风测试标记
let windMarkers = [];
//告警信息/告警闪烁点
let alarmInfo = [];
let loadAudioFile_flag = 0;
//是否打开雾霾告警
let fogOpen = true;
//轨迹描线
let trackLines = [];
//摄像头实时图片获取
let hasClose = 0;
//热力图
let heatmap;
//所有摄像头热力图
let baseHeatmap;
//摄像头聚合图
let markerClusterer;

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
        mapStyle: "amap://styles/2d3db91303cff0f720f018054ffcd273"
    });

    AMapUI.loadUI(['geo/DistrictExplorer'], function(DistrictExplorer) {
        initPage(DistrictExplorer,map);

    });

    map.plugin(["AMap.MarkerClusterer"],function() {
        let markers = [];
        markerClusterer = new AMap.MarkerClusterer(map, markers,{
            gridSize: 40,
            minClusterSize:30,
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

    //热力图插件
    map.plugin(["AMap.Heatmap"], function () {

        //初始化heatmap对象
        heatmap = new AMap.Heatmap(map, {
            radius: 15, //给定半径
            opacity: [0, 1],
            gradient:{
                0.1: 'rgb(244,235,5)',
                0.2: 'rgb(244,213,5)',
                0.3: 'rgb(244,179,5)',
                0.6: 'rgb(255,147,8)',
                1.0: '#9a1200'
            },
            zIndex: 111,
            //zooms: [3, 8]
        });
        // //设置数据集：该数据为北京部分“公园”数据
        // heatmap.setDataSet({
        //     data: heatmapData,
        //     max: 100
        // });
        // setTimeout(function(){
        //     heatmap.hide();
        // }, 2000);
    });

    //热力图插件
    map.plugin(["AMap.Heatmap"], function () {
        //初始化heatmap对象
        baseHeatmap = new AMap.Heatmap(map, {
            radius: 15, //给定半径
            opacity: [0, 1],
            gradient:{
                0.1: 'rgba(47,252,5,0.3)',
                0.2: 'rgba(47,252,5,0.3)',
                0.3: 'rgba(47,252,5,0.3)',
                0.6: 'rgba(47,252,5,0.3)',
                1.0: 'rgba(47,252,5,0.3)'
            },
            zIndex: 90,
            //zooms: [3, 8]
        });
    });
    ////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////地图渲染开始////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    map.on('zoomchange', function(e) {
        let zoom = map.getZoom(); //获取当前地图级别
        let vs = $(".camimg");
        if(zoom > 10) {
            vs.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
        } else {
            vs.css({ height: '20px', width: '20px' });
        }

        //告警渲染
        alarmInfoRender();
    });

    uWeb.setStorage("showInfoData", []);
    uWeb.setStorage("mapEquipmentsInitData", []);
    uWeb.setStorage("showInfoSub_routeData", []);

    //摄像头初始化
    setTimerFunc(function(){
        mapEquipmentsInit(alarmInfoRender);
    },500,false);

    //告警信息刷新
    showInfo(alarmInfoRender, showInfoSub_route);
    setTimerFunc(function(){
        showInfo();
    }, 10000, true);

    //告警信息渲染
    setTimerFunc(function(){
        //渲染函数
        alarmInfoRender();
    }, 30000, true);

    //告警路径渲染
    setTimerFunc(function(){
        let showInfoSub_routeData = uWeb.getStorage("showInfoSub_routeData");
        showInfoSub_route_handle(showInfoSub_routeData);
    }, 20000, true);
    ////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////地图渲染结束////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    // //大风测试初始化
    // windTestInit();
    // setTimerFunc(function(){
    //     windTestInit();
    // }, 2*60*1000, true);

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

/**
 *  地图摄像头初始化
 * */
function mapEquipmentsInit(alarmInfoRender){
    $.ajax({
        url:'/system/equipment/getAllEquipments',
        type:'post',
        data:{hwId:'26'},
        dataType:'json',
        success:function(data){
            uWeb.setStorage("mapEquipmentsInitData", data);
            if(typeof alarmInfoRender === "function"){
                alarmInfoRender();
            }
        }
    });
}

function mapEquipmentsInitHandle(data){
    //摄像头经纬度 >>>30m:#C30004,50m:#F87018,100m:#F9C115,200:#F7F940,300:#357A00,500:#357A00,
    let arrayData = [];
    // data.data.forEach(function(currValue,index,arr){
    //     currValue.hw_equipments.forEach(function(currData,i_,a_){
    //        arrayData.push({lnglat: [currData.lng,currData.lat], epId:currData.epId, name: currData.equ_name,
    //            equLocation:currData.equ_location,id: index+'-'+i_});
    //    });
    //     //摄像头标记
    //     allequ += arrayData.length;
    // },this);
    data.forEach(function(currValue,index,arr){
        //标记摄像头
        arrayData.push({lnglat: [currValue.lng,currValue.lat], epId:currValue.epId, name: currValue.equName,
            equLocation:currValue.equLocation,id: index});
        allequ += arrayData.length;
    },this);
    mapClusterMarker(arrayData);
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
    markerClusterer.setMarkers(markers);
}

/**
 * 告警信息刷新
 * */
function showInfo(alarmInfoRender, showInfoSub_route) {
    //uWeb.setStorage("showInfoData", []);
    $.ajax({
        type: "GET",
        url: "/fog/fogTrack/selectRealAlarmInfoListByUser",
        dataType:'json',
        success: function (data) {
            uWeb.setStorage("showInfoData", data);
            // //闪烁
            // showInfoSub_alarmShock(data);
            // //热力图
            // showInfoSub_heapMap(data);
            // //路线三色图
            // showInfoSub_route(data);
            if(typeof alarmInfoRender === "function"){
                alarmInfoRender();
            }
            if(typeof showInfoSub_route === "function"){
                showInfoSub_route(data);
            }
        }
    });
}

/**
 * 告警信息渲染
 */
function alarmInfoRender(){
    //告警信息
    let showInfoData = uWeb.getStorage("showInfoData");
    //所有摄像头
    let mapEquipmentsInitData = uWeb.getStorage("mapEquipmentsInitData");
    //告警路径
    let showInfoSub_routeData = uWeb.getStorage("showInfoSub_routeData");
    //大风数据
    let windTestInitData = uWeb.getStorage("windTestInitData");
    let zoom = map.getZoom();
    if(zoom < 10){
        //清除摄像头
        mapEquipmentsInitHandle([]);
        //清除告警闪烁
        fogOpen = false;
        showInfoSub_alarmShock(showInfoData);
        //所有摄像头热力图
        mapEquipmentsInit_heapMap(mapEquipmentsInitData);
        //告警热力图
        showInfoSub_heapMap(showInfoData);
        //清除大风
        windTestInitHandle(windTestInitData);
    } else {
        //摄像头初始化
        mapEquipmentsInitHandle(mapEquipmentsInitData);
        //清除所有摄像头热力图
        mapEquipmentsInit_heapMap([]);
        //清除热力图
        showInfoSub_heapMap([]);
        //告警闪烁
        fogOpen = true;
        showInfoSub_alarmShock(showInfoData);
        //显示大风
        windTestInitHandle(windTestInitData, windTest);
    }
    showInfoSub_route_handle(showInfoSub_routeData);
    showRight();
}

//告警闪烁
function showInfoSub_alarmShock(data){
    //告警闪烁点清空
    alarmImgCloseAll(map);
    let alarmHtml_right = "";
    let html = "";
    let unconfirmhtml = "";
    let nwssgjHtml = "";
    let nwssgjCount = 0;
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
            continue;
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
                //加载告警闪烁点
                addCustomMarker(map,tempContent,new AMap.LngLat(parseFloat(data[i].lon), parseFloat(data[i].lat)),data[i].epId,data[i].equName,zIndex,data[i].address);
            }
        }

        try{
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
                    html += '<li style="width:25%;text-align: left;">' + "异常设备</li>";
                    let begintime = data[i].begintime.substring(10);
                    html += '<li style="width:25%;text-align: left;" title=\'" + data[i].begintime + "\'>' + (begintime==''?'&nbsp;':begintime) + "</li>";
                    html += '<li style="width:25%;text-align: left;"><a href=\'#\' style=\'color: #95C1EE;\'' +
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

    $("#contentUl").html(html);
    $("#contentUl_unconfirm").html(unconfirmhtml);
    parent.setHtmlById('nwssgj', nwssgjHtml);
    parent.setHtmlById('nwssgjCount', nwssgjCount);
}

function showRight(){
    let alarmHtml_right = "";
    $.ajax({
        type: "GET",
        url: "/fog/fogTrack/selectRealAlarmInfoListMerge",
        dataType: 'json',
        success: function (data) {
            //***告警信息---右边start***
            try {
                for (let i = 0; i < data.length; i++) {
                    if (data[i].distance != -1) {

                        alarmHtml_right +=
                            '<div class="panel panel-default">';
                        if (data[i].distance == -1) {
                            alarmHtml_right += '<div class="panel-heading" style="background-color: lightcoral;">异常设备</div>';
                        } else {
                            // if (data[i].distance >= 200) {
                            //     alarmHtml_right += '<div class="panel-heading" style="background-color: yellow !important;">黄色告警(100~200m)</div>';
                            // } else if (data[i].distance >= 100) {
                            //     alarmHtml_right += '<div class="panel-heading" style="background-color: yellow !important;">黄色告警(100~200m)</div>';
                            // } else if (data[i].distance >= 50) {
                            //     alarmHtml_right += '<div class="panel-heading" style="background-color: orange !important;">橙色告警(50~100m)</div>';
                            // } else if (data[i].distance >= 0) {
                            //     alarmHtml_right += '<div class="panel-heading" style="background-color: red !important;">红色告警(0~50m)</div>';
                            // }
                            if (data[i].distance >= 100) {
                                alarmHtml_right += '<div class="panel-heading" style="">强浓雾</div>';
                            } else if (data[i].distance >= 50) {
                                alarmHtml_right += '<div class="panel-heading" style="">特强浓雾</div>';
                            } else if (data[i].distance >= 0) {
                                alarmHtml_right += '<div class="panel-heading" style="">特强浓雾</div>';
                            }


                        }
                        date = new Date(data[i].begintime);

                        var year = date.getFullYear();
                        var month = date.getMonth() + 1;
                        var day = date.getDate();
                        var dateStr = year + '-' + month + '-' + day;
                        let $src_src_ = data[i].imgfn;
                        alarmHtml_right +=
                            '<div class="panel-body">' +
                            '<div class="expand-popc-width">' +
                            '<div class="expand-popc-left">' +
                            // '<img src="' + $src_src_ + '" alt="告警图片" onclick="expand_popc_click(this);" class="img-rounded">' +
                            '<div class="expand-popc-text">当前最低能见度</div>' +
                            '<div class="expand-popc-text">告警点位</div>' +
                            '<div class="expand-popc-text">告警路段</div>' +
                            '<div class="expand-popc-text">影响范围</div>' +
                            '</div>' +
                            '<div class="expand-popc-right">' +
                            '<div class="expand-popc-text">' + data[i].distance + '米</div>' +
                            '<div class="expand-popc-text">' + data[i].equName + '&nbsp;&nbsp;<a href="javascript:void(0)" onclick="openFog(\'' + data[i].epId + '\')">查看</a></div>' +
                            '<div class="expand-popc-text">' + data[i].hwName + '</div>' +
                            '<div class="expand-popc-text">' + data[i].influence + 'km</div>' +
                            '</div>' +
                            '</div>' +
                            // '<label>2020-05-16 01:28:56</label>' +
                            '</div>' +
                            '</div>';
                    }
                }
            } catch (e) {
                console.log(e);
            }
            $("#expand-popc").html(alarmHtml_right);
            //***告警信息---右边end***
        }
    });
}

//路径三色图
function showInfoSub_route(showInfoData){
    //路线三色图
    $.ajax({url: '/fog/fogTrack/getAlarmTrack',
        type: 'post',
        data: {epJson: JSON.stringify(showInfoData)},
        dataType: 'json',
        success: function (data) {
            uWeb.setStorage("showInfoSub_routeData", data);
        }
    });
}

//路径三色图渲染
function showInfoSub_route_handle(data){
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

//热力图
function showInfoSub_heapMap(data){
    //详细的参数,可以查看heatmap.js的文档 http://www.patrick-wied.at/static/heatmapjs/docs.html
    //参数说明如下:
    /* visible 热力图是否显示,默认为true
     * opacity 热力图的透明度,分别对应heatmap.js的minOpacity和maxOpacity
     * radius 势力图的每个点的半径大小
     * gradient  {JSON} 热力图的渐变区间 . gradient如下所示
     *	{
     .2:'rgb(0, 255, 255)',
     .5:'rgb(0, 110, 255)',
     .8:'rgb(100, 0, 255)'
     }
     其中 key 表示插值的位置, 0-1
     value 为颜色值
     */
    let heatmapData = [];
    for (let i = 0; i < data.length; i++) {
        let heapValue = 0;
        if(data[i].distance == undefined || data[i].distance == null || data[i].distance == "" || data[i].distance == -1){
            //设备异常
            continue;
        } else if(data[i].distance <= 50){
            heapValue = 200;
        } else if(data[i].distance <= 100){
            heapValue = 80;
        } else if(data[i].distance <= 200){
            heapValue = 65;
        } else if(data[i].distance > 200){
            heapValue = 30;
        }
        if(data[i].lon && data[i].lat){
            heatmapData.push({lng: parseFloat(data[i].lon), lat: parseFloat(data[i].lat), count: heapValue})
        }
    }
    // let heatmapData = [{
    //     "lng": 119.43866,
    //     "lat": 33.669427,
    //     "count": 50
    // }, {
    //     "lng": 119.757264,
    //     "lat": 33.174271,
    //     "count": 100
    // }, {
    //     "lng": 119.449647,
    //     "lat": 33.28455,
    //     "count": 150
    // }, {
    //     "lng": 119.178133,
    //     "lat": 34.596076,
    //     "count": 170
    // }];
    //设置数据集：该数据为北京部分“公园”数据
    heatmap.setDataSet({
        data: heatmapData,
        max: 100
    });
}

//所有摄像头热力图
function mapEquipmentsInit_heapMap(data){
    let heatmapData = [];
    for (let i = 0; i < data.length; i++) {
        let heapValue = 10;
        if(data[i].lng && data[i].lat){
            heatmapData.push({lng: parseFloat(data[i].lng), lat: parseFloat(data[i].lat), count: heapValue})
        }
    }
    //设置数据集：该数据为北京部分“公园”数据
    baseHeatmap.setDataSet({
        data: heatmapData,
        max: 100
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

function realImgViewFunc(epId,name,equLocation){
    hasClose=0;
    $.ajax({url: '/system/equipment/lookup/updateimgpath/'+epId,
        type: 'post',
        dataType: 'json',
        success: function (data) {
            //清除路径
            if(data.fMeter){
                layer.open({
                    type : 2,
                    //title : "ID："+epId+" >> 桩号："+name+" >> 位置："+equLocation,
                    title : "监测点位详情",
                    maxmin : false,
                    shadeClose : false, // 点击遮罩关闭层
                    area : [ '900px', '570px' ],
                    zIndex:99999999,
                    content : '/system/equipment/home_lookup/' + epId + "?blank_=1"
                });
            }
            else{
                layer.msg("前端监控平台维护中", {time: 1000});
            }
        }
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
    });
    marker.setAnimation('AMAP_ANIMATION_BOUNCE');
    marker.setMap(map);
    setTimeout(function(){
        marker.setMap(null);
    }, 3000);
    map.setZoom(14);  // 初始化地图,设置中心点坐标和地图级别
    map.setCenter(point);
}

function gethasClose(){
    return hasClose;
}

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

/****告警****/

/**路段告警获取*/
let queryAlarmRoadByUser = function() {
    $.ajax({url:'/fog/alarmRoad/queryAlarmRoadByUser',type:'get',data:{},dataType:'json',
        success:function(data){
            if(data.length > 0){
                let html = '';
                for(let i = 0; i < data.length; i++){
                    html += '<li>'+data[i].hwName+'当前最小可视距离'+data[i].mindistanceNow+'米。</li>';
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
/**
 * .amap-layer{animation: glow 800ms ease-out infinite alternate;}
 * .amap-labels{animation: glow 800ms ease-out infinite alternate;}
 */
// setTimerFunc(function(){
//     if($('#expand-popc')[0].childNodes.length > 0){
//         $("#alarm_icon img")[0].src = "/img/alarm_red.png";
//         $("#allmap canvas").attr("class", "amap-layer");
//     } else {
//         $("#alarm_icon img")[0].src = "/img/alarm_black.png";
//         $("#allmap canvas").attr("class", "");
//     }
// }, 1000, true);
setTimerFunc(function(){
    if($('#expand-popc')[0].childNodes.length > 0){
        $("#alarm_icon img")[0].src = "/img/alarm_red.png";
        $(".amap-layer").css({"animation": "glow 800ms ease-out infinite alternate"});
        $(".amap-labels").css({"animation": "glow 800ms ease-out infinite alternate"});
    } else {
        $("#alarm_icon img")[0].src = "/img/alarm_black.png";
        $(".amap-layer").css({"animation": ""});
        $(".amap-labels").css({"animation": ""});
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

$('.close_out').click(function(){
    $("#buhegeshipin").hide(500);
    $("#tuanwuyujing").hide(500);
    $("#left_bottom_btn").animate({bottom:"10px"});
});

$('#expand').click(function(){
    $('#expand').hide();
    $('#expand-popc').hide(100);
    setTimerFunc(function(){
        $("#alarm_icon").show(100);
    },50,false);
});

$('#alarm_icon').click(function(){
    $('#alarm_icon').hide(100);
    setTimerFunc(function(){
        $('#expand').show(100);
        $('#expand-popc').show(100);
    },50,false);
});

$('.img_close_out').click(function(){
    $('#expand-popc-preview').hide(250);
});
function expand_popc_click(ele){
    document.getElementById("expand-popc-preview-img").src = ele.src;
    $('#expand-popc-preview').show(250);
}

/**月统计报表**/
function monthReport(){
     let d = new Date();
     var nowYear = d.getFullYear();

    $.ajax({
        type : 'get',
        url : '/report/alarmReport/monthReportStatistics',
        data:{'nowYear':nowYear},
        success : function(data) {
            showMonthReport(data,nowYear);
        }
    });


}

function showMonthReport(data,nowYear){
    //设置雾的颜色
    let colors = [];
    if(data.legendData){
        data.legendData = data.legendData.slice(-4);
        data.seriesData = data.seriesData.slice(-4);
    }else {
        data.legendData = [];
        data.seriesData = [];
    }
    if(data.legendData.length >= 4){
        colors[data.legendData.length-4] = "#f4ea2a";
        colors[data.legendData.length-3] = "#ffa500";
        colors[data.legendData.length-2] = "#d81e06";
        colors[data.legendData.length-1] = "#9a1200";
    }
    myChartMonthReport = echarts.init(document.getElementById('monthReport'));

    let titleText = "    【"+nowYear+"】月告警统计报表";

    optionMonthReport = {
        title: {
            text: titleText,
            textAlign: 'left',
            textStyle:{
                fontSize: 15,//字体大小
                color: '#ffffff'//字体颜色
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow' | 'cross'
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
        /*legend: {
            textStyle:{
                fontSize: 16,//字体大小
                color: '#ffffff'//字体颜色
            },
            top:'6%',
            data: data_func(data.legendData)
        },*/
        color: colors,

        grid: {
            left: '2%',
            right: '2%',
            bottom: '2%',
            top:'22%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                axisLabel:{
                    show: true,
                    textStyle:{
                        color: '#ffffff'
                    }
                },
                triggerEvent:true,	//此属性表示点击['1月', '2月', '3月',。。。]会触发click事件
                data: data.xAxisData
            }
        ],
        yAxis: {
            type: 'value',
            scale: true,
            splitNumber: 2,
            //name: '可见距离（米）',
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
        series : serie_func(data.seriesData)//series这条属性抽出来，写成serie_func函数,根据后端返回值生成对应个数的data[]
    };


    myChartMonthReport.off('click');	//清除之前的点击事件
    //console.log(optionMonthReport);
    myChartMonthReport.setOption(optionMonthReport, true);

    myChartMonthReport.on('click', function (params) {
        let vHwName = "";
        let vAlarmDate = "";
        if(params.componentType =="xAxis"){
            vAlarmDate = nowYear+"-"+params.value.substring(0,2);
        }else{
            vAlarmDate = nowYear+"-"+params.name.substring(0,2);
            if(params.seriesName.indexOf("告警") == -1 ){
                vHwName = params.seriesName;
            }
        }
        alarmDetails('',vHwName,vAlarmDate);
    });
}

/**年统计报表**/
function yearReport(){
    $.ajax({
        type : 'get',
        url : '/report/alarmReport/yearReportStatistics',
        data:{},
        success : function(data) {
            legendHwNamesArray = data.legendData;
            showYearReport(data);
        }
    });
}

/**年统计报表**/
function showYearReport(data){
    if(data.legendData){
        data.legendData = data.legendData.slice(0,-4);
        data.seriesData = data.seriesData.slice(0,-4);
    }else {
        data.legendData = [];
        data.seriesData = [];
    }

    myChartYearReport = echarts.init(document.getElementById('yearReport'));

// 		let colors = "['#1790cf','#1bb2d8','#99d2dd','#88b0bb','#1c7099','#038cc4','#75abd0','#afd6dd']";
    let colors = new Array();
    colors[0] = "#1790cf";
    colors[1] = "#1bb2d8";
    colors[2] = "#99d2dd";
    colors[3] = "#88b0bb";
    colors[4] = "#1c7099";
    colors[5] = "#038cc4";
    colors[6] = "#75abd0";
    colors[7] = "#afd6dd";
    colors[8] = "#9287e7";
    colors[9] = "#60acfc";
    colors[10] = "#32d3eb";
    colors[11] = "#5bc49f";
    colors[12] = "#ff7c7c";
    colors[13] = "#feb64d";
    let d = new Date();
    let nowYear = d.getFullYear();
    let titleText = "    【"+nowYear+"】路段告警统计报表";
    optionYearReport = {
        title: {
            text: titleText,
            textAlign: 'left',
            textStyle:{
                fontSize: 15,//字体大小
                color: '#ffffff'//字体颜色
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow' | 'cross'
            },
            textStyle: {
                fontSize:10
            },
            position: function (pos, params, el, elRect, size) {
                var obj = {top: 10};
                obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
                return obj;
            },
            formatter: function(params){
            	var res = params[0].name.split(":")[0];
	            　　　	res += "<br>"+params[0].marker+params[0].seriesName+"："+params[0].data;
	            　　　return res;
            }

        },
        /*legend: {
            textStyle:{
                fontSize: 16,
                color: '#ffffff'
            },
            top:'6%',
            data: data_func(data.legendData)
        },*/
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top:'22%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                axisLabel:{
                    show: true,
                    textStyle:{
                        color: '#ffffff'
                    },
                    formatter:function (value, index) {
                        return value.split(":")[0];
                    }
                },
                triggerEvent:true,	//此属性表示点击['2020年', '2021年',。。。]会触发click事件
                data: xDate_func(data.seriesData)
            }
        ],
        yAxis: {
            type: 'value',
            scale: true,
            splitNumber: 2,
            //name: '可见距离（米）',
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
        color: colors ,
        series: serie_func2(data.seriesData)//series这条属性抽出来，写成serie_func函数,根据后端返回值生成对应个数的data[]
    };

    // $("#yearReport").css("width","100%");
    // $("#yearReport").css("height","410px");
    // $("#monthReport").hide();
    // $("#yearReport").show();
    myChartYearReport.off('click');	//清除之前的点击事件
    //console.log(optionYearReport);
    myChartYearReport.setOption(optionYearReport, true);

    myChartYearReport.on('click', function (params) {
        let vHwName = params.name.split(":")[0];
        let vHwId = params.name.split(":")[1];
        // let vAlarmDate = "";
        // if(params.componentType =="xAxis"){
        //     vAlarmDate = nowYear+"-"+params.value.substring(0,2);
        // }else{
        //     vAlarmDate = nowYear+"-"+params.name.substring(0,2);
        //     if(params.seriesName.indexOf("告警") == -1 ){
        //         vHwName = params.seriesName;
        //     }
        // }
        let now = new Date()
        alarmDetails(vHwId,vHwName, formatDate(now));
    });

}

/**legend的data参数生成器函数**/
function data_func (item_op) {
    let lgend_data = [];
    let lgend_array = new Array();
    for(let i = 0; i < item_op.length; i++){
        if(item_op.length > 4 && i == item_op.length-4){
            lgend_data.push("\n");  //路段和雾强制换行
        }
        lgend_data.push(item_op[i] );
    };
    return lgend_data;
}

function data_func2 (item_op) {
    let lgend_data = [];
    let lgend_array = new Array();
    for(let i = 0; i < item_op.length; i++){
        lgend_data.push(item_op[i] );
    };
    return lgend_data;
}

/**serie生成器**/
function serie_func(item_op){
    let serie = [];
    for(let i = 0; i < item_op.length; i++){  //根据后端传来的数据item_op的长度循环创建serie里面的data[]，这里取的名字是item
        let item = {
            name:item_op[i].name,
            type: 'bar',
            stack:item_op[i].stack,
            data:data_func2(item_op[i].data)
        }
        serie.push(item );
    };
    return serie;
}

/**serie生成器**/
function serie_func2(item_op){
    let serie = [];
    let item = {
        name:"路段告警次数",
        type: 'bar',
    }
    let datas = [];
    for(let i = 0; i < item_op.length; i++){  //根据后端传来的数据item_op的长度循环创建serie里面的data[]，这里取的名字是item
        let data = data_func2(item_op[i].data)
        datas.push(data[0] );
    };
    item.data = datas;
    serie.push(item);
    return serie;
}

function xDate_func(item_op){
	let xDate = [];
	for(let i = 0; i < item_op.length; i++){
		xDate.push(item_op[i].name);
	}
	return xDate;
}

let optionHighwayReport = {
    title: {
        text: '',
        textAlign: 'left',
        textStyle:{
            fontSize: 15,//字体大小
            color: '#ffffff'//字体颜色
        }
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow' | 'cross'
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
    /*legend: {
        textStyle:{
            fontSize: 16,//字体大小
            color: '#ffffff'//字体颜色
        },
        top:'6%',
        data: data_func(data.legendData)
    },*/
    color: ["#f4ea2a","#ffa500","#d81e06","#9a1200",],

    grid: {
        left: '2%',
        right: '2%',
        bottom: '2%',
        top:'22%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            axisLabel:{
                show: true,
                textStyle:{
                    color: '#ffffff'
                }
            },
            triggerEvent:true,	//此属性表示点击['1月', '2月', '3月',。。。]会触发click事件
            data: []
        }
    ],
    yAxis: {
        type: 'value',
        scale: true,
        splitNumber: 2,
        //name: '可见距离（米）',
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
    series : []
};

function formatDate(date) {
    var date = new Date(date);
    var YY = date.getFullYear();
    var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
    var DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
    var hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours());
    var mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
    var ss = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return YY + '-' + MM;
}
/****告警end****/
/**************************************中间地图**************************************/
function windTestInit(){
    $.ajax({url:'/fog/fogTrack/windTest',type:'post',data:{hwId:'1'},dataType:'json',
        success:function(data){
            uWeb.setStorage("windTestInitData", data);

        }
    });
}

function windTestInitHandle(data, windTest){
    if(data === undefined){
        return;
    }
    try{
        if (document.createElement('canvas').getContext) {  // 判断当前浏览器是否支持绘制海量点
            for(; windMarkers.length > 0;){
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
                    html += "<li class='weizhi_li'><a href='javaScript: ;' style='color: #95C1EE;' title='" +data[i].equLocation+ "' onclick='"+"locationFunc("+param__+")"+"'>" + data[i].equLocation.substr(0,7) + "</a></li>";
                    html += "<li class='fengsu_li'>" + data[i].winds + "</li>";
                    html += "<li class='fengxiang_li'>"+getWindd(data[i].windd)+"</li>";
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
                if(typeof windTest === "function"){
                    windTest(new AMap.LngLat(data[i].lng,data[i].lat),icon,data[i].epId,data[i].epId,data[i].equLocation);
                }
            }
            //大风预警
            $("#contentUl_wind").html(html);
        } else {
            layer.msg("'建议在chrome、firefox、IE8+以上浏览器查看'");
        }
    } catch (e) {
        console.log(e);
    }
}