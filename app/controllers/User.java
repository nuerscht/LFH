package controllers;

import static play.data.Form.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.avaje.ebean.Query;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

import models.Address;
import models.Cart;
import models.CartHasProduct;
import models.CartStatus;
import models.Country;
import models.UserType;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.user.transaction;
import views.html.user.userdata;

/**
 * controller for user functionality
 *
 * @author boe
 */
public class User extends UserData {

    /**
     * helper class to display the user list
     *
     * @author boe
     */
    public static class UserHelper {
        public Integer id;
        public String email;
        public String lastname;
        public String firstname;
        public String usertype;
        public Boolean isActive;
    }

    /**
     * show the full user list
     *
     * @return
     * @author boe
     */
    public static Result list() {
        if (isLoggedIn() && isAdminUser()) {

            List<models.User> users = Ebean.find(models.User.class).where().eq("deleted", false).findList();

            return listUsers(users);
        } else {
            return forbidden();
        }
    }

    /**
     * merges the users into an List<UserHelper>
     *
     * @param users
     * @return
     * @author boe
     */
    protected static Result listUsers(List<models.User> users) {
        List<UserHelper> userHelpers = new ArrayList<UserHelper>();
        Iterator<models.User> itrUsers = users.iterator();
        while (itrUsers.hasNext()) {
            models.User user = itrUsers.next();

            Address address = getAddressByUserId(user.getId());

            UserHelper userHelper = new UserHelper();
            userHelper.id = user.getId();
            userHelper.email = user.getEmail();
            userHelper.lastname = address.getLastname();
            userHelper.firstname = address.getFirstname();
            userHelper.usertype = user.getType().getId();
            userHelper.isActive = user.isActive();

            userHelpers.add(userHelper);
        }


        return ok(
            views.html.user.list.render(userHelpers)
        );
    }

