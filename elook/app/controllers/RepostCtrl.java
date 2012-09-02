package controllers;

import java.util.GregorianCalendar;
import java.util.List;

import play.mvc.Controller;

import models.Comment;
import models.Feed;
import models.FeedIndex;
import models.FreshAtIndex;
import models.FreshcommentIndex;
import models.User;

/**
 * 
 * @author Leaf
 */
public class RepostCtrl extends Controller {

	public static final int PERPAGE = 15;

	public static void repost(Long id, String content) {
		Feed orgFeed = Feed.findById(id);
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		Feed feed = new Feed();
		feed.commentCount = 0;
		feed.content = content;
		feed.datePublish = new GregorianCalendar();
		feed.forwardCount = 0;
		feed.parent = orgFeed;
		feed.writer = me;
		feed.save();
		++orgFeed.forwardCount;
		orgFeed.save();
		++me.feedCount;
		me.save();
		while (feed.parent != null) {
			FreshAtIndex freshAtIndex = new FreshAtIndex();
			freshAtIndex.atUser = feed.parent.writer;
			freshAtIndex.feed = feed;
			freshAtIndex.save();
			feed = feed.parent;
		}
		renderText("OK");
	}

	public static void getFresh() {
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshAtIndex> ats = FreshAtIndex.find("atUser", me).fetch();
		renderJSON(ats.size());
	}

	public static void atList(int page) {
		if (page == 0)
			page = 1;
		String myname = session.get("username");
		User me = User.find("name", myname).first();
		List<FreshAtIndex> freshs = FreshAtIndex.find("atUser", me).fetch();
		List<Feed> feeds = null;
		if (freshs.size() > PERPAGE) {
			feeds = Feed
					.find("select f from feed f, feed t where f.parent=t and t.writer = ? order by f.datePublish desc",
							me).fetch(freshs.size());
		} else {
			feeds = Feed
					.find("select f from feed f, feed t where f.parent=t and t.writer = ? order by f.datePublish desc",
							me).fetch(page, PERPAGE);
		}
		for (FreshAtIndex index : freshs) {
			index.delete();
		}
		long sum = Feed.count("writer = ?", me);
		long maxpage = (sum - 1) / PERPAGE + 1;
		render(feeds, page, maxpage);
	}

}
