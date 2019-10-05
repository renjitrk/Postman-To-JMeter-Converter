package postmantojmx;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONHandler {

	private JSONObject json;
	private BufferedReader br;
	private HashSet<String> variableNames;

	public JSONHandler(String jsonPath) throws IOException, ParseException {

		int totalLineCt, j = 0;
		double percent;
		variableNames = new HashSet<String>();

		InputStream is = new BufferedInputStream(new FileInputStream(jsonPath));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			totalLineCt = (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}

		String jsonString = null;
		br = new BufferedReader(new FileReader(jsonPath));
		String line = br.readLine();

		while (line != null) {
			if (totalLineCt != 0 && j % 10 == 0) {
				percent = ((double) j / totalLineCt) * 10000;
				percent = Math.round(percent) / 100.0;
				System.out.print("\rReading File: " + percent + "% complete");
			}
			j++;
			if (jsonString == null) {
				jsonString = line;
			} else {
				jsonString += "\n";
				jsonString += line;
			}
			line = br.readLine();
		}
		System.out.print("\rReading File Complete               ");
		System.out.println();
		json = (JSONObject) new JSONParser().parse(jsonString);
	}

	public String[] getAPIDetails() {

		String returnStr[] = new String[2];
		
		returnStr[0] = "";
		returnStr[1] = "";
		
		if (!json.containsKey("info")) {
			return returnStr;
		}

		if (((JSONObject) json.get("info")).containsKey("name")) {
			returnStr[0] = ((JSONObject) json.get("info")).get("name").toString();
		}
		if (((JSONObject) json.get("info")).containsKey("description")) {
			returnStr[1] = ((JSONObject) json.get("info")).get("description").toString();
		}

		return returnStr;
	}

	@SuppressWarnings("unchecked")
	public String[][] getFolderDetails() {
		// ArrayList<String[]> returnList = new ArrayList<String[]>();
		String[][] returnStr = new String[100][2];
		String[][] returnStrNew;
		JSONObject tempJO;
		int i = 0;

		JSONArray ja = (JSONArray) json.get("item");
		Iterator<JSONObject> iterator = ja.iterator();

		while (iterator.hasNext()) {
			tempJO = iterator.next();
			// System.out.println(tempJO);
			try {
				returnStr[i][0] = tempJO.get("name").toString();
			} catch (NoSuchElementException | NullPointerException e1) {
				returnStr[i][0] = "";
			}
			try {
				returnStr[i][1] = tempJO.get("description").toString();
			} catch (NoSuchElementException | NullPointerException e2) {
				returnStr[i][1] = "";
			}
			i++;
		}

		returnStrNew = new String[i][2];
		for (int j = 0; j < i; j++) {
			returnStrNew[j][0] = returnStr[j][0];
			returnStrNew[j][1] = returnStr[j][1];
		}

		return returnStrNew;
	}

	public JSONArray getItems() {

		JSONArray returnArr = (JSONArray) json.get("item");
		return returnArr;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAPIParameter(JSONObject apiParamsInput) {

		JSONObject apiParamsOut = new JSONObject();
		JSONObject tempObj;
		JSONArray tempArr;
		String temp;

		// apiName
		tempObj = apiParamsInput;
		if (tempObj.containsKey("name"))
			temp = tempObj.get("name").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("apiName", temp);

		// description
		tempObj = apiParamsInput;
		if (tempObj.containsKey("description"))
			temp = tempObj.get("description").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("description", temp);

		// protocol
		tempObj = ((JSONObject) ((JSONObject) apiParamsInput.get("request")).get("url"));
		if (tempObj.containsKey("protocol"))
			temp = tempObj.get("protocol").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("protocol", temp);

		// url
		tempObj = ((JSONObject) ((JSONObject) apiParamsInput.get("request")).get("url"));
		if (tempObj.containsKey("host")) {
			tempArr = (JSONArray) tempObj.get("host");
			temp = "";
			for (int i = 0; i < tempArr.size(); i++)
				temp = temp + "." + tempArr.get(i);
			temp = temp.substring(1);
		} else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("url", temp);

		// port
		tempObj = ((JSONObject) ((JSONObject) apiParamsInput.get("request")).get("url"));
		if (tempObj.containsKey("port"))
			temp = tempObj.get("port").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("port", temp);

		// path
		tempObj = ((JSONObject) ((JSONObject) apiParamsInput.get("request")).get("url"));
		if (tempObj.containsKey("path")) {
			tempArr = (JSONArray) tempObj.get("path");
			temp = "";
			for (int i = 0; i < tempArr.size(); i++)
				temp = temp + "/" + tempArr.get(i);
			temp = temp.substring(1);
		} else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("path", temp);

		// method
		tempObj = (JSONObject) apiParamsInput.get("request");
		if (tempObj.containsKey("method"))
			temp = tempObj.get("method").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("method", temp);

		// bodyMode
		tempObj = (JSONObject) apiParamsInput.get("request");
		if (tempObj.containsKey("body"))
			if (((JSONObject) tempObj.get("body")).containsKey("mode")) {
				temp = (((JSONObject) tempObj.get("body")).get("mode")).toString();
			} else
				temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("bodyMode", temp);

		// urlencoded body
		if (temp.equalsIgnoreCase("urlencoded")) {
			if (((JSONObject) tempObj.get("body")).containsKey("urlencoded"))
				temp = (((JSONObject) tempObj.get("body")).get("urlencoded")).toString();
			else
				temp = "";
			temp = replaceVariables(temp);
			apiParamsOut.put("urlencodedBody", temp);
		}

		// raw body
		else if (temp.equalsIgnoreCase("raw")) {
			if (((JSONObject) tempObj.get("body")).containsKey("raw"))
				temp = (((JSONObject) tempObj.get("body")).get("raw")).toString();
			else
				temp = "";
			temp = replaceVariables(temp);
			apiParamsOut.put("rawBody", temp);
		}

		// header
		tempObj = (JSONObject) apiParamsInput.get("request");
		if (tempObj.containsKey("header"))
			temp = tempObj.get("header").toString();
		else
			temp = "";
		temp = replaceVariables(temp);
		apiParamsOut.put("headers", temp);

		return apiParamsOut;
	}

	private String replaceVariables(String input) {
		String check;
		int start, stop;

		start = input.indexOf("{{");
		stop = input.indexOf("}}");

		if (start == -1 || stop == -1 || stop <= start)
			return input;

		check = input.substring(start + 2, stop);
		if (check.matches("[$]?[a-zA-Z0-9_]*")) {
			variableNames.add(check);
			return input.substring(0, start) + "${" + check + "}" + input.substring(stop + 2);
		}

		return input;
	}

	public String[] getVariableNames() {
		String variables[] = new String[variableNames.size()];
		variables = variableNames.toArray(new String[variableNames.size()]);
		return variables;
	}

}
