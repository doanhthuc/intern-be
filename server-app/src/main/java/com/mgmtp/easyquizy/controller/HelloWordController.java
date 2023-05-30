package com.mgmtp.easyquizy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWordController {

    @GetMapping("/{name}")
    public ResponseEntity<String> getName(@PathVariable String name) {
        return ResponseEntity.ok("Hello Word: " + name);
    }
}
