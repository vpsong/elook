String.prototype.replaceAll  = function(s1,s2){   
		return this.replace(new RegExp(s1,"gm"),s2);   
}

function refresh() {
	$.get("/tweet/refresh", function(data){
		  if(data == "0")
			  $("#tip").html("");
		  else
		  	$("#tip").html("<a href='javascript:void(0)' onclick='getfresh();'>有" + 
				  data + "条新微博，点击查看</a>");
		});
}

function getfresh(id) {
	$.getJSON("/tweet/getfresh",
			{"id" : id},
			function(data) {
			$.each(data,function(idx,item){
				var obj = eval('(' + item + ')');
			        var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + 
					obj.photo + "' /><a href='/tweet/showFeeds?name=" + obj.writer + 
			        "' target='_blank'>" + obj.writer + "</a>：" + obj.content +
			        "<p class='info'><span class='pubtime'>" + obj.time + "</span><span class='action'>" + 
			        "<a>转发(" + obj.forwardCount + ")</a> | <a href='javascript:void(0)' onclick='comment(" + obj.id + ")'>评论(" + 
			        obj.commentCount + ")</a></span></p></div>";
			    	$("#container").prepend(newtweet);
			    });   
	   });
	 $("#tip").html("");
}

function freshchat() {
	$.get("/chatctrl/getfresh", function(data){
		  if(data == "0")
			  $("#tipChat").html("");
		  else
		  	$("#tipChat").html("<a href='/chatctrl/chatlist'>有" + 
				  data + "条新私信</a>");
		});
}

function freshcomment() {
	$.get("/commentctrl/getfresh", function(data){
		  if(data == "0")
			  $("#tipComment").html("");
		  else
		  	$("#tipComment").html("<a href='/commentctrl/commentlist'>有" + 
				  data + "条新评论</a>");
		});
}

function comment(id) {
	var txt = '<div align="center">评论</div>'
		+
		'<div><textarea name="content" rows="5" cols="50" maxlength="127" wrap="virtual"></textarea></div>';
	$.prompt(txt, {
		buttons : {
			发送 : true,
			取消 : false
		},
		submit : function(v, m, f) {
			if(v) {
				var text = f.content;
				if(text == "") {
					$.prompt("<span class='alertmsg'>评论内容不能为空..</span>");
					return false;
				}
				text = text.replaceAll("\r\n", "<br/>");
				$.get("/commentctrl/comment", 
					{"id" : id, "content" : text},
					function(data){
					  if(data == "OK")
						$.prompt("<span class='alertmsg'>评论成功</span>");
					  else
					  	$.prompt("<span class='alertmsg'>系统繁忙，请稍候再试</span>");
					});
			}
		}
	});
}

function init(curpage) {
	$.getJSON("/tweet/pull",
			{"page" : curpage },
			function(data) {
			$.each(data,function(idx,item){
				var obj = eval('(' + item + ')');
			        var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + 
					obj.photo + "' /><a href='/tweet/showFeeds?name=" + obj.writer + 
			        "' target='_blank'>" + obj.writer + "</a>：" +
			        obj.content +
			        "<p class='info'><span class='pubtime'>" + obj.time + "</span><span class='action'>" + 
			        "<a>转发(" + obj.forwardCount + ")</a> | <a href='javascript:void(0)' onclick='comment(" + obj.id + 
			        ")'>评论(" + obj.commentCount + ")</a></span></p></div>";
			    	$("#container").prepend(newtweet);
			    });   
	   });
}

function publish(name, photo) {
	var con = $("#content").val();
	if(con.length == "") {
		$.prompt("<span class='alertmsg'>发表内容不能为空..</span>");
		return false;
	}
	con = con.replaceAll("\n", "<br/>");
	$.post("/tweet/publish",
			{"content" : con},
			   function(data){
				if(data == "OK") {
					$.prompt("<span class='alertmsg'>发表成功...</span>");
					var now = new Date();
					var time = (now.getMonth() + 1) + "月" +
						now.getDate() + "日" + now.getHours() + ":" + 
						now.getMinutes();
					var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + photo + 
					"'/><a href='/tweet/showfeeds(" + name + ")}' target='_blank'>" + name + "</a>："
						+ con +	"<p class='info'><span class='pubtime'>" + time + 
						"</span><span class='action'>" +
						"<a>转发(0)</a> | <a>评论(0)</a>" +
						"</span></p></div>";
					$("#container").prepend(newtweet);
					$("#content").val("");
				}
				else {
					$.prompt("<span class='alertmsg'>系统繁忙，请稍后再试</span>");
				}
	});
}

function follow(name) {
	$.get("/cuser/follow?name=" + name, function(data){
		  if(data == "OK") {
			  $.prompt("<span class='alertmsg'>关注成功..</span>");
			  $("#isFollowed").html("已关注");
		  }
		  else {
			  $.prompt("<span class='alertmsg'>系统繁忙，请稍后再试</span>");
		  }
		});
}

function chatto(id, name, userId) {
	var txt = '<div align="center">悄悄对 ' + name + ' 说</div>'
		+
		'<div><textarea name="content" rows="5" cols="50" maxlength="127" wrap="virtual"></textarea></div>';
	$.prompt(txt, {
		buttons : {
			发送 : true,
			取消 : false
		},
		submit : function(v, m, f) {
			if(v) {
				var text = f.content;
				if(text == "") {
					$.prompt("<span class='alertmsg'>私信内容不能为空..</span>");
					return false;
				}
				text = text.replaceAll("\r\n", "<br/>");
				$.get("/chatctrl/chat", 
					{"id" : userId, "content" : text},
					function(data){
					  if(data == "OK")
						$.prompt("<span class='alertmsg'>发送成功</span>");
					  else
					  	$.prompt("<span class='alertmsg'>系统繁忙，请稍候再试</span>");
					});
			}
		}
	});
}

function reply(id, name) {
	var txt = '<div align="center">回复 ' + name + '</div>'
		+
		'<div><textarea name="content" rows="5" cols="50" maxlength="127" wrap="virtual"></textarea></div>';
	$.prompt(txt, {
		buttons : {
			发送 : true,
			取消 : false
		},
		submit : function(v, m, f) {
			if(v) {
				var text = f.content;
				if(text == "") {
					$.prompt("<span class='alertmsg'>回复内容不能为空..</span>");
					return false;
				}
				text = text.replaceAll("\r\n", "<br/>");
				$.get("/chatctrl/reply", 
					{"id" : id, "content" : text},
					function(data){
					  if(data == "OK")
						$.prompt("<span class='alertmsg'>发送成功</span>");
					  else
					  	$.prompt("<span class='alertmsg'>系统繁忙，请稍候再试</span>");
					});
			}
		}
	});
}

function pull() {
	$.getJSON("/application/show",
			function(data) {
			$("#container_index").html("");
			$.each(data,function(idx,item){
				var obj = eval('(' + item + ')');
				var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + 
				obj.photo + "' /><a href='/" + obj.writer + "' target='_blank'>" + obj.writer + "</a>：" +
			        obj.content +  "<p class='info'><span class='pubtime'>" + obj.time + 
			        "</span><span class='action'>" + "<a>转发(" + obj.forwardCount + ")</a> | <a>评论(" + 
			        obj.commentCount + ")</a></span></p></div>";
			    	$("#container_index").prepend(newtweet);
			});   
	   });
}




