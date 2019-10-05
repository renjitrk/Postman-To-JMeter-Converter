package postmantojmx;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;

public class JsonJmxConverter {

	private JSONHandler postmanHandler;
	private XMLHandler xmlHandler;

	public void convert(String postmanJsonPath) throws IOException, ParseException {

		int endCt;
		String jmxPath, apiListDetails[];
		Element jmxCurrentElement;
		JSONArray jsonItems;

		endCt = postmanJsonPath.lastIndexOf(".");
		if (endCt == -1)
			endCt = postmanJsonPath.length();
		jmxPath = postmanJsonPath.substring(0, endCt) + ".jmx";

//		create objects
		postmanHandler = new JSONHandler(postmanJsonPath);
		xmlHandler = new XMLHandler(jmxPath);

//		get api collection name and description
		apiListDetails = postmanHandler.getAPIDetails();

//		create the basic structure for JMX XML and create a thread group
		jmxCurrentElement = xmlHandler.initializeXML(apiListDetails);
		jmxCurrentElement = xmlHandler.createThreadGroup(jmxCurrentElement);

//		get list of all items in the api collection
		jsonItems = postmanHandler.getItems();

//		write each item into JMX
		for (int i = 0; i < jsonItems.size(); i++) {
			writeItemtoJMX(jmxCurrentElement, (JSONObject) jsonItems.get(i));
		}
		
		String variableNames[] = postmanHandler.getVariableNames();
		xmlHandler.createUserDefinedVariables(variableNames);

//		finally write the XML into JMX file
		xmlHandler.writeXML();

	}

	private void writeItemtoJMX(Element parentElement, JSONObject jsonObject) {
		
		Element childElement;
		JSONArray childJsonObject;
		
		if(isFolder(jsonObject)) {
			childElement=xmlHandler.createSimpleControler(parentElement, jsonObject.get("name").toString());
			childJsonObject=(JSONArray) jsonObject.get("item");
			for(int i=0; i<childJsonObject.size();i++) {
				writeItemtoJMX(childElement, (JSONObject) childJsonObject.get(i));
			}
		} else {
			xmlHandler.createHTTPRequest(parentElement, postmanHandler.getAPIParameter(jsonObject));
		}
		
	}

	private Boolean isFolder(JSONObject jsonObject) {
		return jsonObject.containsKey("item");
	}

}
