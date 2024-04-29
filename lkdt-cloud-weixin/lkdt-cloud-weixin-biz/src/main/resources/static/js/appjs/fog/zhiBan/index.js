/**一个JSONArray版本***/
/*$(function() {
	// 路段树 
	getTreeData();
	
	//待确认告警列表
	getConfirmList();
	setTimeout(function () {
		getConfirmList();
    }, 60000);
});

function getTreeData() {
	$.ajax({
		type : "GET",
		url : "/system/highway/tree",
		success : function(tree) {
			loadTree(tree);
		}
	});
}
function loadTree(tree) {
	$('#jstree').jstree({
		'core' : {
			'data' : tree
		},
		"plugins" : [ "search" ]
	});
	$('#jstree').jstree().open_all();
}

//初始化以后默认查询G15连云港段
$('#jstree').bind("loaded.jstree", function (event, data) {
	$('#jstree').jstree('select_node', '27' , true);
});

$('#jstree').on("changed.jstree", function(e, data) {
	//console.log(data);
	if (data.selected == -1) {
		var opt = {
			query : {
				hwId : '',
			}
		}
		hId='';
		//$('#exampleTable').bootstrapTable('refresh', opt);
	} else {
		var opt = {
			query : {
				hwId : data.selected[0],
			}
		}
		hId=data.selected[0];
		//$('#exampleTable').bootstrapTable('refresh',opt);
		
		if (typeof(data.node) != "undefined"){
			parent=data.node.parent;
			if(parent != '#' && parent != '-1'){
				$('#hw_id').attr('value',hId);
				loadEqu(hId);
			}
		}else if(hId == 27){
			$('#hw_id').attr('value',hId);
			loadEqu(hId);
		}
		
	}
});

function loadEqu(hwId){
	$.ajax({
        url:'/zhiban/queryEquByHwId',
        type:'post',
        data:{hwId:hwId},
        dataType:'json',
        success:function(data){
        	let equObjHtml = "";
        	for (let i = 0; i < data.length; i++) {
        		if(data[i].distance > 0){
        			if(data[i].stateDesc != '正常'){
        				equObjHtml += "<div class='panel panel-default yc' >";	
	        		}else{
	        			if(data[i].cameraType == 2){
	        				equObjHtml += "<div class='panel panel-default yqr' >";
	        			}else if ((data[i].cameraType == 1 && data[i].distance <= 200) || data[i].cameraType == 5){
	        				equObjHtml += "<div class='panel panel-default dqr' >";
	        			}else {
	        				equObjHtml += "<div class='panel panel-default qb' >";
	        			}
	        		}
        			equObjHtml += "<input id='equ_id' value='"+data[i].epId+"' type='hidden'></input>";
        			equObjHtml += "<input id='equ_name' value='"+data[i].equName+"'  type='hidden'></input>";
        			equObjHtml += "<input id='equ_address' value='"+data[i].address+"'  type='hidden'></input>";
        			
	        		equObjHtml += "<div class='panel-body '>";
	        		equObjHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
	        		equObjHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
	        		
	        		
	        		equObjHtml += "<div><p class='p_c p_c_l'>告警状态：";
	        		if(data[i].cameraType == 2){
	        			equObjHtml += "<span class='span_p_c'>"+data[i].alarmLevel+"</span></p><p class='p_c p_c_r'>是否确认：<span class='span_p_c'>已确认</span>";
        			}else if ((data[i].cameraType == 1 && data[i].distance <= 200) || data[i].cameraType == 5){
        				equObjHtml += "<span class='span_p_c'>"+data[i].alarmLevel+"</span></p><p class='p_c p_c_r'>是否确认：<span class='span_p_c'>待确认</span>";
        			}else {
        				equObjHtml += "<span class='span_p_c'>无告警</span>";
        			}
	        		equObjHtml += "</p></div>";
	        		equObjHtml += "</div>";
	        		equObjHtml += "</div>";
        		}
        		
        	}
			$("#exampleTable").html(equObjHtml);
        	if(equObjHtml.length > 0){
        		$("#option1").prop('checked',true);
        	}
			//绑定图片的点击事件 
			anelBinding();
			//绑定单选按钮的点击事件 
			checkRadioBinding();
        }
    });
}

function refreshEqu(){
	let hwId = $('#hw_id').val();
	loadEqu(hwId);
}

function anelBinding(){
	$('.panel-default').each(function(index){
		$(this).on('click',function(){
			$(this).addClass("panelBackgroundColorHis");

			$(this).siblings('.panel-default').removeClass('panelBackgroundColor'); // 删除其他兄弟元素的样式
			//$(this).siblings('.panel-default').children().children().css("color","#333");
			//$(this).siblings('.panel-default').children().children().children().css("color","#333");
			
			$(this).addClass('panelBackgroundColor'); // 添加当前元素的样式
			//$(this).children().children().css("color","#fff");
			//$(this).children().children().children().css("color","#fff");
			
			
			//$(this).children().children().css("color","#fff");
			//$(this).children().children().children().css("color","#fff");
			
			let epId = $(this).children('#equ_id').val();
			let name = $(this).children('#equ_name').val();
			let equLocation = $(this).children('#equ_address').val();
			confirm(epId,name,equLocation)
		})
	  })	
}

function checkRadioBinding(){
	$('input[name="options"]').each(function(index){
		$(this).on('click',function(){
			let state = $(this).val();
			if(state == 'qb'){
				$('.qb').show();
				$('.yc').show();
				$('.dqr').show();
				$('.yqr').show();
			}else if(state == 'yc'){
				$('.yc').show();
				$('.qb').hide();
				$('.dqr').hide();
				$('.yqr').hide();
			}else if(state == 'dqr'){
				$('.dqr').show();
				$('.qb').hide();
				$('.yc').hide();
				$('.yqr').hide();
			}else if(state == 'yqr'){
				$('.yqr').show();
				$('.qb').hide();
				$('.yc').hide();
				$('.dqr').hide();
			}
		})
	  })	
}

$('#show_hw_right').click(function(){
    $('#show_hw_right').hide();
    $('#show_hw_div').hide(100);
    setTimeout(function () {
    	$("#show_hw").show(100);
    }, 50);
});

$('#show_hw').click(function(){
	let wheight = $(window).height();
	$('#show_hw_right').css("height",wheight);
	$('#show_hw_div').css("height",wheight);
	
    $('#show_hw').hide(100);
    setTimeout(function () {
        $('#show_hw_right').show(100);
        $('#show_hw_div').show(100);
    },50);
});


$('#expand').click(function(){
    $('#expand').hide();
    $('#expand-popc').hide(100);
    setTimeout(function () {
    	$("#alarm_icon").show(100);
    }, 50);
});

$('#alarm_icon').click(function(){
	let wheight = $(window).height();
	$('#expand').css("height",wheight);
	$('#expand-popc').css("height",wheight);
	
	
    $('#alarm_icon').hide(100);
    setTimeout(function () {
        $('#expand').show(100);
        $('#expand-popc').show(100);
    },50);
});

$('.img_close_out').click(function(){
    $('#expand-popc-preview').hide(250);
});

function getConfirmList(){
	$.ajax({
        type: "GET",
        url: "/system/alarm/getConfirmList",
        dataType: 'json',
        success: function (data) {
            let unconfirmhtml = "";
        	for (let i = 0; i < data.length; i++) {
        		unconfirmhtml += "<ul class='alarm_ul' onclick='confirm(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\")'>";
                var param__= data[i].lon+","+data[i].lat;
                unconfirmhtml += "<li class='name_class' ><a href='#' style='color: #95C1EE;' " +
                    "title='" +data[i].equName + "' >"+(data[i].equName)+"</a></li>";
            	unconfirmhtml += "<li class='distance_class'>" + data[i].distance + "</li>";
                
                var begintime = data[i].begintime.substring(10);
                unconfirmhtml += "<li title='" + data[i].begintime + "'>" + (begintime==''?'&nbsp;':begintime) + "</li>";
                unconfirmhtml += "<li><a href='#' style='color: #95C1EE;'> " + data[i].cameraTypeDesc+ "</a></li>";
                unconfirmhtml += "</ul>";
        	}
        	if(unconfirmhtml.length > 0){
        		$("#alarm_icon img")[0].src = "/img/alarm_red.png";
        	}
            
            $("#contentUl_unconfirm").html(unconfirmhtml);
            
        }
    });
}


function confirm(epId,name,equLocation){
    layer.open({
        type : 2,
        title : "ID：" + epId + " >> 桩号：" + name + " >> 位置：" + equLocation,
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '60%', '90%' ],
        zIndex:99999999,
        content : '/system/equipment/zhiban_lookup/' + epId
    });
 }*/


