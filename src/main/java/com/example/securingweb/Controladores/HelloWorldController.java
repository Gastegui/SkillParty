package com.example.securingweb.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloWorldController {

    @GetMapping("/helloWorld")
    public String helloWorld(@RequestParam(name = "secret", required = false) String secret, Model model) {
        model.addAttribute("message", "Hello, World!");
        if ("ludok".equals(secret)) {
            model.addAttribute("showButton", true);
        } else {
            model.addAttribute("showButton", false);
        }
        return "helloWorld";
    }
}
