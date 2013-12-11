package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Address;

import com.avaje.ebean.Ebean;

import play.api.templates.Html;
import play.data.DynamicForm;
import play.mvc.Result;
import views.html.country.*;

/**
 * implements functionality for edit/add/remove/update country
 *
 * @author boe
 */
public class Country extends Eshomo {
    /**
     * renders the html of a list of all countries
     *
     * @return
     * @author boe
     */
    public static Result list() {
        if (isLoggedIn() && isAdminUser()) {
            return ok(
                list.render(getCountries(), "", "")
            );
        } else {
            return forbidden();
        }
    }

    /**
     * update the data of a country
     *
     * @param id country id
     * @return
     * @author boe
     */
    public static Result modify(final Integer id) {
        if (isLoggedIn() && isAdminUser()) {
            String message = "";
            models.Country country = null;
            if (!id.equals(0)) {
                country = models.Country.find.byId(id);
            } else {
                country = new models.Country();
            }
            DynamicForm bindedForm = form().bindFromRequest();
            String countryName = bindedForm.get("name");
            country.setName(countryName);

            Ebean.beginTransaction();
            try {
                Ebean.save(country);
                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            message = countryName.concat(" erfolgreich gespeichert (ID: ").concat(String.valueOf(country.getId())).concat(")");

            Html html = null;
            if (id.equals(0)) {
                html = list.render(getCountries(), message, "info");
            } else {
                html = mask.render(form(models.Country.class).fill(country), country.getId(), message, "info");
            }

            return ok(
                html
            );
        } else {
            return forbidden();
        }
    }

    /**
     * deletes a country
     *
     * @param id country id
     * @return
     * @author boe
     */
    public static Result delete(final Integer id) {
        if (isLoggedIn() && isAdminUser()) {
            String message = "";
            String messageType = "";

            List<Address> addresses = Ebean.createQuery(Address.class).where().eq("country_id", id.toString()).findList();

            if (addresses.size() == 0) {
                models.Country country = models.Country.find.byId(id);
                String countryName = country.getName();
                Ebean.beginTransaction();
                try {
                    Ebean.delete(country);
                    Ebean.commitTransaction();
                } finally {
                    Ebean.endTransaction();
                }
                message = countryName.concat(" erfolgreich gelöscht");
                messageType = "info";
            } else {
                message = "Mit diesem Land sind noch Adressen verknüpft (".concat(String.valueOf(addresses.size())).concat(")");
                messageType = "info";
            }

            return ok(
                list.render(getCountries(), message, messageType)
            );
        } else {
            return forbidden();
        }
    }

    /**
     * renders form to edit an country
     *
     * @param id
     * @return
     * @author boe
     */
    public static Result edit(final Integer id) {
        if (isLoggedIn() && isAdminUser()) {
            models.Country country = null;
            Integer countryId = id;
            if (!id.equals(0)) {
                country = models.Country.find.byId(id);

            } else {
                country = new models.Country();
                countryId = 0;
            }

            return ok(
                mask.render(form(models.Country.class).fill(country), countryId, "", "")
            );
        } else {
            return forbidden();
        }
    }

    /**
     * selects all countries
     *
     * @return
     */
    private static List<models.Country> getCountries() {
        return models.Country.find.findList();
    }
}
