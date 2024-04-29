$(function () {
    var a = $('.visualSssf_left a');
    for (var i = 0; i < a.length; i++) {
        a[i].index = i;
        a[i].onclick = function () {
            for (var i = 0; i < a.length; i++) {
                a[i].className = ''
            }
            this.className = 'active'
        }
    }

    var sfzcllH = $('.sfzcll_box').height();
    var sfzcllHtwo = sfzcllH - 2;
    $('.sfzcll_box').css('line-height', sfzcllH + 'px');
    $('.sfzcll_smallBk>div').css('line-height', sfzcllHtwo + 'px');

    //删除加载动画
    $('#load').fadeOut(1000);

    setTimeout(function () {
        $('#load').remove()
    }, 3000);
    /**********************1****************************/
    execAlarmEquipList();
    statisticAjax();
    setInterval(function(){
        execAlarmEquipList();
        statisticAjax();
    },30000);
    setStyleFun();
    window.onresize = function(){
        setStyleFun();
    };

    /***********************1 end***************************/

    //动画
    animation();
});

function setStyleFun(){
    $('.visualSfzsfl .visual_table table').height($('body').height()/3-35);
}

function animation(){
    // layui.use(['carousel', 'form'], function(){
    //     let carousel = layui.carousel,form = layui.form;
    //
    //     //改变下时间间隔、动画类型、高度
    //     carousel.render({
    //         elem: '#test2'
    //         ,interval: 1800
    //         ,anim: 'fade'
    //     });
    //
    // });
}
/**************************************左边 **************************************/
/**
 * 获取设备图片列表 能见度由低到高
 */
function execAlarmEquipList(){
    $.ajax({url:'/fog/fogTrack/getAlarmEquipList',type:'post',data:{},dataType:'json',
        success:function(data){
            //参数初始化
            loadEquipImg(data);
        }
    });
}

/**
 * 加载摄像头数据
 * @param data
 */
function loadEquipImg(equipImgData){
    if(equipImgData.length > 0){
    	for (var i = 0; i < equipImgData.length; i++) {
    		loadEquipments(i, equipImgData[i]);
    	}
    }
}

/**
 * 加载摄像头图片
 */
function loadEquipments(idx, equipImgData){
    //获取图片
    var $src_src_=equipImgData.imgpath;
    //摄像头图片获取特殊处理
    if(equipImgData.epId.indexOf('_new')>-1) {
        $src_src_ = '/ppv/getimgforpath?imgpath=' + encodeURIComponent('http://127.0.0.1:8002/public/fog/img?cid=NT001&tm=');
    }
    if($('#main1').children()[idx]){
        $('#main1').children()[idx].src = $src_src_;
        $('#main1').children()[idx].alt = equipImgData.equName;
        $('#main1').children()[idx].title = equipImgData.equName;
        //
        $($('#main1').children()[idx]).click(function(){
            var _this = $(this);//将当前的pimg元素作为_this传入函数
            imgShow("#outerdiv", "#innerdiv", "#bigimg", _this);
        });
      
    }
    //$('#main1').children()[0].src = '/img/youwu.jpg';

}

