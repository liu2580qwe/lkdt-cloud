
var prefix = "/system/alarm"
$(function() {
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
								starttime:$('#starttime').val(),
								endtime:$('#endtime').val(),
								alarmstatus:$('#alarmstatus').val(),
								level:$('#level').val(),
								hwId:$('#hwId').val(),
								equcode:$('#camcode').val(),
								type:1,
								iseffective:$('#iseffective').val()
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
//																{
//									field : 'alarmId', 
//									title : '主键' 
//								},
																{
									field : 'equName', 
									title : '摄像头名称' 
								},
																{
									field : 'iseffective', 
									title : '是否有效' ,
									formatter : function(value, row, index) {
										if (value == '1') {
											return '是';
										} else if (value == '0') {
											return '否';
										}
									}
								},
																{
									field : 'confirmor', 
									title : '确认人' 
								},
																{
									field : 'confirmtime', 
									title : '确认时间' 
								},
																{
									field : 'distance', 
									title : '可见距离' 
								},
								{
									field : 'fogType', 
									title : '雾霾/团雾' ,
									formatter : function(value,row,index){
										if(row.fogType==1){
											return "团雾";
										}else if(row.fogType==2){
											return "雾霾";
										}
									}
								},
																{
									field : 'level', 
									title : '雾霾等级' ,
									formatter : function(value, row, index) {
										if(row.level==3){
											return"特浓雾";
						  				}else if(row.level==2){
						  					return"浓雾";
						  				}else if(row.level==1){
						  					return"大雾";
						  				}
									}
								},
																{
									field : 'begintime', 
									title : '开始时间' 
								},
																{
									field : 'endtime', 
									title : '消散时间' 
								},
																{
									field : 'hwName', 
									title : '所属路段' 
								},
																{
									title : '操作',
									field : 'id',
									align : 'center',
									width:'100px',
									formatter : function(value, row, index) {
										/*var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
												+ row.alarmId
												+ '\')"><i class="fa fa-edit"></i></a> ';*/
										var d = '<a class="btn btn-warning btn-sm '+s_remove_h+'" href="#" title="删除"  mce_href="#" onclick="remove(\''
												+ row.alarmId
												+ '\')"><i class="fa fa-remove"></i></a> ';
										var f = '<a class="btn btn-success btn-sm" href="#" title="查看"   mce_href="#" onclick="confirm(\'' + row.alarmId + '\')"><i class="fa fa-eye"></i></a> ';
										return  d + f ;
									}
								} ]
					});
}
function reset() {
	$('#starttime').val("");
	$('#endtime').val("");
	$('#alarmstatus').val("");
	$('#level').val("");
	$('#hwId').val("");
	$('#camcode').val("");
	$('#iseffective').val("");
	$('#name').val("");
}
function reLoad() {
//	$('#exampleTable').bootstrapTable('refresh');
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
function confirm(id) {
	layer.open({
		type : 2,
		title : '详情',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '470px' ],
		content : prefix + '/confirmByAlarmId/' + id 
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
				'alarmId' : id
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
	var rows = $('#exampleTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
	if (rows.length == 0) {
		layer.msg("请选择要删除的数据");
		return;
	}
	layer.confirm("确认要删除选中的'" + rows.length + "'条数据吗?", {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		var ids = new Array();
		// 遍历所有选择的行数据，取每条数据对应的ID
		$.each(rows, function(i, row) {
			ids[i] = row['alarmId'];
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
var openHighway = function(){
	layer.open({
		type:2,
		title:"选择公路",
		area : [ '300px', '450px' ],
		content:"/system/highway/treeView"
	})
}
function loadHy( node ){
	var	hwId = node.id;
	var name = node.text;
	$("#hwId").val(hwId);
	$("#name").val(name);
}