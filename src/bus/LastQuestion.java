package bus;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class LastQuestion extends MyScanner {
	
	HashMap<String, Stop> stops;
	HashMap<String, Edge> edges;
	HashMap<String,Boolean> visitedStops;
	HashMap<String, Integer> distances;
	Graph graph;
	
	LastQuestion() throws IOException{
		this.stops = readStopsFile();
		this.edges = getAllEdges();
		this.graph = new Graph(this.stops,this.edges);
		this.visitedStops = new HashMap<String, Boolean>();
		this.distances = new HashMap<String, Integer>();
	}
	/**
	 * Method to get adjacent nodes to a certain node
	 * Here the node can be a source in the edge. (directed graph)
	 * @param s is the stop 
	 * @throws IOException
	 */
	void setAdjacentNodes(Stop s) throws IOException{
		HashMap<String, Edge> srcEdges = getAllEdges();
		for(Edge e : srcEdges.values()){
			// caso seja source numa edge
			if(e.getSource().stopCode.equals(s.stopCode) ){
				if(!s.adjacentStops.contains(e.getTarget()))s.adjacentStops.add(e.getTarget());
			}
			//TEST HERE
		}
	}
	/**
	 * Diameter calculating
	 * @param s
	 * @throws IOException 
	 */
	int go_BFS_on(Stop s) throws IOException{
		//sources
		HashMap<String, Stop> src_Stops = readStopsFile();
		HashMap<String,Edge> src_Edges = getAllEdges();
		int answer = -1;
		String dest = "";
		setAdjacentNodes(s);
		LinkedList<Stop> list = new LinkedList<Stop>();
		list.addLast(s);
		distances.put(s.stopCode,0);
		visitedStops.put(s.stopCode,true);
		while(!list.isEmpty()){
			Stop target = list.removeFirst();
			setAdjacentNodes(target);
			for(Stop check : target.adjacentStops){
				if(!visitedStops.containsKey(check.stopCode)){
					visitedStops.put(check.stopCode, true);
					list.addLast(check);
					check.distance_BFS = target.distance_BFS+1;
					distances.put(check.stopCode, check.distance_BFS);
					if(check.distance_BFS > answer) {
						answer = check.distance_BFS;
						dest = check.stopCode;
					}
				}
			}
		}// end of while
		System.out.println("RTE2 para "+dest);
		return answer;
	}// end of bfs
	
	/*int test() throws IOException{
		HashMap<String, Stop> src = readStopsFile();
		int answer = -100;
		for(Stop s : src.values()){
			int i = this.go_BFS_on(s);
			System.out.println("Paragem: "+s.stopCode+" "+i);
			answer = Math.max(i, answer);
		}
		return answer;
	}*/
}
