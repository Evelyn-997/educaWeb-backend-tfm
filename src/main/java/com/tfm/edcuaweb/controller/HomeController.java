package com.tfm.edcuaweb.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping
public class HomeController {
	
	 @GetMapping("/")
	    public String home() {
	        return "El Servidor BackEnd funciona correctamente";
	    }

}
