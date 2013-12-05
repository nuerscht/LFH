package models;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@UpdateMode(updateChangesOnly=false)
public class Country extends Model {

    @Id
    private Integer id;

    @Constraints.MaxLength(100)
    private String name;
    
    @UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;
    
    public static Finder<Integer, Country> find = new Finder<Integer, Country>(Integer.class, Country.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public static Map<String,String> countries() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Country c: Country.find.orderBy("name").findList()) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    } 
}
