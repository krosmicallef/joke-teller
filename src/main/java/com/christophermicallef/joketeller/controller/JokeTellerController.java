package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.service.JokeTellerService;
import com.christophermicallef.joketeller.service.RiddleTellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class JokeTellerController {

    private final JokeTellerService jokeTellerService;

    private final RiddleTellerService riddleTellerService;

    @Autowired
    public JokeTellerController(JokeTellerService jokeTellerService, RiddleTellerService riddleTellerService) {
        this.jokeTellerService = jokeTellerService;
        this.riddleTellerService = riddleTellerService;
    }

    @GetMapping("/hello")
    public String helloWorld() {
         return "OK";
    }

    @GetMapping("/jokes")
    public String tellJoke(@RequestParam(defaultValue = "Tell me a joke") String prompt) {
        return jokeTellerService.tellJoke(prompt);
    }

    @GetMapping("/riddles")
    public String tellRiddle(@RequestParam(defaultValue = "Give me a riddle, trick question or conundrum") String prompt) {
        return riddleTellerService.tellRiddle(prompt);
    }
}
