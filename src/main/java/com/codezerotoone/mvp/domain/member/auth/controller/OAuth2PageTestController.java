package com.codezerotoone.mvp.domain.member.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2PageTestController {
    @GetMapping("/custom/login")
    public String oAuth2LoginPage() {
        return "login";
    }
}
