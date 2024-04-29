let prefix = "/system/alarmNotice";
$(function() {
	let startdate=new Date();
	startdate.setHours(20,0,0,0);
	startdate.setTime(startdate.getTime()-1000*60*60*24);
	laydate.render({
		elem: '#starttime',
		type: 'datetime',
		value: startdate
	});
	laydate.render({
		elem: '#endtime',
		type: 'datetime',
		value: new Date()
	});
	setTimeout(load,300);
});

function load() {
	$('#exampleTable').bootstrapTable(
		{
			method:'get', // 服务器数据的请求方式 get or post
			url:prefix + "/list", // 服务器数据的加载地址
		//	showRefresh:true,
		//	showToggle:true,
		//	showColumns:true,
			iconSize:'outline',
			toolbar:'#exampleToolbar',
			striped:true, // 设置为true会有隔行变色效果
			dataType:"json", // 服务器返回的数据类型
			pagination:true, // 设置为true会在底部显示分页条
			// queryParamsType:"limit",
			// //设置为limit则会发送符合RESTFull格式的参数
			singleSelect:false, // 设置为true将禁止多选
			// contentType:"application/x-www-form-urlencoded",
			// //发送到服务器的数据编码类型
			pageSize:10, // 如果设置了分页，每页数据条数
			pageNumber:1, // 如果设置了分布，首页页码
			//search:true, // 是否显示搜索框
			showColumns:false, // 是否显示内容下拉框（选择显示的列）
			sidePagination:"server", // 设置在哪里进行分页，可选值为"client" 或者 "server"
			queryParams:function(params) {
				return {
					//说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
					limit: params.limit,
					offset:params.offset,
					startsendtime:$('#starttime').val(),
					endsendtime:$('#endtime').val(),
					iseffective:$('#iseffective').val()
				};
			},
			// //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
			// queryParamsType = 'limit' ,返回参数必须包含
			// limit, offset, search, sort, order 否则, 需要包含:
			// pageSize, pageNumber, searchText, sortName,
			// sortOrder.
			// 返回false将会终止请求
			columns:[
				// {checkbox:true,width:"5%"},
				{field:'noticeId', title:'',visible: false},
				{field:'alarmId',title:'',visible: false},
				{field:'sender',title:'发送人',width:"5%",visible: false,},
				{field:'sendtext',title:'发送文本',width:"30%",visible: false,
					formatter:function(value, row, index){
						let obj = eval("("+value+")");
						let odate = new Date();
						odate.setTime(obj['imgtime']);
						let imgtime = odate.Format("yyyy-MM-dd");
						let imgpath = obj['imgpath'];
						return "<input style='width: 350px;' value='"+value+"' type='text'/><br>" +
							"设备ID:<label>"+obj['epId']+"</label>，图片时间:<label>"+imgtime+"</label>，可见距离<label>"+obj['distance']+"</label><br>" +
							"<img class='img_equip_list' style='height: 75px;cursor: pointer;' src='"+imgpath+"'></img>";
					}
				},
				{field:'epId',title:'设备ID',width:"5%",
					formatter:function(value, row, index){
						let obj = eval("("+row.sendtext+")");
						return obj['epId'];
					}
				},
				{field:'imgtime',title:'图片时间',width:"5%",
					formatter:function(value, row, index){
						let obj = eval("("+row.sendtext+")");
						let odate = new Date();
						odate.setTime(obj['imgtime']);
						let imgtime = odate.Format("yyyy-MM-dd HH:mm:ss");
						return imgtime;
					}
				},
				{field:'imgpath',title:'图片预览',width:"5%",
					formatter:function(value, row, index){
						let obj = eval("("+row.sendtext+")");
						let odate = new Date();
						odate.setTime(obj['imgtime']);
						let imgtime = odate.Format("yyyy-M-d");
						let imgpath = obj['imgpath'];
						return "<img class='img_equip_list' style='height: 50px;cursor: pointer;' src='"+imgpath+"'></img>";
					}
				},
				{field:'calx',title:'计算数值',width:"5%",
					formatter:function(value, row, index){
						let obj = eval("("+row.sendtext+")");
						return obj['calx'];
					}
				},
				{field:'distance',title:'确认数值',width:"5%"},
				{field:'handler',title:'操作人',width:"5%",visible: false},
				{field:'sendtime',title:'发送时间',width:"5%"},
				{field:'handletime',title:'操作时间',width:"5%"},
				{field:'handleusetime',title:'处理用时',width:"5%",
					formatter:function(value, row, index){
						try{
							if(row.sendtime && row.handletime){
								let sendtime = convertDateFromStringyyyyMMddhhmmss(row.sendtime);
								let handletime = convertDateFromStringyyyyMMddhhmmss(row.handletime);
								let time = handletime.getTime() - sendtime.getTime();
								if(time >= 0){
									return MillisecondToDate(time);
								}
							}
						} catch (e) {
							console.log(e);
						}
						return "";
					}},// 1:发生告警 2:告警等级提高 4:告警解除
				{field:'type',title:'告警类型',width:"5%",
					formatter:function(value, row, index){
						if(value == 1){
							return "发生告警";
						} else if(value == 2){
							return "告警等级提高";
						} else if(value == 4){
							return "告警解除";
						}
						return value;
					}},// 1:发生告警 2:告警等级提高 4:告警解除
				{field:'iseffective',title:'有效类型',width:"5%",
					formatter:function(value, row, index){
						if(value == 1){
							return "人工确认";
						} else if(value == 9){
							return "人工未确认";
						}
						return value;
					}},//1:已确认有效 0:已确认无效 9:未确认
				// {title:'操作',field:'id',align:'center',width:"10%",
				// 	formatter:function(value, row, index) {
				// 		var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
				// 				+ row.noticeId + '\')"><i class="fa fa-edit"></i></a> ';
				// 		var d = '<a class="btn btn-warning btn-sm '+s_remove_h+'" href="#" title="删除"  mce_href="#" onclick="remove(\''
				// 				+ row.noticeId + '\')"><i class="fa fa-remove"></i></a> ';
				// 		var f = '<a class="btn btn-success btn-sm" href="#" title="备用"  mce_href="#" onclick="resetPwd(\''
				// 				+ row.noticeId + '\')"><i class="fa fa-key"></i></a> ';
				// 		return e + d ;
				// 	}
				// }
			],
			onPostBody: function(){
				$($('.img_equip_list')).click(function(){
					imgShow("#outerdiv", "#innerdiv", "#bigimg", $(this));
				});
			}
		});
}

