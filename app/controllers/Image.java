package controllers;

import java.io.IOException;
import java.util.Arrays;

import customactions.LogLevel;
import play.mvc.Result;

public class Image extends Eshomo {
    
    /**
     * loads the byte code from db and returns it to browser
     * @author boe
     * @param id image id to serve
     * @return
     */
    public static Result get(final int id) {
        try {
            models.Image image = models.Image.find.byId(id);
        
            return ok(image.getDataAsFile());
        } catch (IOException e) {
            logger.logToFile(e.getMessage() + "=>" + Arrays.toString(e.getStackTrace()), LogLevel.ERROR, "application");
            return internalServerError();
        }
    }
}
