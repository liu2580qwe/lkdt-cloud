//**************一般异常 start****************//
    //event-事故，plan-施工，control-站点管制，busy-拥堵，road-道路管制，weather-恶劣天气，traffic-交通
    let eventId={'event':'1006001','plan':'1006002','control':'1006007','road':'1006006','busy':'1006008','traffic':'1006010','weather':'1006009'}; //接口传入参数
    //let eventIdStr = '1006001,1006002,1006006,1006008,1006009,1006010' ;
    //let eventIdStr = '1006001,1006002,1006006,1006008,1006010' ;
    //let eventIdStr = '1006002,1006006' ;
    let curYear=new Date().getFullYear(); //当前年份
    var wlyctype = true;//网络延迟提示判断
    
    let exceptionMarkers = [];
    let planExceptionMarkers = [];
    let roadExceptionMarkers = [];
    let accidentExceptionMarkers = [];
    let isZoomchange = false;
    let isPlanZoomchange = false;
    let isAccidentZoomchange = false;
 
   
   //多事件
    function eventMore(eventIdStr){
        if(!!eventIdStr){
            $.ajax({
                url:"https://wx.js96777.com/JiangSuAPIServer/index.php/WXSvgApi/getEventDataByTypesNews?eventtype="+eventIdStr,
                timeout: 3000,
                type:'get',
                jsonType:'jsonp',
                jsonpCallback: 'EventDataByTypesNews',
                data:{},
                success:function(data){
                	isLoadException = false;
                    if(!!data && data.length > 2){
                        data = JSON.parse(data)
                      	var newArr = getNewArray(data)
                      	newArr = reGetArray(newArr);
                      
                        //exceptionMarkers = mapBatchMarkException(newArr);
                        
                        let planArrData = [] , roadArrData = [];
                        newArr.forEach(function(item,index,array){
                        	if(item.eventType == 1006002) {
                        		planArrData.push(item);
                            } else if(item.eventType == 1006006) {
                            	roadArrData.push(item);
                            } else if(item.eventType == 1006001) {
                            	accidentArrData.push(item);
                            }
                        });
                        /**施工批量标记*/
                        planExceptionMarkers = mapBatchMarkPlanException(planArrData);
                        u.setStorage("planArrData",planArrData);
                        /**道路管制批量标记*/
                        roadExceptionMarkers = mapBatchMarkRoadException(roadArrData);
                        u.setStorage("roadArrData",roadArrData);
                        
                        isZoomchange = true;
                        
                    }else{
                    	$.toast("暂无数据", "cancel");
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown) {
                   // layer.closeAll('loading');
                    if(wlyctype){
                            // layer.msg('网络异常');
                        wlyctype = false;
                    }
                }
            });
        }
    }
    
    //道路维修
    function roadConditionDataFun(eventIdStr){
        if(!!eventIdStr){
            $.ajax({
                url:"https://wx.js96777.com/JiangSuAPIServer/index.php/WXSvgApi/getEventDataByTypesNews?eventtype="+eventIdStr,
                timeout: 3000,
                type:'get',
                jsonType:'jsonp',
                jsonpCallback: 'EventDataByTypesNews',
                data:{},
                success:function(data){
                	isLoadException = false;
                    if(!!data && data.length > 2){
                        data = JSON.parse(data)
                      	var newArr = getNewArray(data)
                      	newArr = reGetArray(newArr);
                        
                        if(eventIdStr == '1006001'){
                        	/**事故批量标记*/
                        	accidentExceptionMarkers = mapBatchMarkAccidentException(newArr);
                            u.setStorage("accidentArrData",newArr);
                            isAccidentZoomchange = true;
                            isLoadAccidentException = false;
                        }else if(eventIdStr == '1006002'){
                        	/**施工批量标记*/
                            planExceptionMarkers = mapBatchMarkPlanException(newArr);
                            u.setStorage("planArrData",newArr);
                            isPlanZoomchange = true;
                            isLoadPlanException = false;
                        }
                    }else{
                    	$.toast("暂无数据", "cancel");
                    	if(eventIdStr == '1006001'){
                        	/**事故批量标记*/
                            isLoadAccidentException = false;
                        }else if(eventIdStr == '1006002'){
                        	/**施工批量标记*/
                            isLoadPlanException = false;
                        }
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown) {
                   // layer.closeAll('loading');
                    if(wlyctype){
                            // layer.msg('网络异常');
                        wlyctype = false;
                    }
                }
            });
        }
    }
    
    
    /**一般异常批量标记**/
    // function mapBatchMarkException(arrayDataException){
    //     if(arrayDataException.length == 0){
    //         return;
    //     }
    //     let zoom = map.getZoom() > 6? (map.getZoom()-6): 0;
    //     let massMarksOptions = {
    // 		opacity:0.8,
    //         zIndex:120,
    //         cursor:'pointer',
    //         style: [{
    //             url: "/plugins/weui/img/roadException/icon1.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon2.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon6.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon7.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon8.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon9.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         },{
    //             url: "/plugins/weui/img/roadException/icon10.png",
    //             anchor: new AMap.Pixel(5.5 + zoom/2,5.5 + zoom/2),
    //             size: new AMap.Size(11 + zoom, 11 + zoom),
    //         }],
    //     };
    //     let event = [], plan = [], control = [], busy = [], road = [], weather = [], traffic = [];
    //     arrayDataException.forEach(function(item,index,array){
    //         if(item.eventType == 1006001) {
    //             item.style = 0;
    //             event.push(item);
    //         } else if(item.eventType == 1006002) {
    //             item.style = 1;
    //             plan.push(item);
    //         } else if(item.eventType == 1006006){
    //             item.style = 2;
    //             road.push(item);
    //         } else if(item.eventType == 1006007){
    //             item.style = 3;
    //             control.push(item);
    //         } else if(item.eventType == 1006008){
    //             item.style = 4;
    //             busy.push(item);
    //         } else if(item.eventType == 1006009){
    //             item.style = 5;
    //             weather.push(item);
    //         } else if(item.eventType == 1006010){
    //             item.style = 6;
    //             traffic.push(item);
    //         }
    //     });
    //
    //     exceptionMarkers("event",event);			//事故		icon1.png
    //     exceptionMarkers("plan",plan);				//施工		icon2.png
    //     exceptionMarkers("control",control);		//站点管制	icon7.png
    //     exceptionMarkers("busy",busy);				//拥堵		icon8.png
    //     exceptionMarkers("road",road);				//道路管制	icon6.png
    //     exceptionMarkers("weather",weather);		//恶劣天气	icon9.png
    //     exceptionMarkers("traffic",traffic);		//交通事件	icon10.png
    //
    //     function exceptionMarkers(alarmIndex,data){
    //         if(exceptionMarkers[alarmIndex] == null){
    //         	exceptionMarkers[alarmIndex] = new AMap.MassMarks(data,massMarksOptions);
    //             if(data.length > 0){
    //             	exceptionMarkers[alarmIndex].setMap(map);//TODO:待定
    //             }
    //             exceptionMarkers[alarmIndex].on('click',function(e){
    //             	wxExceptioPop.pop(e.layer);//e.layer.equName+" NO."+e.layer.epId
    //             });
    //         } else {
    //         	exceptionMarkers[alarmIndex].setData(data);
    //             exceptionMarkers[alarmIndex].setStyle(massMarksOptions.style);
    //             if(data.length > 0){
    //             	exceptionMarkers[alarmIndex].setMap(map);//TODO:待定
    //             }
    //             exceptionMarkers[alarmIndex].on('click',function(e){
    //             	wxExceptioPop.pop(e.layer);//e.layer.equName+" NO."+e.layer.epId
    //             });
    //         }
    //     }
    //     return exceptionMarkers;
    //
    // }
    
    
    
   	//施工批量标记
    function mapBatchMarkPlanException(arrayDataException){
        if(arrayDataException.length == 0){
            return;
        }
        let zoom = map.getZoom() > 6? (map.getZoom()-6): 0;
        planExceptionMarkers.clear();
        arrayDataException.forEach(function(item){
            let marker = new LKMap.Marker({
                position: new LKMap.LngLat(item['lnglat'][0], item['lnglat'][1]),
                content: '<div class="camimg" id="'+ item.epId +'" style="background: url(\'/lkdtWX/plugins/weui/img/roadException/icon2.png\')"></div>',
                offset: new LKMap.Pixel(-7, -7)
            });
            marker.eventid = item.eventid;
            marker.setMap(map);
            marker.on('click', function (e) {
                wxExceptioPop.pop(e.layer);
            });
            planExceptionMarkers.push(marker);
        });
        return planExceptionMarkers;
      
    }
    
    //道路管制批量标记
    function mapBatchMarkRoadException(arrayDataException){
        if(arrayDataException.length == 0){
            return;
        }
        let zoom = map.getZoom() > 6? (map.getZoom()-6): 0;
        roadExceptionMarkers.clear();
        arrayDataException.forEach(function(item){
            let marker = new LKMap.Marker({
                position: new LKMap.LngLat(item['lnglat'][0], item['lnglat'][1]),
                content: '<div class="camimg" id="'+ item.epId +'" style="background: url(\'/lkdtWX/plugins/weui/img/roadException/icon6.png\')"></div>',
                offset: new LKMap.Pixel(-7, -7)
            });
            marker.eventid = item.eventid;
            marker.setMap(map);
            marker.on('click', function (e) {
                wxExceptioPop.pop(e.layer);
            });
            roadExceptionMarkers.push(marker);
        });

    }
    
    /**道路管制批量标记图层样式**/
    function mapBatchMarkRoadSetStyle(arrayData){
		if(arrayData.length == 0){
            return;
        }
        let zoom = map.getZoom(); var size = 10;
        if (zoom > 11) {
            size = 3*zoom;
        } else if (zoom > 9) {
            size = 20;
        }
        roadExceptionMarkers.clear();
        arrayData.forEach(function(item){
            let marker = new LKMap.Marker({
                position: new LKMap.LngLat(item['lnglat'][0], item['lnglat'][1]),
                content: '<div class="camimg" id="'+ item.epId +'" style="background: url(\'/lkdtWX/plugins/weui/img/roadException/icon6.png\')"></div>',
                offset: new LKMap.Pixel(-7, -7)
            });
            marker.eventid = item.eventid;
            marker.setMap(map);
            marker.on('click', function (e) {
                wxExceptioPop.pop(e.layer);
            });
            roadExceptionMarkers.push(marker);
        });

    }
    
  //事故排障批量标记
    function mapBatchMarkAccidentException(arrayDataException){
        if(arrayDataException.length == 0){
            return;
        }
        accidentExceptionMarkers.clear();
        let zoom = map.getZoom() > 6? (map.getZoom()-6): 0;
        arrayDataException.forEach(function(item){
            let marker = new LKMap.Marker({
                position: new LKMap.LngLat(item['lnglat'][0], item['lnglat'][1]),
                content: '<div class="camimg" id="'+ item.epId +'" style="background: url(\'/lkdtWX/plugins/weui/img/roadException/icon1.png\')"></div>',
                offset: new LKMap.Pixel(-7, -7)
            });
            marker.eventid = item.eventid;
            marker.setMap(map);
            marker.on('click', function (e) {
                wxExceptioPop.pop(e.layer);
            });
            accidentExceptionMarkers.push(marker);
        });
    }
    
    /**事故排障批量标记图层样式**/
    function mapBatchMarkAccidentSetStyle(arrayData){
		if(arrayData.length == 0){
            return;
        }
        let zoom = map.getZoom();
        var size = 10;
        if (zoom > 11) {
        	size = 3*zoom;
        } else if (zoom > 9) {
        	size = 20;
        }
        accidentExceptionMarkers.clear();
        arrayData.forEach(function(item){
            let marker = new LKMap.Marker({
                position: new LKMap.LngLat(item['lnglat'][0], item['lnglat'][1]),
                content: '<div class="camimg" id="'+ item.epId +'" style="background: url(\'/lkdtWX/plugins/weui/img/roadException/icon1.png\')"></div>',
                offset: new LKMap.Pixel(-7, -7)
            });
            marker.eventid = item.eventid;
            marker.setMap(map);
            marker.on('click', function (e) {
                wxExceptioPop.pop(e.layer);
            });
            accidentExceptionMarkers.push(marker);
        });
        return accidentExceptionMarkers;
    }
    
   
  	//事件接口返回数据的处理
    function getNewArray(arr){
        var temp = []
        for(var i = 0; i < arr.length;i++){
            for(var j = 0; j < arr[i].length;j++){
                temp.push(arr[i][j])
            }
        }

        var obj = {};
        for(var i = 0; i < temp.length;i++) {
        	var key= temp[i].trafficsplitcode;
        	var events = temp[i].eventid.split('/');
        	var eventsXY;
        	try {
        		eventsXY = temp[i].xy.split(',')
			} catch (e) {
				continue ;
			}
            //if(obj[key]) {
            //    obj[key].eventid += (','+events[0]);
            //    obj[key].eventType += (','+events[1]);
            //    obj[key].trafficsplitcode = temp[i].trafficsplitcode;
            //    obj[key].eventX += (','+eventsXY[0]);
            //    obj[key].eventY += (','+eventsXY[1]);
            //} else {
                obj[key] = {};
                obj[key].eventid = events[0];
                obj[key].eventType = events[1];
                obj[key].lng = eventsXY[0];
                obj[key].lat = eventsXY[1];
                obj[key].lnglat = eventsXY;
                obj[key].trafficsplitcode = temp[i].trafficsplitcode;
            //}
        }
        var newArry = [];
        for(var k in obj){
            newArry.push(obj[k])
        }
        return newArry
    }
  	
  	
  	//去重
    function reGetArray(arr){
        var result = [];
        var obj = {};
        for(var i =0; i < arr.length; i++){
            if(!obj[arr[i].eventid]){
                result.push(arr[i]);
                obj[arr[i].eventid] = true;
            }
        }
       // console.log('去重',result);
        return result;
    }
  	
  	//一般异常点击详情 
    let wxExceptioPop = {
        test:function(){},
        /**弹出框*/
        pop:function(equObj){
            try{
                
                let strUrls = "https://wx.js96777.com/JiangSuAPIServer/index.php/WXSvgApi/getEventDetailsById";
            	$.ajax({
            		url:strUrls,
                    timeout: 3000,
                    dataType:'jsonp',
                    data:{"eventids":equObj.eventid},
                    jsonpCallback: 'eventdetails',
                    success:function(data){
                    	$('#wxExceptioPopDiv').show();
                        //console.log('事件详情',data);
                        

                        let datalength = data.length;
                        let type = equObj.eventType;
                        if(datalength>0){
                            console.log(type);
                            if(type == '1006001' || type == '1006002' || type == '1006009' || type == '1006010'){
                                getTrafficDetails(type,equObj.eventid,data,equObj.trafficsplitcode)
                            }else if(type == '1006006'){
                                getRoadDetails(type,equObj.eventid,data,equObj.trafficsplitcode)
                            }else if(type == '1006008'){
                                getBusyDetails(type,equObj.eventid,data,equObj.trafficsplitcode)
                            }else if(type == 'more'){
                                getMoreDetails(eventtype,equObj.eventid,data,equObj.trafficsplitcode)
                            } 
                        }
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown) {
                   // layer.closeAll('loading');
                    if(wlyctype){
                            // layer.msg('网络异常');
                            wlyctype = false;
                        }
                    }
                });
                
            } catch (e) {
                $.toast("ERROR:PO215网络异常", "forbidden");
            }

        },
        close:function(){
            $('#wxExceptioPopDiv').hide();
        },
    };
  	
    //事故、施工、恶劣天气、交通详情
    function getTrafficDetails(type,id,data,trafficsplitcode){
        imgId = trafficsplitcode;
        var str = '',img = '',title = '';
        switch(type){
            case '1006001' :
                console.log('shigu')
                img = '/lkdtWX/plugins/weui/img/roadException/icon1.png';
                title = '事故';
                break;
            case '1006002' :
                img = '/lkdtWX/plugins/weui/img/roadException/icon2.png';
                title = '施工';
                break;
            case '1006009' :
                img = '/lkdtWX/plugins/weui/img/roadException/icon9.png';
                title = '恶劣天气';
                break;
            case '1006010' :
                img = '/lkdtWX/plugins/weui/img/roadException/icon10.png';
                title = '交通事件';
                break;
        }
        console.log(data)
        for(var i in data){
            var info = data[i];
            var roadname = info.roadname?info.roadname:'';
            var reportout = info.reportout?info.reportout:'';
            var occtime = info.occtime?getNewTime(info.occtime):'--';
            var planovertime = info.planovertime?getNewTime(info.planovertime):'--';
            var updatetime = info.updatetime?getNewTime(info.updatetime):'--';
            var directionname = info.directionname?'（'+info.directionname+'）':'--';

            var updateStr =  '';

            str += trafficDetail(img,title,roadname,directionname,reportout,occtime,updateStr,planovertime);

            $(".video-box").empty();
            $(".video-box").append(str);
            //$("#video-swiper").show();
        }
    }

    //拥堵详情
    function getBusyDetails(type,id,data,trafficsplitcode){
        imgId = trafficsplitcode;
        var str = '';
        for(var i in data){
            var info = data[i];
            var roadname = info.roadname?info.roadname:'';
            var reportout = info.reportout?info.reportout:'';
            var occtime = info.occtime?getNewTime(info.occtime):'--';
            var planovertime = info.planovertime?getNewTime(info.planovertime):'--';
            var realovertime = info.realovertime?getNewTime(info.realovertime):'--';
            var jamspeed = info.jamspeed?info.jamspeed+'km/h':'--';
            var jamdist = info.jamdist?info.jamdist/1000+'km':'--';
            var longtime = info.longtime?info.longtime+'min':'--';
            var directionname = info.directionname?'（'+info.directionname+'）':'--';
            if(info.type==2 && is_weixn() == true){
                typestr =  '<div class="busy-foot">信息来源：高德</div>'
            }else{
                typestr = ''
            }

            str += busyDetail(roadname,directionname,reportout,occtime,jamspeed,jamdist,longtime,typestr);

            $(".video-box").empty();
            $(".video-box").append(str);
            //$("#video-swiper").show();
        }
    }

    //道路管制详情
    function getRoadDetails(type,id,data,trafficsplitcode){
        imgId = trafficsplitcode;
        var str = ''
        for(var i in data){
            var info = data[i];
            var roadname = info.roadname?info.roadname:'';
            var reportout = info.reportout?info.reportout:'';
            var occtime = info.occtime?getNewTime(info.occtime):'--';
            var planovertime = info.planovertime?getNewTime(info.planovertime):'--';
            var directionname = info.directionname?'（'+info.directionname+'）':'';
            var picurl = ''
            if(!!info.picurl){
                picurl = info.picurl.replace("http://","https://")
            }else{
                picurl = "/lkdtWX/plugins/weui/img/roadException/icon6.png"
            }
            str += roadDetail(picurl,roadname,directionname,reportout,occtime,planovertime);

            $(".video-box").empty();
            $(".video-box").append(str);
            //$("#video-swiper").show();
        }

    }

    //站点管制详情
    function getControlDetails(type,id,near,trafficsplitcode){
        layer.load(2);
        getClickNum('','3006004 ','1006007')
        var strUrls = InpageUrl+ "WXSvgApi/poiControls";
        var postData = {}
        if(!!latitude){
            postData = {"poiid":id,'longitude':longitude,'latitude':latitude}
        }else{
            postData = {"poiid":id}
        }
        $.ajax({
            url:strUrls,
            timeout: 3000,
            dataType:'jsonp',
            data:postData,
            jsonpCallback: 'poidetails',
            success:function(data){
                //console.log('站点管制详情',data);
                layer.closeAll('loading');
                var Id = String(id);
                var str = '';
                if(!!data.name){
                    imgId = id;
                    var closebtn = "closeDialog()"
                    if(near == 'near'){
                        var ph = paper.select("#s"+id);
                        if(ph){
                            var x = ph.getBBox().cx-12.0;
                            var y = ph.getBBox().cy-26.0;
                            var now = paper.select("#temp_g").g();
                            var img = paper.image(bigImg.nearStation, x, y, 24,28);
                            now.add(img);
                            setCenterInjt(x,y);
                            closebtn = "closeDialog1()"
                        }
                    }
                    var distance = '';
                    var distStr = ''
                    if(!!data.distance){
                        distance = Number(data.distance).toFixed(2);
                        distStr = `<span class="left-contain addr-txt" style="font-size:3.5vw;">距您${distance}km</span>`
                    }
                    var upenter = '',upexit = '',downenter = '',downexit = '';
                    if(data.upenter == 0){
                        upenter = '<span style="color:#07bf5a;">入口开启</span>'
                    }else{
                        upenter = '<span style="color:#e30000;">入口关闭</span>'
                    }
                    if(data.upexit == 0){
                        upexit = '<span class="door-title" style="color:#07bf5a;">出口开启</span>'
                    }else{
                        upexit = '<span class="door-title" style="color:#e30000;">出口关闭</span>'
                    }
                    if(data.downenter == 0){
                        downenter = '<span style="color:#07bf5a;">入口开启</span>'
                    }else{
                        downenter = '<span style="color:#e30000;">入口关闭</span>'
                    }
                    if(data.downexit == 0){
                        downexit = '<span class="door-title" style="color:#07bf5a;">出口开启</span>'
                    }else{
                        downexit = '<span class="door-title" style="color:#e30000;">出口关闭</span>'
                    }
                    var picurl = ''
                    if(!!data.picurl){
                        picurl = data.picurl.replace("http://","https://")
                    }
                    str += ` <div class="swiper-slide video-item">
                        <div class="addr-head">
                        <div class="head-left">
                            <img class="addr-pic" src="${picurl}">
                            <div class="left-txt">
                                <span class="left-title addr-title" style="font-size:0.32rem;">${data.name}</span>
                                ${distStr}
                            </div>
                        </div>
                        <div class="head-right" onclick="${closebtn}">
                            <img style="width:100%;height:100%;" src="assets/images/del.png">
                        </div>
                    </div>
                    <div class="addr-door">
                        <div class="door-item">
                            <div class="door-title">
                                ${upenter}
                                ${upexit}
                            </div>
                            <span class="door-txt open-txt">${data.direction1}方向</span>
                        </div>
                        <div class="door-item">
                            <div class="door-title">
                                ${downenter}
                                ${downexit}
                            </div>
                            <span class="door-txt close-txt">${data.direction2}方向</span>
                        </div>
                    </div></div>`;
                    $(".video-box").empty();
                    $(".video-box").append(str);
                    $("#video-swiper").show();
                }else{
                	$.toast("暂无数据", "cancel");
                }

            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
                layer.closeAll('loading');
                if(wlyctype){
                	$.toast("网络异常", "cancel");
                    wlyctype = false;
                }
            }
        });
    }
    
  //事故、施工、恶劣天气、交通详情弹窗
    function trafficDetail(img,title,roadname,directionname,reportout,occtime,updateStr,planovertime){
        var trafficStr = `<div class="swiper-slide video-item">
                    <div class="video-head">
                    <div class="head-left">
                        <img src="${img}">
                        <div class="left-txt">
                            <span class="left-title">${title}</span>
                            <span class="left-contain">${roadname}${directionname}</span>
                        </div>
                    </div>
                </div>
                <div class="busy-detail">
                    <div class="left-contain"  style="margin:1.5vw 0;font-size: 3.5vw;">${reportout}</div>
                    <div class="busy-info">
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${occtime}</span>
                            <span class="item-txt2">开始时间</span>
                        </div>
                        ${updateStr}
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${planovertime}</span>
                            <span class="item-txt2">预计结束时间</span>
                        </div>
                    </div>
                </div></div>`
        return trafficStr
    }

    //拥堵详情弹窗
    function busyDetail(roadname,directionname,reportout,occtime,jamspeed,jamdist,longtime,typestr){
        var busyStr = `<div class="swiper-slide video-item">
                    <div class="video-head">
                        <div class="head-left">
                            <img src="/lkdtWX/plugins/weui/img/roadException/icon8.png">
                            <div class="left-txt">
                                <span class="left-title">拥堵</span>
                                <span class="left-contain">${roadname}${directionname}</span>
                            </div>
                        </div>
                </div>
                <div class="busy-detail">
                    <div class="left-contain" style="margin:1.5vw 0;font-size: 3.5vw;">${reportout}</div>
                    <div class="busy-info">
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${occtime}</span>
                            <span class="item-txt2">开始时间</span>
                        </div>
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${jamspeed}</span>
                            <span class="item-txt2">拥堵时速</span>
                        </div>
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${jamdist}</span>
                            <span class="item-txt2">拥堵距离</span>
                        </div>
                        <div class="busy-item">
                            <span class="item-txt1 item-txt12">${longtime}</span>
                            <span class="item-txt2">持续时间</span>
                        </div>
                    </div>
                </div>${typestr}</div>`
        return busyStr;
    }

    //道路管制弹窗
    function roadDetail(picurl,roadname,directionname,reportout,occtime,planovertime){
        var roadStr = ` <div class="swiper-slide video-item">
                    <div class="video-head">
                    <div class="head-left">
                        <img src="${picurl}">
                        <div class="left-txt">
                            <span class="left-title">道路管制</span>
                            <span class="left-contain">${roadname}${directionname}</span>
                        </div>
                    </div>
            </div>
            <div class="busy-detail">
                <div class="left-contain"  style="margin:1.5vw 0;font-size: 3.5vw;">${reportout}</div>
                <div class="busy-info">
                    <div class="busy-item">
                        <span class="item-txt1">${occtime}</span>
                        <span class="item-txt2">开始时间</span>
                    </div>
                    <div class="busy-item">
                        <span class="item-txt1">${planovertime}</span>
                        <span class="item-txt2">计划结束时间</span>
                    </div>
                </div>
            </div></div>`
        return roadStr;
    }
    
    
    // 时间处理
    function getNewTime(time){
        var year = time.substr(0,4);
        var newTime = '';
        if(year < curYear){
            newTime = time.substr(0,16);
        }else{
            newTime = time.substr(5,11);
        }
        return newTime
    }
    
    function is_weixn(){
        var ua = navigator.userAgent.toLowerCase();
        if(ua.match(/MicroMessenger/i)=="micromessenger") {
            return true;
        } else {
            return false;
        }
    }
  	
  	//**************一般异常 end****************//