package ProjectPackage;

import java.io.File;
import java.io.FilenameFilter;

public class ParserStart {

	public File[] finder(String dirName){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".java");  }
    	} );

    }	
	
}
