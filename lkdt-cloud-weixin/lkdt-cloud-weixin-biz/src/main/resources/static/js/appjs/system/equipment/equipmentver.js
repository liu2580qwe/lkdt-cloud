
var prefix = "/system/equipment"
$(function() {
	var deptId = '';
	getTreeData();
//	load(deptId);
	load();
});
var hId="";
function load() {
	$('#exampleTable')
			.bootstrapTable(
					{
						method : 'get', // 服务器数据的请求方式 get or post
						url : prefix + "/verlist", // 服务器数据的加载地址
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
								equName:$('#searchName').val(),
								hwId : hId,
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
//																{
//									field : 'epId', 
//									title : '序号' 
//								},
																{
									field : 'equCode', 
									title : '设备编码' 
								},
																{
									field : 'equName', 
									title : '设备名称' 
								},
								{
									field : 'updateTime', 
									title : '最后校验时间' 
								},
																{
									field : 'updator', 
									title : '最后校验人' 
								},
								{
									field : 'updateTime', 
									title : '已过时间' ,
									formatter : function(value, row, index) {
										if(!value){
											return '';
										}
										var updatetime=new Date(value);
										var ygsj=new Date()-updatetime;
										return Math.floor(ygsj / (24 * 3600 * 1000))+'天';
									}
								},
								{
									title : '操作',
									field : 'id',
									align : 'center',
									width:  "200px",
									formatter : function(value, row, index) {
										var e = '<a class="btn btn-primary btn-sm '+'" mce_href="/fog/vef/verification/' + row.epId+'" onclick="menuItem(this)" data-index="9999" href="#" title="执行校验(昼)" ><i class="fa fa-check-circle-o"></i></a> ';
										var d = '<a class="btn btn-warning btn-sm '+'" mce_href="/fog/vef/nightVef2/' + row.epId+'" onclick="menuItem(this)" data-index="9999" title="执行校验(夜)"  href="#" ><i class="fa fa-check-circle" style="color: #000000;"></i></a> ';
										var h = '<a class="btn btn-primary btn-sm '+'" mce_href="/fog/vef/fogVef/' + row.epId+'" onclick="menuItem(this)" data-index="9999" href="#" title="执行校验(雾)" ><i class="fa fa-bars"></i></a> ';
										var g = '<a class="btn btn-warning btn-sm '+'" mce_href="/fog/vef/nightVef/' + row.epId+'" onclick="lookup(\''+row.epId+'\')" data-index="9999" title="监控"  href="#" ><i class="fa fa-video-camera" style="color: #000000;"></i></a> ';
										var f = '<a class="btn btn-success btn-sm " mce_href="/fog/verificationlog/' + row.epId+'/'+row.equName+'" onclick="menuItem(this)" data-index="9999" title="校验记录"  href="#" ><i class="fa fa-bar-chart" style=""></i></a> ';
										if(row.y1&&row.y2&&row.y3&&row.y4){
											return e + d + h + g + f ;
										}else{
											return e + g + f ;
										}
										
									}
								} ]
					});
}
function reLoad() {
//	$('#exampleTable').bootstrapTable('refresh');
	$('#exampleTable').bootstrapTable('selectPage', 1);
}
function menuItem(obj){
	parent.menuItemeq(obj);
}

function lookup(id) {
	layer.open({
		type : 2,
		title : '查看',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '60%', '80%' ],
		content :'/system/equipment/lookup/' + id // iframe的url
	});
}
function verification(id) {
	layer.open({
		type : 1,
		title : '执行校验(昼)',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : '/fog/vef' + '/verification/' + id // iframe的url
	});
}
function nightVef(id) {
	layer.open({
		type : 1,
		title : '执行校验(波谷)',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : '/fog/vef' + '/nightVef/' + id // iframe的url
	});
}
function nightVef2(id) {
	layer.open({
		type : 1,
		title : '执行校验(夜)',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : '/fog/vef' + '/nightVef2/' + id // iframe的url
	});
}
function fogVef(id) {
	layer.open({
		type : 1,
		title : '执行校验(雾)',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : '/fog/vef' + '/fogVef/' + id // iframe的url
	});
}
function add() {
	if(hId==""||hId==null){
		hId="-1";
	}
	layer.open({
		type : 2,
		title : '增加',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/add/'+hId // iframe的url
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
				'epId' : id
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
			ids[i] = row['epId'];
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
$('#jstree').on("changed.jstree", function(e, data) {
	console.log(data);
	if (data.selected == -1) {
		var opt = {
			query : {
				hwId : '',
			}
		}
		hId='';
		$('#exampleTable').bootstrapTable('refresh', opt);
	} else {
		var opt = {
			query : {
				hwId : data.selected[0],
			}
		}
		hId=data.selected[0];
		$('#exampleTable').bootstrapTable('refresh',opt);
	}

});
/**
 * 返回指定format的string
 * format eg:'yyyy-MM-dd hh:mm:ss'
 **/
function formatDate(date, fmt) {
	  var currentDate = new Date(date);
	  var o = {
	    "M+": currentDate.getMonth() + 1, //月份
	    "d+": currentDate.getDate(), //日
	    "h+": currentDate.getHours(), //小时
	    "m+": currentDate.getMinutes(), //分
	    "s+": currentDate.getSeconds(), //秒
	    "q+": Math.floor((currentDate.getMonth() + 3) / 3), //季度
	    "S": currentDate.getMilliseconds() //毫秒
	  };
	  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (currentDate.getFullYear() + "").substr(4 - RegExp.$1.length));
	  for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	  return fmt;
	}
