package controllers;

import play.mvc.Result;

public class Cart extends Eshomo {

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
}
