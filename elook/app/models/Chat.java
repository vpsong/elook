package models;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="chat")
public class Chat extends Model {
	
	@ManyToOne
	@JoinColumn(name="from_id")
	public User from;
	@ManyToOne
	@JoinColumn(name="to_id")
	public User to;
	public Calendar dateChat;
	public String content;
}
