package bus;

import java.util.HashMap;

public class Spot {
	
	String code;
	HashMap<String, Stop> stops;
	
	Spot(String s){
		this.code = s;
		this.stops = new HashMap<String, Stop>();
	}
	void printSpot(){
		System.out.println("code: "+this.code);
		for(Stop stop : stops.values()){
			System.out.print("\t");
			stop.printStop();
		}
	}
}
