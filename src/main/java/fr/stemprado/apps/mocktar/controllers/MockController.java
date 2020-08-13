package fr.stemprado.apps.mocktar.controllers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.stemprado.apps.mocktar.beans.HeaderParam;
import fr.stemprado.apps.mocktar.beans.Mock;
import fr.stemprado.apps.mocktar.beans.QueryParam;
import fr.stemprado.apps.mocktar.exceptions.NotFoundException;
import fr.stemprado.apps.mocktar.services.MockService;

@RestController
public class MockController {

	@Autowired
	private MockService mockService;

	private Logger logger = LoggerFactory.getLogger(MockController.class);

	@GetMapping("/mocks")
	public List<Mock> getMocks(@RequestParam(value = "name", required = false) String name) {
		return mockService.getMocks(name);
	}

	@PostMapping("/mocks")
	public void postMock(@RequestBody Mock mock) {
		mockService.postMock(mock);
	}

	@PutMapping("/mocks")
	public void putMock(@RequestBody Mock mock) {
		mockService.putMock(mock);
	}

	@DeleteMapping("/mocks/{name}")
	public void deleteMock(@PathVariable String name) {
		mockService.deleteMock(name);
	}

	@RequestMapping("/mocktar/**")
	public String handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mockURI = request.getRequestURI().replace("/mocktar", "");
		List<Mock> mocks = mockService.getMocks(null);
		List<Mock> matchingMocks = mocks.stream()
				.filter(m -> m.request.equals(mockURI) && m.verb.equals(request.getMethod()))
				.collect(Collectors.toList());

		String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		matchingMocks = filterMocksByMatchingRequestBody(requestBody, matchingMocks);
		matchingMocks = filterMocksByMatchingHeaderParams(request, matchingMocks);
		Mock bestMatchingMock = filterMocksByMatchingQueryParams(request, matchingMocks);

