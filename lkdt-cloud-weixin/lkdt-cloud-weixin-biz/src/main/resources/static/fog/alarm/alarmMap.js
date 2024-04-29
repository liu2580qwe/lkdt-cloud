/********************* 地图 start ***********************/
function mapInit(data,alarmMapId,alarmType){
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
	map = new AMap.Map(alarmMapId, {
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
		let lightRed = $(".wumai_light_red");	//雾：特级
		let red = $(".wumai_red");				//雾：一级
		let orange = $(".wumai_orange");		//雾：二级
		let yellow = $(".wumai_yellow");		//雾：三级
		let nofog = $(".nofog");				//雾：散
		let windRed = $(".wind-red");			//大风：一级、特级
		let windYellow = $(".wind-yellow");		//大风：二级、三级
		if(zoom > 10) {
			if(lightRed.length > 0){
				lightRed.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(orange.length > 0){
				orange.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(red.length > 0){
				red.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(yellow.length > 0){
				yellow.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(nofog.length > 0){
				nofog.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(windRed.length > 0){
				windRed.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
			if(windYellow.length > 0){
				windYellow.css({ height: (3*zoom)+'px', width: (3*zoom)+'px' });
			}
		} else {
			if(lightRed.length > 0){
				lightRed.css({ height: '20px', width: '20px' });
			}
			if(orange.length > 0){
				orange.css({ height: '20px', width: '20px' });
			}
			if(red.length > 0){
				red.css({ height: '20px', width: '20px' });
			}
			if(yellow.length > 0){
				yellow.css({ height: '20px', width: '20px' });
			}
			if(nofog.length > 0){
				nofog.css({ height: '20px', width: '20px' });
			}
			if(windRed.length > 0){
				windRed.css({ height: '20px', width: '20px' });
			}
			if(windYellow.length > 0){
				windYellow.css({ height: '20px', width: '20px' });
			}
		}
	});

	//摄像头初始化
	setTimeout(function(){
		mapEquipmentsInit(data,alarmType);
	},500);
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

/**
 *  地图摄像头初始化
 * */
function mapEquipmentsInit(data,alarmType){
	//摄像头经纬度 >>>30m:#C30004,50m:#F87018,100m:#F9C115,200:#F7F940,300:#357A00,500:#357A00,
	//标记摄像头
	let arrayData = [];
	data.forEach(function(currData,index,arr){
		if(alarmType == "fog"){
			arrayData.push({lnglat: [currData.lon,currData.lat], epId:currData.epId, name: currData.equName,
				equLocation:currData.hwName,begintime:currData.begintime,endtime:currData.endtime,level:currData.level});
		}else if(alarmType == "wind"){
			arrayData.push({lnglat: [currData.lon,currData.lat], epId:currData.epId, name: currData.windName,
				equLocation:currData.address,begintime:currData.begintime,endtime:currData.endtime,level:currData.level});
		}

	},this);

	//摄像头标记
	mapClusterMarker(arrayData,alarmType);
}

/**大量点聚合标记*/
function mapClusterMarker(arrayData,alarmType){
	let markers = [];
	for (let i = 0; i < arrayData.length; i++) {
		let marker = "";
		if(alarmType == "fog"){
			if(arrayData[i].level == 1){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wumai_yellow"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else if(arrayData[i].level == 2){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wumai_orange"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else if(arrayData[i].level == 3){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wumai_light_red"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else if(arrayData[i].level == 4){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wumai_red"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else{
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="nofog"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}
		}else if(alarmType == "wind"){
			if(arrayData[i].level == 1 || arrayData[i].level == 2){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wind-yellow"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else if(arrayData[i].level == 3 || arrayData[i].level == 4){
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wind-red"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}else{
				marker = new AMap.Marker({
					position: arrayData[i]['lnglat'],
						content: '<div class="wind-blue"></div>',
					offset: new AMap.Pixel(-7, -7)
				});
			}
		}


		marker.epId = arrayData[i].epId;
		marker.name = arrayData[i].name;
		marker.equLocation = arrayData[i].equLocation;
		marker.begintime = arrayData[i].begintime;
		marker.endtime = arrayData[i].endtime;
		marker.on('click',function(e){
			openFog(this.epId,this.name,this.equLocation,this.begintime,this.endtime);
		});
		markers.push(marker);
	}
	//最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
	map.plugin(["AMap.MarkerClusterer"],function() {
		let markerClusterer = new AMap.MarkerClusterer(map, markers,{
			gridSize: 1,
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


function getLongestRing(feature) {
	let rings = getAllRings(feature);
	rings.sort(function(a, b) {
		return b.length - a.length;
	});
	return rings[0];
}

function getAllRings(feature) {
	let coords = feature.geometry.coordinates,
		rings = [];
	for (let i = 0, len = coords.length; i < len; i++) {
		rings.push(coords[i][0]);
	}
	return rings;
}

/********************* 地图 end ***********************/