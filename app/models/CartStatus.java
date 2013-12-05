package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.annotation.UpdateMode;

@Entity
@UpdateMode(updateChangesOnly=false)
public class CartStatus extends Model {

    public static final String OPEN = "open";
    public static final String ORDERED = "ordered";

    @Id
    @Constraints.MaxLength(45)
    private String id;

    @Constraints.Required
    private String description;

    public static Finder<String, CartStatus> find = new Finder<String, CartStatus>(String.class, CartStatus.class);

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
}
