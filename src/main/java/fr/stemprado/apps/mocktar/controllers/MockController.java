package fr.stemprado.apps.mocktar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.stemprado.apps.mocktar.beans.Mock;
import fr.stemprado.apps.mocktar.services.MockService;

@RestController
public class MockController {

	@Autowired
	private MockService mockService;

	@GetMapping("/mock")
	public Mock getMock(@RequestParam(value = "name") String name) {
		return mockService.getMock(name);
	}

	@PostMapping("/mock")
	public void postMock(@RequestBody Mock mock) {
		mockService.postMock(mock);
	}

}