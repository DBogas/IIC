package bus;

import java.util.*;
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
	
	BusStreet(String s){
		this.street = s;
		this.stops = new LinkedList<Stop>();
		this.neighbours = new LinkedList<BusStreet>();
	}
	
}
