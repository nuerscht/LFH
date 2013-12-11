package controllers;

import models.*;
import play.mvc.Result;
import play.data.Form;
import play.data.DynamicForm;

import java.util.*;

public class Cart extends Eshomo {

    public static class CartUpdate {
        public Integer remove;
        public Map<Integer, Integer> products = new HashMap<>();
    }

    public static Result index() {
        models.User currentUser = getUserObj();
    	models.Cart cart = models.Cart.fetchOrCreateOpenCart(currentUser);

        return ok(views.html.cart.index.render(cart));
    }

    public static Result indexById(Integer id) {
        models.Cart cart = models.Cart.find.byId(id);
        models.User cartUser = cart.getUser();
        models.User currentUser = getUserObj();

        if (cartUser != null && !cartUser.getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot access this cart");
        }

        return ok(views.html.cart.index.render(cart));
    }

    public static Result update() {
        models.User currentUser = getUserObj();
        models.Cart cart = models.Cart.fetchOrCreateOpenCart(currentUser);

        // get the request params as a CartUpdate
        Form<CartUpdate> form = Form.form(CartUpdate.class);
        CartUpdate update = form.bindFromRequest().get();

        // update the amount of every product in the cart
        for(Map.Entry<Integer, Integer> entry : update.products.entrySet()) {
            Integer productId = entry.getKey();
            Integer amount = entry.getValue();

            models.Product product = models.Product.find.byId(productId);
            product.setToCart(cart, amount);
        }

        // remove an element if the user wants to
        if (update.remove != null) {
            models.Product product = models.Product.find.byId(update.remove);
            product.removeFromCart(cart);
        }

        return ok(views.html.cart.index.render(cart));
    }
}
