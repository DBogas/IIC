package bus;

public class Stop {

	String code;
	String address;
	String zone;
	String name;

	Stop(String code, String address, String zone, String name) {
		this.code = code;
		this.address = address;
		this.zone = zone;
		this.name = name;
	}
	
	void printStop(){
		System.out.println("code: "+this.code+" address: "+this.address+" zone: "+this.zone+" name: "+this.name);
	}
}
