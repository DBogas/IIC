package bus;
/**
 * This represents an edge in the graph where no grouping occurred
 */
public class Edge {
	private Stop source;
	private Stop target;
	private String desc;
	int weight;
	
	Edge(Stop s, Stop t){
		this.source = s;
		this.target = t;
		this.desc = "";
		this.weight = 1;
	}
	void setDesc(String s){this.desc = s;}
	Stop getSource(){return this.source;}
	Stop getTarget(){return this.target;}
	
	void printSelf(){System.out.println("Source:"+this.source.stopCode+" Target:"+this.target.stopCode);}
}
