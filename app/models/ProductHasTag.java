package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
@UpdateMode(updateChangesOnly = false)
public class ProductHasTag extends Model {

    @ManyToOne
    private Product product;

    @ManyToOne
    private Tag tag;

    @CreatedTimestamp
    private Date createdAt;

    public static Finder<String, ProductHasTag> find = new Finder<String, ProductHasTag>(String.class, ProductHasTag.class);
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