		if (bestMatchingMock == null) {
			noMatchingMockLog(request, requestBody, mockURI);
			throw new NotFoundException();
		} else {
			matchingMockLog(request, requestBody, mockURI, bestMatchingMock);
			return bestMatchingMock.response;
		}
	}

	private String catchedRequestLog(HttpServletRequest request, String requestBody, String mockURI) throws IOException {
		boolean definedBody = requestBody != null && requestBody.length() != 0;
		String bodyLog = definedBody ? "\nbody:" + requestBody : "\n";
		String headerParams = "\nheaders:[\n";
		Enumeration<String> requestHeaderNames = request.getHeaderNames();
		while (requestHeaderNames.hasMoreElements()) {
			String requestHeaderName = requestHeaderNames.nextElement();
			headerParams += '\t' + requestHeaderName + "=";
			Enumeration requestHeaderValues = request.getHeaders(requestHeaderName);
			if (requestHeaderValues != null) {
				while (requestHeaderValues.hasMoreElements()) {
					String requestHeaderValue = (String) requestHeaderValues.nextElement();
					headerParams += requestHeaderValue + ",";
				}
				headerParams = headerParams.substring(0, headerParams.length() - 1);
			}
			headerParams += '\n';
		}
		headerParams += ']';

		String queryParamsLog = ",\nqueryParams:[\n";
		Map<String, String[]> queryParameters = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : queryParameters.entrySet()) {
			queryParamsLog += '\t' + entry.getKey() + "=" + entry.getValue() + '\n';
		}
		queryParamsLog += "]\n";
		
		return "\nCatched Request = " + request.getMethod() + " " + mockURI + bodyLog + headerParams + queryParamsLog;
	}

	private void noMatchingMockLog(HttpServletRequest request, String requestBody, String mockURI) throws IOException {
		String noMatchingMockLog = catchedRequestLog(request, requestBody, mockURI) + "\n--> No matching mock\n";
		logger.info(noMatchingMockLog);
	}

	private void matchingMockLog(HttpServletRequest request, String requestBody, String mockURI, Mock bestMatchingMock) throws IOException {
		String matchingMockLog = catchedRequestLog(request, requestBody, mockURI) + "\n--> Best matching mock = \n" + bestMatchingMock.name + "\n" + bestMatchingMock.toString();
		logger.info(matchingMockLog);
	}

	private List<Mock> filterMocksByMatchingRequestBody(String requestBody, List<Mock> matchingMocks)
			throws IOException {
		return matchingMocks.stream()
				.filter(mock -> "".equals(mock.body)
						|| mock.body.replaceAll("(\r\n|\n)", "").equals(requestBody.replaceAll("(\r\n|\n)", "")))
				.collect(Collectors.toList());
	}

	private List<Mock> filterMocksByMatchingHeaderParams(HttpServletRequest request, List<Mock> matchingMocks)
			throws IOException {
		boolean customHeaders = false;

		Enumeration<String> requestHeaderNames = request.getHeaderNames();
		Set<Mock> mocksToRemove = new HashSet<>();
		for (Mock currentMock : matchingMocks) {
			int foundHeaderParamsCounter = 0;
			while (requestHeaderNames.hasMoreElements()) {
				String requestHeaderName = requestHeaderNames.nextElement();

				if (requestHeaderName.equals("host") || requestHeaderName.equals("connection") || requestHeaderName.equals("content-length") 
					|| requestHeaderName.equals("user-agent") || requestHeaderName.equals("content-type") || requestHeaderName.equals("accept") 
					|| requestHeaderName.equals("origin") || requestHeaderName.equals("sec-fetch-site") || requestHeaderName.equals("sec-fetch-mode")  
					|| requestHeaderName.equals("sec-fetch-dest") || requestHeaderName.equals("accept-encoding") || requestHeaderName.equals("accept-language") ) {
					continue;
				}
				
				customHeaders = true;
				Enumeration requestHeaderValues = request.getHeaders(requestHeaderName);
				if (requestHeaderValues != null) {
					while (requestHeaderValues.hasMoreElements()) {
						String requestHeaderValue = (String) requestHeaderValues.nextElement();
						for (HeaderParam mockHeaderParam : currentMock.headerParams) {
							if (mockHeaderParam.name.equals(requestHeaderName) && mockHeaderParam.value.equals(requestHeaderValue)) {
								foundHeaderParamsCounter++;
								break;
							}
						}
					}
				}
			}
			if (foundHeaderParamsCounter != currentMock.headerParams.size()) {
				mocksToRemove.add(currentMock);
			}
		}

		if (!customHeaders) {
			matchingMocks = matchingMocks.stream().filter(mock -> mock.headerParams.size() == 0).collect(Collectors.toList());
		}

		for (Mock mockToRemove : mocksToRemove) {
			matchingMocks.remove(mockToRemove);
		}
		
		return matchingMocks;
	}

	private Mock filterMocksByMatchingQueryParams(HttpServletRequest request, List<Mock> matchingMocks) {
		Mock matchingMock = matchingMocks.stream().findFirst().orElse(null);

		if (matchingMock != null) {
			Map<String, String[]> queryParameters = request.getParameterMap();
			int highestMatchingQueryParamsCounter = 0;
			for (Mock currentMock : matchingMocks) {
				int matchingQueryParamCounter = 0;
				for (QueryParam queryParam : currentMock.queryParams) {
					for (Map.Entry<String, String[]> entry : queryParameters.entrySet()) {
						if (queryParam.name.equals(entry.getKey())) {
							for (String queryParamValue : entry.getValue()) {
								Pattern p = Pattern.compile(queryParam.value);
								if (p.matcher(queryParamValue).matches()) {
									matchingQueryParamCounter++;
								}
							}
						}
					}
				}
				// what happens when we have 2 matching mocks (QR a,b vs QR c,d) ? --> TODO add
				// an order field
				if (matchingQueryParamCounter > highestMatchingQueryParamsCounter) {
					highestMatchingQueryParamsCounter = matchingQueryParamCounter;
					matchingMock = currentMock;
				}
			}
		}
		return matchingMock;
	}

}