package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class UserType extends Model {

    public static final String CUSTOMER = "customer";
    public static final String ADMIN = "admin";

    @Id
    @Constraints.MaxLength(45)
    private String id;

    @Constraints.Required
    private String description;

    public static Finder<String, UserType> find = new Finder<String, UserType>(String.class, UserType.class);

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}