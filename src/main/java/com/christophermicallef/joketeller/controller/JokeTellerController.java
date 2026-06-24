package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.service.JokeTellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class JokeTellerController {

    private final JokeTellerService jokeTellerService;

    @Autowired
    public JokeTellerController(JokeTellerService jokeTellerService) {
        this.jokeTellerService = jokeTellerService;
    }

    @GetMapping("/hello")
    public String helloWorld() {
         return "OK";
    }

    @GetMapping("/jokes")
    public String tellJoke(@RequestParam(defaultValue = "Tell me a joke") String prompt) {
        return jokeTellerService.tellJoke(prompt);
    }
}
