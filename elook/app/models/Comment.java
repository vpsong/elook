package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OrderBy;

import play.db.jpa.Model;

import com.google.gson.JsonObject;

/** 
 *
 * @author Leaf 
 */
@Entity(name="comment")
public class Comment extends Model {
	
	@ManyToOne
	@JoinColumn(name="from_user")
	public User from;
	@ManyToOne
	@JoinColumn(name="to_user")
	public User to;
	@ManyToOne
	@JoinColumn(name="feed_parent")
	public Feed parentFeed;
	@ManyToOne
	@JoinColumn(name="comment_parent")
	public Comment parentComment;
//	@OneToMany(mappedBy="parent")
//	@OrderBy(clause="datePublish DESC")
//	public List<Feed> children;
	public Calendar dateComment;
	public String content;
	
	public String showDate() {
		String date = (dateComment.get(Calendar.MONTH) + 1) + "月" + 
					dateComment.get(Calendar.DATE) + "日 " + 
					dateComment.get(Calendar.HOUR_OF_DAY) + ":" +
					dateComment.get(Calendar.MINUTE);
		return date;
	}
}
