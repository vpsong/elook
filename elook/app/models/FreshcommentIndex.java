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
@Entity(name="freshcommentIndex")
public class FreshcommentIndex extends Model {
	
	@OneToOne
	@JoinColumn(name="comment_id")
	public Comment comment;
	@ManyToOne
	@JoinColumn(name="to_user")
	public User toUser;

}