function imgShow(outerdiv, innerdiv, bigimg, _this){
	var src = _this.attr("src");//获取当前点击的pimg元素中的src属性
	$(bigimg).attr("src", src);//设置#bigimg元素的src属性

	/*获取当前点击图片的真实大小，并显示弹出层及大图*/
	$("<img/>").attr("src", src).load(function(){
		var windowW = $(window).width();//获取当前窗口宽度
		var windowH = $(window).height();//获取当前窗口高度
		var realWidth = this.width;//获取图片真实宽度
		var realHeight = this.height;//获取图片真实高度
		var imgWidth, imgHeight;
		var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放

		if(realHeight>windowH*scale) {//判断图片高度
			imgHeight = windowH*scale;//如大于窗口高度，图片高度进行缩放
			imgWidth = imgHeight/realHeight*realWidth;//等比例缩放宽度
			if(imgWidth>windowW*scale) {//如宽度扔大于窗口宽度
				imgWidth = windowW*scale;//再对宽度进行缩放
			}
		} else if(realWidth>windowW*scale) {//如图片高度合适，判断图片宽度
			imgWidth = windowW*scale;//如大于窗口宽度，图片宽度进行缩放
			imgHeight = imgWidth/realWidth*realHeight;//等比例缩放高度
		} else {//如果图片真实高度和宽度都符合要求，高宽不变
			imgWidth = realWidth;
			imgHeight = realHeight;
		}
		$(bigimg).css("width",imgWidth);//以最终的宽度对图片缩放

		var w = (windowW-imgWidth)/2;//计算图片与窗口左边距
		var h = (windowH-imgHeight)/2;//计算图片与窗口上边距
		$(innerdiv).css({"top":h, "left":w});//设置#innerdiv的top和left属性
		$(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg
	});

	$(outerdiv).click(function(){//再次点击淡出消失弹出层
		$(this).fadeOut("fast");
	});
}

function find(value){
	alert(value);
}
function reLoad() {
//	$('#exampleTable').bootstrapTable('refresh');
	$('#exampleTable').bootstrapTable('selectPage', 1);
}
function add() {
	layer.open({
		type:2,
		title:'增加',
		maxmin:true,
		shadeClose:false, // 点击遮罩关闭层
		area:[ '800px', '520px' ],
		content:prefix + '/add' // iframe的url
	});
}
function edit(id) {
	layer.open({
		type:2,
		title:'编辑',
		maxmin:true,
		shadeClose:false, // 点击遮罩关闭层
		area:[ '800px', '520px' ],
		content:prefix + '/edit/' + id // iframe的url
	});
}
function remove(id) {
	layer.confirm('确定要删除选中的记录？', {
		btn:[ '确定', '取消' ]
	}, function() {
		$.ajax({
			url:prefix+"/remove",
			type:"post",
			data:{
				'noticeId':id
			},
			success:function(r) {
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

function reset(id) {
	let startdate=new Date();
	startdate.setHours(20,0,0,0);
	startdate.setTime(startdate.getTime()-1000*60*60*24);
	laydate.render({
		elem: '#starttime',
		type: 'datetime',
		value: startdate
	});
	laydate.render({
		elem: '#endtime',
		type: 'datetime',
		value: new Date()
	});
	$('#iseffective').val("");
}
function batchRemove() {
	var rows = $('#exampleTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
	if (rows.length == 0) {
		layer.msg("请选择要删除的数据");
		return;
	}
	layer.confirm("确认要删除选中的'" + rows.length + "'条数据吗?", {
		btn:[ '确定', '取消' ]
	// 按钮
	}, function() {
		var ids = new Array();
		// 遍历所有选择的行数据，取每条数据对应的ID
		$.each(rows, function(i, row) {
			ids[i] = row['noticeId'];
		});
		$.ajax({
			type:'POST',
			data:{
				"ids":ids
			},
			url:prefix + '/batchRemove',
			success:function(r) {
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