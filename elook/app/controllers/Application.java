package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;

import java.util.*;

import com.google.gson.JsonObject;
import models.*;


public class Application extends Controller {
	
	public static final int PERPAGE = 25;
	
    public static void index() {
        render();
    }
    
    public static void wall() {
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
    
    public static void wallShow(int page) {
    	List<Feed> feeds = Feed.find("order by datePublish desc").fetch(page, PERPAGE);
        TreeMap<Calendar, String> map = new TreeMap<Calendar, String>(Collections.reverseOrder()); 
		for(Feed feed : feeds) {
			map.put(feed.datePublish, feed.toString());
		}
		renderJSON(map);
    }
 

}