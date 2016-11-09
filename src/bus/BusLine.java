package bus;

import java.util.*;

class BusLine {
	
	int accessibility;
	String code;
	String description;
	List<String> LineStops;
	
	public BusLine(int accessibility,String code,String description){
		this.accessibility = accessibility;
		this.code = code;
		this.description = description;
		this.LineStops = new LinkedList<String>();
	}
}
