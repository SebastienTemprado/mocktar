package fr.stemprado.apps.mocktar.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		matchingMocks = filterMocksByMatchingRequestBody(request, matchingMocks);
		matchingMocks = filterMocksByMatchingHeaderParams(request, matchingMocks);
		Mock bestMatchingMock = filterMocksByMatchingQueryParams(request, matchingMocks);

		return bestMatchingMock.response;
	}

	private List<Mock> filterMocksByMatchingRequestBody(HttpServletRequest request, List<Mock> matchingMocks)
			throws IOException {
		String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
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
		Mock matchingMock = matchingMocks.stream().findFirst().orElseThrow(() -> new NotFoundException());

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
		return matchingMock;
	}

}