function imgShow(outerdiv, innerdiv, bigimg, _this){
    var src = _this.attr("src");//获取当前点击的pimg元素中的src属性
    $(bigimg).attr("src", src);//设置#bigimg元素的src属性

    /*获取当前点击图片的真实大小，并显示弹出层及大图*/
    $("<img/>").attr("src", src).load(function(){
        var windowW = $(window).width();//获取当前窗口宽度
        var windowH = $(window).height();//获取当前窗口高度
        var realWidth = this.width;//获取图片真实宽度
        var realHeight = this.height;//获取图片真实高度
        var imgWidth, imgHeight;
        var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放

        if(realHeight>windowH*scale) {//判断图片高度
            imgHeight = windowH*scale;//如大于窗口高度，图片高度进行缩放
            imgWidth = imgHeight/realHeight*realWidth;//等比例缩放宽度
            if(imgWidth>windowW*scale) {//如宽度扔大于窗口宽度
                imgWidth = windowW*scale;//再对宽度进行缩放
            }
        } else if(realWidth>windowW*scale) {//如图片高度合适，判断图片宽度
            imgWidth = windowW*scale;//如大于窗口宽度，图片宽度进行缩放
            imgHeight = imgWidth/realWidth*realHeight;//等比例缩放高度
        } else {//如果图片真实高度和宽度都符合要求，高宽不变
            imgWidth = realWidth;
            imgHeight = realHeight;
        }
        $(bigimg).css("width",imgWidth);//以最终的宽度对图片缩放

        var w = (windowW-imgWidth)/2;//计算图片与窗口左边距
        var h = (windowH-imgHeight)/2;//计算图片与窗口上边距
        $(innerdiv).css({"top":h, "left":w});//设置#innerdiv的top和left属性
        $(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg
    });

    $(outerdiv).click(function(){//再次点击淡出消失弹出层
        $(this).fadeOut("fast");
    });
}

//路段能见度趋势
var dom2 = document.getElementById("routVisibility");
var myChart2 = echarts.init(dom2);
/*option2 = {
    title: {
        top:'top',
        textStyle:{
            fontSize: 13,
            color:'#fff'
        }
    },
    tooltip: {//鼠标指上时的标线
        trigger: 'axis',
        axisPointer: {
            lineStyle: {
                color: '#fff'
            }
        }
    },
    grid:{
        x:35,
        y:25,
        x2:40,
        y2:25,
        borderWidth:1
    },
    xAxis: {
        type: 'category',
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
        },
        data: ['K100', 'K200', 'K300', 'K500', 'K600', 'K700', 'K800'],
    },
    yAxis: {
        type: 'value',
        max: 400,
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
        },
        splitLine: {
            lineStyle: {
                color: 'rgba(255,255,255,.2)',
                type:'dotted',
            }
        },
    },
    visualMap: {
        top: 'top',
        left:'middle',
        color:['#16DDFD'],
        textStyle:{
            color:'#fff'
        },
        pieces: [{
            gt: 0,
            lte: 300,
            color: 'rgba(0,136,212,0.8)' //#0088D4
        }]
    },
    series: [{
        data: [530, 540, 560, 550, 600, 900, 1000],
        type: 'line',
        name:'可见距离曲线',
        smooth: true,
        lineStyle: {
            normal: {
                width: 2
            }
        },
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(0, 136, 212, 0.3)'
                }, {
                    offset: 0.8,
                    color: 'rgba(0, 136, 212, 0)'
                }], false),
                shadowColor: 'rgba(0, 0, 0, 0.1)',
                shadowBlur: 10
            }
        },
        itemStyle: {
            normal: {
                color: 'rgb(0,136,212)'
            }
        },
    }]
};*/
option2 = {
    title: {
        top:'top',
        textStyle:{
            fontSize: 13,
            color:'#fff'
        }
    },
    tooltip: {//鼠标指上时的标线
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#283b56'
            }
        }
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
            lte: 30,
            color: '#ff0000'
        }, {
            gt: 30,
            lte: 50,
            color: '#fd4a05'
        }, {
            gt: 50,
            lte: 100,
            color: '#eff321'
        }, {
            gt: 100,
            lte: 200,
            color: '#007eff'
        }, {
            gt: 200,
            color: '#48ef8b'
        }]
    },
    grid:{
        x:35,
        y:25,
        x2:40,
        y2:25,
    },
    xAxis: {
        type: 'category',
        //name: 'HH:ss',
        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
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
            margin: 10,
            textStyle: {
                fontSize: 14
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
        lineStyle: {
            normal: {
                width: 2
            }
        },
        markLine: {
            silent: true,
            symbol:'none',
            // data: [{
            //     yAxis: 30
            // }, {
            //     yAxis: 50
            // }],
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
        },
        data: [97.3,99.2,99.3,100.0,99.6,90.6,80.0,91.5,69.8,67.5,90.4,84.9]
    }]
};
myChart2.setOption(option2, true);
myChart2.on('click',function(params){
    console.log(params);
    let epId = 'undefind';
    let name = 'undefind';
    let equLocation = 'undefind';
    if(getDistanceStatistic_data){
        let epIds = getDistanceStatistic_data[getDistanceStatistic_index].series.epIds.split(',');
        let equLocations = getDistanceStatistic_data[getDistanceStatistic_index].series.equLocations.split(',');
        epId = epIds[params.dataIndex];
        name = params.name;
        equLocation = equLocations[params.dataIndex];
    }
    realImgViewFunc(epId,name,equLocation);//epId,name,equLocation
});

var getDistanceStatistic_data;//路段统计
var getDistanceStatistic_index = 0;//路段统计下标

/***
 * 统计图
 */
