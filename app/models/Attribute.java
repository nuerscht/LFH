package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;


@Entity
@UpdateMode(updateChangesOnly = false)
public class Attribute extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private Product product;


    @Constraints.Required
    private String value;

    @UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Attribute> find = new Finder<Integer, Attribute>(Integer.class, Attribute.class);

    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
