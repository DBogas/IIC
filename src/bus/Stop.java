package bus;

import java.util.LinkedList;

public class Stop {

	String stopCode;
	String address;
	String zone;
	String name;
	float longitude;
	float latitude;
	int totalLinesServed;
	LinkedList<String> linesServed;
	LinkedList<Stop> adjacentStops;
	int distance_BFS;

	Stop(String code, String address, String zone, String name) {
		this.stopCode = code;
		this.address = address;
		this.zone = zone;
		this.name = name;
		this.longitude = 0;
		this.latitude = 0;
		this.totalLinesServed=0;
		this.linesServed = new LinkedList<String>();
		this.adjacentStops = new LinkedList<Stop>();
		this.distance_BFS = 0;
	}
	
	void printStop(){
		System.out.println(this.stopCode+" , "+this.address+" , "+this.zone+" , "+this.name+" , "+this.longitude+" , "+this.latitude);
	}
}
