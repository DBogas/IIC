package bus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;

class MyScanner {

	/**
	 * 
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

	static List<String> getAllLines(String info) {
		List<String> result = new LinkedList<String>();
		JSONObject jo = new JSONObject(info);
		JSONArray ja = jo.getJSONArray("records");
		for (int i = 0; i < ja.length(); i++) {
			JSONObject obj = ja.getJSONObject(i);
			result.add(obj.get("pubcode").toString());
		}
		return result;
	}

	public static void main(String args[]) throws Exception {

		// sentido 0 ex: bolhao -> castelo do queijo
		String readFromURLDirection0 = readUrl("http://www.stcp.pt/pt/itinerarium/callservice.php?action=lineslist&service=1&madrugada=1");
		// sentido 1 (castelo queijo -> bolhao)
		// String readFromURLDirection1;
		List<String> example = getAllLines(readFromURLDirection0);
		for(String s : example){
			System.out.print(s+" ");
		}

	}// end main
}// end class