/**多个JSONArray版本**/
$(function() {
	/* 路段树 */
	getTreeData();
	
	//待确认告警列表
	getConfirmList();
	/* setTimeout(function () {
		getConfirmList();
    }, 60000); */
});

function getTreeData() {
	$.ajax({
		type : "GET",
		url : "/system/highway/tree",
		success : function(tree) {
			loadTree(tree);
		}
	});
}
function loadTree(tree) {
	$('#jstree').jstree({
		'core' : {
			'data' : tree
		},
		"plugins" : [ "search" ]
	});
	$('#jstree').jstree().open_all();
}

//初始化以后默认查询G15连云港段
$('#jstree').bind("loaded.jstree", function (event, data) {
	$('#jstree').jstree('select_node', '27' , true);
});

$('#jstree').on("changed.jstree", function(e, data) {
	//console.log(data);
	if (data.selected == -1) {
		var opt = {
			query : {
				hwId : '',
			}
		}
		hId='';
	} else {
		var opt = {
			query : {
				hwId : data.selected[0],
			}
		}
		hId=data.selected[0];
		
		if (typeof(data.node) != "undefined"){
			parent=data.node.parent;
			if(parent != '#' && parent != '-1'){
				$('#hw_id').attr('value',hId);
				loadEqu(hId);
			}
		}else if(hId == 27){
			$('#hw_id').attr('value',hId);
			loadEqu(hId);
		}
		
	}
});

