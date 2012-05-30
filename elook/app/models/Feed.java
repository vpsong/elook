package models;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.OrderBy;

import com.google.gson.JsonObject;

import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="feed")
public class Feed extends Model{
		
	@ManyToOne
	@JoinColumn(name="writer_id")
	public User writer;
	@ManyToOne
	@JoinColumn(name="parent_id")
	public Feed parent;
//	@OneToMany(mappedBy="parent")
//	@OrderBy(clause="datePublish DESC")
//	public List<Feed> children;
	public Calendar datePublish;
	public String content;
	public int forwardCount;
	public int commentCount;
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("id", this.id);
		obj.addProperty("content", this.content);
		obj.addProperty("writer", this.writer.name);
		obj.addProperty("forwardCount", this.forwardCount);
		obj.addProperty("commentCount", this.commentCount);
		String time = (datePublish.get(Calendar.MONTH) + 1) + "月" + 
					datePublish.get(Calendar.DATE) + "日 " + 
					datePublish.get(Calendar.HOUR_OF_DAY) + ":" +
					datePublish.get(Calendar.MINUTE);
		obj.addProperty("time", time);
		return obj.toString();
	}
}
