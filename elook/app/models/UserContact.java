package models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import models.User;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity
@Table(name="user_contact",
uniqueConstraints={@UniqueConstraint(columnNames={"user_id", "follower_id"})}
)
public class UserContact extends Model {
	
	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;
	@ManyToOne
	@JoinColumn(name="follower_id")
	public User follower;
}