function loadEqu(hwId){
	$.ajax({
        url:'/zhiban/queryEquByHwId',
        type:'post',
        data:{hwId:hwId},
        dataType:'json',
        success:function(json){
        	//全部
        	let equAllHtml = "";
        	let data = json.jsonArrayAll;
        	for (let i = 0; i < data.length; i++) {
        		equAllHtml += "<div class='panel panel-default qb' onclick='panelClick(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\",this)' >";
        		equAllHtml += "<div class='panel-body '>";
        		equAllHtml += "<img class='equImg' src='"+data[i].imgpath+"' >";
        		equAllHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
        		equAllHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
        		equAllHtml += "<div><p class='p_c p_c_l'>告警等级：<span class='span_p_c'>"+data[i].alarmLevel+"</span></p></div>";
        		equAllHtml += "</div>";
        		equAllHtml += "</div>";
        	}
        	$("#exampleTable_qb").html(equAllHtml);
        	equAllHtml = "";
        	data = "";
        	
        	//有效
        	let equSuccessfulHtml = "";
        	data = json.jsonArraySuccessful;
        	for (let i = 0; i < data.length; i++) {
        		equSuccessfulHtml += "<div class='panel panel-default qb' onclick='panelClick(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\",this)' >";
        		equSuccessfulHtml += "<div class='panel-body '>";
        		equSuccessfulHtml += "<img class='equImg' src='"+data[i].imgpath+"' >";
        		equSuccessfulHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
        		equSuccessfulHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
        		equSuccessfulHtml += "<div><p class='p_c p_c_l'>告警等级：<span class='span_p_c'>"+data[i].alarmLevel+"</span></p></div>";
        		equSuccessfulHtml += "</div>";
        		equSuccessfulHtml += "</div>";
        	}
        	$("#exampleTable_yx").html(equSuccessfulHtml);
        	equSuccessfulHtml = "";
        	data = "";
        	
        	//已确认
        	let equAlarmHtml = "";
        	data = json.jsonArrayAlarm;
        	for (let i = 0; i < data.length; i++) {
        		equAlarmHtml += "<div class='panel panel-default yqr' onclick='panelClick(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\",this)' >";
        		equAlarmHtml += "<div class='panel-body '>";
        		equAlarmHtml += "<img class='equImg' src='" + data[i].imgpath + "' >";
        		equAlarmHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
        		equAlarmHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
        		equAlarmHtml += "<div><p class='p_c p_c_l'>告警等级：<span class='span_p_c'>"+data[i].alarmLevel+"</span></p></div>";
        		equAlarmHtml += "</div>";
        		equAlarmHtml += "</div>";
        	}
        	$("#exampleTable_yqr").html(equAlarmHtml);
        	equAlarmHtml = "";
        	data = "";
        	
        	//异常
        	let equErrorHtml = "";
        	data = json.jsonArrayError;
        	for (let i = 0; i < data.length; i++) {
        		equErrorHtml += "<div class='panel panel-default yc' onclick='panelClick(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\",this)' >";
        		equErrorHtml += "<div class='panel-body '>";
        		equErrorHtml += "<img class='equImg' src='"+data[i].imgpath+"' >";
        		equErrorHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
        		equErrorHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
        		equErrorHtml += "<div><p class='p_c p_c_l'>告警等级：<span class='span_p_c'>无告警</span></p></div>";
        		equErrorHtml += "</div>";
        		equErrorHtml += "</div>";
        	}
        	$("#exampleTable_yc").html(equErrorHtml);
        	equErrorHtml = "";
        	data = "";
        	
			
			//绑定单选按钮的点击事件
			checkRadioBinding();
			
			//默认选中全部
			var radioCheckedVal = $('input:radio[name="options"]:checked').val();
        	if(radioCheckedVal == null){
        		$("#option_qb").prop('checked',true);
        		$('#exampleTable_qb').show();
    			$('#exampleTable_yc').hide();
    			$('#exampleTable_dqr').hide();
    			$('#exampleTable_yqr').hide();
    			$('#exampleTable_yx').hide();
            }else{
            	radioCheckedShow(radioCheckedVal);
            }
        }
    });
}

