package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Date;


@Entity
@UpdateMode(updateChangesOnly = false)
public class CartHasProduct extends Model {

    @Embeddable
    public class CartHasProductId {
        public Integer productId;
        public Integer cartId;

        public int hashCode() {
            return productId + cartId * 100000;
        }

        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            if (!(obj instanceof CartHasProductId)) return false;
            CartHasProductId id = (CartHasProductId) obj;
            return id.productId == productId && id.cartId == cartId;
        }
    }

    @EmbeddedId
    public CartHasProductId id;

    @ManyToOne
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id", updatable = false, insertable = false)
    private Cart cart;

    @Constraints.Required
    private Double price;

    @Constraints.Required
    private Integer amount = 1;

    @Constraints.Required
    private Float discount = 0F;

    @UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return amount * (price - price * discount);
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
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

    public static CartHasProduct fetchByCartAndProduct(Cart cart, Product product) {
        return Ebean.find(CartHasProduct.class)
            .where()
            .eq("product_id", product.getId())
            .eq("cart_id", cart.getId())
            .findUnique();
    }

    @Override
    public void save() {
        id = new CartHasProductId();
        id.cartId = cart.getId();
        id.productId = product.getId();

        super.save();
    }
}
