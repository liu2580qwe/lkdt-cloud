
let prefix = "/fog/artificalModiLog"
$(function() {

	let currDate = new Date();
	let startDate = new Date(currDate.setDate(currDate.getDate() - 30)).Format("yyyy-MM-dd");
	let endDate = new Date(currDate.setDate(currDate.getDate() + 31)).Format("yyyy-MM-dd");
	$('#selectTime').daterangepicker({
		"showDropdowns": true,
		"showWeekNumbers": true,
		"timePicker": true,
		"timePicker24Hour": true,
		"locale": {
			"direction": "ltr",
			"format": "YYYY-MM-DD HH:mm",
			"separator": " - ",
			"applyLabel": "确定",
			"cancelLabel": "取消",
			"fromLabel": "From",
			"toLabel": "To",
			"customRangeLabel": "Custom",
			"daysOfWeek": [
				"日",
				"一",
				"二",
				"三",
				"四",
				"五",
				"六"
			],
			"monthNames": [
				"一月",
				"二月",
				"三月",
				"四月",
				"五月",
				"六月",
				"七月",
				"八月",
				"九月",
				"十月",
				"十一月",
				"十二月"
			],
			"firstDay": 1
		},
		"startDate": startDate,
		"endDate": endDate
	}, function(start, end, label) {
		console.log("New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')");
	});

	load();
});

function load() {
	$('#exampleTable')
		.bootstrapTable(
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
						startTime:$('#selectTime').val().split(" - ")[0],
						endTime:$('#selectTime').val().split(" - ")[1],
					   	logType:$('#logType').val(),
						exceptionType:$('#exceptionType').val(),
						sort:'create_time',
						order:'desc'
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
					{field : 'logId', title : '主键', visible:false},
					{field : 'logType', title : '日志类型',
						formatter : function(value, row, index) {
							if(value === "1"){
								return "摄像头异常设置";
							} else if(value === "2"){
								return "能见度设置";
							}
							return value;
						}
					},
					{field : 'exceptionType', title : '摄像头异常类型',
						formatter : function(value, row, index) {
							if(value === "2"){
								return "已处理但未确认";
							} else if(value === "9"){
								return "停用";
							} else if(value === "3"){
								return "画面模糊";
							} else if(value === "4"){
								return "信号异常";
							} else if(value === "5"){
								return "摄像头污损、遮盖";
							} else if(value === "7"){
								return "位置偏移";
							} else if(value === "8"){
								return "夜间不合规";
							} else if(value === "6"){
								return "其它";
							}
							return value;
						}
					},
					{field : 'artificialAlarmDistanceInit', title : '可见距离原始值'},
					{field : 'artificialAlarmDistance', title : '可见距离设置'},
					{field : 'artificialAlarmImgUrl', title : '图片地址'},
					{field : 'createTime', title : '操作时间'},
					{field : 'createUser', title : '操作用户'},
					{field : 'craateUserName', title : '用户名称'},
					{title : '操作',field : 'id',align : 'center',
						formatter : function(value, row, index) {
							let e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
									+ row.logId
									+ '\')"><i class="fa fa-edit"></i></a> ';
							let d = '<a class="btn btn-warning btn-sm '+s_remove_h+'" href="#" title="删除"  mce_href="#" onclick="remove(\''
									+ row.logId
									+ '\')"><i class="fa fa-remove"></i></a> ';
							let f = '<a class="btn btn-success btn-sm" href="#" title="备用"  mce_href="#" onclick="resetPwd(\''
									+ row.logId
									+ '\')"><i class="fa fa-key"></i></a> ';
							return e + d ;
						}
					}
				]
			});
}
function reLoad() {
	//$('#exampleTable').bootstrapTable('refresh');
	$('#exampleTable').bootstrapTable('selectPage', 1);

}
function add() {
	layer.open({
		type : 2,
		title : '增加',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/add' // iframe的url
	});
}
function edit(id) {
	layer.open({
		type : 2,
		title : '编辑',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/edit/' + id // iframe的url
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
				'logId' : id
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

function resetPwd(id) {
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
			ids[i] = row['logId'];
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