
$().ready(function() {
	validateRule();

});

let he = HE.getEditor('reportContent', {
	uploadPhoto : false,
	lang : 'zh-jian',
	skin : 'HandyEditor',
	item : ['bold','italic','strike','underline','fontSize','fontName','paragraph','color','backColor','|',
		'center','left','right','full','indent','outdent','|',
		'link','unlink','textBlock','code','selectAll','removeFormat','trash','|',
		'subscript','superscript','horizontal','orderedList','unorderedList','|','undo','redo']
});

$.validator.setDefaults({
	submitHandler : function() {
		save();
	}
});

function save() {
	$.ajax({
		cache : true,
		type : "POST",
		url : "/fog/dayReport/save",
		data : {reportDate: $("#reportDate").val(), reportUser: $("#reportUser").val(), reportContent: he.getHtml(), remark: $("#remark").val()},// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				parent.reLoad();
				let index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
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

let startdate = new Date();
startdate.setHours(0,0,0,0);
laydate.render({
	elem: '#reportDate',
	type: 'date',
	value: startdate
});


