package bus;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import sun.awt.image.ImageWatched.Link;
/**
 *structure that answers the last question
 *in the introduction
 */
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
	AnswerBFS go_BFS_on(Stop s) throws IOException{
		//sources
		HashMap<String, Stop> src_Stops = readStopsFile();
		HashMap<String,Edge> src_Edges = getAllEdges();
		int answer = -1;
		AnswerBFS resp = new AnswerBFS();
		String dest = "";
		setAdjacentNodes(s);
		LinkedList<Stop> list = new LinkedList<Stop>();
		list.addLast(s);
		distances.put(s.stopCode,0);
		visitedStops.put(s.stopCode,true);
		while(!list.isEmpty()){
			Stop target = list.removeFirst();
			resp.setSource(s.stopCode);
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
						resp.setTarget(dest);
						resp.setDistance(check.distance_BFS);
					}
				}
			}
		}// end of while
		resp.distances = distances;
		return resp;
	}// end of bfs
	
	
	class Dijkstra{
		
		AnswerDijkstra shortestPathBetween(Stop source,Stop target) throws IOException{
		//
		HashMap<String, Stop> allNodes = readStopsFile();
		HashMap<String, Edge> allEdges = getAllEdges();
		AnswerDijkstra resp = new AnswerDijkstra();
		// 
		HashMap<String, Integer> distances = go_BFS_on(source).distances;
		HashMap<String, String> predecessors = new HashMap<String, String>();
		LinkedList<String> visitedStops = new LinkedList<String>();
		//
		for(Integer i : distances.values())
			i = Integer.MAX_VALUE;
		distances.put(source.stopCode, 0);
		//
		while(!allNodes.isEmpty()){
			Stop next = allNodes.get(getNext(distances,visitedStops));
			visitedStops.add(next.stopCode);
			allNodes.remove(next);
			
		}
		
		
		
		
		
		return resp;
		}
		
		String getNext(HashMap<String, Integer> distances,LinkedList<String> visitedStops ){
			String resp = null;
			int i = Integer.MAX_VALUE;
			for(String test : distances.keySet()){
				int ii = distances.get(test);
				if(!visitedStops.contains(test) && ii < i){
					i = ii;
					resp = test; 
				}
			}
			return resp;
		}
		
		class AnswerDijkstra{
			LinkedList<Stop> path;
			public AnswerDijkstra() {
				this.path = new LinkedList<Stop>();
			}
		}
	}
	
	
	
/**
 *answer when BFS is used
 *has source, target and distance
 *relative to the network diameter
 */
class AnswerBFS{
	private String source;
	private String target;
	private int distance;
	HashMap<String,Integer> distances;
	
	AnswerBFS(){
		this.source = "";
		this.target = "";
		this.distance = 0;
		this.distances = new HashMap<String, Integer>();
	}
	
	String getSource(){return this.source;}
	String getTarget(){return this.target;}
	int getDistance(){return this.distance;}
	
	void setSource(String s){this.source = s;}
	void setTarget(String s){this.target = s;}
	void setDistance(int i){this.distance = i;}
	}
}// end of class