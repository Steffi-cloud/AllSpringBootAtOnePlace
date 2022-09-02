package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CassandraConfig;

@RestController
public class TestController {
@Autowired
private CassandraConfig config;
	@GetMapping("/testName")
	public String getTest() {
		config.initialiseConnect();
		return "hello";
	}
}
