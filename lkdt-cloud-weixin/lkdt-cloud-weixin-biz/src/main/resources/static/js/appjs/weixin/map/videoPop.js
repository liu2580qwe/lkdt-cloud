
var player = null;
function videoPop() {
	var div = document.createElement("div"); 
	div.id="videoPop";
	div.style="width:100%;height:60vw;background-color: #132535;z-index: 10000;";
	div.class="videoWindow";
	/*.videoWindow{
		position: absolute;
		top: 0px;
		left: 0px;
		width: 100%;
		height: 50%;
		z-index: 999;
	}*/
//	var video = document.createElement("video"); 
//	video.id="video1";
//	var muted = document.createAttribute("muted"); //创建属性
//	video.setAttributeNode(muted);
//	video.src="/video/shouye.mp4";
//	video.style.height="60vw";
//	video.style.width="100%";
//	video.autoplay="autoplay";
//	div.appendChild(video); 
	var url = '';
	if(user=="1"){
		url = '/video/shouye.webm';
	}else{
		url = '/video/shouye1.mp4'
	}
	
	var videoObject = {
			container: '#videoPop',//“#”代表容器的ID，“.”或“”代表容器的class
			variable: 'player',//该属性必需设置，值等于下面的new chplayer()的对象
			autoplay:true,//自动播放
			live:true,
			mobileCkControls:true,
			loaded:"startv",
			video:url//url//视频地址
	};
	
	var pdiv = document.createElement("div"); 
	pdiv.id="p-videoPop"
	pdiv.style="display: none;position: fixed;top:0px;width:100%;height:65vw;background-color: #132535;z-index: 10000;";
	
	//<a href="javascript:;" style="margin-top: 10px;" class="weui-btn weui-btn_primary" onclick="wxWindPop.close()">关闭</a>
	var close = document.createElement("a"); 
	close.href="javascript:;";
	close.style="margin-top: 0px;";
	close.setAttribute("class","weui-btn weui-btn_primary");
	close.setAttribute("onclick","closeVideo()");
	close.innerText="关闭";
	
	pdiv.appendChild(div);
	pdiv.appendChild(close);
	
	document.body.appendChild(pdiv);
	player = new ckplayer(videoObject);
	player.newVideo(videoObject);
	$('#p-videoPop').show();
//	document.getElementById("video1").play();
}
function startv(){
	player.videoPlay();
//	player.changeControlBarShow(false);
	
}
function closeVideo(){
	$('#p-videoPop').hide();
}