package com.playground.interceptor;

import java.io.IOException;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

public class ResponseTimeInterceptor  implements IClientInterceptor {
	public static Long everyRequestTime = 0L;
	private static final org.slf4j.Logger myLog = org.slf4j.LoggerFactory.getLogger(ResponseTimeInterceptor.class);
	
	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		// Nothing to do before sending request
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		StopWatch s = theResponse.getRequestStopWatch();
		everyRequestTime = everyRequestTime + s.getMillis();
		myLog.info("==========================Time taken: "
				+ s.getMillis()
				+ " ms.=========================================");
	}
}
