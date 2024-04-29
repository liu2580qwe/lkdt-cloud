//网页加载
$(function () {
    setStyleFun();
    $("#timeCalc").text(10);
    execAlarmEquipList();
    statisticAjax();//统计图
    window.onresize = function(){
        setStyleFun();
    };
    setInterval(function(){
        setStyleFun();
    },1000);
    setInterval(function(){
        setSystemTime();
        $("#timeCalc").text($("#timeCalc").text()-1);
    },1000);
    setInterval(function(){
        $("#timeCalc").text(10);
        execAlarmEquipList();
        statisticAjax();
    },10000);

    //创建地图
    createMap();
});

/**
 * 设置样式
 */
function setStyleFun(){
    //图片列表样式
    // $('#alarmEquipImgRealTime img').attr("height",$('#row2_map').height()/3);
    //地图样式自适应高度
    $('#container').css('height',$('#row2_map').height());
    //列表样式自适应高度
    $('#alarm_list').css('height',$('#row2_map').height());
}
/****************************************** 1-3 系统时间 start ************************************************/
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

/**
 * 设置系统时间
 */
function setSystemTime(){
    var date = new Date();
    document.getElementById('timeCurr').innerText = date.Format("yyyy年MM月dd日 hh:mm:ss "+getDay(date.getDay()));
}

function getDay(day) {
    switch (day) {
        case 0:
            text = "星期日";
            break;
        case 1:
            text = "星期一";
            break;
        case 2:
            text = "星期二";
            break;
        case 3:
            text = "星期三";
            break;
        case 4:
            text = "星期四";
            break;
        case 5:
            text = "星期五";
            break;
        case 6:
            text = "星期六";
            break;
    }
    return text;
}
/****************************************** 1-3 系统时间 end ************************************************/

/****************************************** 4 告警设备图像列表 start ************************************************/
var equipImgData = [];
var equipImgDataIndex = -1;
/**
 * 获取告警设备列表 能见度由低到高
 */
function execAlarmEquipList(){
    $.ajax({url:'/fog/fogTrack/getAlarmEquipList',type:'post',data:{},dataType:'json',
        success:function(data){
            //参数初始化
            equipImgData = data;
            equipImgDataIndex = -1;
            loadEquipImg(equipImgData);
        }
    });
}

/**
 * 加载摄像头数据
 * @param data
 */
function loadEquipImg(equipImgData){
    if(equipImgData.length > 0){
        loadEquipments();
    }
}
function openVideo(){
	layer.open({
        type : 1,
        title : "视频",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '65%', '95%' ],
        zIndex:99999999,
        content : '<video id="video1" src="/video/shouye.mp4" autoplay="autoplay" style="height: 400px;width:700px;margin-left:20px;"></video>'
    });
	
}
function openVideo1(){
	layer.open({
        type : 1,
        title : "视频",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '65%', '95%' ],
        zIndex:99999999,
        content : '<video id="video1" src="/video/shouye1.mp4" autoplay="autoplay" style="height: 400px;width:700px;margin-left:20px;"></video>'
    });
	
}

/**
 * 加载摄像头图片
 */
function loadEquipments(){
    var curr = equipImgData.shift();
    equipImgDataIndex++;
    //获取图片
    $.ajax({type: "post",url: "/system/equipment/lookup/updateimgpath/"+curr.epId,})
    .success(function(message) {
        var $src_src_= encodeURI(message.path);
        if($('#alarmEquipImgRealTime td').children()[equipImgDataIndex]){
            $('#alarmEquipImgRealTime td').children()[equipImgDataIndex].src = $src_src_;
            $('#alarmEquipImgRealTime td').children()[equipImgDataIndex].alt = curr.equName;
            $('#alarmEquipImgRealTime td').children()[equipImgDataIndex].title = curr.equName;
            loadEquipments();
        } else {
            return;
            //$('#alarmEquipImgRealTime').append('<img src="'+$src_src_+'" alt="'+curr.equName+'" class="img-rounded img_equip_list"/>');
        }
        $('#alarmEquipImgRealTime td').children()[0].src = '/img/youwu.jpg';
    }).fail(function(err){
        console.log(err);
    });
}
/****************************************** 4 告警设备图像列表 end ************************************************/