function statisticAjax(){
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
                    $('#routVisibility-dropdown-menu').append('<li><a href="##" onclick="switchFunc('+index+')" style="width:100%;text-align: left;">'+curr.hwName+'</a></li>');
                });
            }
        }
    });

}

/**
 * 切换路段可见度统计
 * @param index
 */
function switchFunc(index){
    getDistanceStatistic_index = index;
    option2.title.text = ""+getDistanceStatistic_data[index].hwName+"路段";
    option2.xAxis.data = getDistanceStatistic_data[index].xData.split(',');
    option2.series[0].data = getDistanceStatistic_data[index].series.data.split(',');
    myChart2.setOption(option2, true);
}


// 7-9 统计图 end
/**************************************左边 end **************************************/
/**************************************中间地图**************************************/
//自定义图层
setTimeout(function(){
    mapInit();
},2000);

function mapInit(){
    var map = new BMap.Map("allmap",{minZoom:5,maxZoom:14});    // 创建Map实例
    var point_ = new BMap.Point(119.117709,33.136569);
    map.centerAndZoom(point_, 7);  // 初始化地图,设置中心点坐标和地图级别
    map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
    //右键菜单
    var menu = new BMap.ContextMenu();
    var txtMenuItem = [
        {
            text:'清除标记',
            callback:function(){
                //清空
                markerAttr.forEach(function(curr,i,array){
                    map.removeOverlay(curr);
                });
            }
        },
    ];
    for(var i=0; i < txtMenuItem.length; i++){
        menu.addItem(new BMap.MenuItem(txtMenuItem[i].text,txtMenuItem[i].callback,100));
    }
    map.addContextMenu(menu);
    // map.setDefaultCursor("url('bird.cur')");   //鼠标指针样式
    // map.disableDragging();     //禁止拖拽
    //统计查询
    //analysisControl(map,point_);
    //定位
    locationFun(map,point_);
    drawBoundary(map);
    //摄像头初始化
    setTimeout(function(map){
        mapEquipmentsInit(map);
    },500,map);
    //大风测试初始化
    windTestInit(map);
    setInterval(function(map){
        windTestInit(map);
    },2*60*1000,map);
}

/**
 * 统计查询
 * @param map
 */
function analysisControl(map,point_){
    function ZoomControl(){
        this.defaultAnchor = BMAP_ANCHOR_BOTTOM_LEFT;
        this.defaultOffset = new BMap.Size(10, 10);
    }
    ZoomControl.prototype = new BMap.Control();
    ZoomControl.prototype.initialize = function(map){
        var div = document.createElement("img");
        // 设置样式
        div.style.cursor = "pointer";
        div.style.width = '20px';
        div.style.height = '20px';
        div.src = '/plugins/offLineMap/bmap-offline/images/expend.png';
        div.title = "数据统计";
        // 绑定事件
        div.onclick = function(e){
            //map.setZoom(map.getZoom() + 2);
            map.centerAndZoom(point_, 7);  // 初始化地图,设置中心点坐标和地图级别
        };
        map.getContainer().appendChild(div);
        return div;
    };
    // 创建控件
    var myZoomCtrl = new ZoomControl();
    // 添加到地图当中
    map.addControl(myZoomCtrl);
}

/**
 * 定位控件
 * @param map
 */
function locationFun(map,point_){
    function ZoomControl(){
        this.defaultAnchor = BMAP_ANCHOR_BOTTOM_RIGHT;
        this.defaultOffset = new BMap.Size(10, 10);
    }
    ZoomControl.prototype = new BMap.Control();
    ZoomControl.prototype.initialize = function(map){
        var div = document.createElement("img");
        // 设置样式
        div.style.cursor = "pointer";
        div.style.width = '20px';
        div.style.height = '20px';
        div.src = '/plugins/offLineMap/bmap-offline/images/default-40x40.png';
        div.title = "还原";
        // 绑定事件
        div.onclick = function(e){
            //map.setZoom(map.getZoom() + 2);
            map.centerAndZoom(point_, 7);  // 初始化地图,设置中心点坐标和地图级别
        };
        map.getContainer().appendChild(div);
        return div;
    };
    // 创建控件
    var myZoomCtrl = new ZoomControl();
    // 添加到地图当中
    map.addControl(myZoomCtrl);
}

/**
 * 遮蔽层
 * @param map
 */
