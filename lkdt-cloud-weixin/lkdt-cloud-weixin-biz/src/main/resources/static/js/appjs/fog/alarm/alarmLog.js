
var prefix = "/system/alarm"
$(function() {
	var startdate=new Date();
	startdate.setHours(0,0,0,0);
	laydate.render({
		elem: '#starttime',
		type: 'datetime',
		value: startdate
	});
	laydate.render({
		elem: '#endtime',
		type: 'time',
		value: new Date()
	});
	console.log($('#starttime').val());
	setTimeout("selectLogInfo()",500);
});

var date = new Date();
var month = date.getMonth() + 1<10? "0"+(date.getMonth() + 1):date.getMonth() + 1;
var strDate = date.getDate()<10? "0" + date.getDate():date.getDate();
var currentdate = date.getFullYear() + "-"  + month  + "-"  + strDate;

var message;
function selectLogInfo() {
	
	$('#exampleTable').bootstrapTable(
			{
				method : 'get', // 服务器数据的请求方式 get or post
				url : prefix + "/selectLogInfo", // 服务器数据的加载地址
			//	showRefresh : true,
			//	showToggle : true,
			//	showColumns : true,
				iconSize : 'outline',
				toolbar : '#exampleToolbar',
				paginationShowPageGo: true,
				striped : true, // 设置为true会有隔行变色效果
				dataType : "json", // 服务器返回的数据类型
				pagination : true, // 设置为true会在底部显示分页条
				// queryParamsType : "limit",
				// //设置为limit则会发送符合RESTFull格式的参数
				singleSelect : false, // 设置为true将禁止多选
				// contentType : "application/x-www-form-urlencoded",
				// //发送到服务器的数据编码类型
				pageSize : 10, // 如果设置了分页，每页数据条数
				pageNumber : 1, // 如果设置了分布，首页页码
				//search : true, // 是否显示搜索框
				showColumns : false, // 是否显示内容下拉框（选择显示的列）
				sidePagination : "server", // 设置在哪里进行分页，可选值为"client" 或者 "server"
				queryParams : function(params) {
					return {
						//说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
						limit: params.limit,
						offset:params.offset,
						startTime:$('#starttime').val(),
						endTime:$('#starttime').val().substring(0,11)+$('#endtime').val(),
						hwId:$('#hwId').val(),
						equcode:$('#epid').val()
					};
				},
				// //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
				// queryParamsType = 'limit' ,返回参数必须包含
				// limit, offset, search, sort, order 否则, 需要包含:
				// pageSize, pageNumber, searchText, sortName,
				// sortOrder.
				// 返回false将会终止请求
				columns : [
						{
							checkbox : true
						},
//														{
//							field : 'alarmId', 
//							title : '主键' 
//						},
						{
							field : 'dateTime',
							title : '时间' 
						},
														{
							field : 'epName',
							title : '摄像头名称' 
						},
														{
							field : 'paramNum',
							title : '能见度系数' 
						},
														{
							field : 'originVal',
							title : '程序计算值' 
						},
														{
							field : 'modifyVal',
							title : '可见距离' 
						},
						{
							field : 'alarmId', 
							title : '是否告警' ,
							formatter : function(value,row,index){
								if(row.alarmId!='-'){
									return "是";
								}else{
									return "否";
								}
							}
						},
														{
							title : '操作',
							field : 'id',
							align : 'center',
							formatter : function(value, row, index) {
								/*var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
										+ row.alarmId
										+ '\')"><i class="fa fa-edit"></i></a> ';*/
								console.log(row);
								var todayDate=new Date(); 
								todayDate.setHours(0,0,0,0);
								var endtime=new Date($('#starttime').val().substring(0,11)+$('#endtime').val());
								var f='';
								if(todayDate<endtime||row.alarmId!='-'){
									f = '<a class="btn btn-success btn-sm" href="#" title="查看图片"   mce_href="#" onclick="showPic(&quot;'+row.alarmtime+'&quot;,&quot;'+row.epId+'&quot;,&quot;'+row.imgName+'&quot;)"><i class="fa fa-eye"></i></a> ';
								}
								return   f ;
							}
						} ]
			});
	/*$.ajax({
		type : 'get',
		url : prefix + '/selectLogInfo',
		data:{'startTime':startTime, 'endtime':endtime,'hwId':hwId,'camcode':camcode},
		success : function(data) {
			if(data!=""){
				message = data.split(/[\n]/);
				var html="";
				if(currentdate== startTime.substring(0,10)){
					html="<tr><td align='center' style=' width: 800px;'>信息</td>/tr>";
				}else{
					html="<tr><td align='center' style=' width: 800px;'>信息</td><td align='center' style=' width: 200px;'>图片链接</td></tr>";
				}
				var length=100;
				if(message.length<100){
					length=message.length-1;
				}
				
				for(var i=0;i<length;i++){
					var m = message[i].split("/");
					var time = m[0].substring(0,10);
					var f = m[5].split("\\");
					var epId = f[2];
					var picName = f[3];
					if(currentdate== startTime.substring(0,10)){
						html+="<tr><td>"+message[i]+"</td></tr>";
					}else{
						html+="<tr><td>"+message[i]+"</td><td><a href='#' onclick='showPic(&quot;"+time+"&quot;,&quot;"+epId+"&quot;,&quot;"+picName+"&quot;)'>点击显示图片</a></td></tr>";
					}
					
				}
				$("#logInfo").html(html);
				flag++;
			}else{
				$("#logInfo").html("无匹配数据！");
			}
			
 		}
	});*/
}

