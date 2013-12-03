package controllers;

import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.avaje.ebean.Ebean;

import models.Address;
import models.Cart;
import models.CartHasProduct;
import models.CartStatus;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.user.transaction;
import views.html.user.userdata;

/**
 * controller for user functionality
 * @author boe
 */
public class User extends UserData {
	
	/**
	 * shows user data (login/address) in backend
	 * @author boe
	 * @return
	 */
	public static Result showData() {
		if (isLoggedIn()) {		
			models.User user = getUserObj();
		
			
			Address address = getAddressByUserId(user.getId());


			return ok (
					userdata.render(form(models.User.class).fill(user), form(Address.class).fill(address), "", "", getLoginContent())
					);
		} else {
			return forbidden();
		}
	}
	
	private static Address getAddressByUserId(final int userid) {
		List<Address> addresses = Ebean.find(Address.class).where().eq("user_id", userid).where().eq("is_active", 1).findList();
		
		Address address = null;
		if (addresses.size() > 0) {
			address = addresses.get(0);
		} else {
			address = new Address();
		}
		
		return address;
	}
	
	/**
	 * handles update requests for user data updates
	 * @author boe
	 * @return
	 */
	public static Result updateData() {
		if (isLoggedIn()) {
			String      message     = "";
    		DynamicForm bindedForm  = form().bindFromRequest();
    		    		
			models.User user = getUserObj();			
			Address address = getAddressByUserId(user.getId());
			

			//if new address
			if (address.getUser() == null)
				address.setUser(user);
			
			//has Password changed
			if (bindedForm.get("password") != null && !bindedForm.get("password").isEmpty()) {
		    	message = validatePassword(address, user, bindedForm);
				
				if (!message.isEmpty()) {
					return ok(
						userdata.render(form(models.User.class).fill(user), form(Address.class).fill(address), message, "info", getLoginContent())
					);
				}
				
				user.setPassword(bindedForm.get("password"));
			}
			
			address.setFirstname(bindedForm.get("firstname"));
			address.setLastname(bindedForm.get("lastname"));
			address.setPlace(bindedForm.get("place"));
			address.setStreet(bindedForm.get("street"));
			address.setZip(bindedForm.get("zip"));
			address.setIsActive(true);
	    	
	    	Ebean.beginTransaction();
	    	try {
	    		Ebean.save(user);
	    		Ebean.save(address);
	    		Ebean.commitTransaction();
	    	} finally {
	    		Ebean.endTransaction();
	    	}
			
	    	return ok(
	    			userdata.render(form(models.User.class).fill(user), form(Address.class).fill(address), "Ihr Daten wurde erfolgreich aktualisiert.", "success", getLoginContent())
	    	);
		} else {
			return forbidden();
		}
	}
	
	/**
	 * helper class to display transactions
	 * @author boe
	 */
	public static class Order {
		public Integer    id;
		public String     date;
		public String	  status;
		public String     price;
	}
	
	/**
	 * shows the orders for the logged in user
	 * @return
	 */
	public static Result showTransactions() {
		if (isLoggedIn()) {
			List<Cart> carts = Ebean.find(Cart.class).where().eq("user_id", getLoggedInUserId()).where().eq("status_id", CartStatus.ORDERED).orderBy().asc("updated_at").findList();
	
			List<Order> orders = new ArrayList<Order>();
			
			Iterator<Cart> itrCarts= carts.iterator();
			while (itrCarts.hasNext()) {
				Cart cart = itrCarts.next();
				
				Order order  = new Order();
				order.id     = cart.getId();
				order.date   = new SimpleDateFormat("dd.MM.yyyy").format(cart.getUpdatedAt());
				order.status = cart.getStatus().getDescription().toString();
				
				List<CartHasProduct> cartDetails = Ebean.find(CartHasProduct.class).where().eq("cart_id", cart.getId()).findList();
				
				Double price = 0.0;
				Iterator<CartHasProduct> itrCartDetails = cartDetails.iterator();
				while (itrCartDetails.hasNext()) {
					CartHasProduct cartDetail = itrCartDetails.next();
					
					price += cartDetail.getPrice() * cartDetail.getAmount();
				}
				
				order.price = String.format("%1$,.2f", price);
				orders.add(order);
			}
			
			return ok(
				transaction.render(orders, getLoginContent())
			);
		} else {
			return forbidden();
		}
	}
}
