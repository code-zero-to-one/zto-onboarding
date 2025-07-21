package com.codezerotoone.mvp.domain.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Is this needed?
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "백엔드 서버가 정상 작동합니다.";
    }
}
