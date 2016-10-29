package bus;

import java.util.*;

class BusLine {
	
	int accessibility;
	String code;
	String pubCode;
	String description;
	
	public BusLine(int accessibility,String code,String pubCode,String description){
		this.accessibility = accessibility;
		this.code = code;
		this.pubCode = pubCode;
		this.description = description;
	}
}
