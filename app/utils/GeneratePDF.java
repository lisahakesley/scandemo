//  From Apache FOP embedded java examples.

package utils;

//Java
import java.io.File;
import java.io.OutputStream;

//JAXP
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

//FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
 
 import play.Logger;
 
 
public class GeneratePDF
{


    public static void genPDF( String xslFilePath, String xmlFIlePath, String pdfFilePath ) throws Exception
    {
  
            // Setup input and output files
            File xmlfile = new File(xmlFIlePath);
            File xsltfile = new File(xslFilePath);
            File pdffile = new File(pdfFilePath);

            Logger.info("Input: XML (" + xmlfile + ")");
            Logger.info("Stylesheet: " + xsltfile);
            Logger.info("Output: PDF (" + pdffile + ")");
            Logger.info("");
            Logger.info("Transforming...");

 
            // configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance();

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);

            } finally {
                out.close();
            }

    }
 
}

