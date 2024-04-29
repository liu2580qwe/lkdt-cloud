
var prefix = "/ppv/serviceAreaCamera"
	var selectArea='';
$(function() {
	var areaCode = '';
	getTreeData();
	load(areaCode);
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
								offset:params.offset
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
								{
									checkbox : true
								},
								{
									field : 'serviceareaName', 
									title : '服务区' 
								},
																{
									field : 'cameraId', 
									title : '摄像头编号' 
								},
																{
									field : 'cameraName', 
									title : '摄像头名称' 
								},
																/*{
									field : 'imgUrl', 
									title : '图片路径' ,
									width:  "100px",
								},
																{
									field : 'videoUrl', 
									title : '视频路径' 
								},*/
																{
									field : 'areaName', 
									title : '所在区域' 
								},
																{
									field : 'parkingRange', 
									title : '摄像头范围' 
								},
																{
									field : 'parkingType', 
									title : '摄像头类型' 
								},
																{
									title : '操作',
									field : 'id',
									align : 'center',
									width: '100px',
									formatter : function(value, row, index) {
										var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
												+ row.cameraId
												+ '\')"><i class="fa fa-edit"></i></a> ';
										var d = '<a class="btn btn-warning btn-sm '+s_remove_h+'" href="#" title="删除"  mce_href="#" onclick="remove(\''
												+ row.cameraId
												+ '\')"><i class="fa fa-remove"></i></a> ';
										var f = '<a class="btn btn-primary btn-sm '+s_remove_h+'" href="#" title="建模" mce_href="'+prefix+'/verification/' + row.areaCode+'/'+row.cameraId +'" onclick="menuItem(this)"><i class="fa fa-check-circle-o"></i></a> ';
										return e + d + f ;
									}
								} ]
					});
}
function reLoad() {
	$('#exampleTable').bootstrapTable('refresh');
}
function menuItem(obj){
	parent.menuItemeq(obj);
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
				'cameraId' : id
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
			ids[i] = row['cameraId'];
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
function addArea() {
	layer.open({
		type : 2,
		title : '增加',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : "/ppv/serviceArea" + '/add' // iframe的url
	});
}
function batchRemoveArea() {
	if (!selectArea) {
		layer.msg("请选择要删除的数据");
		return;
	}
	layer.confirm("确认要删除选中的数据吗?", {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		$.ajax({
			type : 'POST',
			data : {
				"areaCode" : selectArea
			},
			url : "/ppv/serviceArea" + '/remove',
			success : function(r) {
				if (r.code == 0) {
					layer.msg(r.msg);
					getTreeData();
				} else {
					layer.msg(r.msg);
				}
			}
		});
	}, function() {

	});
}
function getTreeData() {
	$.ajax({
		type : "GET",
		url : "/ppv/serviceArea/tree",
		success : function(tree) {
			console.log(tree);
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
	$('#jstree').jstree(true).settings.core.data = tree;
	$('#jstree').jstree().refresh();
	$('#jstree').jstree().open_all();
}
$('#jstree').on("changed.jstree", function(e, data) {
	if (data.selected == -1) {
		var opt = {
			query : {
				areaCode : '',
			}
		}
		selectArea='';
		$('#exampleTable').bootstrapTable('refresh', opt);
	} else {
		var opt = {
			query : {
				areaCode : data.selected[0],
			}
		}
		selectArea=data.selected[0];
		$('#exampleTable').bootstrapTable('refresh',opt);
	}

});