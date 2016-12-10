package bus;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

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
	
	public String toJS(int direction) throws Exception{
		// output
		
		String res = 	"var "+this.pubcode+"_"+direction+" ={\n"
						+"\t accessibility: "+this.accessibility+",\n"
						+"\t pubcode: \""+this.pubcode+"\",\n"
						+"\t code: \""+this.code+"\",\n"
						+"\t description: \""+this.description+"\",\n"
						+"\t paragens : [";
		for(String s: this.LineStops){
			if(this.LineStops.indexOf(s) == this.LineStops.size()-1) res+= "\""+s+"\"";
			else res+= "\""+s+"\""+",";
		}
		res+="]\n}";
		return res;
	}
}
