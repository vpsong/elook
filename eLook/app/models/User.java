package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumns;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity
@Table(name="user",
uniqueConstraints={@UniqueConstraint(columnNames={"name"})}
)
public class User extends Model {
	
	public String name;
	public String password;
	public int sex;
	public String mail;
	public String remark;
	public Calendar dateRegist;
	public int feedCount;
	public int followerCount;
	public int followCount;
	@ManyToMany
	@JoinTable(name="user_contact",
		joinColumns =@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="follower_id")
	) 
	public List<User> followers;
	@ManyToMany(mappedBy="followers")
	public List<User> follows;
	
	public static int SEX_MALE = 1;
	public static int SEX_FEMALE = 0;
	
	public void follow(User other) {
		this.follows.add(other);
		++this.followCount;
		other.followers.add(this);
		++other.followerCount;
		this.save();
		other.save();
	}
	
	public boolean isFollowed(User other) {
		if(this.follows.contains(other))
			return true;
		else
			return false;
	}
}
