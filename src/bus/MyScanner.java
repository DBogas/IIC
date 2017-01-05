package bus;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

class MyScanner {
	
	
	  //@param urlString is the url we want to read the json from
	  //@return gives us a string with all the un-parsed json
	  //@throws Exception if it cant connect, for various reasons.
	 
	static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[4096];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
				return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	
	 //this method gives us a list of strings that represent the codes of the bus stops we want.
	 //@param info is a string of parsed json
	 //@return gives us a list
	 
	static List<String> getAllLineNumbers(String info) {
		List<String> result = new LinkedList<String>();
		JSONObject jo = new JSONObject(info);
		JSONArray ja = jo.getJSONArray("records");
		for (int i = 0; i < ja.length(); i++) {
			JSONObject obj = ja.getJSONObject(i);
			result.add(obj.get("code").toString());
		}
		return result;
	}

	/**
	 * method to get a line l in a direction d.
	 * @param l is the line's code
	 * @param d is the direction we want
	 * @return a list of all stops
	 * @throws Exception
	 */
	static List<JSONObject> getLine(String l, String d) throws Exception {

		String URLpart1 = "http://www.stcp.pt/pt/itinerarium/callservice.php?action=linestops&lcode=";
		String URLpart2 = "&ldir=";
		String completeURL = URLpart1 + l + URLpart2 + d;
		String readFromURL = readUrl(completeURL);

		List<JSONObject> result = new LinkedList<JSONObject>();
		JSONObject jo = new JSONObject(readFromURL);
		JSONArray ja = jo.getJSONArray("records");
		for (int i = 0; i < ja.length(); i++) {
			JSONObject obj = ja.getJSONObject(i);
			result.add(obj);
		}
		return result;
	}

	/**
	 * method that iterates through all the lines
	 *  in each line it iterates through all the bus stops 
	 *  if a bus stop isn't stored in the hashmap, stores it 
	 * @throws Exception
	 */

	// static void getAllStops() throws Exception {
	static HashMap<String, Stop> getAllStops() throws Exception {
		HashMap<String, Stop> AllStops = new HashMap<String, Stop>();

		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=lineslist&service=1&madrugada=1");
		List<String> allLineNumbers = getAllLineNumbers(readFromURL);

		// iterate all the lines
		for (String s : allLineNumbers) {

			// we have to do this twice, for direction 0
			List<JSONObject> aux = getLine(s, "0");
			for (JSONObject o : aux) {
				float[] coords = getStopGPSCoords(o.get("code").toString());
				Stop stop = new Stop(o.get("code").toString(), o.get("address")
						.toString(), o.get("zone").toString(), o.get("name")
						.toString());
				stop.longitude = coords[0];
				stop.latitude = coords[1];
				if (!(AllStops.containsKey(stop.stopCode)))
					AllStops.put(stop.stopCode, stop);
				else if (AllStops.containsKey(stop.stopCode))
					continue;
			}
			// and for direction 1
			aux = getLine(s, "1");
			for (JSONObject o : aux) {
				float[] coords = getStopGPSCoords(o.get("code").toString());
				Stop stop = new Stop(o.get("code").toString(), o.get("address")
						.toString(), o.get("zone").toString(), o.get("name")
						.toString());
				stop.longitude = coords[0];
				stop.latitude = coords[1];
				if (!(AllStops.containsKey(stop.stopCode)))
					AllStops.put(stop.stopCode, stop);
				else if (AllStops.containsKey(stop.stopCode))
					continue;
			}
		}
		return AllStops;
	}

	/**
	 * prints all stops
	 */

	static void printAllStops(HashMap<String, Stop> AllStops) {
		//System.out.println(AllStops.size());
		for (String auxS : AllStops.keySet()) {
			AllStops.get(auxS).printStop();
		}
	}

	/**
	 * this method aims to create a .txt file with all the Bus Lines in the
	 * following format line code, direction stop1_code, stop2_code, etc
	 * 
	 * @throws Exception
	 */
	static void getAllLines() throws Exception {
		// file handling
		File outputFile = new File("AllLines.txt");
		// actual code
		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=lineslist&service=1&madrugada=0");
		List<String> allLineNumbers = getAllLineNumbers(readFromURL);
		// iterating
		for (String s : allLineNumbers) {
			List<JSONObject> aux1 = getLine(s, "0");
			List<JSONObject> aux2 = getLine(s, "1");
			// print in direction 0
			System.out.print(s + ",0" + "," + aux1.size() + ",");
			for (JSONObject o : aux1) {
				System.out.print(o.get("code") + ",");
			}
			System.out.println();
			// print in direction 1
			System.out.print(s + ",1" + "," + aux2.size() + ",");
			for (JSONObject o : aux2) {
				System.out.print(o.get("code") + ",");
			}
			System.out.println();
		}
		System.out.flush();
	}

