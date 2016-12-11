package bus;

public class AddressEdge {
	BusStreet src;
	BusStreet dest;
	int weight;
	String nome; // ex: Av.Aliados-Trindade
	
	AddressEdge(BusStreet a, BusStreet b){
		this.src = a;
		this.dest = b;
		this.weight = 1;
		this.nome = a.street+"-"+b.street;
	}
	void print(){
		System.out.println(this.nome+" "+this.weight);
	}
}