/****************************************** 5-6 地图三色图|告警列表 start ************************************************/
var map$zoom = 7;
var map$center = [119.117709,33.136569];//初始地图中心点 江苏
//创建地图
var map;

/**创建地图*/
function createMap(){
    map = new AMap.Map('container', {
        cursor: 'default',
        resizeEnable: true,
        zoom: map$zoom,
        center: map$center,
        mapStyle: "amap://styles/1de1ab4a6f3d6b4a31d558771f073fa2"
    });

    AMapUI.loadUI(['geo/DistrictExplorer'], function(DistrictExplorer) {
        initPage(DistrictExplorer);
    });
}

function initPage(DistrictExplorer) {
    //创建一个实例
    var districtExplorer = new DistrictExplorer({
        map: map
    });
    var countryCode = 100000,provCodes = [320000],cityCodes = [];
    districtExplorer.loadMultiAreaNodes(
        //只需加载全国和市，全国的节点包含省级
        [countryCode].concat(cityCodes),
        function(error, areaNodes) {
            var countryNode = areaNodes[0],
                cityNodes = areaNodes.slice(1);
            var path = [];
            //首先放置背景区域，这里是大陆的边界
            path.push(getLongestRing(countryNode.getParentFeature()));
            for (var i = 0, len = provCodes.length; i < len; i++) {
                //逐个放置需要镂空的省级区域
                path.push.apply(path, getAllRings(countryNode.getSubFeatureByAdcode(provCodes[i])));
            }
            for (var i = 0, len = cityNodes.length; i < len; i++) {
                //逐个放置需要镂空的市级区域
                path.push.apply(path, getAllRings(cityNodes[i].getParentFeature()));
            }
            //绘制带环多边形，江苏省边界
            //https://lbs.amap.com/api/javascript-api/reference/overlay#Polygon
            var polygon = new AMap.Polygon({
                bubble: true,
                lineJoin: 'round',
                strokeColor: 'white', //线颜色
                strokeOpacity: 1, //线透明度
                strokeWeight: 0, //线宽
                fillColor: 'black', //填充色
                fillOpacity: 0.80, //填充透明度
                map: map,
                path: path
            });
            //摄像头初始化
            mapEquipmentsInit();
        });
}

function getAllRings(feature) {
    var coords = feature.geometry.coordinates,
        rings = [];
    for (var i = 0, len = coords.length; i < len; i++) {
        rings.push(coords[i][0]);
    }
    return rings;
}

function getLongestRing(feature) {
    var rings = getAllRings(feature);
    rings.sort(function(a, b) {
        return b.length - a.length;
    });
    return rings[0];
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
            if(data.code != 200){
                alert("内部错误，请联系管理员");
                return;
            }
            //摄像头经纬度 >>>30m:#C30004,50m:#F87018,100m:#F9C115,200:#F7F940,300:#357A00,500:#357A00,
            data.data.forEach(function(currValue,index,arr){
                //标记摄像头
                var arrayData = [];
                currValue.hw_equipments.forEach(function(currData,i_,a_){
                    arrayData.push({lnglat: [currData.lng,currData.lat], epId:currData.epId, name: currData.equ_name, equLocation:currData.equ_location,id: index+'-'+i_});
                });
                //摄像头标记
                mapClusterMark(arrayData);
                //告警信息刷新
                showInfo();
                setInterval(showInfo,30000);
            },this);
        }
    });
}

/**
 * 摄像头实时图片获取
 * */
function realImgViewFunc(epId,name,equLocation){
    layer.open({
        type : 2,
        title : "ID："+epId+" >> 桩号："+name+" >> 位置："+equLocation,
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '95%', '100%' ],
        zIndex:99999999,
        content : '/system/equipment/home_lookup/' + epId
    });

}

/**定义闪烁点divMarker*/
function addCustomMarker(content,position,epId,name,zIndex,equLocation){
    var marker1 = new AMap.Marker({
        map:map,
        offset:new AMap.Pixel(-10,-10), //相对于基点的偏移位置
        draggable:false,  //是否可拖动
        content:content,   //自定义覆盖物内容,
        position:position, //基点位置
        zIndex:zIndex,//显示级别
    });
    marker1.on( 'click', function(){
        realImgViewFunc(epId,name,equLocation);
    });
    return marker1;
}

