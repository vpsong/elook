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
		String myname = session.get("username");
		if (!myname.equals(name))
			showFeeds(name, page);
		if (page == 0)
			page = 1;
		User me = User.find("name", myname).first();
		Cache.add("outbox" + me.id, new ArrayList<FeedIndex>(), "20mn");
		// long maxpage = me.followCount + 1;
		TreeMap<Calendar, String> map = new TreeMap<Calendar, String>();
		List<User> follows = me.follows;
		follows.add(me);
		for (User follow : follows) {
			List<Feed> list = Feed.find("writer = ? order by datePublish desc",
					follow).fetch(PERPAGE);
			for (Feed feed : list) {
				map.put(feed.datePublish, feed.toString());
			}
		}
		if (map.size() > 0)
			Cache.set("pullmap" + me.id, map, "3mn");
		long maxpage = (map.size() - 1) / PERPAGE + 1;
		render(me, page, maxpage);
	}

	public static void pull(int page) {
		if (page == 0)
			page = 1;
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		TreeMap<Calendar, String> map = pull4me(me, page);
		renderJSON(map);
	}

	public static TreeMap<Calendar, String> pull4me(User me, int page) {
		TreeMap<Calendar, String> map = (TreeMap<Calendar, String>) Cache
				.get("pullmap" + me.id);
		if (map == null) {
			map = new TreeMap<Calendar, String>();
			List<User> follows = me.follows;
			follows.add(me);
			for (User follow : follows) {
				List<Feed> list = Feed.find(
						"writer = ? order by datePublish desc", follow).fetch(
						PERPAGE);
				for (Feed feed : list) {
					map.put(feed.datePublish, feed.toString());
				}
			}
			if (map.size() > 0)
				Cache.set("pullmap" + me.id, map, "3mn");
		}
		map = (TreeMap<Calendar, String>) map.clone();
		TreeMap<Calendar, String> limit = new TreeMap<Calendar, String>();
		for (int i = 1; i < page; ++i) {
			for (int j = 0; j < PERPAGE; ++j)
				map.pollLastEntry();
		}
		for (int i = 0; i < PERPAGE; ++i) {
			if (map.size() == 0)
				break;
			Entry<Calendar, String> entry = map.pollLastEntry();
			limit.put(entry.getKey(), entry.getValue());
		}
		return limit;
	}

	public static void refresh() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		TreeMap<Calendar, String> map = (TreeMap<Calendar, String>) Cache
				.get("freshmap" + me.id);
		if (map == null)
			map = freshmap(me);
		renderJSON(map.size());
	}

	public static void getfresh() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		TreeMap<Calendar, String> map = (TreeMap<Calendar, String>) Cache
				.get("freshmap" + me.id);
		if (map == null)
			map = freshmap(me);
		Cache.set("updateTime" + me.id, new GregorianCalendar(), "20mn");
		Cache.delete("freshmap" + me.id);
		renderJSON(map);
	}

	public static TreeMap<Calendar, String> freshmap(User me) {
		TreeMap<Calendar, String> map = new TreeMap<Calendar, String>();
		Calendar updateTime = (Calendar) Cache.get("updateTime" + me.id);
		List<User> follows = me.follows;
		if (follows.size() == 0)
			return map;
		for (User follow : follows) {
			ArrayList<FeedIndex> tmp = (ArrayList<FeedIndex>) Cache
					.get("outbox" + follow.id);
			if (tmp != null) {
				for (FeedIndex index : tmp) {
					Feed feed = Feed.findById(index.feedId);
					if (updateTime == null
							|| updateTime.compareTo(feed.datePublish) < 0)
						map.put(feed.datePublish, feed.toString());
				}
			}
		}
		Cache.set("freshmap" + me.id, map, "1mn");
		return map;
	}

	public static void publish(String content) {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		Feed feed = new Feed();
		feed.content = content;
		feed.datePublish = new GregorianCalendar();
		feed.writer = me;
		feed.save();
		++me.feedCount;
		me.save();
		ArrayList<FeedIndex> tmp = (ArrayList<FeedIndex>) Cache.get("outbox"
				+ me.id);
		if (tmp == null) {
			tmp = new ArrayList<FeedIndex>();
			Cache.add("outbox" + me.id, tmp, "20mn");
		}
		FeedIndex index = new FeedIndex();
		index.publishTime = feed.datePublish;
		index.feedId = feed.id;
		tmp.add(index);
		renderText("OK");
	}

	public static void showFeeds(String name, int page) {
		User user = User.find("name", name).first();
		if (user == null)
			tweet(session.get("username"), 1);
		if (page == 0)
			page = 1;
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<Feed> feeds = Feed.find("writer = ? order by datePublish desc",
				user).fetch(page, PERPAGE);
		int maxpage = (user.feedCount - 1) / PERPAGE + 1;
		boolean isFollowed = false;
		if (me.follows.contains(user))
			isFollowed = true;
		render(user, feeds, page, isFollowed, maxpage);
	}

}
