package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.agent.JokeTellerAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class JokeTellerController {

    private final JokeTellerAgent jokeTellerAgent;

    @Autowired
    public JokeTellerController(JokeTellerAgent jokeTellerAgent) {
        this.jokeTellerAgent = jokeTellerAgent;
    }


    @GetMapping("/hello")
    public String helloWorld() {
         return "OK";
    }

    @GetMapping("/jokes")
    public String tellJoke(@RequestParam(defaultValue = "Tell me a joke") String prompt) {
        return jokeTellerAgent.tellJoke(prompt);
    }
}
