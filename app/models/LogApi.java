package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.db.ebean.Model;
import play.data.validation.*;

import javax.persistence.*;

import java.util.Date;

@Entity
@UpdateMode(updateChangesOnly=false)
public class LogApi extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private User user;

    @Constraints.Required
    private String requestUri;

    @CreatedTimestamp
    private Date createdAt;
    
    private String info;
    
    private String params;

    public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public static Finder<Integer, LogApi> find = new Finder<Integer, LogApi>(Integer.class, LogApi.class);

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
