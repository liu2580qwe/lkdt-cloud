/**摄像头边长*/
var camL = 20;
/**摄像头增加边长*/
var camAddL = 3;
/**地图放大缩小幅度*/
var sizeVat = 250;
/**摄像头闪动频率*/
var camVat = 500;
/**颜色*/
var $color = '#FFFF00';
/**定时*/
var $timeD_;
/**可见区域宽度*/
var width_=document.documentElement.clientWidth*0.98;
/**宽度*/
var widthTemp = width_;
/**放大次数*/
var addCount = 0;
var cameras = {
    1:{'x':2199,'y':131},2:{'x':2190,'y':187},3:{'x':2190,'y':233},4:{'x':2190,'y':293},5:{'x':2190,'y':349},
    6:{'x':2190,'y':401},7:{'x':2190,'y':447},8:{'x':2190,'y':497},9:{'x':2190,'y':547},10:{'x':2221,'y':611},
    11:{'x':2247,'y':657},12:{'x':2273,'y':699},13:{'x':2295,'y':739},14:{'x':2321,'y':783},15:{'x':2347,'y':829},
    16:{'x':2379,'y':879},17:{'x':2405,'y':929},18:{'x':2433,'y':977},19:{'x':2463,'y':1027},20:{'x':2497,'y':1081},
    21:{'x':2525,'y':1131},22:{'x':2553,'y':1177},23:{'x':2581,'y':1227},24:{'x':2613,'y':1277},25:{'x':2653,'y':1347},
    26:{'x':2689,'y':1407},27:{'x':2717,'y':1455},28:{'x':2749,'y':1511},29:{'x':2789,'y':1577},30:{'x':2821,'y':1629},
    31:{'x':2853,'y':1683},32:{'x':2881,'y':1733},33:{'x':2911,'y':1789},34:{'x':2951,'y':1847},35:{'x':2987,'y':1913},
    36:{'x':3009,'y':1969},37:{'x':2969,'y':2029},38:{'x':2931,'y':2085},39:{'x':2891,'y':2147},40:{'x':2853,'y':2203},
    41:{'x':2855,'y':2271},42:{'x':2855,'y':2351},43:{'x':2855,'y':2421},44:{'x':2861,'y':2505},45:{'x':2907,'y':2563},
    46:{'x':2963,'y':2631},47:{'x':3015,'y':2693},48:{'x':3061,'y':2749},49:{'x':3105,'y':2799},50:{'x':3147,'y':2853}
};

var camerasFixed = {
    1:{'x':0,'y':0},2:{'x':0,'y':0},3:{'x':0,'y':0},4:{'x':0,'y':0},5:{'x':0,'y':0},
    6:{'x':0,'y':0},7:{'x':0,'y':0},8:{'x':0,'y':0},9:{'x':0,'y':0},10:{'x':0,'y':0},
    11:{'x':0,'y':0},12:{'x':0,'y':0},13:{'x':0,'y':0},14:{'x':0,'y':0},15:{'x':0,'y':0},
    16:{'x':0,'y':0},17:{'x':0,'y':0},18:{'x':0,'y':0},19:{'x':0,'y':0},20:{'x':0,'y':0},
    21:{'x':0,'y':0},22:{'x':0,'y':0},23:{'x':0,'y':0},24:{'x':0,'y':0},25:{'x':0,'y':0},
    26:{'x':0,'y':0},27:{'x':0,'y':0},28:{'x':0,'y':0},29:{'x':0,'y':0},30:{'x':0,'y':0},
    31:{'x':0,'y':0},32:{'x':0,'y':0},33:{'x':0,'y':0},34:{'x':0,'y':0},35:{'x':0,'y':0},
    36:{'x':0,'y':0},37:{'x':0,'y':0},38:{'x':0,'y':0},39:{'x':0,'y':0},40:{'x':0,'y':0},
    41:{'x':0,'y':0},42:{'x':0,'y':0},43:{'x':0,'y':0},44:{'x':0,'y':0},45:{'x':0,'y':0},
    46:{'x':0,'y':0},47:{'x':0,'y':0},48:{'x':0,'y':0},49:{'x':0,'y':0},50:{'x':0,'y':0}
};

