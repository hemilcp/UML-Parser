package ProjectPackage;

import java.io.File;
import java.io.IOException;

import com.github.javaparser.ParseException;

public class ProjMain {

	public static void main(String args[]) throws IOException, ParseException
	{
		final String targetDirectory = args[0];
		String outputfile = args[1];
		ParseTheFile parseTheFile = new ParseTheFile();
		ParserStart parserStart = new ParserStart();
		File[] list = parserStart.finder(targetDirectory);
		for(int i=0;i<list.length;i++){
			parseTheFile.findElements(list[i]);
		}
	    parseTheFile.findIfPublic();
	    parseTheFile.writeToFile(outputfile);
	    parseTheFile.CreatePNG(outputfile);
	}
		
}
