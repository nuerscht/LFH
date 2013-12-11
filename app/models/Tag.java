package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Entity
@UpdateMode(updateChangesOnly = false)
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

    public List<Product> getProducts() {
        String sql
            = " select product.id, product.title, product.price, product.description, product.ean, product.updated_at, product.created_at"
            + " from product"
            + " join product_has_tag on product_has_tag.product_id = product.id"
            + " group by product.id ";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();

        return Ebean.find(Product.class)
            .setRawSql(rawSql)
            .where()
            .eq("product_has_tag.tag_id", getId())
            .findList();
    }
}