// var h=document.documentElement.clientHeight;

/**
 * 绘图
 * @param w 画布宽度
 * @param camAddL 摄像头额外添加的边长
 */
var makeMap = function(wid,camAddL){
    if(!camAddL){
        camAddL = 0;
    }
    //设置画布宽度and高度
    document.getElementById('myCanvas').style.width = wid+"px";
    var $obj_ = new Object();
    //背景图片
    $obj_.myImage = new Image();
    $obj_.myImage.src = "/fog/eastHighWay.jpg";
    //摄像头图片
    $obj_.cameral = new Image();
    $obj_.cameral.src = "/fog/cameral.png";
    //画布
    $obj_.myCanvas = document.getElementById('myCanvas');
    //画笔
    $obj_.penCanvas = $obj_.myCanvas.getContext('2d');
    //摄像头画笔
    //$obj_.cameralCanvas = $obj_.myCanvas.getContext('2d');

    /**绘制*/
    $obj_.myImage.onload = function(){
        deMyCanvas($obj_);
        makeBackgroundCameral($obj_,camAddL);
        addBtn($obj_);
    };

    /**绘制*/
    $obj_.cameral.onload = function(){
        deMyCanvas($obj_);
        makeBackgroundCameral($obj_,camAddL);
        addBtn($obj_);
    };

    //每次画图时清空
    clearTimeout($timeD_);
    timeModule($obj_);

};

/**
 * 增加按钮
 * @param $obj_
 */
var addBtn = function($obj_){
    //移除历史元素
    for(var k in cameras){
        var btn=document.getElementById('_$btnId_'+k);
        if(btn){
            document.body.removeChild(btn);
        }
    }
    //增加新元素
    for(var k in cameras){
        var location = new GetLocationByCanvas($obj_.myCanvas,cameras[k]);
        var btn_a = document.createElement("a");
        btn_a.id = '_$btnId_'+k;
        btn_a.style.position = "absolute";
        btn_a.style.left = (location.x + addCount*camAddL + camAddL*5 + document.documentElement.scrollLeft) + 'px';
        btn_a.style.top = (location.y + document.documentElement.scrollTop) + 'px';
        btn_a.style.color = 'blue';
        btn_a.style.cursor = 'pointer';
        btn_a.style.fontSize = (10 + addCount*camAddL) + 'px';
        btn_a.innerHTML = k;
        btn_a.title = '摄像头';
        document.body.appendChild(btn_a);
        document.getElementById(btn_a.id).addEventListener('click',function(event){
            alert(event.currentTarget.id);
        })
    }
};

/**定时任务*/
var timeModule_flag = 1;
/**定时任务->光点闪烁*/
var timeModule= function(el){
    if(timeModule_flag == 0){
        for(var k in cameras){
            el.penCanvas.beginPath();
            el.penCanvas.fillStyle=$color;
            el.penCanvas.fillRect(cameras[k].x,cameras[k].y,camL+addCount*camAddL,camL+addCount*camAddL);
            el.penCanvas.closePath();
            el.penCanvas.stroke();
        }
        timeModule_flag = 1;
    } else {
        for(var k in cameras){
            el.penCanvas.drawImage(el.cameral,cameras[k].x,cameras[k].y,camL+addCount*camAddL,camL+addCount*camAddL);
        }
        timeModule_flag = 0;
    }
    $timeD_ = setTimeout(timeModule,camVat,el);
};

/**定义画布大小*/
var deMyCanvas = function(el){
    el.myCanvas.width = el.myImage.width;
    el.myCanvas.height = el.myImage.height;

};

/**背景图，摄像头*/
var makeBackgroundCameral = function(el,camAddL){
    el.penCanvas.drawImage(el.myImage,0,0,el.myImage.width,el.myImage.height);
    for(var k in cameras){
        el.penCanvas.drawImage(el.cameral,cameras[k].x,cameras[k].y,camL+camAddL,camL+camAddL);
    }
};



