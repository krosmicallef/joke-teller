package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.entity.JokeHistory;
import com.christophermicallef.joketeller.service.JokeTellerService;
import com.christophermicallef.joketeller.service.RiddleTellerService;
import com.christophermicallef.joketeller.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class JokeTellerController {

    private final JokeTellerService jokeTellerService;

    private final RiddleTellerService riddleTellerService;

    private final SubscriberService subscriberService;

    @Autowired
    public JokeTellerController(JokeTellerService jokeTellerService, RiddleTellerService riddleTellerService,
                                SubscriberService subscriberService) {
        this.jokeTellerService = jokeTellerService;
        this.riddleTellerService = riddleTellerService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/hello")
    public String helloWorld() {
         return "OK";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping({"/subscribers", "/subscriptions"})
    public ResponseEntity<Map<String, String>> subscribe(@Valid @RequestBody SubscribeRequest request) {
        if (!subscriberService.subscribe(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "ALREADY_SUBSCRIBED", "message", "Email is already subscribed"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("code", "SUBSCRIBED", "message", "Successfully subscribed"));
    }

    @GetMapping("/jokes")
    public String tellJoke(@RequestParam(defaultValue = "Tell me a joke") String prompt) {
        return jokeTellerService.tellJoke(prompt);
    }

    @GetMapping("/jokes/random")
    public ResponseEntity<JokeHistory> tellRandomJoke() {
        return jokeTellerService.tellRandomJoke()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/riddles")
    public String tellRiddle(@RequestParam(defaultValue = "Give me a riddle, trick question or conundrum") String prompt) {
        return riddleTellerService.tellRiddle(prompt);
    }
}
