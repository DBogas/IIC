package bus;

public class Stop {
	
	int sequence;
	String code;
	String address;
	String zone;
	String name;
	
	Stop(int sequence,String code,String address,String zone,String name){
		this.sequence = sequence;
		this.code = code;
		this.address = address;
		this.zone = zone;
		this.name = name;
	}
}
