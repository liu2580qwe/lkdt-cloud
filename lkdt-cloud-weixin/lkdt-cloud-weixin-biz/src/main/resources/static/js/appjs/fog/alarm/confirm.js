
var iseffect=1;
$().ready(function() {
	validateRule();
	var fname=$("#fname").val();
	var dateStr=$("#dateStr").val();
	//alert($('#imgpath').val());
	$("#img1").attr("src", $('#imgpath').val());
	if(!$("input[name='iseffective']:checked").val()){
		$("input[name='iseffective'][value='0']").attr("checked",true);
		iseffect=9;
	}/*else{
		iseffect=$("input[name='iseffective']:checked").val();
		if(iseffect==1){
			showsms();
		}
	}*/
	//$("#sms").hide();
	var address=$("#address").val();
	var distance=$("#distance").val();
	var equName=$("#equName").val();
	
	var oldLevel=$("#oldLevel").val();
	var level=$("#level").val();
	// 1:发生告警  2：告警等级增加  3：告警等级减少    4：告警解除
	if(oldLevel==0){
		$("#alarmtypetext").val("发生告警");
//		$("input[name='alarmtype'][value=1]").attr("checked",true);
		$("#alarmtype").val(1);
	}else if(level>oldLevel){
		$("#alarmtypetext").val("告警等级增加,原告警等级:"+oldLevel+",现告警等级："+level);
//		$("input[name='alarmtype'][value=2]").attr("checked",true);
		$("#alarmtype").val(2);
	}else if(level<oldLevel){
		$("#alarmtypetext").val("告警等级减少,原告警等级:"+oldLevel+",现告警等级："+level);
//		$("input[name='alarmtype'][value=2]").attr("checked",true);
		$("#alarmtype").val(2);
	}
	if(distance>200){
		$("#alarmtypetext").val("告警解除");
//		$("input[name='alarmtype'][value=4]").attr("checked",true);
		$("#alarmtype").val(4);
	}
	$("#smtext").html(smstemplate(address,getfogLevel(distance),getLevel(distance),distance,equName));
		
	//th:src="@{{path}(path=${alarm.imgpath})}"
});
function distancechange(){
	var address=$("#address").val();
	var distance=$("#distance").val();
	var equName=$("#equName").val();
	if(distance>200){
		$("#alarmtypetext").val("告警解除");
		$("input[name='alarmtype'][value=4]").attr("checked",true);
//		$("#alarmtype").val(4);
	}else{
		$("input[name='alarmtype'][value=4]").attr("checked",false);
	}
	$("#smtext").html(smstemplate(address,getfogLevel(distance),getLevel(distance),distance,equName));
}
function getLevel(distance){
	var level='';
	if(distance<=200){
		level= "三";
	}
	if(distance<=100){
		level= "二";
	}
	if(distance<=50){
		level= "一";
	}
	if(distance<=30){
		level= "特";
	}
	return level;
}
function getfogLevel(distance){
	var fogLevel='';
	if(distance<=200){
		fogLevel= "大雾";
	}
	if(distance<=100){
		fogLevel= "浓雾";
	}
	if(distance<=50){
		fogLevel= "特浓雾";
	}
	if(distance<=30){
		fogLevel= "特大浓雾";
	}
	return fogLevel;
}
function smstemplate(location,foglevel,level,distance,equCode){
	//return "【团雾检测】"+location+"路段"+equCode+"处发生"+foglevel+",当前能见度"+distance+"米,建议实施"+level+"级管制。";
//	var alarmtype=$("input[name='alarmtype']:checked").val();
	var alarmtype=$("#alarmtype").val();
	var distance=$("#distance").val();
	if(distance>200){
		return "【解除大雾管制提示】"+"东部高速盐城段当前能见度"+distance+"米,依据交通安全管理规范,建议解除管制。（中交信科集团海德科技公司）";
	}
	if(alarmtype==1){
		return "【雾霾实时预警】"+"东部高速盐城段发生"+foglevel+",当前能见度"+distance+"米,依据交通安全管理规范,建议实施"+level+"级管制。（中交信科集团海德科技公司）";
	}else if(alarmtype==2||alarmtype==3){
		return "【调整管制等级提示】"+"东部高速盐城段当前能见度"+distance+"米,依据交通安全管理规范,建议变更为"+level+"级管制。（中交信科集团海德科技公司）";
	}else if(alarmtype==4){
		return "【解除大雾管制提示】"+"东部高速盐城段当前能见度"+distance+"米,依据交通安全管理规范,建议解除管制。（中交信科集团海德科技公司）";
	}
	
}

/*$(document).keydown(function(event){
	　　　if(event.keyCode == 40){
	　　　　　　alert('你按下了向下'); 
	　　　}
	if(event.keyCode == 38){
		alert('你按下了向上'); 
	}
});*/


/*document.onkeydown=function(event){
	 var starttime =window.parent.document.getElementById("starttime").value;
	 var endtime =window.parent.document.getElementById("endtime").value;
	 var alarmstatus =window.parent.document.getElementById("alarmstatus").value;
	 var iseffective =window.parent.document.getElementById("iseffective").value;
	 var level =window.parent.document.getElementById("level").value;
	 var hwId =window.parent.document.getElementById("hwId").value;
	 var camcode =window.parent.document.getElementById("camcode").value;
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==40){ 
       //要做的事情/confirm/{alarmId}
    	$.ajax({
    		cache : true,
    		type : "GET",
    		url : "/system/alarm/confirmByOrder",
    		data : {"starttime":starttime,"endtime":endtime,"alarmstatus":alarmstatus,"iseffective":iseffective,"level":level,"hwId":hwId,"camcode":camcode},// 你的formid
    		async : false,
    		error : function(request) {
    			parent.layer.alert("Connection error");
    		},
    		success : function(data) {
    			console.log(data);
    			document.getElementById("alarm_id").value = data.alarmId;
    			document.getElementById("imgpath").value = data.imgpath;
    			document.getElementById("epId").value = data.epId;
    			document.getElementById("distance").value = data.distance;
    			var level="";
    			if(data.level==3){
    				level="特浓雾";
    			}else if(data.level==2){
    				level="浓雾";
    			}else if(data.level==1){
    				level="大雾";
    			}
    			document.getElementById("level").value = level;
    			document.getElementById("begintime").value = data.begintime;
    			document.getElementById("endtime").value = data.endtime;
    			document.getElementById("address").value = data.address;
    			if(data.iseffective==1){
    				$("input[name='iseffective'][value=1]").attr("checked",true); 
    			}else{
    				$("input[name='iseffective'][value=0]").attr("checked",true); 
    			}
    		}
    	});
      }
            
}; */


$.validator.setDefaults({
	submitHandler : function() {
		update();
	}
});
function update() {
	$.ajax({
		cache : true,
		type : "POST",
		url : "/weixin/alarm/confirm",
		data : $('#signupForm').serialize(),// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				var index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
				if(index){
					parent.reLoad();
					parent.layer.close(index);
				}
				

			} else {
				parent.layer.alert(data.msg)
			}

		}
	});

}
function validateRule() {
	var icon = "<i class='fa fa-times-circle'></i> ";
	$("#signupForm").validate({
		rules : {
			name : {
				required : true
			}
		},
		messages : {
			name : {
				required : icon + "请输入名字"
			}
		}
	})
}