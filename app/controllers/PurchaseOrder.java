package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import utils.*;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import com.google.zxing.WriterException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

public class PurchaseOrder extends Controller {

	public static Result PODisplay() {
		return ok(
		    views.html.purchaseordergen.render( "Purchase Order Demo")
		);	
	}

	public static Result LabelPrint() {
		
		//  Generate a tempfile name that the QRcode, XML, and PDF files
		//  will share.
		//
		String fileRoot = Labels.TempFilename("purchaseorder_");
		Logger.info("fileRoot " + fileRoot);

		//
		//   Get the PO number from the form.
		//
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
		final String poNumber = values.get("ponumber")[0];
		
		Logger.info("poNumber " + poNumber);


		//  Get the PO data for the given PO number
		//
		ArrayList<POLabelData> poData;
		poData = getPOItems(poNumber);

		Logger.info("num items = " + poData.size() );



		//  Generate a QR code for each item in the label list
		//
		String commonFileRoot = Play.application().path() + "/public/labels/tempfiles/" + fileRoot;
		int i = 0;

		for ( POLabelData data : poData ) {
			
			String decodeUrl = makeDecodeUrl( data );
			String qrcodePath = genQRCode( decodeUrl, commonFileRoot + "_qritem" + i );
			data.QRcodepath = qrcodePath;
			Logger.info("qrcodePath " + qrcodePath);
			i++;
		}


	    	
		//  Construct the label data XML file that drives the label PDF
		//  generation using the array of PO label info.
		//
		String xmlFilePath = GenerateXML.genXML( poData, commonFileRoot );
		Logger.info("xmlFilePath " + xmlFilePath);



		//  Finally, generate the labels print PDF using the
		//  label data XML file
		//
		String xslFilePath = Play.application().path() + "/public/labels/stylesheets/purchaseorderlabel.xsl";
		
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

		String vendorName = request().getQueryString("vendor");
		String itemName = request().getQueryString("item");
		String packSize = request().getQueryString("packsize");

		return ok(views.html.purchaseorderscan.render(vendorName, itemName, packSize));

	}

	private static String makeDecodeUrl( POLabelData data ) {

		//
		//  Decode url should contain the vendor,  item, and packsize as params,
		//  and the URL is that of the Scan method.
		//
		String decodeUrl = routes.PurchaseOrder.Scan().absoluteURL(request()) 
				+ "?vendor=" + data.vendorname 
				+ "&item=" + data.itemname 
				+ "&packsize=" + data.packsize;
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

	static ArrayList<POLabelData> getPOItems( String poNumber ) {

		String poDataFile = Play.application().path() + "/public/data/podata.json";
		String poData = null;

		try{
			poData = readFile(  poDataFile, StandardCharsets.UTF_8 );
		}
		catch (IOException e)
		{
			Logger.info(e.getMessage());
		}

		//  Turn the data into json for processing
		//
		JsonNode  poJson = Json.parse( poData );

		//  Cycle through all the PO nodes looking for the
		//  selected item.
		// 
		ArrayList<POLabelData> poLabelData = new ArrayList<POLabelData>();

		Iterator<JsonNode> ite = poJson.elements();

		Logger.info("PO Number search : " + poNumber );
		while (ite.hasNext()) {
			JsonNode poTopNode = ite.next();
			JsonNode poNumberNode = poTopNode.path("PONumber");
			JsonNode poVendorNode = poTopNode.path("Vendor");
			JsonNode poItemsNode = poTopNode.path("OrderItems");

			Logger.info("PO stuff : " + poNumberNode.textValue() + " " + 
				poVendorNode.textValue() );

			if ( poNumberNode.textValue().equals(poNumber) ) {
				//  Found the desired PO number node.
				//  Now iterate through the OrderItems data
				//
				Logger.info("Found PO!");
				Iterator<JsonNode> iteItems = poItemsNode.elements();


				while (iteItems.hasNext()) {
					JsonNode itemTopNode = iteItems.next();
					JsonNode itemDescriptionNode = itemTopNode.path("Description");
					JsonNode itemPackSizeNode = itemTopNode.path("PackSize");
					JsonNode itemQtyNode = itemTopNode.path("Qty");

					Logger.info("PO Item stuff : " + itemDescriptionNode.textValue() + " " +
						itemPackSizeNode.textValue() + " "  + itemQtyNode.textValue() );

					POLabelData matchdata = new POLabelData();
					matchdata.vendorname = poVendorNode.textValue();
					matchdata.itemname = itemDescriptionNode.textValue();
					matchdata.packsize = itemPackSizeNode.textValue();
					matchdata.copies = Integer.parseInt(itemQtyNode.textValue());

					poLabelData.add( matchdata );

				}

				return poLabelData;

			} else {
				Logger.info("No Match this time");
			}
		}

		//  Nothing found.
		//
		return null;
	}

	//  Get the PO json data 
	//
	static String readFile(String path, Charset encoding) 
		throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}	

}