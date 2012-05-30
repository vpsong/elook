package controllers;

import java.util.GregorianCalendar;
import java.util.List;

import models.Chat;
import models.FreshchatIndex;
import models.User;
import play.mvc.Controller;

/** 
 *
 * @author Leaf 
 */
public class ChatCtrl extends Controller {
	
	public static final int PERPAGE = 15;
	
	public static void chat(Long id, Long parentId, String content) {
		User other = User.findById(id);
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		Chat chat = new Chat();
		chat.from = me;
		chat.to = other;
		chat.dateChat = new GregorianCalendar();
		//content = content.replaceAll("\r\n", "song");
		chat.content = content;
		if(parentId != null) {
			chat.parent = Chat.findById(parentId);
		}
		chat.save();
		FreshchatIndex freshchat = new FreshchatIndex();
		freshchat.chat = chat;
		freshchat.toUser = other;
		freshchat.save();
		renderText("OK");
	}
	
	public static void getFresh() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshchatIndex> chats = FreshchatIndex.find("toUser", me).fetch();
		renderJSON(chats.size());
	}
	
	public static void chatList() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshchatIndex> freshs = FreshchatIndex.find("toUser", me).fetch();
		List<Chat> chats = null;
		if(freshs.size() > PERPAGE) {
			chats = Chat.find("to", me).fetch(freshs.size());
		}
		else {
			chats = Chat.find("to = ? order by dateChat desc", me).fetch(PERPAGE);
		}
		for(FreshchatIndex index : freshs) {
			index.delete();
		}
		render(chats);
	}
	
	public static void reply(Long id, String content) {
		Chat chat = Chat.findById(id);
		Chat reply = new Chat();
		reply.parent = chat;
		reply.from = chat.to;
		reply.to = chat.from;
		reply.dateChat = new GregorianCalendar();
		reply.content = content;
		reply.save();
		FreshchatIndex freshchat = new FreshchatIndex();
		freshchat.chat = reply;
		freshchat.toUser = reply.to;
		freshchat.save();
		renderText("OK");
	}
	
}
