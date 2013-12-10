package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.*;

import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@UpdateMode(updateChangesOnly=false)
public class Cart extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Address address;

    @ManyToOne
    private CartStatus status;

    @UpdatedTimestamp
    private Date updatedAt;
    
    @OneToMany(cascade=CascadeType.ALL)
    private List<CartHasProduct> cartHasProduct;
    
    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Cart> find = new Finder<Integer, Cart>(Integer.class, Cart.class);

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /**
	 * @return the cartHasProduct
	 */
	public List<CartHasProduct> getCartHasProduct() {
		return cartHasProduct;
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

    /**
     * Fetches a new open Cart from the database if one exists.
     * If no open Cart has been found it creates a new one.
     *
     * @param user The current logged in user.
     * @return
     */
    public static Cart fetchOrCreateOpenCart(User user) {
        Cart cart = Ebean.find(Cart.class)
                .where()
                .eq("user_id", user.getId())
                .eq("status_id", CartStatus.OPEN)
                .findUnique();

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setStatus(CartStatus.find.byId(CartStatus.OPEN)); // it's okay if this throws when status not found
            cart.save();
        }

        return cart;
    }
}
