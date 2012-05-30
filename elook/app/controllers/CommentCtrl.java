package controllers;

import java.util.GregorianCalendar;
import java.util.List;

import models.Chat;
import models.Comment;
import models.Feed;
import models.FreshchatIndex;
import models.FreshcommentIndex;
import models.User;
import play.mvc.Controller;

/** 
 *
 * @author Leaf 
 */
public class CommentCtrl extends Controller {
	
	public static final int PERPAGE = 15;
	
	public static void comment(Long id, String content) {
		Feed feed = Feed.findById(id);
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		Comment comment = new Comment();
		comment.from = me;
		comment.to = feed.writer;
		comment.dateComment = new GregorianCalendar();
		comment.content = content;
		comment.parentFeed = feed;
		comment.save();
		++feed.commentCount;
		feed.save();
		FreshcommentIndex freshcomment = new FreshcommentIndex();
		freshcomment.toUser = feed.writer;
		freshcomment.comment = comment;
		freshcomment.save();
		renderText("OK");
	}
	
	public static void getFresh() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshcommentIndex> comments = FreshcommentIndex.find("toUser", me).fetch();
		renderJSON(comments.size());
	}
	
	public static void commentList() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshcommentIndex> freshs = FreshcommentIndex.find("toUser", me).fetch();
		List<Comment> comments = null;
		if(freshs.size() > PERPAGE) {
			comments = Comment.find("to", me).fetch(freshs.size());
		}
		else {
			comments = Comment.find("to = ? order by dateComment desc", me).fetch(PERPAGE);
		}
		for(FreshcommentIndex index : freshs) {
			index.delete();
		}
		render(comments);
	}
	
	public static void reply(Long id, String content) {
		Comment comment = Comment.findById(id);
		Comment reply = new Comment();
		reply.parentComment = comment;
		reply.from = comment.to;
		reply.to = comment.from;
		reply.dateComment = new GregorianCalendar();
		reply.content = content;
		reply.save();
		FreshcommentIndex freshcomment = new FreshcommentIndex();
		freshcomment.comment = reply;
		freshcomment.toUser = reply.to;
		freshcomment.save();
		while(comment.parentFeed == null) {
			comment = comment.parentComment;
		}
		++comment.parentFeed.commentCount;
		comment.parentFeed.save();
		renderText("OK");
	}
	 
}
