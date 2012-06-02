package models;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;

import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="chat")
public class Chat extends Model {
	
	@ManyToOne
	@JoinColumn(name="from_user")
	public User from;
	@ManyToOne
	@JoinColumn(name="to_user")
	public User to;
	public Calendar dateChat;
	public String content;
	@ManyToOne
	@JoinColumn(name="parent_id")
	public Chat parent;
	
	public String showDate() {
		String date = (dateChat.get(Calendar.MONTH) + 1) + "月" + 
					dateChat.get(Calendar.DATE) + "日 " + 
					dateChat.get(Calendar.HOUR_OF_DAY) + ":" +
					dateChat.get(Calendar.MINUTE);
		return date;
	}
}
