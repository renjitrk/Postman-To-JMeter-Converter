package postmantojmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLHandler {

	private Document document;
	private String JmxPath;

	public XMLHandler(String jmxPath) throws IOException {

		String input, fileName;
		Boolean replace = true;
		File jmxFile = new File(jmxPath);
		int i = 0;

		if (jmxFile.exists()) {
			replace = false;
			System.out.println("File \"" + jmxFile.getName() + "\" already exists. Do you want to replace? (Y/N): ");
			input = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if (input.substring(0, 1).equalsIgnoreCase("Y")) {
				replace = true;
			} else {
				replace = false;
			}
		}

		if (replace) {
			JmxPath = jmxPath;
		} else {
			fileName = jmxPath.trim();
			fileName = fileName.substring(0, fileName.length() - 4);
			while (new File(fileName + "(" + ++i + ").jmx").exists()) {
			}
			JmxPath = fileName + "(" + i + ").jmx";
		}

	}

	public Element initializeXML(String[] apiDetails) {

		try {

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			document = documentBuilder.newDocument();

//          JMeter root element
			Element root = document.createElement("jmeterTestPlan");
			root.setAttribute("jmeter", "5.0 r1840935");
			root.setAttribute("properties", "5.0");
			root.setAttribute("version", "1.2");
			document.appendChild(root);

//          Root hashTree element
			Element hashTree = document.createElement("hashTree");
			root.appendChild(hashTree);

//          testPlan element
			Element testPlan = document.createElement("TestPlan");
			testPlan.setAttribute("enabled", "true");
			testPlan.setAttribute("testname", apiDetails[0] + " - TestPlan");
			testPlan.setAttribute("testclass", "TestPlan");
			testPlan.setAttribute("guiclass", "TestPlanGui");
			hashTree.appendChild(testPlan);

//          return hashTree element
			Element returnElement = document.createElement("hashTree");
			hashTree.appendChild(returnElement);

//        	testPlan comments element
			Element stringProp = document.createElement("stringProp");
			stringProp.setAttribute("name", "TestPlan.comments");
			stringProp.appendChild(document.createTextNode(apiDetails[1]));
			testPlan.appendChild(stringProp);

//          testPlan comments element
			Element boolProp1 = document.createElement("boolProp");
			boolProp1.setAttribute("name", "TestPlan.functional_mode");
			boolProp1.appendChild(document.createTextNode("false"));
			testPlan.appendChild(boolProp1);

//          testPlan comments element
			Element boolProp2 = document.createElement("boolProp");
			boolProp2.setAttribute("name", "TestPlan.tearDown_on_shutdown");
			boolProp2.appendChild(document.createTextNode("true"));
			testPlan.appendChild(boolProp2);

//          testPlan comments element
			Element boolProp3 = document.createElement("boolProp");
			boolProp3.setAttribute("name", "TestPlan.serialize_threadgroups");
			boolProp3.appendChild(document.createTextNode("false"));
			testPlan.appendChild(boolProp3);

//            testPlan elementProp element
			Element elementProp = document.createElement("elementProp");
			elementProp.setAttribute("enabled", "true");
			elementProp.setAttribute("testname", "User Defined Variables");
			elementProp.setAttribute("testclass", "Arguments");
			elementProp.setAttribute("guiclass", "ArgumentsPanel");
			elementProp.setAttribute("elementType", "Arguments");
			elementProp.setAttribute("name", "TestPlan.user_defined_variables");
			testPlan.appendChild(elementProp);

//            elementProp collectionProp element
			Element collectionProp = document.createElement("collectionProp");
			collectionProp.setAttribute("name", "Arguments.arguments");
			elementProp.appendChild(collectionProp);

//            testPlan comments element
			Element stringProp1 = document.createElement("stringProp");
			stringProp1.setAttribute("name", "TestPlan.user_define_classpath");
			stringProp1.appendChild(document.createTextNode(apiDetails[1].substring(0, 10)));
			testPlan.appendChild(stringProp1);

			return returnElement;

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		return null;

	}

	public void writeXML() {

		String xmlFilePath = JmxPath;

		try {

//        transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(xmlFilePath));

			transformer.transform(domSource, streamResult);

		} catch (TransformerException e) {

		}
		
		System.out.println("XML Successfully saved at: " + xmlFilePath + "..");
	}

	public Element createThreadGroup(Element parentElement) {

//		ThreadGroup Element
		Element threadGroup = document.createElement("ThreadGroup");
		threadGroup.setAttribute("enabled", "true");
		threadGroup.setAttribute("testname", "Thread Group");
		threadGroup.setAttribute("testclass", "ThreadGroup");
		threadGroup.setAttribute("guiclass", "ThreadGroupGui");
		parentElement.appendChild(threadGroup);

//		stringProp element
		Element stringProp = document.createElement("stringProp");
		stringProp.setAttribute("name", "ThreadGroup.on_sample_error");
		stringProp.appendChild(document.createTextNode("continue"));
		threadGroup.appendChild(stringProp);

//		thread elementProp Element
		Element elementProp = document.createElement("elementProp");
		elementProp.setAttribute("enabled", "true");
		elementProp.setAttribute("testname", "Loop Controller");
		elementProp.setAttribute("testclass", "LoopController");
		elementProp.setAttribute("guiclass", "LoopControlPanel");
		elementProp.setAttribute("elementType", "LoopController");
		elementProp.setAttribute("name", "ThreadGroup.main_controller");
		threadGroup.appendChild(elementProp);

//      elementProp boolProp element
		Element boolProp1 = document.createElement("boolProp");
		boolProp1.setAttribute("name", "LoopController.continue_forever");
		boolProp1.appendChild(document.createTextNode("false"));
		elementProp.appendChild(boolProp1);

//		elementProp comments element
		Element stringProp1 = document.createElement("stringProp");
		stringProp1.setAttribute("name", "LoopController.loops");
		stringProp1.appendChild(document.createTextNode("1"));
		elementProp.appendChild(stringProp1);

//		threadGroup comments element
		Element stringProp2 = document.createElement("stringProp");
		stringProp2.setAttribute("name", "ThreadGroup.num_threads");
		stringProp2.appendChild(document.createTextNode("1"));
		threadGroup.appendChild(stringProp2);

//		threadGroup comments element
		Element stringProp3 = document.createElement("stringProp");
		stringProp3.setAttribute("name", "ThreadGroup.ramp_time");
		stringProp3.appendChild(document.createTextNode("1"));
		threadGroup.appendChild(stringProp3);

//		threadGroup boolProp element
		Element boolProp2 = document.createElement("boolProp");
		boolProp2.setAttribute("name", "ThreadGroup.scheduler");
		boolProp2.appendChild(document.createTextNode("false"));
		threadGroup.appendChild(boolProp2);

//		threadGroup comments element
		Element stringProp4 = document.createElement("stringProp");
		stringProp4.setAttribute("name", "ThreadGroup.duration");
		threadGroup.appendChild(stringProp4);

//		threadGroup comments element
		Element stringProp5 = document.createElement("stringProp");
		stringProp5.setAttribute("name", "ThreadGroup.ramp_time");
		threadGroup.appendChild(stringProp5);

//		return hashTree element
		Element returnElement = document.createElement("hashTree");
		parentElement.appendChild(returnElement);

		return returnElement;
	}

	public Element createSimpleControler(Element parentElement, String ControllerName) {

//		GenericController element
		Element genericController = document.createElement("GenericController");
		genericController.setAttribute("enabled", "true");
		genericController.setAttribute("testname", ControllerName);
		genericController.setAttribute("testclass", "GenericController");
		genericController.setAttribute("guiclass", "LogicControllerGui");
		parentElement.appendChild(genericController);

//		return hashTree element
		Element returnElement = document.createElement("hashTree");
		parentElement.appendChild(returnElement);

		return returnElement;

	}

	public Element createHTTPRequest(Element parentElement, JSONObject requestParams) {

		String apiName, description, protocol, url, port, path, method, contentEncoding, embedded_url_re,
				connect_timeout, response_timeout;
		Boolean follow_redirects, auto_redirects, use_keepalive, DO_MULTIPART_POST;

//		get apiName
		if (requestParams.containsKey("apiName"))
			apiName = requestParams.get("apiName").toString();
		else
			apiName = "";

//		get description
		if (requestParams.containsKey("description"))
			description = requestParams.get("description").toString();
		else
			description = "";

//		get protocol
		if (requestParams.containsKey("protocol"))
			protocol = requestParams.get("protocol").toString();
		else
			protocol = "";

//		get url
		if (requestParams.containsKey("url"))
			url = requestParams.get("url").toString();
		else
			url = "";

//		get port
		if (requestParams.containsKey("port"))
			port = requestParams.get("port").toString();
		else
			port = "";

//		get path
		if (requestParams.containsKey("path"))
			path = requestParams.get("path").toString();
		else
			path = "";

//		get method
		if (requestParams.containsKey("method"))
			method = requestParams.get("method").toString();
		else
			method = "";

//		get contentEncoding
		if (requestParams.containsKey("contentEncoding"))
			contentEncoding = requestParams.get("contentEncoding").toString();
		else
			contentEncoding = "";

//		get embedded_url_re
		if (requestParams.containsKey("embedded_url_re"))
			embedded_url_re = requestParams.get("embedded_url_re").toString();
		else
			embedded_url_re = "";

//		get connect_timeout
		if (requestParams.containsKey("connect_timeout"))
			connect_timeout = requestParams.get("connect_timeout").toString();
		else
			connect_timeout = "";

//		get response_timeout
		if (requestParams.containsKey("response_timeout"))
			response_timeout = requestParams.get("response_timeout").toString();
		else
			response_timeout = "";

//		get follow_redirects
		if (requestParams.containsKey("follow_redirects"))
			follow_redirects = (Boolean) requestParams.get("follow_redirects");
		else
			follow_redirects = true;

//		get auto_redirects
		if (requestParams.containsKey("auto_redirects"))
			auto_redirects = (Boolean) requestParams.get("auto_redirects");
		else
			auto_redirects = false;

//		get use_keepalive
		if (requestParams.containsKey("use_keepalive"))
			use_keepalive = (Boolean) requestParams.get("use_keepalive");
		else
			use_keepalive = true;

//		get DO_MULTIPART_POST
		if (requestParams.containsKey("DO_MULTIPART_POST"))
			DO_MULTIPART_POST = (Boolean) requestParams.get("DO_MULTIPART_POST");
		else
			DO_MULTIPART_POST = false;

//		HTTPSamplerProxy element
		Element httpSamplerProxy = document.createElement("HTTPSamplerProxy");
		httpSamplerProxy.setAttribute("enabled", "true");
		httpSamplerProxy.setAttribute("testname", apiName);
		httpSamplerProxy.setAttribute("testclass", "HTTPSamplerProxy");
		httpSamplerProxy.setAttribute("guiclass", "HttpTestSampleGui");
		parentElement.appendChild(httpSamplerProxy);

//		response_timeout stringProp element
		Element stringProp = document.createElement("stringProp");
		stringProp.setAttribute("name", "TestPlan.comments");
		stringProp.appendChild(document.createTextNode(description));
		httpSamplerProxy.appendChild(stringProp);

//		elementProp element
		Element elementProp = document.createElement("elementProp");
		elementProp.setAttribute("enabled", "true");
		elementProp.setAttribute("testname", "User Defined Variables");
		elementProp.setAttribute("testclass", "Arguments");
		elementProp.setAttribute("guiclass", "HTTPArgumentsPanel");
		elementProp.setAttribute("elementType", "Arguments");
		elementProp.setAttribute("name", "HTTPsampler.Arguments");
		httpSamplerProxy.appendChild(elementProp);

//		collectionProp element
		Element collectionProp = document.createElement("collectionProp");
		collectionProp.setAttribute("name", "Arguments.arguments");
		elementProp.appendChild(collectionProp);

//		URL stringProp element
		Element stringProp1 = document.createElement("stringProp");
		stringProp1.setAttribute("name", "HTTPSampler.domain");
		stringProp1.appendChild(document.createTextNode(url));
		httpSamplerProxy.appendChild(stringProp1);

//		Port stringProp element
		Element stringProp2 = document.createElement("stringProp");
		stringProp2.setAttribute("name", "HTTPSampler.port");
		stringProp2.appendChild(document.createTextNode(port));
		httpSamplerProxy.appendChild(stringProp2);

//		Protocol stringProp element
		Element stringProp3 = document.createElement("stringProp");
		stringProp3.setAttribute("name", "HTTPSampler.protocol");
		stringProp3.appendChild(document.createTextNode(protocol));
		httpSamplerProxy.appendChild(stringProp3);

//		contentEncoding stringProp element
		Element stringProp4 = document.createElement("stringProp");
		stringProp4.setAttribute("name", "HTTPSampler.contentEncoding");
		stringProp4.appendChild(document.createTextNode(contentEncoding));
		httpSamplerProxy.appendChild(stringProp4);

//		Path stringProp element
		Element stringProp5 = document.createElement("stringProp");
		stringProp5.setAttribute("name", "HTTPSampler.path");
		stringProp5.appendChild(document.createTextNode(path));
		httpSamplerProxy.appendChild(stringProp5);

//		method stringProp element
		Element stringProp6 = document.createElement("stringProp");
		stringProp6.setAttribute("name", "HTTPSampler.method");
		stringProp6.appendChild(document.createTextNode(method));
		httpSamplerProxy.appendChild(stringProp6);

//		follow_redirects stringProp element
		Element stringProp7 = document.createElement("stringProp");
		stringProp7.setAttribute("name", "HTTPSampler.follow_redirects");
		stringProp7.appendChild(document.createTextNode(follow_redirects.toString()));
		httpSamplerProxy.appendChild(stringProp7);

//		auto_redirects stringProp element
		Element stringProp8 = document.createElement("stringProp");
		stringProp8.setAttribute("name", "HTTPSampler.auto_redirects");
		stringProp8.appendChild(document.createTextNode(auto_redirects.toString()));
		httpSamplerProxy.appendChild(stringProp8);

//		use_keepalive stringProp element
		Element stringProp9 = document.createElement("stringProp");
		stringProp9.setAttribute("name", "HTTPSampler.use_keepalive");
		stringProp9.appendChild(document.createTextNode(use_keepalive.toString()));
		httpSamplerProxy.appendChild(stringProp9);

//		DO_MULTIPART_POST stringProp element
		Element stringProp10 = document.createElement("stringProp");
		stringProp10.setAttribute("name", "HTTPSampler.DO_MULTIPART_POST");
		stringProp10.appendChild(document.createTextNode(DO_MULTIPART_POST.toString()));
		httpSamplerProxy.appendChild(stringProp10);

//		embedded_url_re stringProp element
		Element stringProp11 = document.createElement("stringProp");
		stringProp11.setAttribute("name", "HTTPSampler.embedded_url_re");
		stringProp11.appendChild(document.createTextNode(embedded_url_re));
		httpSamplerProxy.appendChild(stringProp11);

//		connect_timeout stringProp element
		Element stringProp12 = document.createElement("stringProp");
		stringProp12.setAttribute("name", "HTTPSampler.connect_timeout");
		stringProp12.appendChild(document.createTextNode(connect_timeout));
		httpSamplerProxy.appendChild(stringProp12);

//		response_timeout stringProp element
		Element stringProp13 = document.createElement("stringProp");
		stringProp13.setAttribute("name", "HTTPSampler.response_timeout");
		stringProp13.appendChild(document.createTextNode(response_timeout));
		httpSamplerProxy.appendChild(stringProp13);

//		return hashTree element
		Element returnElement = document.createElement("hashTree");
		parentElement.appendChild(returnElement);

		return returnElement;

	}

}
