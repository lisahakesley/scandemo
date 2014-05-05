  //  from http://www.javacodegeeks.com/2012/10/generate-qr-code-image-from-java-program.html

package utils;

import play.*;
import play.mvc.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class GenerateQRCode {

 public static String genImage( String stringToEncode,  String fileRoot ) throws WriterException, IOException  {

  String filePath = fileRoot + ".png";
  int size = 100;
  String fileType = "png";
  File qrFile = new File(filePath);
  createQRImage(qrFile, stringToEncode, size, fileType);

  return filePath;

}

private static void createQRImage(File qrFile, String qrCodeText, int size,
 String fileType) throws WriterException, IOException {
    // Create the ByteMatrix for the QR-Code that encodes the given String
  Hashtable hintMap = new Hashtable();
  hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
  hintMap.put(EncodeHintType.MARGIN, 3);
  QRCodeWriter qrCodeWriter = new QRCodeWriter();
  BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
    BarcodeFormat.QR_CODE, size, size, hintMap);
    // Make the BufferedImage that are to hold the QRCode
  int matrixWidth = byteMatrix.getWidth();
  BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
    BufferedImage.TYPE_INT_RGB);
  image.createGraphics();

  Graphics2D graphics = (Graphics2D) image.getGraphics();
  graphics.setColor(Color.WHITE);
  graphics.fillRect(0, 0, matrixWidth, matrixWidth);
    // Paint and save the image using the ByteMatrix
  graphics.setColor(Color.BLACK);

  for (int i = 0; i < matrixWidth; i++) {
   for (int j = 0; j < matrixWidth; j++) {
    if (byteMatrix.get(i, j)) {
     graphics.fillRect(i, j, 1, 1);
   }
 }
}
ImageIO.write(image, fileType, qrFile);
}

}