package postmantojmx;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class ConverterMain {

	public static void main(String[] args) throws IOException, ParseException {

		if (args.length == 0) {
			System.out.println("Expected argument: Path to Postman Collection file (Only v2+ exported json files)");
		} else {
			String postmanJsonPath = args[0];

			JsonJmxConverter converter = new JsonJmxConverter();
			converter.convert(postmanJsonPath);
		}

	}

}
