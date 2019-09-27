package postmantojmx;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class ConverterMain {

	public static void main(String[] args) throws IOException, ParseException {
		
		System.out.println("Hello!");
		String postmanJsonPath = "C:/Users/renjith/Desktop/StarkTest.postman_collection.json";
		
		JsonJmxConverter converter = new JsonJmxConverter();
		converter.convert(postmanJsonPath);
		
		
		

	}
	
	

}