/*document.getElementById("logDiv").onscroll = function() {
    var $this =$(this),
    viewH =$(this).height(),//可见高度
    contentH =$(this).get(0).scrollHeight,//内容高度
    scrollTop =$(this).scrollTop();//滚动高度
    console.log(1);
    if (contentH - viewH - scrollTop <= 10) {
        //滚动条滚到最底部
    	var html="";
    	var len=flag*100;;
    	if((flag*100)>message.length){
    		len=message.length-1;
    	}
    	var startTime = $('#starttime').val();
		for(var i=(flag-1)*100;i<len;i++){
			var m = message[i].split("/");
			var time = m[0].substring(0,10);
			var f = m[5].split("\\");
			var epId = f[2];
			var picName = f[3];
			
			if(currentdate== startTime.substring(0,10)){
				html+="<tr><td>"+message[i]+"</td></tr>";
			}else{
				html+="<tr><td>"+message[i]+"</td><td><a href='#' onclick='showPic(&quot;"+time+"&quot;,&quot;"+epId+"&quot;,&quot;"+picName+"&quot;)'>点击显示图片</a></td></tr>";
			}
		}
		$("#logInfo").append(html);
		flag++;
    }
};*/

function reset() {
	var startdate=new Date();
	startdate.setHours(0,0,0,0);
	laydate.render({
		elem: '#starttime',
		type: 'datetime',
		value: startdate
	});
	laydate.render({
		elem: '#endtime',
		type: 'time',
		value: new Date()
	});
	$('#hwId').val("");
	$('#epid').val("");
	$('#name').val("");
	$('#camName').val("");
}
function reLoad() {
	$('#exampleTable').bootstrapTable('refresh',{pageNumber:1});
	// $('#exampleTable').bootstrapTable('selectPage', 1);
}
function showPic(time,epId,picName){
	var date = new Date(time);
	var dateStr = date.getFullYear() + "-"  + parseInt(date.getMonth()+1)  + "-"  + date.getDate();
	picName=picName.replace(/\+/g, "%2B");
	picName=picName.replace(/%/g, "%25");
	picName=picName.replace(/&/g, "%26");
	layer.open({
		type:2,
		title:"显示图片",
		area : [ '650px', '450px' ],
		content:"/system/alarm/showPic?dateStr=" + dateStr + "&camName=" + epId + "&imgName=" + picName
	})
}

var openHighway = function(){
	layer.open({
		type:2,
		title:"选择公路",
		area : [ '300px', '450px' ],
		content:"/system/highway/treeView"
	})
}
var hid=null;
function loadHy( node ){
	var	hwId = node.id;
	var name = node.text;
	$("#hwId").val(hwId);
	$("#name").val(name);
	$("#epid").val("");
	$("#camName").val("");
	hid = hwId;
}
function loadCamera( epId,equName){
	$("#epid").val(epId);
	$("#camName").val(equName);
}
function exportExcel(){
	var startTime=$('#starttime').val();
	var endTime=$('#starttime').val().substring(0,11)+$('#endtime').val();
	var hwId=$('#hwId').val();
	var equcode=$('#epid').val();
	var hrefurl=prefix+"/export?startTime="+startTime+"&endTime="+endTime+"&hwId="+hwId+"&equcode="+equcode;
	window.location.href=hrefurl;
}


var openCamera = function(){
	layer.open({
		type:2,
		title:"选择摄像头",
		area : [ '400px', '550px' ],
		content:"/system/alarm/equipment/"+ hid
	})
}