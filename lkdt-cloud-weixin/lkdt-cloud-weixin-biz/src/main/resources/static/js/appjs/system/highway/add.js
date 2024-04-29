
var deptIds;
$().ready(function() {
	getDeptTreeData();
	validateRule();
});

$.validator.setDefaults({
	submitHandler : function() {
		getAllSelectNodes();
		save();
	}
});

function getAllSelectNodes() {
	var ref = $('#deptTree').jstree(true); // 获得整个树
	
	deptIds = ref.get_selected(); // 获得所有选中节点的，返回值为数组
	// console.log(deptIds);
	// $('#deptId').val(deptIds[0]);
	//
	// $("#deptTree").find(".jstree-undetermined").each(function(i, element) {
	// 	deptIds.push($(element).closest('.jstree-node').attr("id"));
	// });
}

function save() {
	$('#menuIds').val(deptIds);
	var role =  $('#signupForm').serialize();
	$.ajax({
		cache : true,
		type : "POST",
		url : "/system/highway/save",
		data :role,// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				parent.reLoad();
				var index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
				parent.layer.close(index);

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
				required : icon + "请输入姓名"
			}
		}
	})
}



function getDeptTreeData() {
	$.ajax({
		type : "GET",
		url : "/system/sysDept/tree",
		success : function(deptTree) {
			loadDeptTree(deptTree);
		}
	});
}

function loadDeptTree(deptTree) {
	$('#deptTree').jstree({
		'core' : {
			'data' : deptTree
		},
		"checkbox" : {
			"three_state" : false,
		},
		"plugins" : [ "wholerow", "checkbox" ]
	});
	//$('#menuTree').jstree("open_all");

}


var openArea = function(){
	layer.open({
		type:2,
		title:"选择区域",
		area : [ '300px', '450px' ],
		content:"/system/area/treeView"
	})
}

function loadArea( areaid,areaName,idPath){
	$("#areaid").val(areaid);
	$("#areaName").val(areaName);
	$("#areapath").val(idPath);
	
}