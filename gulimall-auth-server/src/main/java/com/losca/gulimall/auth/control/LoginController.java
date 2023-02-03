package com.losca.gulimall.auth.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String login(){
        return "index";
    }
}
