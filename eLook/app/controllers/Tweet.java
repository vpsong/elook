package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import models.Feed;
import models.FeedIndex;
import models.User;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

/** 
 *
 * @author Leaf 
 */

@With(Secure.class)
public class Tweet extends Controller {
	
	public static final int PERPAGE = 15; 
	
	   
    public static void tweet(String name, int page) { 
    	int curpage = page;
    	if(curpage == 0)
    		curpage = 1;
    	User user = User.find("name", name).first();
    	String myname = session.get("username");
    	User me = User.find("name", myname).first();
    	if(user == null || name.equals(myname)) {
    		if(user == null)
    			user = me;
    		if(Cache.get("outbox" + user.id) == null) {
    			Cache.add("outbox" + user.id, new ArrayList<FeedIndex>(), "20mn");
    		}
    		long maxpage = me.followCount + 1;
    		render(user, curpage, maxpage);
    	}
    	else {
    		Boolean isFollowed = me.isFollowed(user);
    		long maxpage = (Feed.count("writer = ?", user) - 1) / PERPAGE + 1;
    		render(user, isFollowed, curpage, maxpage);
    	}
    	
    }
    
    public static void pull(String name, int page) {
    	if(page == 0)
    		page = 1;
    	String myname = session.get("username");
    	User user = User.find("name", name).first();
    	TreeMap<Calendar, String> map = null;
    	if(name.equals(myname)) {
	    	map = pull4me(user, page);
	    	renderJSON(map);
    	}
    	else {
    		map = new TreeMap<Calendar, String>(); 
    		List<Feed> list = Feed.find("writer = ? order by datePublish desc", user).fetch(page, PERPAGE);
    		for(Feed feed : list) {
    			map.put(feed.datePublish, feed.toString());
    		}
    		renderJSON(map);
    	}
    }
    
    public static TreeMap<Calendar, String> pull4me(User user, int page) {
    	TreeMap<Calendar, String> map = new TreeMap<Calendar, String>();
    	List<User> follows = user.follows;
    	follows.add(user);
		for(User follow : follows) {
			List<Feed> list = Feed.find("writer = ? order by datePublish desc", 
					follow).fetch(PERPAGE);
			for(Feed feed : list) {
				map.put(feed.datePublish, feed.toString());
			}
		}
		TreeMap<Calendar, String> limit = new TreeMap<Calendar, String>();
		for(int i=1; i<page; ++i) {
			for(int j=0; j<PERPAGE; ++j) 
				map.pollLastEntry();
		}
		for(int i=0; i<	PERPAGE; ++i) {
			if(map.size() == 0)
				break;
			Entry<Calendar, String> entry = map.pollLastEntry();
			limit.put(entry.getKey(), entry.getValue());
		}
		return limit;
    }
    
    public static void refresh(Long id) {
    	TreeMap<Calendar, String> map = (TreeMap<Calendar, String>)Cache.get("freshmap" + session.getId());
    	if(map == null)
    		freshmap(id);
    	renderJSON(map.size());
    }
    
    public static void getfresh(Long id) {
    	TreeMap<Calendar, String> map = (TreeMap<Calendar, String>)Cache.get("freshmap" + session.getId());
    	if(map == null)
    		freshmap(id);
    	Cache.set("updateTime" + session.getId(), new GregorianCalendar(), "20mn");
    	renderJSON(map);
    }
    
    public static TreeMap<Calendar, String> freshmap(Long id) {
    	TreeMap<Calendar, String> map = new TreeMap<Calendar, String>();
    	Calendar updateTime = (Calendar)Cache.get("updateTime" + session.getId());
    	User me = User.findById(id);
    	List<User> follows = me.follows;
    	for(User follow : follows) {
    		ArrayList<FeedIndex> tmp = (ArrayList<FeedIndex>)Cache.get("outbox" + follow.id);
    		if(tmp != null) {
    			for(FeedIndex index : tmp) {
    				Feed feed = Feed.findById(index.feedId);
    				if(updateTime == null || updateTime.compareTo(feed.datePublish) < 0)	
    					map.put(feed.datePublish, feed.toString());
    			}    			
    		}
    	}
    	Cache.set("freshmap" + session.getId(), map, "3mn");
    	return map;
    }
    
    public static void publish(String content) {
    	Feed feed = new Feed();
    	feed.content = content;
    	feed.datePublish = new GregorianCalendar();
    	feed.type = Feed.TYPE_NORMAL;
    	String username = session.get("username");
    	User writer = User.find("name", username).first();
    	feed.writer = writer;
    	feed.save();
    	++writer.feedCount;
    	writer.save();
    	ArrayList<FeedIndex> tmp = (ArrayList<FeedIndex>)Cache.get("outbox" + writer.id);
    	if(tmp == null) {
    		tmp = new ArrayList<FeedIndex>();
    		Cache.add("outbox" + writer.id, tmp, "20mn");
    	}
    	FeedIndex index = new FeedIndex();
    	index.publishTime = feed.datePublish;
    	index.feedId = feed.id;
    	tmp.add(index);
    	renderText("OK");
    }
    
}
