//定义全局变量函数
let uzStorage = function () {
    // let ls = window.sessionStorage;
    let ls = window.localStorage;
    return ls;
};
//定义全局变量u
let u = {};
//设置缓存
u.setStorage = function (key, value) {
    let v = value;
    if (typeof v == 'object') {
        v = JSON.stringify(v);
        v = 'obj-' + v;
    } else {
        v = 'str-' + v;
    }
    let ls = uzStorage();
    if (ls) {
        ls.setItem(key, v);
    }
};
//获取缓存
u.getStorage = function (key) {
    let ls = uzStorage();
    if (ls) {
        let v = ls.getItem(key);
        if (!v) {
            return;
        }
        if (v.indexOf('obj-') === 0) {
            v = v.slice(4);
            return JSON.parse(v);
        } else if (v.indexOf('str-') === 0) {
            return v.slice(4);
        }
    }
};

/**解析url路径,获取参数*/
function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
}

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

/**
 * 输入的时间格式为yyyy-MM-dd
 * @param dateString
 * @returns {Date}
 */
function convertDateFromStringyyyyMMdd(dateString) {
    if (dateString) {
        var date = new Date(dateString.replace(/-/,"/"))
        return date;
    }
}

/**
 * 输入的时间格式为yyyy-MM-dd hh:mm:ss
 * @param dateString
 * @returns {Date}
 */
function convertDateFromStringyyyyMMddhhmmss(dateString) {
    if (dateString) {
        var arr1 = dateString.split(" ");
        var sdate = arr1[0].split('-');
        var sdate2 = arr1[1].split(':');
        var date = new Date(sdate[0], sdate[1]-1, sdate[2], sdate2[0], sdate2[1], sdate2[2]);
        return date;
    }
}

/**
 * 将毫秒秒转化为xx小时xx分钟xx秒
 * @param msd
 * @returns {number}
 * @constructor
 */
function MillisecondToDate(msd) {
    var time = parseFloat(msd) / 1000;   //先将毫秒转化成秒
    if (null != time && "" != time) {
        if (time > 60 && time < 60 * 60) {
            time = parseInt(time / 60.0) + "分钟" + parseInt((parseFloat(time / 60.0) -
                parseInt(time / 60.0)) * 60) + "秒";
        }
        else if (time >= 60 * 60 && time < 60 * 60 * 24) {
            time = parseInt(time / 3600.0) + "小时" + parseInt((parseFloat(time / 3600.0) -
                parseInt(time / 3600.0)) * 60) + "分钟" +
                parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
                    parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
        }
        else {
            time = parseInt(time) + "秒";
        }
    }
    return time;
}

//操作缓存
// let p_model_store_s_fog = {
//     saveStatus:function(){
//         let saveObj = {s:$('#serviceAreaDropDown').val(),c:$('input[name="camera"]:checked').val(),yc:document.getElementById('previewCheckbox').checked};
//         u.setStorage("p_model_store_s_fog",saveObj)
//     },
//     findServerAreaId:function(){
//         return u.getStorage("p_model_store_s_fog")?u.getStorage("p_model_store_s_fog").s:null;
//     },
//     findCameraId:function(){
//         return u.getStorage("p_model_store_s_fog")?u.getStorage("p_model_store_s_fog").c:null;
//     },
//     findPreviewChecked:function(){
//         return u.getStorage("p_model_store_s_fog")?u.getStorage("p_model_store_s_fog").yc:null;
//     }
// };