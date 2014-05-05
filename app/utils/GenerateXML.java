// from http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/

package utils;
 
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

 
public class GenerateXML {
 
	public static String genXML ( 
		String prepItemName, 
		String prepItemExpirationDate, 
		int copies,
		String qrcodeFilePath, 
		String fileRoot ) {
 
 	String xmlFilename =  fileRoot + ".xml";

	  try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root element is labels list
		//
		Document doc = docBuilder.newDocument();
		Element labellist = doc.createElement("labels");
		doc.appendChild(labellist);

		//  Create N copies of the label
		//
		for(int x = 1; x <= copies; x = x+1) {

			// label element
			//
			Element label = doc.createElement("label");
			labellist.appendChild(label);
	 
			// itemName element
			Element itemName = doc.createElement("itemName");
			itemName.appendChild(doc.createTextNode(prepItemName));
			label.appendChild(itemName);
	 
			// expirationDate element
			Element expirationDate = doc.createElement("expirationDate");
			expirationDate.appendChild(doc.createTextNode(prepItemExpirationDate));
			label.appendChild(expirationDate);
	 
			// qrcode element
			Element qrcode = doc.createElement("qrcode");
			qrcode.appendChild(doc.createTextNode(qrcodeFilePath));
			label.appendChild(qrcode);
 
		}

 
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");

		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(new File(xmlFilename));
 
		transformer.transform(source, result);


 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	  finally {
	  	
		return xmlFilename;

	  }
	}

	public static String genXML ( 
		ArrayList<POLabelData> podata, 
		String fileRoot ) {
 
 	String xmlFilename =  fileRoot + ".xml";

	  try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root element is labels list
		//
		Document doc = docBuilder.newDocument();
		Element labellist = doc.createElement("labels");
		doc.appendChild(labellist);

		//
		//  Iterate through the array of individual item data.
		//  Each one can generate more than one label, as defined
		//  by the "copies" field.
		//
		for ( POLabelData data : podata ) {

			//  Create N copies of the label
			//
			for(int x = 1; x <= data.copies; x = x+1) {

				// label element
				//
				Element label = doc.createElement("label");
				labellist.appendChild(label);
		 
				// item name element
				Element itemName = doc.createElement("itemName");
				itemName.appendChild(doc.createTextNode(data.itemname));
				label.appendChild(itemName);
		 
				// vendor name element
				Element vendorName = doc.createElement("vendorName");
				vendorName.appendChild(doc.createTextNode(data.vendorname));
				label.appendChild(vendorName);
		 
				// pack size element
				Element packSize = doc.createElement("packSize");
				packSize.appendChild(doc.createTextNode(data.packsize));
				label.appendChild(packSize);
		 
				// qrcode element
				Element qrcode = doc.createElement("qrcode");
				qrcode.appendChild(doc.createTextNode(data.QRcodepath));
				label.appendChild(qrcode);
	 
			}


		}


 
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");

		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(new File(xmlFilename));
 
		transformer.transform(source, result);


 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	  finally {
	  	
		return xmlFilename;

	  }
	}	
}