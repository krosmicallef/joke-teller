package com.christophermicallef.joketeller.controller;

import com.christophermicallef.joketeller.service.JokeTellerService;
import com.christophermicallef.joketeller.service.RiddleTellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JokeTellerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JokeTellerService jokeTellerService;
    @Mock
    private RiddleTellerService riddleTellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        JokeTellerController controller = new JokeTellerController(jokeTellerService, riddleTellerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void helloWorld_returnsOk() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verifyNoInteractions(jokeTellerService);
    }

    @Test
    void tellJoke_withExplicitPrompt_returnsJokeFromService() throws Exception {
        when(jokeTellerService.tellJoke("Tell me a joke about cats"))
                .thenReturn("Why did the cat sit on the computer? To keep an eye on the mouse.");

        mockMvc.perform(get("/api/jokes").param("prompt", "Tell me a joke about cats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Why did the cat sit on the computer? To keep an eye on the mouse."));

        verify(jokeTellerService).tellJoke("Tell me a joke about cats");
    }

    @Test
    void tellJoke_withNoPrompt_usesDefaultPrompt() throws Exception {
        when(jokeTellerService.tellJoke("Tell me a joke"))
                .thenReturn("A default joke");

        mockMvc.perform(get("/api/jokes"))
                .andExpect(status().isOk())
                .andExpect(content().string("A default joke"));

        verify(jokeTellerService).tellJoke("Tell me a joke");
    }

    @Test
    void tellJoke_withEmptyPrompt_fallsBackToDefaultPrompt() throws Exception {
        when(jokeTellerService.tellJoke("Tell me a joke")).thenReturn("Fallback joke");

        mockMvc.perform(get("/api/jokes").param("prompt", ""))
                .andExpect(status().isOk())
                .andExpect(content().string("Fallback joke"));

        verify(jokeTellerService).tellJoke("Tell me a joke");
    }

    @Test
    void tellJoke_delegatesToServiceExactlyOnce() throws Exception {
        when(jokeTellerService.tellJoke(anyString())).thenReturn("Some joke");

        mockMvc.perform(get("/api/jokes").param("prompt", "anything"));

        verify(jokeTellerService, times(1)).tellJoke("anything");
        verifyNoMoreInteractions(jokeTellerService);
    }
}