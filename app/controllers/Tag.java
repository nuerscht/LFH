package controllers;

import com.avaje.ebean.Ebean;
import models.Cart;
import models.Rating;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Result;

import java.util.List;

public class Tag extends Eshomo {

    /**
     * @return Tag sidebar
     */
    public static Html getSidebarHtml() {
        List<models.Tag> tags = models.Tag.find.all();
        return views.html.tag.sidebar.render(tags);
    }
}
