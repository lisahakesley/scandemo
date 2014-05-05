package controllers;

import play.*;
import play.data.*;
import play.mvc.*;

import views.html.*;

import utils.*;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import com.google.zxing.WriterException;
import java.util.Map;


public class PrepRecipe extends Controller {

	public static Result LabelGen() {
	    return ok(
	        views.html.preplabelgen.render( "Prep Recipe Label Demo")
	    );
	}

	public static Result LabelPrint() {
		
		//  Generate a tempfile name that the QRcode, XML, and PDF files
		//  will share.
		//
		String fileRoot = Labels.TempFilename("preprecipe_");
		Logger.info("fileRoot " + fileRoot);

		//
		//  Get the form values for item name, expiration date, and
		//  number of copies
		//
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
		final String itemName = values.get("itemname")[0];
		final String expirationDate = values.get("expirationdate")[0];
		final String labelCount = values.get("labelcount")[0];
		
		Logger.info("itemName " + itemName);
		Logger.info("expirationDate " + expirationDate);
		Logger.info("labelCount " + labelCount);


		//  Generate a QR code image using the fields from the form
		//
		String commonFileRoot = Play.application().path() + "/public/labels/tempfiles/" + fileRoot;
		
		String decodeUrl = makeDecodeUrl( itemName, expirationDate);
		String qrcodePath = genQRCode( decodeUrl, commonFileRoot );
		Logger.info("qrcodePath " + qrcodePath);


	    	
		//  Construct the label data XML file that drives the label PDF
		//  generation.
		//
		int copies = Integer.parseInt(labelCount);
		String xmlFilePath = GenerateXML.genXML( 
			itemName, expirationDate, copies, qrcodePath, commonFileRoot );


		//  Finally, generate the labels print PDF using the
		//  label data XML file
		//
		String xslFilePath = Play.application().path() + "/public/labels/stylesheets/preprecipelabel.xsl";
		
		String pdfFile  = commonFileRoot + ".pdf";
		try
		{
			GeneratePDF.genPDF( xslFilePath, xmlFilePath, pdfFile );
		}
		catch (Exception e)
		{
			Logger.info(e.getMessage());
		}

		Logger.info("PDF file at " + pdfFile);

		Result pageResult;
		try {
			pageResult = ok(new FileInputStream(pdfFile)).as("application/pdf");
		}
		catch ( FileNotFoundException e ){
			pageResult = internalServerError("PDF file not found; cannot generate PDF stream.");
		}

		return pageResult;
	}

	public static Result Scan() {

		String itemName = request().getQueryString("itemname");
		String expDate = request().getQueryString("expirationdate");

		return ok(views.html.preplabelscan.render(itemName, expDate));

	}

	private static String makeDecodeUrl( String itemName, String expirationDate ) {

		//
		//  Decode url should contain the item name and expiration date as param,
		//  and the URL is that of the Scan method.
		//
		String decodeUrl = routes.PrepRecipe.Scan().absoluteURL(request()) + "?itemname=" +
				itemName + "&expirationdate=" + expirationDate;
		decodeUrl = decodeUrl.replace("localhost", "192.168.1.15");
		Logger.info("makeDecodeUrl " + decodeUrl);

		return decodeUrl;
	}

	//  Generate a QR code image file and return the file path
	//
	private static String  genQRCode( String decodeUrl, String fileRoot ) {

		//  Now generate the image file and return the URL
		String filePath = "";

		try
		{
			filePath = GenerateQRCode.genImage( decodeUrl, fileRoot );
		}
		catch (WriterException we) {
			Logger.info(we.getMessage());
		}
		catch (IOException ioe) {
			Logger.info(ioe.getMessage());
		}

      		return filePath;
	}

}