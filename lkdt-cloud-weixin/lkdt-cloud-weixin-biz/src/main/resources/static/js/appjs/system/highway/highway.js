
var prefix = "/system/highway"
$(function() {
	load();
});

function load() {
	$('#exampleTable')
			.bootstrapTreeTable(
					{
						id : 'hwId',
						code : 'hwId',
		                parentCode : 'parentId',
		                type : "GET", // 请求数据的ajax类型
						url : prefix + '/list', // 请求数据的ajax的url
						ajaxParams : {"name":$('#searchName').val()}, // 请求数据的ajax的data属性
						expandColumn : '1', // 在哪一列上面显示展开按钮
						striped : true, // 是否各行渐变色
						bordered : true, // 是否显示边框
						expandAll : false, // 是否全部展开
//						queryParams : function(params) {
//							return {
//								//说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
////								limit: params.limit,
////								offset:params.offset
//					            name:$('#searchName').val(),
//					           // username:$('#searchName').val()
//							};
//						},
						// //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
						// queryParamsType = 'limit' ,返回参数必须包含
						// limit, offset, search, sort, order 否则, 需要包含:
						// pageSize, pageNumber, searchText, sortName,
						// sortOrder.
						// 返回false将会终止请求
						columns : [
								{
									title : '编号',
									field : 'hwId',
									visible : false,
									align : 'center',
									valign : 'center',
									width : '50px',
									checkbox : true
								},
//																{
//									field : 'parentId', 
//									title : '路段父ID' 
//								},
																{
									field : 'name', 
									title : '编码' 
								},
																{
									field : 'detail', 
									title : '路段详情' 
								},
																{
									field : 'waytype', 
									title : '类型',
									formatter : function(value, row, index) {
										console.log(value.waytype);
										if (value.waytype == '1') {
											return '国家高速';
										} else if (value.waytype == '2') {
											return '省级高速';
										}else if(value.waytype=='3'){
											return '市级高速';
										}
									}
								},
																{
									field : 'isdanger', 
									title : '是否高危路段' ,
									formatter : function(value, row, index) {
										if (value.isdanger == '1') {
											return '是';
										} else if (value.isdanger == '0') {
											return '否';
										}
									}
								},
																{
									title : '操作',
									field : 'id',
									align : 'center',
									formatter : function(item, index) {
										var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
												+ item.hwId
												+ '\')"><i class="fa fa-edit"></i></a> ';
										var a = '<a class="btn btn-primary btn-sm ' + s_add_h + '" href="#" title="增加下級"  mce_href="#" onclick="add(\''
										+ item.hwId
										+ '\')"><i class="fa fa-plus"></i></a> ';
										var d = '<a class="btn btn-warning btn-sm '+s_remove_h+'" href="#" title="删除"  mce_href="#" onclick="remove(\''
												+ item.hwId
												+ '\')"><i class="fa fa-remove"></i></a> ';
										var f = '<a class="btn btn-success btn-sm" href="#" title="备用"  mce_href="#" onclick="resetPwd(\''
												+ item.hwId
												+ '\')"><i class="fa fa-key"></i></a> ';
										return e + a + d ;
									}
								} ]
					});
}
function reLoad() {
	load();
}
function add(pId) {
	layer.open({
		type : 2,
		title : '增加',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/add/'+ pId // iframe的url
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
				'hwId' : id
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
			ids[i] = row['hwId'];
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