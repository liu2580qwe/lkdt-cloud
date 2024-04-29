$().ready(function() {
	validateRule();
	if($('#clarity').val() == ""){
		clarity.setValue(15);
	} else {
		clarity.setValue($('#clarity').val());
	}
});

$.validator.setDefaults({
	submitHandler : function() {
		update();
	}
});



function update() {
	$.ajax({
		cache : true,
		type : "POST",
		url : "/system/equipment/update",
		data : $('#signupForm').serialize(),// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				parent.$('#exampleTable').bootstrapTable('refresh');
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
				required : icon + "请输入名字"
			}
		}
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

function loadHy( node ){
	var	hwId = node.id;
	var name = node.text;
	$("#hwId").val(hwId);
	$("#name").val(name);
}

let clarity;
//ip个数
clarity = $('.single-slider3').jRange({
	//滑块滑动时 触发的函数
	onstatechange: function () {
		$('.ipgs').val($('.single-slider3').val());
	},
	from: 10,//最小值
	to: 18,//最大值
	step: 0.02,//步进
	//这里改变值
	scale: [10,11,12,13,14,15,16,17,18],
	format: '%s',
	width: 250,//总宽度
	showLabels: true,
	showScale: true
});
$(".ipgs").blur(function () {
	clarity.setValue( $(this).val());//能绑定滑块的变化
});