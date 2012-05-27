package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;

import java.util.*;

import com.google.gson.JsonObject;
import models.*;


public class Application extends Controller {
	
	public static final int PERPAGE = 40;
	
    public static void index() {
        render();
    }
    
    public static void show() {
    	List<Feed> feeds = Feed.find("order by datePublish desc").fetch(PERPAGE);
        TreeMap<Calendar, String> map = new TreeMap<Calendar, String>(); 
		for(Feed feed : feeds) {
			map.put(feed.datePublish, feed.toString());
		}
		renderJSON(map);
    }
 

}