function drawBoundary(map) {
    //js
    // var ply = new BMap.Polygon(jSuBoundaries, { strokeWeight: 1, strokeColor: "#065A96", fillOpacity: 0.00001,
    //     fillColor: "#ffffff" ,enableClicking:false}); //建立多边形覆盖物
    // map.addOverlay(ply);

    // tw
    // var twPly = new BMap.Polygon(twBoundaries, { strokeWeight: 5, strokeColor: "#061537", fillOpacity: 1,
    //     fillColor: "#061537" ,enableClicking:false}); //建立多边形覆盖物
    //map.addOverlay(twPly);

    //ww
    // var wwBoundar = "106.303884,38.336979;135.738562,39.171283;141.147367,14.334409;110.571059,17.007925;106.303884,38.336979;";
    // var wwPly = new BMap.Polygon(wwBoundar, { strokeWeight: 5, strokeColor: "#061537", fillOpacity: 1,
    //     fillColor: "#061537" ,enableClicking:false}); //建立多边形覆盖物
    // var pArray = [];
    // pArray = pArray.concat(ply.getPath());
    // pArray = pArray.concat(wwPly.getPath());
    // //添加遮蔽层
    // var plyAll = new BMap.Polygon(pArray, { strokeOpacity: 0, strokeColor: "#FFFFFF", strokeWeight: 0.0000001,
    //     fillColor: "#061537", fillOpacity: 1,enableClicking:false}); //建立多边形覆盖物
    // map.addOverlay(plyAll);

    //zgdl
    // var chPly = new BMap.Polygon(chLandBoundaries, { strokeWeight: 5, strokeColor: "#061537", fillOpacity: 1,
    //     fillColor: "#061537" ,enableClicking:false}); //建立多边形覆盖物
    // var pArray = [];
    // pArray = pArray.concat(ply.getPath());
    // pArray = pArray.concat(chPly.getPath());
    // //添加遮蔽层
    // var plyAll = new BMap.Polygon(pArray, { strokeOpacity: 0, strokeColor: "#FFFFFF", strokeWeight: 0.0000001,
    //     fillColor: "#061537", fillOpacity: 1,enableClicking:false}); //建立多边形覆盖物
    //map.addOverlay(plyAll);
}

/**
 * 大风测试标记
 * @param map
 */
