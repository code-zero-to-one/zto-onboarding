package com.codezerotoone.mvp.global.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorTestController {

    @GetMapping("/api/v1/throw-error")
    public String test() {
        int a = 1 / 0;
        return null;
    }
}
