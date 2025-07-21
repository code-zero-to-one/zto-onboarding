package com.codezerotoone.mvp.global.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DummyController {
    public static String RETURN_VALUE = "Hello, World!";

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(RETURN_VALUE);
    }
}