let windMarkers = [];
let pointCollection;//海量点
function windTestInit(map){
    $.ajax({url:'/fog/fogTrack/windTest',type:'post',data:{hwId:'1'},dataType:'json',
        success:function(data){
            if (document.createElement('canvas').getContext) {  // 判断当前浏览器是否支持绘制海量点
                for(let i = 0; i < windMarkers.length; i++){
                    map.removeOverlay(windMarkers.shift());
                }
                let html = '';
                for(let i = 0; i < data.length; i++){
                    //告警
                	data[i].epId=data[i].epId.replace('+','_');
                    if(data[i].windLevel != -1){
                        html += "<ul>";
                        let param__= data[i].lng+","+data[i].lat;
                        // html += "<li style='word-wrap:break-word;' ><a href='#' style='color: #95C1EE;' " +
                        //     "title='" + data[i].epId + "' onclick='"+"locationFunc("+param__+")"+"'>" + data[i].epId.substr(0,5) + "</a></li>";
                        html += "<li><a href='#' style='color: #95C1EE;' title='" +data[i].equLocation+ "' onclick='"+"locationFunc("+param__+")"+"'>" + data[i].equLocation.substr(0,5) + "</a></li>";
                        html += "<li>" + data[i].winds + "</li>";
                        html += "<li>"+data[i].windd+"</li>";
                        html += "<li>"+data[i].time?data[i].time.substring(10):'-'+"</li>";
                        html += "</ul>";
                    }
                    let icon = null;
                    switch(data[i].windLevel){
                        case -1: icon = new BMap.Icon('/fog/windImg/wind-blue.png','16px',{imageSize:{width:16,height:16}});break;
                        case 3:
                            icon = new BMap.Icon('/fog/windImg/wind-yellow.png','16px',{imageSize:{width:16,height:16}});
                            break;
                        case 2:
                            icon = new BMap.Icon('/fog/windImg/wind-yellow.png','16px',{imageSize:{width:16,height:16}});
                            break;
                        case 1:
                            icon = new BMap.Icon('/fog/windImg/wind-red.png','16px',{imageSize:{width:16,height:16}});
                            break;
                        case 0:
                            icon = new BMap.Icon('/fog/windImg/wind-red.png','16px',{imageSize:{width:16,height:16}});
                            break;
                    }
                    windTest(map,new BMap.Point(data[i].lng,data[i].lat),icon,data[i].epId,data[i].epId,data[i].equLocation);
                }
                //大风预警
                $("#contentUl_wind").html(html);
                // windTest(map,new BMap.Point(120.997492655872,31.8217689193746),new BMap.Icon('/fog/windImg/wind-red.png','16px',{imageSize:{width:16,height:16}})
                //     ,"K1197+800苏州","K1197+800苏州","苏通大桥");
                // windTest(map,new BMap.Point(120.999581156785,31.7756101485953),new BMap.Icon('/fog/windImg/wind-blue.png','16px',{imageSize:{width:16,height:16}})
                //     ,"K1202+866苏州","K1202+866苏州","崇启大桥");

                //如下代码解决marker的点击事件无效问题
                //移除海量点
                if(pointCollection){
                    map.removeOverlay(pointCollection);
                }
                let points = [];  //添加海量点数据
                let point_;
                for (let i = 0; i < windMarkers.length; i++) {
                    // var marker = new BMap.Marker(new BMap.Point(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]));  // 创建标注
                    point_ = new BMap.Point(windMarkers[i].point.lng, windMarkers[i].point.lat);
                    point_.epId = windMarkers[i].epId;
                    point_.name = windMarkers[i].name;
                    point_.equLocation = windMarkers[i].equLocation;
                    points.push(point_);
                }
                let options = {
                    size: BMAP_POINT_SIZE_BIG,//BMAP_POINT_SIZE_NORMAL BMAP_POINT_SIZE_BIG BMAP_POINT_SIZE_BIGGER BMAP_POINT_SIZE_HUGE
                    shape: BMAP_POINT_SHAPE_CIRCLE,//BMAP_POINT_SHAPE_STAR BMAP_POINT_SHAPE_SQUARE BMAP_POINT_SHAPE_RHOMBUS BMAP_POINT_SHAPE_WATERDROP
                    color: 'rgba(43,185,73,0)',//'rgba(43,33,132,1)'
                };
                pointCollection = new BMap.PointCollection(points, options);  // 初始化PointCollection
                pointCollection.addEventListener('click', function (e) {
                    realWindTestFunc(e.point.epId,e.point.name,e.point.equLocation);
                });
                map.addOverlay(pointCollection);  // 添加Overlay
                //最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
                // var markerClusterer = new BMapLib.MarkerClusterer(map, {markers:windMarkers});
            } else {
                alert('请在chrome、safari、IE8+以上浏览器查看');
            }
        }
    });
}

/**
 * 摄像头实时图片获取
 * */
function realWindTestFunc(epId,name,equLocation){
    layer.open({
        type : 2,
        title : "ID："+epId+" >> 位置："+equLocation,
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
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
function windTest(map,point,icon,epId,name,equLocation){
    let marker1 = new BMap.Marker(point,{
        icon:icon,
        offset:{width:-8,height:-8},
        title:equLocation,
        enableClicking:true,
    });
    marker1.setZIndex(0);
    marker1.epId = epId;
    marker1.name = name;
    marker1.equLocation = equLocation;
    map.addOverlay(marker1);
    //点击事件无效
    // marker1.addEventListener('click',function(e){
    //     realImgViewFunc(this.epId,this.name,this.equLocation);
    // });
    windMarkers.push(marker1);


}

/**
 *  地图摄像头初始化
 * */
function mapEquipmentsInit(map){
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
                    arrayData.push({lnglat: [currData.lng,currData.lat], epId:currData.epId, name: currData.equ_name,
                        equLocation:currData.equ_location,id: index+'-'+i_});
                });
                //摄像头圆圈标记
                mapOverlayPoints(map,arrayData);
                //摄像头标记
                //mapClusterMarker(map,arrayData);
                /**监听点击事件*/
                //addCamEventClick(map,arrayData);
                //告警信息刷新
                showInfo(map);
                setInterval(showInfo,30000,map);
            },this);
        }
    });
}

