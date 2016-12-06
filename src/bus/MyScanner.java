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
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

class MyScanner {

	/**
	 * @param urlString
	 *            is the url we want to read the json from
	 * @return gives us a string with all the un-parsed json
	 * @throws Exception
	 *             if it cant connect, for various reasons.
	 */
	static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	/**
	 * this method gives us a list of strings that represent the codes of the
	 * bus stops we want.
	 * 
	 * @param info
	 *            is a string of parsed json
	 * @return gives us a list
	 */
	static List<String> getAllLineNumbers(String info) {
		List<String> result = new LinkedList<String>();
		JSONObject jo = new JSONObject(info);
		JSONArray ja = jo.getJSONArray("records");
		for (int i = 0; i < ja.length(); i++) {
			JSONObject obj = ja.getJSONObject(i);

			// result.add(obj.get("pubcode").toString());
			result.add(obj.get("code").toString());
		}
		return result;
	}

	/**
	 * method to get a line l in a direction d.
	 * 
	 * @param l
	 *            is the line's code
	 * @param d
	 *            is the direction we want
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

		// AllStops.clear();
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
	 * print all stops
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
		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=lineslist&service=1&madrugada=1");
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
	 * this method takes each line from the all stops file and converts them to
	 * JS objects
	 * 
	 * @throws IOException
	 */
	static void stopsToJS() throws IOException {
		// create output files
		File stopsOutputJS = new File("webcrawler/stops.js");
		// writers
		PrintWriter writer = new PrintWriter("stops.js", "UTF-8");
		// buffered reader to stops / lines files
		BufferedReader br = new BufferedReader(new FileReader("AllStops.txt"));
		// read line by line the stops file
		LinkedList<String> listOfStops = new LinkedList<String>();
		try {
			// 1st line has number of stops!!
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				// stops
				String[] stop = line.toString().split(",");
				String res = "var p_" + stop[0] + " = {\n" + "\tnome:" + "\""
						+ stop[1] + "\"" + ",\n" + "\tcodigo:" + "\"" + stop[0]
						+ "\"" + ",\n" + "\tzona:" + "\"" + stop[2] + "\""
						+ ",\n" + "\tmorada:" + "\"" + stop[3] + "\"" + ",\n"
						+ "\tlongitude:" + stop[4] + ",\n" + "\tlatitude:"
						+ stop[5] + "\n};";
				// pra lista
				listOfStops.add(stop[0]);
				// write to file
				writer.println(res);
			}
		} finally {
			writer.print("var allstops = [");
			for (String s : listOfStops) {
				if (listOfStops.indexOf(s) == listOfStops.size() - 1)
					writer.print("\"" + s + "\"];");
				else
					writer.print("\"" + s + "\",");
			}
			writer.close();
			br.close();
		}

	}// end of method

	/**
	 * this method makes a JS file that contains all the lines as JS objects
	 * some code was re-used from methods above this one prints directly to a
	 * file
	 * 
	 * @throws Exception
	 */
	static List<BusLine> constructLines() throws Exception {

		List<BusLine> result = new LinkedList<BusLine>();
		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=lineslist&service=1&madrugada=1");
		//File stopsOutputJS = new File("webcrawler/stops.js");
		File linesOutputJS = new File("webcrawler/lines.js");
		PrintWriter writer = new PrintWriter("lines.js", "UTF-8");
		// parse JSON
		JSONObject jo = new JSONObject(readFromURL);
		JSONArray ja = jo.getJSONArray("records");
		// JSONObject obj = ja.getJSONObject(i);
		for (int i = 0; i < ja.length(); i++) {
			JSONObject aux = ja.getJSONObject(i);
			// now we have the beginning of a line
			BusLine bl = new BusLine(Integer.parseInt(aux.get("accessibility")
					.toString()), aux.get("code").toString(), aux.get(
					"description").toString(), aux.get("pubcode").toString());
			// for each line we need the stops

			// sentido 0
			List<JSONObject> stops = getLine(bl.code, "0");
			for (JSONObject o : stops) {
				bl.LineStops.add(o.get("code").toString());
			}
			if (bl.LineStops.size() > 0)
				writer.println(bl.toJS(0));
			bl.LineStops.clear();

			// sentido 1
			stops.clear();
			stops = getLine(bl.code, "1");
			for (JSONObject o : stops) {
				bl.LineStops.add(o.get("code").toString());
			}

			if (bl.LineStops.size() > 0)
				writer.println(bl.toJS(1));
			bl.LineStops.clear();

		}
		writer.close();
		return result;
	}

	/*
	 * Following methods make gephi readable files
	 */
	/**
	 * This method takes all the stops we have and makes a csv file
	 * 
	 * @throws Exception
	 */
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
	/**
	 * method to make a csv file for each line, in each direction
	 * Line 200 runs in 2 directions, so it will generate 2 files (200_0.csv and 200_1.csv)
	 * @throws FileNotFoundException if it cant find the source file
	 * also return a list with all edges. yes, it has repeated edges, thinking about the edge weight kinda thing
	 */
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

	public static void main(String args[]) throws Exception {

		Scanner in = new Scanner(System.in);
		System.out.println("Insert "
				+ "1 to refresh data about lines, "
				+ "2 to refresh data about stops, " 
				+ "3 to generate JS files,"
				+ "4 to generate csv files"
				);
		int choice = in.nextInt();
		if (choice == 1) {
			// lines data
			System.setOut(new PrintStream(new BufferedOutputStream(
					new FileOutputStream("AllLines.txt"))));
			getAllLines();
		} else if (choice == 2) {
			// stops data
			System.setOut(new PrintStream(new BufferedOutputStream(
					new FileOutputStream("AllStops.txt"))));
			printAllStops(getAllStops());
		}

		else if (choice == 3) {
			stopsToJS();
			constructLines();

		} else if (choice == 4) {
			
			makeNodesCSV();
			allEdgesCSV(makeAllLinesCSV());
		}
	}// end main
}// end class
