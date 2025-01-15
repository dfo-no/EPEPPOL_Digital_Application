package no.dfo.peppol.common.functions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CommonFunction {
	public byte[] loadFileAsBinary(File initialFile) throws IOException {
		InputStream targetStream = new FileInputStream(initialFile);
		 byte[] b = new byte[1024];
		    ByteArrayOutputStream os = new ByteArrayOutputStream();
		    int c;
		    while ((c = targetStream.read(b)) != -1) {
		        os.write(b, 0, c);
		    }
		    return os.toByteArray();
		
	}
	
	public ArrayList<String> listFilesUsingJavaIO(String dir) {
	      
	        ArrayList<String>filelistname=new ArrayList<String>();
	      File directory = new File(dir); 
	        
	      File[] files = directory.listFiles(); 
	        
	      if (files != null) { 
	        for (File file : files) { 
	          System.out.println(file.getName());
	          filelistname.add(file.getName());
	          
	}

}
	      return filelistname;
	}
	
}