//图片
// var imgRealFromUrl = imgRealFromUrl_src;

var fogmarkers=[];
var accidentmarkers=[];


/**点聚合标记*/
function mapClusterMark(arrayData){
    var cluster, markers = [];
    for (var i = 0; i < arrayData.length; i += 1) {
        var marker = new AMap.Marker({
            position: arrayData[i]['lnglat'],
            content: '<div style="background-color: hsla(180, 100%, 50%, 0.4); height: 14px; width: 14px; border: 1px solid hsl(180, 100%, 40%); border-radius: 12px; box-shadow: hsl(180, 100%, 50%) 0px 0px 1px;"></div>',
            offset: new AMap.Pixel(-7, -7)
        });
        // imgRealFromUrl = "/public/getLastImg?epId="+arrayData[i].epId;
        // marker.content = '<div style="width:100px;height:100px;">' +
        //     '<text class="updatecsssubstring" title="'+arrayData[i].name+'">'+arrayData[i].name+'</text>'+
        //     '<img src="'+imgRealFromUrl+'" onerror="nofind(this)" style="width:100%;height:80%;cursor: pointer;" /></div>';
        marker.on('click',function(e){
            realImgViewFunc(this.epId,this.name,this.equLocation);
        },arrayData[i]);
        markers.push(marker);
    }
    //添加聚合组件
    map.plugin(["AMap.MarkerClusterer"],function() {
        cluster = new AMap.MarkerClusterer(map,markers,
            {
                gridSize: 40,
                minClusterSize:6,
                averageCenter: true,
                renderClusterMarker:function (context) {
                    var count = markers.length;
                    var factor = Math.pow(context.count / count, 1 / 18);
                    var div = document.createElement('div');
                    var Hue = 180 - factor * 180;
                    var bgColor = 'hsla(210,72%,76%,0.5)';
                    var fontColor = 'hsla(0,0%,100%,1)';
                    var borderColor = 'hsla(210,100%,40%,0.7)';
                    var shadowColor = 'hsla(' + Hue + ',100%,50%,0.7)';
                    div.style.backgroundColor = bgColor;
                    var size = Math.round(5 + Math.pow(context.count / count, 1 / 5) * 20);
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
    //设置缩放级别和中心点
    map.setZoomAndCenter(map$zoom, map$center);
}

/**
 * 根据坐标列表划线
 * */
function polylineFunc(equipmentLngLat,colorDesc){
    //给定路径划线
    var polyline = new AMap.Polyline({
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
}

var alarmInfo = [];
/**
 * 告警信息渲染
 * */
function showInfo() {
    $.ajax({
        type: "GET",
        url: "/fog/fogTrack/selectRealAlarmInfoList",
        dataType:'json',
        success: function (data) {
            // console.log(data);
            //告警列表初始化清空
            alarmImgCloseAll();
            alarmInfo = [];
            var html = "";
            for (var i = 0; i < data.length; i++) {
                //imgRealFromUrl = imgRealFromUrl_src+"path=" + data[i].imgpath.replace(/\\/g,"/")+"&epid=" + data[i].epId;
                //渲染告警图片
                var tempContent;
                var zIndex = 100;
                if(data[i].distance <= 30){
                    tempContent = '<img src="/img/point_red.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    zIndex = 200;
                } else if(data[i].distance <= 50){
                    tempContent = '<img src="/img/point_light_red.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    zIndex = 199;
                } else {
                    tempContent = '<img src="/img/point_yellow.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    zIndex = 198;
                }
               
                if(fogopen){
                	 var alarmMarker=addCustomMarker(tempContent,new AMap.LngLat(parseFloat(data[i].lon), parseFloat(data[i].lat)),data[i].epId,data[i].equName,zIndex,data[i].address);
                	 alarmInfo.push({marker:alarmMarker,name:"alarmInfo_"+i});
                	 fogmarkers.push(alarmMarker);
                }
                
                html += "<tr>";
                /*if (i == 0) {
                    html += "<th scope='row'><span class='label label-danger'>" + (i + 1) + "</span></th>";
                } else if (i == 1) {
                    html += "<th scope='row'><span class='label label-warning'>" + (i + 1) + "</span></th>";
                } else {
                    html += "<th scope='row'><span class='label label-default'>" + (i + 1) + "</span></t  h>";
                }*/
                var param__= data[i].lon+","+data[i].lat;
                html += "<td style='word-wrap:break-word;' ><a href='#' style='color: #95C1EE;' title='" +data[i].epId+"："+ data[i].equName + "' onclick='"+"locationFunc("+param__+")"+"'>" +data[i].epId+"："+ data[i].equName + "</a></td>";
                //团雾/雾霾
                if (data[i].fogType==11) {
                    html += "<td>团雾</td>";
                } else if (data[i].fogType==12){
                    html += "<td>雾霾</td>";
                }
                html += "<td>" + data[i].distance + "(m)</td>";
                var begintime = data[i].begintime.substring(10);
                html += "<td title='" + data[i].begintime + "'>" + begintime + "</td>";
                html += "<td>" + data[i].forTime + "</td>";
                html += "</tr>";
            }
            $("#content").html(html);
            //地图右键菜单
            rightAddMenu();
            //路线三色图
            $.ajax({url: '/fog/fogTrack/getAlarmTrack',
                type: 'post',
                data: {epJson: JSON.stringify(data)},
                dataType: 'json',
                success: function (data) {
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

//告警定位标记
var markerAttr = [];
/**
 * 警告列表摄像头定位
 * */
function locationFunc(lon,lat){
    //清空
    markerAttr.forEach(function(curr,i,array){
        curr.marker.setMap(null);
    });
    //定位标记
    var marker = new AMap.Marker({
        position: new AMap.LngLat(lon, lat),
        icon: new AMap.Icon({
            size: new AMap.Size(26, 40), // 图标尺寸
            image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png', // Icon的图像
            imageSize: new AMap.Size(26, 40), // 根据所设置的大小拉伸或压缩图片
        }),
        offset: new AMap.Pixel(-13, -35),
        draggable: false,// 设置是否可拖拽
        cursor: 'move'
    });
    markerAttr.push({marker:marker});
    marker.setMap(map);
    marker.setAnimation('AMAP_ANIMATION_BOUNCE');// 设置点标记的动画效果，此处为弹跳效果
    map.setCenter(new AMap.LngLat(lon, lat));
}

/**
 * 右键菜单
 * */
function rightAddMenu(){
    var contextmenu=new AMap.ContextMenu();
    var pos=[];
    // 添加右键菜单内容项
    contextmenu.addItem("<span style='color:\'#00000\''>清除标记</span>",function () {
        markerAttr.forEach(function(curr,i,array){
            curr.marker.setMap(null);
        })
    },2);
    // 监听鼠标右击事件
    map.on("rightclick",function (e) {
        contextmenu.open(map,e.lnglat);
        pos=e.lnglat;
    });
}

/**
 * 告警信息初始化
 * */
function connect() {
    var sock = new SockJS("/endpointChat");
    var stomp = Stomp.over(sock);
    stomp.connect('guest', 'guest', function (frame) {
        //首页初始化告警信息
        showInfo();
        /**  订阅了/user/queue/notifications 发送的消息,这里雨在控制器的 convertAndSendToUser 定义的地址保持一致, 
         *  这里多用了一个/user,并且这个user 是必须的,使用user 才会发送消息到指定的用户。 
         *  */
        stomp.subscribe('/topic/getWarning', function (response) { //订阅/topic/getResponse 目标发送的消息。这个是在控制器的@SendTo中定义的。
            if (JSON.parse(response.body).responseMessage == "true") {
                showInfo();
            }
        });
    }, function (error) {
        alert(error.headers.message);
    });
}

/**
 * 清空所有告警闪烁点
 */
function alarmImgCloseAll(){
    alarmInfo.forEach(function(curr,i,array){
        curr.marker.setMap(null);
    });
}

function nofind(_this){
    _this.src="/fog/nofind.jpg";
    _this.onerror=null;
}

/**
 * 全屏
 */
function showFull(){
    var full=document.getElementById("container");
    launchIntoFullscreen(full);
}

/**
 * 退出全屏
 */
function delFull() {
    exitFullscreen();
}

/**
 * 全屏
 * @param element
 */
function launchIntoFullscreen(element) {
    if(element.requestFullscreen){
        element.requestFullscreen();
    }
    else if(element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    }
    else if(element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    }
    else if(element.msRequestFullscreen) {
        element.msRequestFullscreen();
    }
}

/**
 * 退出全屏
 */
function exitFullscreen() {
    if(document.exitFullscreen) {
        document.exitFullscreen();
    } else if(document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if(document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    }
}
/****************************************** 5-6 地图三色图|告警列表 end************************************************/

/****************************************** 7-9 统计图 start ************************************************/
//设备实时在线情况
var dom1 = document.getElementById("equipRealCon");
var myChart1 = echarts.init(dom1);
option1 = {
    backgroundColor:'#252432',
    title: {
        text: '设备实时运行情况',
        left: 'center',
        textStyle:{
            fontSize: 13,
            color:'#fff'
        }
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)",
        textStyle: {
            "fontSize": 10
        }
    },
    legend: {
        // orient: 'vertical',
        // top: 'top',
        bottom: 10,
        left: 'center',
        data: ['在运行', '无效设备'],
        textStyle:{
            color:'#fff'
        }
    },
    series : [
        {
            name:"设备运行情况",
            type: 'pie',
            radius : '65%',
            center: ['50%', '50%'],
            selectedMode: 'single',
            data:[
                {value:0, name: '在运行'},
                {value:0, name: '无效设备'}
            ],
            itemStyle: {
                normal:{
                    color:function(params) {
                        //自定义颜色
                        var colorList = ['#2BB949', '#C23531'];
                        return colorList[params.dataIndex]
                    }
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};
myChart1.setOption(option1, true);

//路段能见度趋势
var dom2 = document.getElementById("routVisibility");
var myChart2 = echarts.init(dom2);
option2 = {
    backgroundColor:'#252432',
    title: {
        top:'top',
        text: "可见距离统计-路段",
        textStyle:{
            fontSize: 13,
            color:'#fff'
        }
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        }
    },
    // legend: {
    //     data:['可见距离曲线'],
    //     textStyle:{
    //         color:'#fff'
    //     }
    // },
    xAxis: {
        type: 'category',
        name: '桩号',
        boundaryGap: false,
        data: ['K100', 'K200', 'K300', 'K500', 'K600', 'K700', 'K800'],
        axisLabel:{color:'#fff'},   // x轴字体颜色
        axisLine:{lineStyle:{color:'#fff'}},// x轴坐标轴颜色
        axisTick:{lineStyle:{color:'#fff'}},    // x轴刻度的颜色
    },
    yAxis: {
        type: 'value',
        scale: true,
        name: '可见距离（米）',
        max: 400,
        min: 0,
        axisLabel:{color:'#fff'},   // y轴字体颜色
        axisLine:{lineStyle:{color:'#fff'}},// y轴坐标轴颜色
        axisTick:{lineStyle:{color:'#fff'}},    // y轴刻度的颜色
    },
    visualMap: {
        // calculable: true,
        // min: 0,
        // max: 1000,
        top: 'top',
        left:'middle',
        // right: 5,
        color:['#2BB949'],
        textStyle:{
            color:'#fff'
        },
        pieces: [{
            gt: 0,
            lte: 1000,
            color: '#2BB949'
        }]
    },
    series: [{
        data: [530, 540, 560, 550, 600, 900, 1000],
        type: 'line',
        name:'可见距离曲线',
        smooth: true,
        areaStyle: {},
    }]
};
myChart2.setOption(option2, true);

//设备实时在线情况
var dom3 = document.getElementById("equipAlarmStatistics");
var myChart3 = echarts.init(dom3);
option3 = {
    backgroundColor:'#252432',
    title: {
        text: '设备告警情况统计-24小时以内',
        subtext: '',
        left: 'center',
        textStyle:{
            fontSize: 13,
            color:'#fff'
        }
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    legend: {
        // orient: 'vertical',
        // top: 'middle',
        bottom: 10,
        left: 'center',
        data: ['有效', '无效','未确认'],
        textStyle:{
            color:'#fff'
        }
    },
    series : [
        {
            name:'设备告警情况',
            type: 'pie',
            radius : '65%',
            center: ['50%', '50%'],
            selectedMode: 'single',
            data:[
                {value:535, name: '有效'},
                {value:510, name: '无效'},
                {value:510, name: '未确认'}
            ],
            itemStyle: {
                normal:{
                    color:function(params) {
                        //自定义颜色
                        var colorList = ['#2BB949', '#C23531', 'gray'];
                        return colorList[params.dataIndex]
                    }
                },
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};
myChart3.setOption(option3, true);


var getDistanceStatistic_data;//路段统计
var getDistanceStatistic_index = 0;//路段统计下标
var getEquipAlarmConStatistic_data;
var getEquipAlarmConStatistic_index;//告警统计
var getEquipAlarmConStatistic_index_num = 24;
/***
 * 统计图
 */
function statisticAjax(){
    //设备实时运行情况
    $.ajax({url:'/ai/listen/getEquipRealCon',type:'post',data:{hwId:'1'},dataType:'json',
        success:function(data){
            option1.series[0].data = data;
            myChart1.setOption(option1, true);
        }
    });
    //路段可见距离统计
    $.ajax({url:'/fog/fogTrack/getDistanceStatistic',type:'post',data:{hwId:'1'},dataType:'json',
        success:function(data){
            if(data.length > 0){
                //初始化
                getDistanceStatistic_data = data;
                switchFunc(getDistanceStatistic_index);
                myChart2.setOption(option2, true);
                $('#routVisibility-dropdown-menu').empty();
                data.forEach(function(curr,index,arr){
                    $('#routVisibility-dropdown-menu').append('<li><a href="##" onclick="switchFunc('+index+')">'+curr.hwName+'</a></li>');
                });
            }
        }
    });
    //设备告警情况统计
    $.ajax({url:'/fog/fogTrack/getEquipAlarmConStatistic',type:'post',data:{},dataType:'json',
        success:function(data){
            getEquipAlarmConStatistic_data = data;
            getEquipAlarmConStatistic_switchFunc(getEquipAlarmConStatistic_index_num);
        }
    });
}

/**
 * 切换路段可见度统计
 * @param index
 */
function switchFunc(index){
    getDistanceStatistic_index = index;
    option2.title.text = "可见距离统计-"+getDistanceStatistic_data[index].hwName+"路段";
    option2.xAxis.data = getDistanceStatistic_data[index].xData.split(',');
    option2.series[0].data = getDistanceStatistic_data[index].series.data.split(',');
    myChart2.setOption(option2, true);
}

/**
 * 切换告警统计
 * @param index
 */
function getEquipAlarmConStatistic_switchFunc(data){
    if(data == 24){

        getEquipAlarmConStatistic_index = "alarm24";
    } else if(data == 48){
        getEquipAlarmConStatistic_index = "alarm48";
    } else if(data == 72){
        getEquipAlarmConStatistic_index = "alarm72";
    }
    getEquipAlarmConStatistic_index_num = data;
    option3.title.text = '设备告警情况统计-'+getEquipAlarmConStatistic_index_num+'小时以内';
    option3.series[0].data = getEquipAlarmConStatistic_data[getEquipAlarmConStatistic_index];
    myChart3.setOption(option3, true);
}
/****************************************** 7-9 统计图 end ************************************************/
/**
 * 雾霾告警开关
 */
var fogopen=true;
var accopen=true;
function fogimgclick(){
	if(fogopen){
		$("#fogimg").attr("src","/img/fogclose.png");
		map.remove(fogmarkers);
		fogopen=false;
	}else{
		$("#fogimg").attr("src","/img/fogopen.png");
		map.add(fogmarkers);
		fogopen=true;
	}
	
}
function accimgclick(){
	if(accopen){
		$("#accimg").attr("src","/img/accclose.png");
		accopen=false;
	}else{
		$("#accimg").attr("src","/img/accopen.png");
		accopen=true;
	}
	
}

