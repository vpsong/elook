package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;

import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="freshAtIndex")
public class FreshAtIndex extends Model {
	
	@OneToOne
	@JoinColumn(name="feed_id")
	public Feed feed;
	@Index(name="IDX_AT")
	@ManyToOne
	@JoinColumn(name="at_user")
	public User atUser;

}

