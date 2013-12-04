package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;


@Entity
public class Product extends Model {

    @Id
    private Integer id;

    @Constraints.Required
    private String title;

    @Constraints.Required
    private Double price;   
    
    @Column
    private String description;

    @Constraints.Required
    private Long ean;
    

    @OneToMany
    private List<Attribute> attributes;
    
    @UpdatedTimestamp
    private Date updatedAt;


    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Product> find = new Finder<Integer, Product>(Integer.class, Product.class);

    public Integer getId() {
        return id;
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

    public void addToCart(Cart cart) {
        CartHasProduct rel = CartHasProduct.fetchByCartAndProduct(cart, this);

        if (rel == null) {
            rel = new CartHasProduct();
            rel.setProduct(this);
            rel.setCart(cart);
        } else {
            rel.setAmount(rel.getAmount() + 1);
        }

        rel.save();
    }

    public Boolean removeFromCart(Cart cart) {
        CartHasProduct rel = CartHasProduct.fetchByCartAndProduct(cart, this);

        if (rel != null) {
            rel.delete();
            return true;
        }

        return false;
    }
}