    /**
     * sets the deleted flag for the given userid
     *
     * @param userid
     * @return
     * @author boe
     */
    public static Result delete(final int userid) {
        if (isLoggedIn() && isAdminUser()) {
            models.User user    = getUserByUserId(userid);
            Address     address = getAddressByUserId(user.getId());

            user.setDeleted(true);
            address.setDeleted(true);
            user.setIsActive(false);
            address.setIsActive(false);

            Ebean.beginTransaction();
            try {
                Ebean.save(user);
                Ebean.save(address);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            //if the changed user is the current user
            if (getLoggedInUserId().equals(userid)) {
                userLogout();
                return Application.index();
            }

            return list();
        } else {
            return forbidden();
        }
    }


    /**
     * changes type of the user with the given userid
     *
     * @param userid
     * @return
     * @author boe
     */
    public static Result changeUserType(final int userid) {
        if (isLoggedIn() && isAdminUser()) {
            models.User user = getUserByUserId(userid);

            UserType userType = null;
            if (user.getType().getId().equals(UserType.ADMIN))
                userType = UserType.find.byId(UserType.CUSTOMER);
            else
                userType = UserType.find.byId(UserType.ADMIN);

            user.setType(userType);

            Ebean.beginTransaction();
            try {
                Ebean.save(user);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            return list();
        } else {
            return forbidden();
        }
    }

    /**
     * changes status of the user with the given userid
     *
     * @param userid
     * @return
     * @author boe
     */
    public static Result changeStatus(final int userid) {
        if (isLoggedIn() && isAdminUser()) {
            models.User user = getUserByUserId(userid);

            user.setIsActive(!user.isActive());

            Ebean.beginTransaction();
            try {
                Ebean.save(user);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            //if the changed user is the current user
            if (getLoggedInUserId().equals(userid)) {
                userLogout();
                return Application.index();
            }

            return list();
        } else {
            return forbidden();
        }
    }


    /**
     * handles search in user list
     *
     * @param userid
     * @return
     * @author boe
     */
    public static Result search() {
        if (isLoggedIn() && isAdminUser()) {
            List<models.User> users = null;
            DynamicForm bindedForm = form().bindFromRequest();
            String searchString = bindedForm.get("keyword");
            StringBuilder strB = new StringBuilder();
            strB.append("%");
            strB.append(searchString);
            strB.append("%");
            String searchStringLike = strB.toString();

            List<Address> addresses = Ebean.find(Address.class).where().or(
                Expr.like("firstname", searchStringLike),
                Expr.like("lastname", searchStringLike)
            ).where().eq("deleted", false).findList();


            if (addresses.size() > 0) {
                users = new ArrayList<models.User>();
                Iterator<Address> itrAddress = addresses.iterator();
                while (itrAddress.hasNext()) {
                    Address address = itrAddress.next();
                    models.User user = getUserByUserId(address.getUser().getId());

                    users.add(user);
                }
            } else {
                users = Ebean.find(models.User.class).where().or(
                    Expr.eq("id", searchString),
                    Expr.like("email", searchStringLike)
                ).findList();
            }


            return listUsers(users);
        } else {
            return forbidden();
        }
    }

    public static Result showDataCurrent() {
        models.User user = getLoggedInUser();

        return showData(user.getId());
    }

    /**
     * shows user data (login/address) in backend
     *
     * @return
     * @author boe
     */
    public static Result showData(final int userid) {
        if ((isLoggedIn() && getLoggedInUserId().equals(userid)) ||
            (isLoggedIn() && !getLoggedInUserId().equals(userid) && isAdminUser())) {
            models.User user = getUserByUserId(userid);

            Address address = getAddressByUserId(user.getId());

            String strCountry = getCountry(address);

            return ok(
                userdata.render(form(models.User.class).fill(user)
                    , form(Address.class).fill(address), strCountry
                    , "", ""
                    , userid == getLoggedInUserId() && isAdminUser())
            );
        } else {
            return forbidden();
        }
    }

    protected static String getCountry(Address address) {
        Country country = address.getCountry();

        String strCountry = "1";
        if (country != null)
            strCountry = country.getId().toString();
        return strCountry;
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

    private static models.User getUserByUserId(final int userid) {
        models.User user = models.User.find.byId(userid);

        return user;
    }

    /**
     * handles update requests for user data updates
     *
     * @return
     * @author boe
     */
    public static Result updateData(final int userid) {
        if ((isLoggedIn() && getLoggedInUserId().equals(userid)) ||
            (isLoggedIn() && !getLoggedInUserId().equals(userid) && isAdminUser())) {
            String message = "";
            DynamicForm bindedForm = form().bindFromRequest();

            models.User user = getUserByUserId(userid);
            Address address = getAddressByUserId(user.getId());


            //if new address
            if (address.getUser() == null)
                address.setUser(user);

            //has Password changed
            if (bindedForm.get("password") != null && !bindedForm.get("password").isEmpty()) {
                message = validatePassword(address, user, bindedForm);

                String strCountry = getCountry(address);

                if (!message.isEmpty()) {
                    return ok(
                        userdata.render(form(models.User.class).fill(user)
                            , form(Address.class).fill(address)
                            , strCountry, message, "info"

                            , userid == getLoggedInUserId() && isAdminUser())
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

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                address.setBirthday(dateFormat.parse(bindedForm.get("birthday")));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Country country = Country.find.byId(Integer.decode(bindedForm.get("country")));
            address.setCountry(country);


            String strCountry = getCountry(address);

            Ebean.beginTransaction();
            try {
                Ebean.save(user);
                Ebean.save(address);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            return ok(
                userdata.render(form(models.User.class).fill(user)
                    , form(Address.class).fill(address)
                    , strCountry
                    , "Ihr Daten wurde erfolgreich aktualisiert."
                    , "success"

                    , userid == getLoggedInUserId() && isAdminUser())
            );
        } else {
            return forbidden();
        }
    }

    /**
     * helper class to display transactions
     *
     * @author boe
     */
    public static class Order {
        public Integer id;
        public String date;
        public String status;
        public String price;
        public models.User user;
        public Integer amount;
    }

    /**
     * shows the orders for the logged in user
     *
     * @return
     */
    public static Result showTransactions(final int userid) {
        if (isAdminUser()) {
            Query<Cart> cartsQuery = Ebean.find(Cart.class);

            // get orders for all (userid = -1) or one user in particular
            if (userid < 0) {
                cartsQuery.where().eq("status_id", CartStatus.ORDERED).orderBy().asc("updated_at").findList();
            } else {
                cartsQuery.where().eq("user_id", userid).where().eq("status_id", CartStatus.ORDERED).orderBy().asc("updated_at").findList();
            }

            List<Cart> carts = cartsQuery.findList();

            return ok(transaction.render(carts));
        } else {
            return forbidden();
        }
    }
}
