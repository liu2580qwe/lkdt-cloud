let prefix = "/fog/alarmRoad";
$(function() {
	load();
});

function load() {
	$('#exampleTable').bootstrapTable(
		{
			method : 'get', // 服务器数据的请求方式 get or post
			url : prefix + "/list", // 服务器数据的加载地址
		//	showRefresh : true,
		//	showToggle : true,
		//	showColumns : true,
			iconSize : 'outline',
			toolbar : '#exampleToolbar',
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
					starttime:$('#startTime').val(),
					endtime:$('#endTime').val(),
					hwIds:$('#hwIds').val(),
					sort:'starttime',
					order:'desc'
				   // name:$('#searchName').val(),
				   // username:$('#searchName').val()
				};
			},
			// //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
			// queryParamsType = 'limit' ,返回参数必须包含
			// limit, offset, search, sort, order 否则, 需要包含:
			// pageSize, pageNumber, searchText, sortName,
			// sortOrder.
			// 返回false将会终止请求
			columns : [
				{checkbox : true},
				{field : 'roadAlarmId', title : '主键', visible:false},
				{field : 'hwId', title : '路段ID'},
				{field : 'hwName', title : '路段名称'},
				{field : 'alarmLevel', title : '告警等级'},
				{field : 'mindistanceNow', title : '当前能见度'},
				{field : 'mindistanceHis', title : '历史最低能见度'},
				{field : 'starttime', title : '开始时间'},
				{field : 'endtime', title : '结束时间'},
				{field : 'imgpath', title : '图片地址'},
				{field : 'imgtime', title : '图片时间'},
				{field : 'roadAlarmType', title : '告警类型', align: 'center', width: '80px',
					formatter: function(value, row, index){
						let a = '';
						switch (value) {
							case "1":
								a = '<a class="btn btn-warning btn-sm" style="background-color: #ff2601" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">雾</a> ';
								break;
							case "2":
								a = '<a class="btn btn-warning btn-sm" style="background-color: #ff8004" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">团雾</a> ';
								break;
							case "3":
								a = '<a class="btn btn-warning btn-sm" style="background-color: #caa5ff" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">雨</a> ';
								break;
							case "4":
								a = '<a class="btn btn-warning btn-sm" style="background-color: #2e7bff" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">雪</a> ';
								break;
							case "5":
								a = '<a class="btn btn-warning btn-sm" style="background-color: #ffcd05" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">霾</a> ';
								break;
							default :
								a = '<a class="btn btn-sm" style="background-color: #e6e6ea;" href="#" title="告警类型" onclick="bindRoadAlarmType(\''
									+ row.roadAlarmId
									+ '\')">--</a> ';
								break;
						}
						return a;
					}
				},
				{title : '操作', field : 'id', align : 'center', width:'150px',
					formatter : function(value, row, index) {
						let e = '<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="绑定路段管制" onclick="bindRoadControl(\''
								+ row.roadAlarmId
								+ '\')"><i class="glyphicon glyphicon-road"></i></a> ';
						let d = '';
						if(row.bindNum > 0){
							d = '<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="查看" onclick="findBind(\''
								+ row.roadAlarmId
								+ '\')"><span class="badge">'+row.bindNum+'</span></a> ';
						} else {
							d = '<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="查看" onclick="findBind(\''
								+ row.roadAlarmId
								+ '\')"><i class="glyphicon glyphicon-eye-open"></i></a> ';
						}
						return e + d;
					}
				}
			]
		}
	);
}
function reLoad() {
//	$('#exampleTable').bootstrapTable('refresh');
	$('#exampleTable').bootstrapTable('selectPage', 1);
}

function bindRoadControl(id){
	layer.open({
		type : 2,
		title : '路段管制绑定',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '900px', '570px' ],
		content : prefix + '/bindRoadControl/' + id // iframe的url
	});
}

function bindRoadAlarmType(id){
	layer.open({
		type : 2,
		title : '路段告警类型绑定',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '500px', '270px' ],
		content : prefix + '/bindRoadAlarmType/' + id // iframe的url
	});
}

function findBind(id){
	layer.open({
		type : 2,
		title : '查看已绑定信息',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '900px', '570px' ],
		content : prefix + '/findBind/' + id // iframe的url
	});
}

function remove(id) {
	layer.confirm('确定要删除选中的记录？', {
		btn : [ '确定', '取消' ]
	}, function() {
		$.ajax({
			url : prefix+"/remove",
			type : "post",
			data : {
				'roadAlarmId' : id
			},
			success : function(r) {
				if (r.code==0) {
					layer.msg(r.msg);
					reLoad();
				}else{
					layer.msg(r.msg);
				}
			}
		});
	})
}

function batchRemove() {
	let rows = $('#exampleTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
	if (rows.length == 0) {
		layer.msg("请选择要删除的数据");
		return;
	}
	layer.confirm("确认要删除选中的'" + rows.length + "'条数据吗?", {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		let ids = new Array();
		// 遍历所有选择的行数据，取每条数据对应的ID
		$.each(rows, function(i, row) {
			ids[i] = row['roadAlarmId'];
		});
		$.ajax({
			type : 'POST',
			data : {
				"ids" : ids
			},
			url : prefix + '/batchRemove',
			success : function(r) {
				if (r.code == 0) {
					layer.msg(r.msg);
					reLoad();
				} else {
					layer.msg(r.msg);
				}
			}
		});
	}, function() {

	});
}

let openHighway = function(){
	layer.open({
		type:2,
		title:"选择公路",
		area : [ '300px', '450px' ],
		content:"/system/highway/treeViewCheckbox"
	})
}

function loadHy(nodeIds,nodeNames ){
	$("#hwIds").val(nodeIds);
	$("#hwNames").val(nodeNames);
//	console.log("nodeIds:"+nodeIds+";nodeNames:"+nodeNames);
}

function exportExcel(){
	let startTime = $('#startTime').val();
	let endTime = $('#endTime').val();
	let hwIds = $('#hwIds').val();
	let hwNames = $('#hwNames').val();
	let hrefurl = prefix+"/export?startTime="+startTime+"&endTime="+endTime+"&hwIds="+hwIds+"&hwNames="+hwNames;
	window.location.href=hrefurl;
}

