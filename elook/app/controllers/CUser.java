package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.omg.CORBA.PERSIST_STORE;

import models.Feed;
import models.User;
import models.UserContact;
import models.UserInfo;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/** 
 *
 * @author Leaf 
 */


public class CUser extends Controller {
	
	public static final int PERPAGE = 15;
	
	public static void userInfo(Long id) {
		User user = null;
		if(id != null)
			user = User.findById(id);
		render(user);
	}
	
	public static void saveOrUpdate(User user, UserInfo info) {
		User tmp = User.find("name", user.name).first();
		if(tmp != null) {
			String msg = "用户名已存在！！";
			render("@userInfo", user, msg);
		}
		info.dateRegist = new GregorianCalendar();
		info.password = Codec.hexMD5(info.password);
		info.user = user;
		user.save();
		info.save();
		session.put("username", user.name);
		Tweet.tweet(user.name, 1);
	}
	
	public static void follow(String name) {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		User other = User.find("name", name).first();
		me.follow(other);
		renderText("OK");
	}
	
	public static void showFollowers(int page) {
		if(page == 0)
			page = 1;
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<UserContact> contacts = UserContact.find("user", me).fetch(page, PERPAGE);
		long maxpage = (me.followerCount - 1) / PERPAGE + 1;
		List<User> followers = new ArrayList<User>();
		for(UserContact contact : contacts) {
			User u = contact.follower;
			u.lastFeed = Feed.find("writer = ? order by datePublish desc", u).first();
			followers.add(u);
		}
		render(followers, page, maxpage);
	}
	
}
