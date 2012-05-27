function pull() {
	$.getJSON("/application/show",
			function(data) {
			$("#container_index").html("");
			$.each(data,function(idx,item){
				var newtweet = "<div class='tweet_index'><a href='/" + eval('(' + item + ')').writer + 
			        "' target='_blank'>" + eval('(' + item + ')').writer + "</a>：" +
			        eval('(' + item + ')').content +  
			        "<p class='pubtime'>" + eval('(' + item + ')').time + "</p></div>";
			    	$("#container_index").prepend(newtweet);
			});   
	   });
}

function follow() {
	$.get("@{CUser.follow(user.name)}", function(data){
		  if(data == "OK") {
			  alert("关注成功..");
			  $("#isFollowed").html("已关注");
		  }
		  else {
			  alert("系统繁忙..请稍后再试");
		  }
		});
}

function publishing() {
	$.post("/tweet/publish",
			{"content" : $("#content").val()},
			   function(data){
				if(data == "OK") {
					alert("发表成功...");
					var now = new Date();
					var time = (now.getMonth() + 1) + "月" +
						now.getDate() + "日" + now.getHours() + ":" + 
						now.getMinutes();
					var newtweet = "<div class='tweet'><a href='/" + "${session.username}" +
						"' target='_blank'>" + "${session.username}" + "</a>："
						+ $("#content").val() +  
						"<p class='pubtime'>" + time + "</p></div>";
					$("#container").prepend(newtweet);
					$("#content").val("");
				}
				else {
					alert("系统繁忙..请稍后再试");
				}
	});
}

function refresh() {
	$.get("@{refresh(user.id)}", function(data){
		  if(data == "0")
			  $("#tip").html("");
		  else
		  	$("#tip").html("<a href='javascript:void(0)' onclick='getfresh(${user.id});'>有" + 
				  data + "条新微博，点击查看</a>");
		});
}

function getfresh(id) {
	$.getJSON("/tweet/getfresh",
			{"id" : id},
			function(data) {
			$.each(data,function(idx,item){
			        var newtweet = "<div class='tweet'><a href='/" + eval('(' + item + ')').writer + 
			        "' target='_blank'>" + eval('(' + item + ')').writer + "</a>：" +
			        eval('(' + item + ')').content +
			        "<p class='pubtime'>" + eval('(' + item + ')').time + "</p></div>";
			    	$("#container").prepend(newtweet);
			    });   
	   });
	 $("#tip").html("");
}
