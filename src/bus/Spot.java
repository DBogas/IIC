package bus;

import java.util.HashMap;
import java.util.LinkedList;
/**
 *representing a Spot
 */
public class Spot {
	
	String code;
	HashMap<String, Stop> stops;
	HashMap<String,String> LinesServed;
	
	Spot(String s){
		this.code = s;
		this.stops = new HashMap<String, Stop>();
		this.LinesServed =  new HashMap<String,String>();
	}
	void printSpot(){
		System.out.println("code: "+this.code);
		for(Stop stop : stops.values()){
			System.out.print("\t");
			stop.printStop();
		}
	}
}
