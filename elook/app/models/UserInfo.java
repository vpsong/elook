package models;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

/** 
 *
 * @author Leaf 
 */
@Entity(name="user_info")
public class UserInfo extends GenericModel {
	
	@Id
    @GeneratedValue(generator="userinfo")
	@GenericGenerator(name="userinfo",strategy="foreign",parameters=@Parameter(name="property",value="user"))
    public Long id;
	public String password;
	public int sex;
	public String mail;
	public String remark;
	public Calendar dateRegist;
	@OneToOne
	@PrimaryKeyJoinColumn
	public User user;
	
}
