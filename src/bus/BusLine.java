package bus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

class BusLine {
	int sentido;
	int accessibility;
	String code;
	String description;
	String pubcode;
	List<String> LineStops;
	
	public BusLine(int accessibility,String code,String description,String pubcode){
		this.accessibility = accessibility;
		this.pubcode = pubcode;
		this.code = code;
		this.description = description;
		this.LineStops = new LinkedList<String>();
	}
	
	public void testPrint() throws FileNotFoundException, UnsupportedEncodingException{
		/*File linesOutputJS = new File("/home/diogo/workspace/iic/webcrawler/lines.js");
		PrintWriter writer = new PrintWriter("lines.js", "UTF-8");*/
		System.out.println(this.accessibility+" "+this.code+" "+this.description+" "+this.pubcode);
	
	}
	
	public void toJS(int direction) throws Exception{
		// output
		File linesOutputJS = new File("/home/diogo/workspace/iic/webcrawler/lines.js");
		PrintWriter writer = new PrintWriter("lines.js", "UTF-8");
		//build string
		String res = "var "+this.pubcode+"_"+direction+" ={";
		writer.println(res);
		
	}
}
