package fr.stemprado.apps.mocktar.controllers;

import java.util.List;

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

import fr.stemprado.apps.mocktar.beans.Mock;
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
		Mock mock = mocks.stream().filter(m -> m.request.equals(mockURI)).findFirst().orElseThrow(() -> new NotFoundException());

      	return mock.response;
    }

}