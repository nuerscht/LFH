package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;


@Entity
@UpdateMode(updateChangesOnly = false)
public class Product extends Model {

    @Id
    private Integer id;

    @Constraints.Required
    private String title;

    @Constraints.Required
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Constraints.Required
    private Long ean;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Attribute> attributes;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Image> images;

    @UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Product> find = new Finder<Integer, Product>(Integer.class, Product.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEan() {
        return ean;
    }

    public void setEan(Long ean) {
        this.ean = ean;
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void addImage(Image image) {
        this.images.add(image);
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

    public List<Rating> getRatings() {
        return Rating.find.where().eq("product_id", this.getId()).orderBy("updatedAt desc").findList();
    }

    public List<Image> getImages() {
        return images;
    }

    public boolean hasImage() {
        return this.images.size() > 0;
    }

    /**
     * Return a page of computer
     *
     * @param page     Page to display
     * @param pageSize Number of products per page
     * @param sortBy   Product property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the products
     */
    public static Page<Product> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
            find.where()
                .or(Expr.like("Title", "%" + filter + "%"),
                    Expr.like("Description", "%" + filter + "%"))
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }
}