/**大量点聚合标记*/
function mapClusterMarker(map,arrayData){
    var markers = [];
    var lnglatT;
    var point;
    var marker;
    var icon;
    for (var i = 0; i < arrayData.length; i++) {
        lnglatT = coordtransform.gcj02tobd09(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]);
        point = new BMap.Point(lnglatT[0], lnglatT[1]);
        icon = new BMap.Icon('/fog/cameral.png','16px',{imageSize:{width:16,height:16}});
        marker = new BMap.Marker(point,{
            icon:icon,
            offset:{width:-8,height:-8},
            title:'测试',
            enableClicking:true,
        });
        // marker.setIcon(icon);
        marker.setZIndex(10);
        marker.epId = arrayData[i].epId;
        marker.name = arrayData[i].name;
        marker.equLocation = arrayData[i].equLocation;
        marker.addEventListener('click',function(e){
            realImgViewFunc(this.epId,this.name,this.equLocation);
        });
        markers.push(marker);
    }
    //最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    var markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});
}

/**摄像头添加点击事件*/
function addCamEventClick(map,arrayData){
    if (document.createElement('canvas').getContext) {  // 判断当前浏览器是否支持绘制海量点
        var points = [];  // 添加海量点数据
        var lnglatT;
        var point;
        for (var i = 0; i < arrayData.length; i++) {
            // var marker = new BMap.Marker(new BMap.Point(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]));  // 创建标注
            lnglatT = coordtransform.gcj02tobd09(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]);
            point = new BMap.Point(lnglatT[0], lnglatT[1]);
            point.epId = arrayData[i].epId;
            point.name = arrayData[i].name;
            point.equLocation = arrayData[i].equLocation;
            points.push(point);
        }

        var options = {
            size: BMAP_POINT_SIZE_NORMAL,//BMAP_POINT_SIZE_NORMAL BMAP_POINT_SIZE_BIG BMAP_POINT_SIZE_BIGGER BMAP_POINT_SIZE_HUGE
            shape: BMAP_POINT_SHAPE_CIRCLE,//BMAP_POINT_SHAPE_STAR BMAP_POINT_SHAPE_SQUARE BMAP_POINT_SHAPE_RHOMBUS BMAP_POINT_SHAPE_WATERDROP
            color: 'rgb(19,181,177,0.5)'
        };

        var pointCollection = new BMap.PointCollection(points, options);  // 初始化PointCollection
        pointCollection.addEventListener('click', function (e) {
            realImgViewFunc(e.point.epId,e.point.name,e.point.equLocation);
        });

        map.addOverlay(pointCollection);  // 添加Overlay
    } else {
        alert('请在chrome、safari、IE8+以上浏览器查看本示例');
    }
}

/**大量点覆盖物标记*/
function mapOverlayPoints(map,arrayData){
    if (document.createElement('canvas').getContext) {  // 判断当前浏览器是否支持绘制海量点
        var points = [];  // 添加海量点数据
        var lnglatT;
        var point;
        for (var i = 0; i < arrayData.length; i++) {
            // var marker = new BMap.Marker(new BMap.Point(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]));  // 创建标注
            lnglatT = coordtransform.gcj02tobd09(arrayData[i]['lnglat'][0], arrayData[i]['lnglat'][1]);
            point = new BMap.Point(lnglatT[0], lnglatT[1]);
            point.epId = arrayData[i].epId;
            point.name = arrayData[i].name;
            point.equLocation = arrayData[i].equLocation;
            points.push(point);
        }

        var options = {
            size: BMAP_POINT_SIZE_NORMAL,//BMAP_POINT_SIZE_NORMAL BMAP_POINT_SIZE_BIG BMAP_POINT_SIZE_BIGGER BMAP_POINT_SIZE_HUGE
            shape: BMAP_POINT_SHAPE_CIRCLE,//BMAP_POINT_SHAPE_STAR BMAP_POINT_SHAPE_SQUARE BMAP_POINT_SHAPE_RHOMBUS BMAP_POINT_SHAPE_WATERDROP
            color: 'rgba(43,185,73,1)',//'rgba(43,33,132,1)'
        };

        var pointCollection = new BMap.PointCollection(points, options);  // 初始化PointCollection
        pointCollection.addEventListener('click', function (e) {
            realImgViewFunc(e.point.epId,e.point.name,e.point.equLocation);
        });

        map.addOverlay(pointCollection);  // 添加Overlay
    } else {
        alert('请在chrome、safari、IE8+以上浏览器查看');
    }
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
        area : [ '60%', '90%' ],
        zIndex:99999999,
        content : '/system/equipment/home_lookup/' + epId
    });
}

