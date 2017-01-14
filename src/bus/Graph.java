package bus;
/**
 * Simple representation of a graph, for the LastQuestion structure
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Graph extends MyScanner{
	private HashMap<String,Stop> vertex;
	private HashMap<String,Edge> edges;
	
	public Graph(HashMap<String,Stop> vertex, HashMap<String, Edge> edges ){
		this.vertex = vertex;
		this.edges = edges;
	}
	
	public HashMap<String, Stop> getVertex(){return this.vertex;}
	public HashMap<String, Edge> getEdges(){return this.edges;}
}
