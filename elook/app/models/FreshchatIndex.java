package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="freshchatIndex")
public class FreshchatIndex extends Model {
	
	@OneToOne
	@JoinColumn(name="chat_id")
	public Chat chat;
	@ManyToOne
	@JoinColumn(name="to_user")
	public User toUser;
}
