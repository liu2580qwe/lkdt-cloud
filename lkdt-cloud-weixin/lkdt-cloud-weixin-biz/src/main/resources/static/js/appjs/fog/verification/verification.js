function doVerification(nh) {
		var y1 = parseFloat($("#y1").val())/nh;
		var y2 = parseFloat($("#y2").val())/nh;
		var y4 = parseFloat($("#y4").val());
		var ymax = parseFloat($("#ymax").val())/nh;
		var H = parseFloat($("#H").val());
		var w1 = parseFloat($("#w1").val());
		var w2 = parseFloat($("#w2").val());
		var d = parseFloat($("#d").val());
		
		var z1 = Math.sqrt(Math.pow(w2/(w1-w2)*d, 2) - H*H);
		$("#z1").val(z1);
		
		
		
		
		//var c1 = (2*(y2*y2-y1*y3) + Math.sqrt(4*(y2*y2-y1*y3)*(y2*y2-y1*y3) -4*(y2*y3+y1*y2-2*y1*y3)*y2*(2*y2-y1-y3)))/(2*y2*(y2*y3+y1*y2-2*y1*y3));
		//var c2 = (2*(y2*y2-y1*y3) - Math.sqrt(4*(y2*y2-y1*y3)*(y2*y2-y1*y3) -4*(y2*y3+y1*y2-2*y1*y3)*y2*(2*y2-y1-y3)))/(2*y2*(y2*y3+y1*y2-2*y1*y3));
		
		var c = nh/y4;
		$("#c").val(c);
		
		var b = ((z1+15)*(1-c*y2)-z1*(1-c*y1))/(y2-y1);
		$("#b").val(b);
		
		var a= b*y1-z1*(1-c*y1);
		$("#a").val(a);
	
		var zmax = (-1*a + b * ymax)/(1-ymax*c);
		$("#zmax").val(zmax);
		$("#result").val(zmax);
		//$(".avatar-wrapper").html('<video src="' + mp4url + '" autoplay="autoplay"></video>');
	}

function doVerification_day(ymax) {
	var zmax=4.32+0.329272060862452*ymax-0.00514261072298011*ymax*ymax+3.94556278054707*0.00001*ymax*ymax*ymax-1.28905832808181*0.0000001*ymax*ymax*ymax*ymax+1.62927340505916*0.0000000001*ymax*ymax*ymax*ymax*ymax;
	return zmax;
	
}