//初始绘制地图
var makeMapLoad = function(){
    addCount = 0;
    makeMap(width_);
};

//放大地图
var add = function(){
    if(addCount <= 7){
        addCount++;
    }
    widthTemp += sizeVat;
    makeMap(widthTemp,camAddL*addCount);
};

//缩小地图
var sub = function(){
    if(addCount > 0){
        addCount--;
    }
    widthTemp -= sizeVat;
    if(widthTemp <= width_){
        widthTemp = width_;
        makeMap(width_);
        return;
    }
    makeMap(widthTemp,camAddL*addCount);
};

var _x = -1;
var _y = -1;
$("#myCanvas").bind({
    mouseover:function(e){
        document.getElementById("myCanvas").style.cursor = 'move';
    },
    mousedown:function(e){
        _x = e.clientX;
        _y = e.clientY;
    },
    mouseup:function(e,t){
        _x = -1;
        _y = -1;
    },
    mousemove:function(e,t){
        if(_x == -1 || _y == -1){
            return;
        }
        document.documentElement.scrollTop -= e.clientY - _y;
        document.documentElement.scrollLeft -= e.clientX - _x;
        _x = e.clientX;
        _y = e.clientY;
    }
});

/**
 * 是否处在摄像头位置
 * @param canvas 画布对象
 * @param event 事件
 */
var isNearCamCalMouseLocation = function(canvas, event){
    return false;
    //根据颜色判别
    // var location = new GetMousePos(canvas, event);
    // // 获取该点像素的数据
    // var colorData = canvas.getPixelColor(location.x, location.y);
    // if(colorData.rgb == 'rgb(255,255,0)'){//rgb(255,255,0) #FFF00
    //     return true;
    // }
    // return false;
    //根据坐标判别【废弃】
    // var location = new GetMousePos(canvas, event);
    // for(var k in cameras){
    //     if(location.x >= cameras[k].x && location.x <= cameras[k].x+camL+addCount*camAddL
    //         && location.y >= cameras[k].y && location.y <= cameras[k].y+camL+addCount*camAddL){
    //         return true;
    //     }
    // }
    // return false;
};

/**
 * 取得相对于当前屏幕的坐标
 * @param canvas 画布
 * @param obj 画布中的点{'x':123,'y':123}
 * @constructor
 */
var GetLocationByCanvas = function GetLocationByCanvas(canvas,obj){
    var rect = canvas.getBoundingClientRect();
    this.x = parseInt(rect.left + obj.x * (rect.width / canvas.width));
    this.y = parseInt(rect.top + obj.y * (rect.height / canvas.height));
};

/**
 * 获取鼠标位置（相对于画布）
 * @param canvas 画布
 * @param event 事件
 * @returns {string}
 */
var GetMousePos = function GetMousePos(canvas, event) {
    var rect = canvas.getBoundingClientRect();
    this.x = parseInt((event.clientX - rect.left) * (canvas.width / rect.width));
    this.y = parseInt((event.clientY - rect.top) * (canvas.height / rect.height));
};

// document.getElementById('myCanvas').addEventListener('click', function(e) {
//     alert('offsetX:' + e.offsetX + ',offsetY:' + e.offsetY+"|||"+getMousePos(document.getElementById('myCanvas'),e));
//
//     //getMousePos(document.getElementById('myCanvas'),e);
//     // alert('layerX:' + e.layerX + ',layerY:' + e.layerY);
//     // alert('X:' + (e.clientX - myCanvas.getBoundingClientRect().left) + ',Y:' + (e.clientY - myCanvas.getBoundingClientRect().top));
//     // penCanvas.lineWidth = 1;
//     // penCanvas.strokeStyle = 'red';
//     // penCanvas.moveTo(50, 50);
//     // penCanvas.lineTo(50, 100);
//     // penCanvas.stroke();
// });