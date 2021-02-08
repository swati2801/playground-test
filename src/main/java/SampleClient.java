import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.StopWatch;

public class SampleClient implements IClientInterceptor {
	ArrayList<String> lastNameList = null;	//stores name read from file to iterate over
	static Long everyRequestsTime = 0L;
	private static final org.slf4j.Logger myLog = org.slf4j.LoggerFactory.getLogger(SampleClient.class);
	
	public static void main(String[] theArgs) {
		SampleClient cl = new SampleClient();
		//Reading names from file
		cl.readLastNameFromFile();
		//Making requests in loop
		cl.makeRequest();

	}

	public void makeRequest() {
		if (!lastNameList.isEmpty() && lastNameList.size() > 0) {
			// Create a FHIR client
			FhirContext fhirContext = FhirContext.forR4();
			IGenericClient client = fhirContext
					.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
			
			client.registerInterceptor(new SampleClient());
			client.registerInterceptor(new LoggingInterceptor(false));

			
			CacheControlDirective c = new CacheControlDirective();
			c.setNoCache(false);
			for (int i = 0; i < 3; i++) {
				for (String name : lastNameList) {
					if (i == 2)
						c.setNoCache(true);		//Disabling cache
					client.search().forResource("Patient")
							.where(Patient.FAMILY.matches().value(name))//inside name, sending names from file
							.returnBundle(Bundle.class).cacheControl(c)
							.execute();
				}
				;
				myLog.info("Average Time for iteration " + i + "::::  "
						+ everyRequestsTime / lastNameList.size());
				everyRequestsTime = 0L;
			}
		} else {
			myLog.info("File is empty. There are no names found in files.");
		}
	}

	public void readLastNameFromFile() {
		try {
			String fileName = "lastName.txt";
			ClassLoader classLoader = getClass().getClassLoader();
			File myObj = new File(classLoader.getResource(fileName).getFile());
			Scanner myReader = new Scanner(myObj);
			lastNameList = new ArrayList<>();
			while (myReader.hasNextLine()) {
				String name = myReader.nextLine();
				if (!name.trim().isEmpty() && !name.trim().equals(""))
					lastNameList.add(name.trim());
			}
			//lastNameList.forEach(System.out::println);
			myReader.close();
		} catch (FileNotFoundException e) {
			myLog.error("An error occurred.");
			e.printStackTrace();
		}
	}

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		// Nothing to do before sending request
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		StopWatch s = theResponse.getRequestStopWatch();
		everyRequestsTime = everyRequestsTime + s.getMillis();
		myLog.info("==========================Time taken: "
				+ s.getMillis()
				+ " ms.=========================================");
	}

}
