package utils;

import java.text.*;
import java.util.*;
import java.io.File;
import play.*;

public class Labels {

	public static String TempFilename( String prefix ) {

		//
		//  Construct unique filenames for the QRCode, XML, and PDF files
		//
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String tempfile = prefix + dateFormat.format(date);

		return tempfile;
	}

	public static void CleanUpFiles( String dirName ) {

		final File directory = new File(dirName);
		if(directory.exists()){
			Logger.info("dirName " + dirName);
			final File[] listFiles = directory.listFiles();          
			Calendar cal = Calendar.getInstance();  
			cal.add(Calendar.DAY_OF_MONTH, -1);  
			long purgeTime = cal.getTimeInMillis();   	        
			Logger.info("purgeTime " + purgeTime);

			for(File listFile : listFiles) {
				Logger.info("listFile.lastModified()  " + listFile.lastModified() );
				if(listFile.lastModified() < purgeTime) {
					if(!listFile.delete()) {
					//
					}
				}
			}
		} 

	}
}
