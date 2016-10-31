package bus;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.nashorn.internal.parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

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
			result.add(obj.get("pubcode").toString());
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
	 * method that iterates through all the lines in each line it iterates
	 * through all the bus stops if a bus stop isn't stored in the hashmap,
	 * stores it
	 * 
	 * it creates a file for output too.
	 * 
	 * @throws Exception
	 */

	static void getAllStops() throws Exception {
		// file handling
		File outputFile = new File("AllStops.txt");
		// outputFile.createNewFile();
		// actual code
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
				if (!AllStops.containsKey(stop.code))
					AllStops.put(stop.code, stop);
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
				if (!AllStops.containsKey(stop.code))
					AllStops.put(stop.code, stop);
			}
			// print
			for (String auxS : AllStops.keySet()) {
				AllStops.get(auxS).printStop();
			}

		}
		System.out.println(AllStops.size());
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
			System.out.println(s + ",0");
			for (JSONObject o : aux1) {
				System.out.print(o.get("code") + " ");
			}
			System.out.println();
			// print in direction 1
			System.out.println(s + ",1");
			for (JSONObject o : aux2) {
				System.out.print(o.get("code") + " ");
			}
			System.out.println();
		}
	}

	static float[] getStopGPSCoords(String stopCode) throws Exception {
		
		String readFromURL = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=srchstoplines&stopcode="+stopCode);
		
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
	
	static List<String> getAllStopCodes(){
		List<String> res = new ArrayList<String>();
		return res;
	}

	public static void main(String args[]) throws Exception {
		 System.setOut(new PrintStream(new BufferedOutputStream(new
		 FileOutputStream("AllStops.txt"))));
		 getAllStops();
		 System.setOut(new PrintStream(new BufferedOutputStream(new
		 FileOutputStream("AllLines.txt"))));
		 getAllLines();
	}// end main
}// end class
