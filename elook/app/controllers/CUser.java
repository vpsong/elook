package controllers;

import java.util.Calendar;
import java.util.GregorianCalendar;

import models.User;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/** 
 *
 * @author Leaf 
 */


public class CUser extends Controller {
	
	public static void userInfo(Long id) {
		User user = null;
		if(id != null)
			user = User.findById(id);
		render(user);
	}
	
	public static void saveOrUpdate(User user) {
		User tmp = User.find("name", user.name).first();
		if(tmp != null) {
			String msg = "用户名已存在！！";
			render("@userInfo", user, msg);
		}
		user.dateRegist = new GregorianCalendar();
		user.password = Codec.hexMD5(user.password);
		user.save();
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
	
}