function refreshEqu(){
	let hwId = $('#hw_id').val();
	loadEqu(hwId);
	getConfirmList();
}

function writeDayReport(){
	window.open("/fog/dayReport", "blank_")
}

function checkRadioBinding(){
	$('input:radio[name="options"]').each(function(index){
		$(this).on('click',function(){
			let radioCheckedVal = $(this).val();
			radioCheckedShow(radioCheckedVal);
		})
	  })	
}

function radioCheckedShow(state){
	if(state == 'qb'){
		$('#exampleTable_qb').show();
		$('#exampleTable_yc').hide();
		$('#exampleTable_dqr').hide();
		$('#exampleTable_yqr').hide();
		$('#exampleTable_yx').hide();
	}else if(state == 'yc'){
		$('#exampleTable_yc').show();
		$('#exampleTable_qb').hide();
		$('#exampleTable_dqr').hide();
		$('#exampleTable_yqr').hide();
		$('#exampleTable_yx').hide();
	}else if(state == 'dqr'){
		$('#exampleTable_dqr').show();
		$('#exampleTable_qb').hide();
		$('#exampleTable_yc').hide();
		$('#exampleTable_yqr').hide();
		$('#exampleTable_yx').hide();
	}else if(state == 'yqr'){
		$('#exampleTable_yqr').show();
		$('#exampleTable_qb').hide();
		$('#exampleTable_yc').hide();
		$('#exampleTable_dqr').hide();
		$('#exampleTable_yx').hide();
	}else if(state == 'yx'){
		$('#exampleTable_yx').show();
		$('#exampleTable_yqr').hide();
		$('#exampleTable_qb').hide();
		$('#exampleTable_yc').hide();
		$('#exampleTable_dqr').hide();
	}
}

function panelClick(epId,name,equLocation,obj){
	$(obj).addClass("panelBackgroundColorHis");
	$(obj).siblings('.panel-default').removeClass('panelBackgroundColor'); // 删除其他兄弟元素的样式
	$(obj).addClass('panelBackgroundColor'); // 添加当前元素的样式
	confirm(epId,name,equLocation);
}