/**
 * 设备实时在线情况
 */
function equipRealCon(){
    layer.open({
        type : 2,
        title : "设备实时在线情况",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '50%', '70%' ],
        zIndex:99999999,
        content : '/system/equipment/equipRealCon'
    });
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

function showmp4(){
	layer.open({
        type : 2,
        title : "视频",
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '730px', '470px' ],
        zIndex:99999999,
        content : '/system/equipment/mainVideo'
    });
}



/**
 * 雾霾告警开关
 */
var fogOpen=true;
var alarmInfo = [];
var showInfoMap;
//var fogMarkers=[];
/**
 * 告警信息渲染
 * */
function showInfo(map) {
    showInfoMap = map;
    $.ajax({
        type: "GET",
        url: "/fog/fogTrack/selectRealAlarmInfoList",
        dataType:'json',
        success: function (data) {
            //告警信息初始化清空
            alarmImgCloseAll(map);
            alarmInfo = [];
            var html = "";
            for (var i = 0; i < data.length; i++) {
                //imgRealFromUrl = imgRealFromUrl_src+"path=" + data[i].imgpath.replace(/\\/g,"/")+"&epid=" + data[i].epId;
                //渲染告警闪烁
                var tempContent;
                var zIndex = 100;
                if(data[i].distance <= 30){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    // tempContent = '<img src="/img/point_red.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    tempContent = "/img/wumai_red.gif";
                    zIndex = 200;
                } else if(data[i].distance <= 50){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    // tempContent = '<img src="/img/point_light_red.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    tempContent = "/img/point_light_red.gif";
                    zIndex = 199;
                } else if(data[i].distance <= 200){
                    alarmPlay("/fog/windImg/alarm-5s.mp3");
                    // tempContent = '<img src="/img/point_yellow.gif" style="width:20px;height:20px;cursor: pointer;" />';
                    tempContent = "/img/wumai_yellow.gif";
                    zIndex = 198;
                }

                if(fogOpen){
                    //闪烁点渲染
                    var alarmMarker=addCustomMarker(map,tempContent,new BMap.Point(parseFloat(data[i].lon), parseFloat(data[i].lat)),data[i]
                        .epId,data[i].equName,zIndex,data[i].address);
                    alarmInfo.push({marker:alarmMarker,name:"alarmInfo_"+i});
                    //fogMarkers.push(alarmMarker);
                }

                html += "<ul>";
                var param__= data[i].lon+","+data[i].lat;
                html += "<li style='word-wrap:break-word;' ><a href='#' style='color: #95C1EE;' " +
                    "title='" +data[i].epId+"："+data[i].equName + "' onclick='"+"locationFunc("+param__+")"+"'>" +
                    (data[i].epId+"："+ data[i].equName).substr(0,5)+"..."+ "</a></li>";
                html += "<li>" + data[i].distance + "(m)</li>";
                var begintime = data[i].begintime.substring(10);
                html += "<li title='" + data[i].begintime + "'>" + (begintime==''?'&nbsp;':begintime) + "</li>";
                html += "<li><a href='#' style='color: #95C1EE;' " +
                    "title='" +data[i].forTime+ "' onclick='"+"locationFunc("+param__+")"+"'>" +
                    (data[i].forTime).substr(0,5)+"..."+ "</a></li>";
                html += "</ul>";
            }
            $("#contentUl").html(html);
            //地图右键菜单
            //rightAddMenu();
            //路线三色图
            $.ajax({url: '/fog/fogTrack/getAlarmTrack',
                type: 'post',
                data: {epJson: JSON.stringify(data)},
                dataType: 'json',
                success: function (data) {
                    if(data && data.length > 0){
                        data.forEach(function(curr,index,arr){
                            if(curr.color && curr.track){
                                polylineFunc(map,curr.track,curr.color);
                            }
                        });
                    }
                }
            });
        }
    });
}

/**
 * 播放告警声音
 * */
function alarmPlay(url){
    try{
        let audio = document.createElement('audio');
        let source = document.createElement('source');
        source.type = "audio/mpeg";
        source.type = "audio/mpeg";
        source.src = url;
        source.autoplay = "autoplay";
        source.controls = "controls";
        audio.appendChild(source);
        audio.play();
    } catch (e) {
        console.log(e);
        layer.msg("EXCEPTION·音频播放失败");
    }
}

/**
 * 清空所有告警闪烁点
 */
