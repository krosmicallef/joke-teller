package com.christophermicallef.joketeller.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    // Reads from application.properties (openai.api-key) if set,
    // otherwise falls back to the OPENAI_API_KEY environment variable.
    @Value("${openai.api-key:#{null}}")
    private String configuredApiKey;

    @Bean
    public OpenAIClient openAIClient() {
        if (configuredApiKey != null && !configuredApiKey.isBlank()) {
            return OpenAIOkHttpClient.builder()
                    .apiKey(configuredApiKey)
                    .build();
        }
        // Falls back to OPENAI_API_KEY / OPENAI_ORG_ID / OPENAI_PROJECT_ID env vars
        return OpenAIOkHttpClient.fromEnv();
    }
}
