package controllers;

import views.html.cart.*;
import play.mvc.Result;
import play.data.Form;
import views.html.cart.order;

import java.util.*;

public class Cart extends Eshomo {

    public static Result index() {
        models.Cart cart = getCurrentCart();

        return ok(index.render(cart));
    }

    public static Result indexById(Integer id) {
        models.Cart cart = models.Cart.find.byId(id);
        models.User cartUser = cart.getUser();
        models.User currentUser = getUserObj();

        if (cartUser == null || !cartUser.getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot access this cart");
        }

        return ok(index.render(cart));
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
        models.User user = getUserObj();
        models.Address address = user.getCurrentAddress();
        models.Cart cart = getCurrentCart();

        // add logic

        return ok(order.render(cart, address));
    }

    public static Result submitOrder() {
        models.User user = getUserObj();
        models.Address address = user.getCurrentAddress();
        models.Cart cart = getCurrentCart();

        // add logic

        return ok(order.render(cart, address));
    }

    /**
     * @return The current open cart of the logged in user.
     */
    private static models.Cart getCurrentCart() {
        models.User currentUser = getUserObj();
        return models.Cart.fetchOrCreateOpenCart(currentUser);
    }

    /**
     * Holds the form information of a cart update POST request.
     */
    public static class CartUpdate {
        public Integer remove;
        public Map<Integer, Integer> products = new HashMap<>();
    }
}
