package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import utils.Labels;

public class Application extends Controller {

    public static Result index() {
    		//  Clean up temp label files.
    		//
 		String tempFiles = Play.application().path() + "/public/labels/tempfiles/";
   		Labels.CleanUpFiles(tempFiles);
        	return ok(index.render());
    }

}
