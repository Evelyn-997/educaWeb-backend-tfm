package com.tfm.edcuaweb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController  {

	@GetMapping
	public ResponseEntity<String> test() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Ruta no encontrada ❌");
	}
	

}
