package controllers;

import models.*;
import views.html.cart.*;
import play.mvc.Result;
import play.data.Form;
import java.util.*;
import com.typesafe.plugin.*;

public class Cart extends Eshomo {

    public static Result index() {
        models.Cart cart = getCurrentCart();

        return ok(index.render(cart));
    }

    public static Result indexById(Integer id) {
        models.Cart cart = models.Cart.find.byId(id);
        models.User cartUser = cart.getUser();
        models.User currentUser = getLoggedInUser();

        if (cartUser == null || !cartUser.getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot access this cart");
        }

        return ok(indexOrdered.render(cart));
    }

    public static Result update() {
        models.Cart cart = getCurrentCart();

        // get the request params as a CartUpdate
        Form<CartUpdate> form = Form.form(CartUpdate.class);
        CartUpdate update = form.bindFromRequest().get();

        // update the amount of every product in the cart
        for (Map.Entry<Integer, Integer> entry : update.products.entrySet()) {
            Integer productId = entry.getKey();
            Integer amount = entry.getValue();

            models.Product product = models.Product.find.byId(productId);
            cart.setProduct(product, amount);
        }

        // remove an element if the user wants to
        if (update.remove != null) {
            models.Product product = models.Product.find.byId(update.remove);
            cart.removeProduct(product);
        }

        return ok(index.render(cart));
    }

    public static Result order() {
        models.User user = getLoggedInUser();
        models.Address address = user.getCurrentAddress();
        models.Cart cart = getCurrentCart();

        return ok(order.render(cart, address));
    }

    public static Result submitOrder() {
        models.User user = getLoggedInUser();
        models.Address address = user.getCurrentAddress();
        models.Cart cart = getCurrentCart();

        // get the request params as a CartUpdate
        Form<CartOrder> form = Form.form(CartOrder.class);
        CartOrder order = form.bindFromRequest().get();

        // render email
        String subject = "Bestellung LFH Shop";
        String content;
        if (order.type.equals("invoice")) {
            content = mailInvoice.render(subject, cart, address).toString();
        } else {
            content = mailPrepayment.render(subject, cart, address).toString();
        }

        // get admin's email address
        String adminEmail = play.Play.application().configuration().getString("email.admin");

        // send email to store admin and current user
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject(subject);
        mail.setFrom(adminEmail);
        mail.setRecipient(adminEmail, user.getEmail());
        mail.sendHtml(content);

        // mark cart as ordered
        cart.setStatusOrdered();
        cart.save();

        return redirect("/");
    }

    public static Result history() {
        List<models.Cart> carts = models.Cart.find
            .where()
            .eq("user_id", getLoggedInUserId())
            .eq("status_id", CartStatus.ORDERED)
            .findList();

        return ok(history.render(carts));
    }

    /**
     * @return The current open cart of the logged in user.
     */
    private static models.Cart getCurrentCart() {
        models.User currentUser = getLoggedInUser();
        return models.Cart.fetchOrCreateOpenCart(currentUser);
    }

    /**
     * Holds the form information of a cart update POST request.
     */
    public static class CartUpdate {
        public Integer remove;
        public Map<Integer, Integer> products = new HashMap<>();
    }

    /**
     * Holds the form information of a cart order POST request.
     */
    public static class CartOrder {
        public String type;
    }
}
