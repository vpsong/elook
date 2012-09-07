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
					//var obj = eval('(' + item + ')');
					var obj = JSON.parse(item);    
					var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + 
					obj.photo + "' /><a href='/tweet/showFeeds?name=" + obj.writer + 
			        "' target='_blank'>" + obj.writer + "</a>：" + obj.content +
			        "<p class='info'><span class='pubtime'>" + obj.time + "</span><span class='action'>" + 
			        "<a id='fcount" + obj.id + "'>转发(" + obj.forwardCount + 
			        ")</a> | <a id='ccount" + obj.id + "' href='javascript:void(0)' onclick='comment(" + obj.id + ")'>评论(" + 
			        obj.commentCount + ")</a></span></p></div>";
			    	$("#container").prepend(newtweet);
			    });   
	   });
	 $("#tip").html("");
}

function freshchat() {
	$.get("/chatctrl/getfresh", function(data){
		  if(data == "0") {
			  $("#tipChat").html("");
			  $(".tipBox").hide();
		  }
		  else {
		  	$("#tipChat").html("<a href='/chatctrl/chatlist'>有" + 
				  data + "条新私信</a>");
		  	$(".tipBox").show();
		  }
		});
}

function freshcomment() {
	$.get("/commentctrl/getfresh", function(data){
		  if(data == "0") {
			  $("#tipComment").html("");
			  $(".tipBox").hide();
		  }
		  else {
		  	$("#tipComment").html("<a href='/commentctrl/commentlist'>有" + 
				  data + "条新评论</a>");
		  	$(".tipBox").show();
	  	  }
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
					  if(data == "OK") {
						$.prompt("<span class='alertmsg'>评论成功</span>");
						var count = $("#ccount" + id).html();
						count = parseInt(count.substring(3, count.length - 1)) + 1;
						$("#ccount" + id).html("评论(" + count + ")");
					  }
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
					//var obj = eval('(' + item + ')');
					var obj = JSON.parse(item);
			        var newtweet = "<div class='tweet' id='tweet" + obj.id + "'><img class='photo' src='/photo/" + 
					obj.photo + "' /><a href='/tweet/showFeeds?name=" + obj.writer + 
			        "' target='_blank'>" + obj.writer + "</a>：" +
			        "<span>" + obj.content + "</span>" +
			        "<p class='info'><span class='pubtime'>" + obj.time + "</span><span class='action'>" + 
			        "<a id='fcount" + obj.id + "' href='javascript:void(0)' onclick='repost(" + obj.id +")'>转发(" + obj.forwardCount + 
			        ")</a> | <a id='ccount" + obj.id + "' href='javascript:void(0)' onclick='comment(" + obj.id + 
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

function chatto(name, userId) {
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
				//var obj = eval('(' + item + ')');
				var obj = JSON.parse(item);
				var newtweet = "<div class='tweet'><img class='photo' src='/photo/" + 
				obj.photo + "' /><a href='/" + obj.writer + "' target='_blank'>" + obj.writer + "</a>：" +
			        obj.content +  "<p class='info'><span class='pubtime'>" + obj.time + 
			        "</span><span class='action'>" + "<a>转发(" + obj.forwardCount + 
			        ")</a> | <a>评论(" + obj.commentCount + ")</a></span></p></div>";
			    	$("#container_index").prepend(newtweet);
			});   
	   });
}

function repost(id) {
	var pn = $("#tweet" + id + " a:first").html();
	var pc = $("#tweet" + id + " span:first").html();
	var txt = '<div align="center">转发</div>'
		+
		'<div><textarea name="content" rows="5" cols="50" maxlength="127" wrap="virtual">//@' + pn + "：" + pc + '</textarea></div>';
	$.prompt(txt, {
		buttons : {
			发送 : true,
			取消 : false
		},
		submit : function(v, m, f) {
			if(v) {
				var text = f.content;
				if(text == "") {
					$.prompt("<span class='alertmsg'>转发内容不能为空..</span>");
					return false;
				}
				text = text.replaceAll("\r\n", "<br/>");
				$.get("/repostctrl/repost", 
					{"id" : id, "content" : text},
					function(data){
					  if(data == "OK") {
						$.prompt("<span class='alertmsg'>转发成功</span>");
						var count = $("#fcount" + id).html();
						count = parseInt(count.substring(3, count.length - 1)) + 1;
						$("#fcount" + id).html("转发(" + count + ")");
					  }
					  else
					  	$.prompt("<span class='alertmsg'>系统繁忙，请稍候再试</span>");
					});
			}
		}
	});
}

function freshat() {
	$.get("/repostctrl/getfresh", function(data){
		  if(data == "0") {
			  $("#tipAt").html("");
			  $(".tipBox").hide();
		  }
		  else {
		  	$("#tipAt").html("<a href='/repostctrl/atlist'>有" + 
				  data + "条@到你</a>");
		  	$(".tipBox").show();
		  }
		});
}

function wallShow(page) {
	$.getJSON("/application/wallShow", {"page": page},
			function(data) {
			$.each(data,function(idx,item){
				var obj = JSON.parse(item);
				var newtweet = "<div class='tweet'><img class='photo_wall' src='/photo/" + 
				obj.photo + "' /><a href='/" + obj.writer + "' target='_blank'>" + obj.writer + "</a>：" +
			        obj.content +  "<p class='info_wall'><span class='pubtime'>" + obj.time + 
			        "</span><span class='action'>" + "<a>转发(" + obj.forwardCount + 
			        ")</a> | <a>评论(" + obj.commentCount + ")</a></span></p></div>";
			    var cols = $(".col");
			    var i = 1;
			    var min = 0;
			    for(; i<cols.length; ++i) {
			    	if(cols[min].clientHeight > cols[i].clientHeight) {
			    		min = i; 
			    	}
			    }
			    $(cols[min]).append(newtweet);
			});   
	   });
}

function bottomRefresh() {
    var a = document.documentElement.clientHeight == 0 ? document.body.clientHeight : document.documentElement.clientHeight;
    var b = document.documentElement.scrollTop == 0 ? document.body.scrollTop : document.documentElement.scrollTop;
    var c = document.documentElement.scrollHeight == 0 ? document.body.scrollHeight : document.documentElement.scrollHeight;
    if (b != 0) {
        if (a + b >= c) {
        	wallShow(page++);
        }
    }
}