$('#expand').click(function(){
    $('#expand').hide();
    $('#expand-popc').hide(100);
    setTimeout(function () {
    	$("#alarm_icon").show(100);
    }, 50);
});

$('#alarm_icon').click(function(){
	let wheight = $(window).height();
	$('#expand').css("height",wheight);
	$('#expand-popc').css("height",wheight);
	
	
    $('#alarm_icon').hide(100);
    setTimeout(function () {
        $('#expand').show(100);
        $('#expand-popc').show(100);
    },50);
});

$('.img_close_out').click(function(){
    $('#expand-popc-preview').hide(250);
});


function getConfirmList(){
	$.ajax({
        type: "GET",
        url: "/system/alarm/getConfirmList",
        dataType: 'json',
        success: function (data) {
        	let equConfirmHtml = "";
            let unconfirmhtml = "";
        	for (let i = 0; i < data.length; i++) {
        		//右侧列表
        		unconfirmhtml += "<ul class='alarm_ul' onclick='confirm(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\")'>";
                var param__= data[i].lon+","+data[i].lat;
                unconfirmhtml += "<li class='name_class' ><a href='#' style='color: #95C1EE;' " +
                    "title='" +data[i].equName + "' >"+(data[i].equName)+"</a></li>";
            	unconfirmhtml += "<li class='distance_class'>" + data[i].distance + "</li>";
                
                var begintime = data[i].begintime.substring(10);
                unconfirmhtml += "<li title='" + data[i].begintime + "'>" + (begintime==''?'&nbsp;':begintime) + "</li>";
                unconfirmhtml += "<li><a href='#' style='color: #95C1EE;'> " + data[i].cameraTypeDesc+ "</a></li>";
                unconfirmhtml += "</ul>";
                
                //图片列表
                equConfirmHtml += "<div class='panel panel-default dqr' onclick='panelClick(\""+data[i].epId+"\",\""+data[i].equName+"\",\""+data[i].address+"\",this)' >";
                equConfirmHtml += "<div class='panel-body '>";
                equConfirmHtml += "<img class='equImg' src='" + data[i].imgpath + "' >";
                equConfirmHtml += "<div><p class='p_c p_c_l'><span title='"+data[i].equName+"'>"+data[i].equName+"</span></p><p class='p_c p_c_r'>ID：<span  class='span_p_c' title='"+data[i].epId+"'>"+data[i].epId+"</span></p></div>";
                equConfirmHtml += "<div><p class='p_c p_c_l'>能见度：<span class='njd'>"+data[i].distance+"</span></p><p class='p_c p_c_r'>设备状态：<span  class='span_p_c' title='"+data[i].stateDesc+"'>"+data[i].stateDesc+"</span></p></div>";
                equConfirmHtml += "<div><p class='p_c p_c_l'>路段：<span class='span_p_c' title='"+data[i].hwName+"'>"+data[i].hwName+"</span></p><p class='p_c p_c_r'>位置：<span  class='span_p_c' title='"+data[i].address+"'>"+data[i].address+"</span></p></div>";
                equConfirmHtml += "</div>";
                equConfirmHtml += "</div>";
        	}
        	if(unconfirmhtml.length > 0){
        		$("#alarm_icon img")[0].src = "/img/alarm_red.png";
        	}else{
        		$("#alarm_icon img")[0].src = "/img/alarm_black.png";
        	}
            $("#contentUl_unconfirm").html(unconfirmhtml);
            unconfirmhtml = "";
            
            
        	$("#exampleTable_dqr").html(equConfirmHtml);
        	equConfirmHtml = "";
        	
        	var radioCheckedVal = $('input:radio[name="options"]:checked').val();
        	if(radioCheckedVal != null){
        		radioCheckedShow(radioCheckedVal);
            }
            
        }
    });
}


function confirm(epId,name,equLocation){
    layer.open({
        type : 2,
        title : "ID：" + epId + " >> 桩号：" + name + " >> 位置：" + equLocation,
        maxmin : true,
        shadeClose : false, // 点击遮罩关闭层
        area : [ '68%', '90%' ],
        zIndex:99999999,
        content : '/system/equipment/zhiban_lookup/' + epId
    });
 }