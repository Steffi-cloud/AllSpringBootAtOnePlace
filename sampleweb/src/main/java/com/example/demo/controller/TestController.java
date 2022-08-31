package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/getName")
public class TestController {

	@GetMapping(value="/fetch")
	public String getName() {
		return "hello";
	}
}
