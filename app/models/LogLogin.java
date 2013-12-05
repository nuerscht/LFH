package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
@UpdateMode(updateChangesOnly=false)
public class LogLogin extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private User user;

    @CreatedTimestamp
    private Date createdAt;
    
    private String info;

    public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public static Finder<Integer, LogLogin> find = new Finder<Integer, LogLogin>(Integer.class, LogLogin.class);

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
