package bus;

public class Stop {

	String code;
	String address;
	String zone;
	String name;
	float longitude;
	float latitude;

	Stop(String code, String address, String zone, String name) {
		this.code = code;
		this.address = address;
		this.zone = zone;
		this.name = name;
		this.longitude = 0;
		this.latitude = 0;
	}
	
	void printStop(){
		System.out.println("code: "+this.code+" address: "+this.address+" zone: "+this.zone+" name: "+this.name+" longitude: "+this.longitude+" latitude: "+this.latitude);
	}
}