function alarmImgCloseAll(map){
    alarmInfo.forEach(function(curr,i,array){
        map.removeOverlay(curr.marker);
    });
}

/**
 * 根据坐标列表划线
 * */
function polylineFunc(map,equipmentLngLat,colorDesc){
    //给定路径划线
    var polygon = new BMap.Polygon(JSON.parse(equipmentLngLat),{
        strokeColor: colorDesc,   // 线颜色
        strokeOpacity: 1,         // 线透明度
        strokeWeight: 5,          // 线宽
        enableClicking:false,
    });
    map.addOverlay(polygon);   //划线
}

/**定义闪烁点divMarker*/
function addCustomMarker(map,content,position,epId,name,zIndex,equLocation){
    lnglatT = coordtransform.gcj02tobd09(position.lng, position.lat);
    point = new BMap.Point(lnglatT[0], lnglatT[1]);
    icon = new BMap.Icon(content,'16px',{imageSize:{width:16,height:16}});
    marker = new BMap.Marker(point,{
        icon:icon,
        offset:{width:-8,height:-8},
    });
    marker.setZIndex(zIndex);
    marker.epId = epId;
    marker.name = name;
    marker.equLocation = equLocation;
    marker.addEventListener('click',function(e){
        realImgViewFunc(this.epId,this.name,this.equLocation);
    });
    map.addOverlay(marker);
    return marker;

    // var marker1 = new BMap.Point({
    //     map:map,
    //     offset:new AMap.Pixel(-10,-10), //相对于基点的偏移位置
    //     draggable:false,  //是否可拖动
    //     content:content,   //自定义覆盖物内容,
    //     position:position, //基点位置
    //     zIndex:zIndex,//显示级别
    // });
    // marker1.on( 'click', function(){
    //     realImgViewFunc(epId,name,equLocation);
    // });
    // return marker1;
}

var markerAttr = [];
/**
 * 警告列表摄像头定位
 * */
function locationFunc(lon,lat){
    //清空
    markerAttr.forEach(function(curr,i,array){
        showInfoMap.removeOverlay(curr);
    });
    lnglatT = coordtransform.gcj02tobd09(lon, lat);
    point = new BMap.Point(lnglatT[0], lnglatT[1]);
    var marker = new BMap.Marker(point,{
        //offset:{width:-8,height:-8},
    });
    marker.setAnimation(BMAP_ANIMATION_BOUNCE);
    // marker.epId = epId;
    // marker.name = name;
    // marker.equLocation = equLocation;
    marker.addEventListener('click',function(e){
        // realImgViewFunc(this.epId,this.name,this.equLocation);
        showInfoMap.centerAndZoom(this.point, 14);  // 初始化地图,设置中心点坐标和地图级别
    });
    showInfoMap.addOverlay(marker);
    markerAttr.push(marker);
    showInfoMap.centerAndZoom(point, 14);  // 初始化地图,设置中心点坐标和地图级别


}

// customOverlay.prototype.addEventListener = function (event, fun) {
//     this.div['on' + event] = fun;
// }

/**************************************中间地图 end **************************************/
/**************************************右边 **************************************/
//收费站收费量
// var myChart9 = echarts.init(document.getElementById('main9'));
// myChart9.setOption(option9);
//本月发生事件
var myChart3 = echarts.init(document.getElementById('main3'));
myChart3.setOption(option3);
// var myChart31 = echarts.init(document.getElementById('main31'));
// myChart31.setOption(option31);
// var mySwiper1 = new Swiper('.visual_swiper1', {
//     autoplay: true,//可选选项，自动滑动
//     speed: 800,//可选选项，滑动速度
//     autoplay: {
//         delay: 5000,//1秒切换一次
//     },
// });
let swiper = new Swiper('.swiper-container', {
    pagination: {
        el: '.swiper-pagination',
        type: 'fraction',
    },
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
});



//服务区剩余车位排行
// var myChart4 = echarts.init(document.getElementById('main4'));
// myChart4.setOption(option4);
// var myChart41 = echarts.init(document.getElementById('main41'));
// myChart41.setOption(option41);
var mySwiper2 = new Swiper('.visual_swiper2', {
    autoplay: true,//可选选项，自动滑动
    direction: 'vertical',//可选选项，滑动方向
    speed: 2000,//可选选项，滑动速度
});
/**************************************右边 end **************************************/

function nofind(_this){
    _this.src="/fog/nofind.png";
}