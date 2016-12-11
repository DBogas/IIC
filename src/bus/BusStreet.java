package bus;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * this class basically lets us 
 * group all the bus stops we have by street
 * hopefully, this lets us answer the question:
 * What is the busiest street in the network?
 */
public class BusStreet {
	
	String street; // this is the street name
	LinkedList<Stop> stops; // this is the group of stops a street has
	LinkedList<BusStreet> neighbours; // this is the group of edges (in and out going)
	float longitude;
	float latitude;
	
	BusStreet(String s){
		this.street = s;
		this.stops = new LinkedList<Stop>();
		this.neighbours = new LinkedList<BusStreet>();
	}
	// helps debug
	BusStreet(){
		this.street ="";
		this.neighbours = new LinkedList<BusStreet>();
		this.stops = new LinkedList<Stop>();
	}
	
	BusStreet cloneMe(){
		BusStreet a = new BusStreet(this.street);
		a.neighbours = this.neighbours;
		a.stops = this.stops;
		a.latitude = this.latitude;
		a.longitude = this.longitude;
		return a;
	}
}
