var prefix = "/fog/alarmRoad";
$(function() {
    let startTime = "";
    if($('#startTime_').val() != ""){
        startTime = $('#startTime_').val().substr(0,10) + " 00:00:00";
    }
    $('#startTime').val(startTime);
    $('#endTime').val($('#endTime_').val());
    load();
});

function load() {
    $('#exampleTable').bootstrapTable(
        {
            method : 'get', // 服务器数据的请求方式 get or post
            url : prefix + "/roadControlList", // 服务器数据的加载地址
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
            sidePagination : "client", // 设置在哪里进行分页，可选值为"client" 或者 "server"
            queryParams : function(params) {
                return {
                    //说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
                    limit: params.limit,
                    offset:params.offset,
                    roadAlarmId:$('#roadAlarmId_').val(),
                    startTime:$('#startTime').val(),
                    endTime:$('#endTime').val(),
                    hwId:$('#hwId').val()
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
                {field : 'recordeid', title : '收费站管制ID', visible:false},
                {field : 'io', title : '出入口', visible:false},
                {field : 'tsnum', title : '收费站编号', visible:false},
                {field : 'tsname', title : '收费站名称'},
                {field : 'roadnum', title : '高速编号'},
                {field : 'roadname', title : '高速名称'},
                {field : 'direction', title : '收费站方向'},
                {field : 'lon', title : '经度',visible:false},
                {field : 'lat', title : '纬度',visible:false},
                {field : 'closereason', title : '关闭原因',visible:false},
                {field : 'closereasontext', title : '关闭描述'},
                {field : 'startDate', title : '开始时间', width:'150px'},
                {field : 'endDate', title : '结束时间', width:'150px'},
                {field : 'description', title : '描述',visible:false},
                {title : '操作', field : 'id', align : 'center', width:'100px',
                    formatter : function(value, row, index) {
                        var e = ``;
                        if(row.isBind == "0"){
                            e = `<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="绑定" onclick="bind(this)">` +
                                `<i class="glyphicon glyphicon-plus"></i>` +
                                `</a> `;
                        } else {
                            e = `<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="解绑" onclick="bind(this)">` +
                                `<i class="glyphicon glyphicon-minus"></i>` +
                                `</a> `;
                        }
                        return e;
                    }
                }
            ],
            onClickRow:function(row,element){

            }
        }
    );
}
function reLoad() {
    $('#exampleTable').bootstrapTable('refresh');
}
function bind($this){
    //$this.firstElementChild
    //$this.parentNode.parentNode.childNodes
    //$this.parentNode.parentNode.parentNode.childNodes[0].getAttribute("data-index")
    let data = $('#exampleTable').bootstrapTable("getData");
    let index = parseInt($this.parentNode.parentNode.getAttribute("data-index"));
    let row = data[index];
    row.roadAlarmId = $('#roadAlarmId_').val();
    if($this.firstElementChild.getAttribute("class") == "glyphicon glyphicon-plus"){
        $.post(prefix + "/bindSave", row, (rs) => {
            if(rs.code == 0){
                let sfz = $this.parentNode.parentNode.childNodes[1].innerText;
                let fx = $this.parentNode.parentNode.childNodes[4].innerText;
                layer.msg(sfz+"【"+fx+"方向】"+"绑定成功");
                $this.setAttribute("title","解绑");
                $this.firstElementChild.setAttribute("class","glyphicon glyphicon-minus");
            }
        });
    } else {
        $.post(prefix + "/bindCancel", row, (rs) => {
            if(rs.code == 0){
                let sfz = $this.parentNode.parentNode.childNodes[1].innerText;
                let fx = $this.parentNode.parentNode.childNodes[4].innerText;
                layer.msg(sfz+"【"+fx+"方向】"+"已解绑");
                $this.setAttribute("title","绑定");
                $this.firstElementChild.setAttribute("class","glyphicon glyphicon-plus");
            }
        });
    }
}

