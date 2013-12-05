package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.util.Date;

@Entity
@UpdateMode(updateChangesOnly=false)
public class Tag extends Model {

    @Id
    @Constraints.MaxLength(45)
    private String id;

    private String description;


	@CreatedTimestamp
    private Date createdAt;

    public static Finder<String, Tag> find = new Finder<String, Tag>(String.class, Tag.class);

    public String getId() {
        return id;
    }

    /**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
        return description;
    }
    /**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
