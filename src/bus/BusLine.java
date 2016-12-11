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
	
	public BusLine(String code,String direction){
		this.code = code;
		this.sentido = Integer.parseInt(direction);
	}
	
	
}