	static float[] getStopGPSCoords(String stopCode) throws Exception {

		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=srchstoplines&stopcode="
				+ stopCode);

		JSONArray ja = new JSONArray(readFromURL);
		JSONObject jo = (JSONObject) ja.get(0);
		String s = (String) jo.get("geomdesc");
		// saturday night delight parsing
		String aux = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '-')
				aux += '-';
			if (s.charAt(i) == '.')
				aux += '.';
			if (s.charAt(i) == ',')
				aux += ' ';
			else if (Character.isDigit(s.charAt(i)))
				aux += s.charAt(i);
		}
		aux = aux.trim();
		String[] auxres = aux.split(" ");
		// 0 -> longitude 1-> latitude
		float res[] = { Float.valueOf(auxres[0]), Float.valueOf(auxres[1]) };
		return res;
	}
	
	/**
	 * this method groups stops by street
	 * @return
	 * @throws IOException 
	 */
	static HashMap<String,BusStreet> stopsByStreet() throws IOException{
		HashMap<String, BusStreet> result = new HashMap<String, BusStreet>();
		HashMap<String, Stop> src = readStopsFile();
		for(Stop s : src.values()){
			if(!result.containsKey(s.address)){
				BusStreet bs = new BusStreet(s.address);
				bs.latitude = s.latitude;
				bs.longitude = s.longitude;
				bs.stops.add(s);
				result.put(bs.street, bs);
			}
			else{
				result.get(s.address).stops.add(s);
			}
		}
		return result;
	}
	
	// method to read all lines from txt file and put them in a list
	static LinkedList<BusLine> readLinesFromFile() throws IOException{
		LinkedList<BusLine> answer = new LinkedList<BusLine>();
		// read from the file
		BufferedReader br = new BufferedReader(new FileReader("AllLines.txt"));
		String line;
		while((line = br.readLine()) != null){
			String[] pieces = line.split(",");
			BusLine bl = new BusLine(pieces[0], pieces[1]);
			for(int i=3;i<pieces.length;i++){
				if(!(pieces[0].equals("")))bl.LineStops.add(pieces[i]);
			}
			answer.add(bl);
		}
		return  answer;
		
	}
	
	
	static BusStreet getStreet(String s) throws IOException{
		HashMap<String,BusStreet> streets = stopsByStreet();
		HashMap<String, Stop> stops = readStopsFile();
		Stop st = stops.get(s);
		//st.printStop();
		
		for(BusStreet b : streets.values()){
			if(b.street.equals(st.address)) {
				return b;
			}
		}
		System.out.println("Street not found");
		return null;
	}
	
	
	static HashMap<String,AddressEdge> generateEdgesToStreets() throws IOException{
		// answer
		HashMap<String,AddressEdge> answer = new HashMap<String, AddressEdge>();
		// streets
		HashMap<String,BusStreet> streets = stopsByStreet();
		System.out.println("streets: " +streets.size());
		// lines
		LinkedList<BusLine> allLines = readLinesFromFile();
		System.out.println("lines: "+allLines.size());
		for(BusLine bl :allLines){
			//System.out.println(bl.code);
			//System.out.println("line size: "+bl.LineStops.size());
			for(int i=0;i<bl.LineStops.size()-1;i++){
				//ruas de inicio e destino
				//System.out.println(i+1);
				BusStreet src = getStreet(bl.LineStops.get(i));
				//System.out.print("src: "+src.street);
				BusStreet dest = getStreet(bl.LineStops.get(i+1));
				//System.out.println(" - dest: "+dest.street);
				String s = src.street;
				String d = dest.street;
				String check =s+"-"+d;
				if(answer.containsKey(check)){
					answer.get(check).weight++;
					//answer.get(check).print();
				}
				else{
					AddressEdge nova = new AddressEdge(src, dest);
					answer.put(nova.nome, nova);
					//answer.get(check).print();
				}
			}// fecha for
		}//fecha while
		
	return answer;
	}
	
	// group stops by code -> concept of spots
	static HashMap<String, Spot> groupStopsByCode() throws IOException{
		HashMap<String, Spot> answer =  new HashMap<String, Spot>();
		HashMap<String, Stop> src = readStopsFile();
		//update individual stops with the stops they serve
		for(Stop s : src.values()){
			s.linesServed = (LinkedList<String>) getLinesServed(s.stopCode);
			s.totalLinesServed = s.linesServed.size();
		}
		// do stuff
		for(Stop s : src.values()){
			// codigo a verificar e linhas que esse codigo serve
			String to_check = s.stopCode;
			LinkedList<String> linhas = (LinkedList<String>) getLinesServed(to_check);
			// se precisar de retirar o ultimo char, retira-o
			if(Character.isDigit(to_check.charAt(to_check.length()-1))){
				StringBuilder sb = new StringBuilder(to_check);
				sb.deleteCharAt(to_check.length()-1);
				to_check = sb.toString();
			}
			// se n estiver na hash
			if(!answer.containsKey(to_check)){
				// criar spot e por a paragem la dentro
				Spot novo =  new Spot(to_check);
				novo.stops.put(s.stopCode, s);
				for(String linha : linhas){
					if(!novo.LinesServed.containsKey(linha))
						novo.LinesServed.put(linha, linha);
				}
				answer.put(to_check, novo);
			}
			// se estiver na hash
			else{
				answer.get(to_check).stops.put(s.stopCode, s);
				for(String ss : linhas){
					if(!answer.get(to_check).LinesServed.containsKey(ss))
						answer.get(to_check).LinesServed.put(ss, ss);
				}
			}
		}
		return answer;
	}
	
	// make edges to spots
	static HashMap<String,SpotEdge> makeEdgesToSpots() throws IOException{
		HashMap<String,SpotEdge> answer = new HashMap<String, SpotEdge>();
		// sources
		HashMap<String,	Spot> spots = groupStopsByCode();
		LinkedList<BusLine> lines = readLinesFromFile();
		// para todas as linhas
		for(BusLine bl : lines){
			// para todas as paragens em cada linha
			for(int i=0; i<bl.LineStops.size()-1;i++){
				// spots
				Spot s1 = getSpot(bl.LineStops.get(i));
				Spot s2 = getSpot(bl.LineStops.get(i+1));
				// ver se contem edge ou nao
				if(!answer.containsKey(s1.code+"-"+s2.code)){
					SpotEdge novo = new SpotEdge(s1, s2);
					answer.put(novo.name, novo);
				}
				else{
					answer.get(s1.code+"-"+s2.code).weight++;
				}
			}// end of inner for
		}// end of outer for
		return answer;
	}
	
	static Spot getSpot(String s) throws IOException{
		HashMap<String, Spot> src = groupStopsByCode();
		for(Spot spot : src.values()){
			if(spot.stops.containsKey(s))return spot;
		}
		return null;
	}
	
	static List<String> getLinesServed(String targetStop) throws IOException{
		List<String> answer = new LinkedList<String>();
		LinkedList<BusLine> src = readLinesFromFile();
		for(BusLine bl : src){
			if(bl.LineStops.contains(targetStop)){
				if(!answer.contains(bl.code.toString()))
					answer.add(bl.code.toString());
			}
		}
		return answer;
	}
	
	static HashMap<String,Stop> readStopsFile() throws IOException{
		HashMap<String,Stop> answer = new HashMap<String, Stop>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("AllStops.txt"));
			String line;
			while((line = br.readLine()) != null){
				String[] pieces = line.trim().split(",");
				Stop nova = new Stop(	pieces[0].trim(), 
										pieces[1].trim(), 
										pieces[2].trim(),
										pieces[3].trim());
				nova.longitude = Float.parseFloat(pieces[4].trim());
				nova.latitude = Float.parseFloat(pieces[5].trim());
				answer.put(nova.stopCode,nova);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		//System.out.println(answer.size());
		return answer;
	}
	
	
	/*
	 * Following methods make gephi readable files realated to the concept of Stop
	 */
	
	// makes nodes
	static void makeNodesCSV() throws Exception{
		// output stuff
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/stops.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		
		BufferedReader br = new BufferedReader(new FileReader("AllStops.txt"));
			try {
				writer.println("Id;Address;Zone;Name;Longitude;Latitude");
				String line;
				int i=0;
			    while(( line = br.readLine()) != null){
			    // stops
			    	//if(line != null) {
			    		String[] aux1 = line.toString().split(",");
			    		String res = aux1[0].trim()+";"+aux1[1].trim()+";"+aux1[2].trim()+";"+aux1[3].trim()+";"+aux1[4].trim()+";"+aux1[5].trim();	
			    		writer.println(res);
			    	//}
			    }
			}
			finally{
				writer.close();
			}
			
		}// end of method
	
	//method to make a csv file for each line, in each direction
	static HashMap<String, Integer> makeAllLinesCSV() throws FileNotFoundException{
		HashMap<String, Integer> allEdges = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader("AllLines.txt"));
		String line;
		try{
			while((line = br.readLine()) != null){
		    	String[] brokenLine = line.split(",");
		    	String fileName = brokenLine[0]+"_"+brokenLine[1]+".csv";
		    	File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/lines/"+fileName);
				file.getParentFile().mkdirs();
				PrintWriter writer = new PrintWriter(file);
		    	writer.println("Source;Target;Type");
		    	for(int i=3; i < brokenLine.length-1;i++){
		    		String aux = "\""+brokenLine[i]+"\""+";"+"\""+brokenLine[i+1]+"\""+";Directed";
		    		writer.println(aux);
		    		// hash it
		    		if(!allEdges.containsKey(aux))allEdges.put(aux, 1);
		    		//map.put(key, map.get(key) + 1);
		    		else allEdges.put(aux, allEdges.get(aux)+1);

		    	}
		    	writer.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return allEdges;
		
	}// end of method
	
	// method to make a csv file with all the edges.
	static void allEdgesCSV(HashMap<String, Integer> lista ) throws FileNotFoundException{
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/allEdges.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		writer.println("Source;Target;Type;Weight");
		for(String s : lista.keySet()){
			writer.println(s+";"+lista.get(s));
		}
		writer.close();
	}
	
	/*
	 * Methods related to the concept of Street
	 */
	
	// method to make nodes
	static void makeStreetNodeCSV() throws IOException{
		System.out.println("Entering makeStreetNodeCSV");
		// sources
		HashMap<String, BusStreet> src = stopsByStreet();
		//writer
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/streetNodes.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		//write nodes
		// 1st line
		writer.println("Id;TotalStops;Longitude;Latitude");
		for(BusStreet bs : src.values()){
			writer.println(bs.street+";"+bs.stops.size()+";"+bs.longitude+";"+bs.latitude);
		}
		writer.close();
	}
	
	// method to make edges
	static void makeStreetEdgesCSV() throws IOException{
		System.out.println("Entering makeStreetEdgesCSV");
		//sources
		HashMap<String, AddressEdge> src = generateEdgesToStreets();
		//writer
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/streetEdges.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		//write
		writer.println("Weight;Source;Target;Type");
		for(AddressEdge ae : src.values()){
			writer.println(+ae.weight+";"+ae.src.street+";"+ae.dest.street+";Directed");
		}
		writer.close();
	}
	
	/*
	 * Mehtods related to the concept of Spot
	 */
	
	// method to make nodes
	static void makeSpotNodeCSV() throws IOException{
		System.out.println("Entering makeSpotNodeCSV");
		//sources
		HashMap<String,Spot> src = groupStopsByCode();
		//writer
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/spotNodes.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		//write
		writer.println("Id;totalStops;linesServed");
		
		for(Spot sp : src.values() ){
			writer.println(sp.code+";"+sp.stops.size()+";"+sp.LinesServed.values().size());
		}
		writer.close();
	}
	
	//method to make edges
	static void makeSpotEdgesCSV() throws IOException{
		System.out.println("Entering makeSpotEdgesCSV");
		//sources
		HashMap<String, SpotEdge> src = makeEdgesToSpots();
		//writer
		File file = new File("/home/diogo/workspace/iic/webcrawler/gephi_src/spotEdges.csv");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		//write
		writer.println("Source;Target;Weight;Type");
		for(SpotEdge se : src.values()){
			writer.println(se.from.code+";"+se.to.code+";"+se.weight+";Directed");
		}
		writer.close();
		
	}
	
	
	
	public static void main(String args[]) throws Exception {

		Scanner in = new Scanner(System.in);
		System.out.println("Insert "
				+ "1 to refresh data about lines, "
				+ "2 to refresh data about stops, " 
				+ "3 to generate csv files, "
				+ "4 to test whatever."
				);
		int choice = in.nextInt();
		if (choice == 1) {
			// lines data
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("AllLines.txt"))));
			getAllLines();
		} else if (choice == 2) {
			// stops data
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("AllStops.txt"))));
			printAllStops(getAllStops());
			System.out.flush();
		}
		else if (choice == 3) {
			// descomentar antes de submeter!
			// STOPS
			//makeNodesCSV();
			//allEdgesCSV(makeAllLinesCSV());
			//STREETS
			//makeStreetNodeCSV();
			//makeStreetEdgesCSV();
			//SPOTS
			makeSpotNodeCSV();
			makeSpotEdgesCSV();
		}else if(choice == 4){
			
			/*HashMap<String, SpotEdge> src = makeEdgesToSpots();
			for(SpotEdge se : src.values()){
				se.print();
			}*/
			
			List<String> src = getLinesServed("AL1");
			for(String s : src)
				System.out.println(s);
			
			
		
		}
			
	}// end main
}// end class
