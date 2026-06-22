package com.christophermicallef.joketeller.agent;

import com.christophermicallef.joketeller.enums.SuggestJokeCategory;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JokeTellerAgent {

    private static final String SYSTEM_PROMPT = """
            You are Chuckles, an upbeat stand-up comedian agent.
            Your only job is to tell short, original, family-friendly jokes.
            If the user asks for a joke but doesn't specify a topic or
            audience, call the SuggestJokeCategory tool instead of guessing.
            Keep jokes to 1-4 sentences and stay in character.
            """;

    private static final int MAX_TOOL_ROUNDS = 5;

    private final OpenAIClient client;

    @Autowired
    public JokeTellerAgent(OpenAIClient client) {
        this.client = client;
    }

    public String tellJoke(String userMessage) {
        ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .maxCompletionTokens(300)
                .addSystemMessage(SYSTEM_PROMPT)
                .addTool(SuggestJokeCategory.class)
                .addUserMessage(userMessage);

        for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
            ChatCompletion completion = client.chat().completions().create(paramsBuilder.build());
            ChatCompletionMessage message = completion.choices().getFirst().message();
            paramsBuilder.addMessage(message);

            List<ChatCompletionMessageToolCall> toolCalls = message.toolCalls().orElse(List.of());
            if (toolCalls.isEmpty()) {
                return message.content().orElse("I'm drawing a blank - ask me again!");
            }

            for (ChatCompletionMessageToolCall toolCall : toolCalls) {
                ChatCompletionMessageFunctionToolCall function = toolCall.function().orElseThrow();
                Object result = runTool(function);
                paramsBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                        .toolCallId(function.id())
                        .contentAsJson(result)
                        .build());
            }
        }
        return "Sorry, I got tangled up in my own punchline!";
    }

    private Object runTool(ChatCompletionMessageFunctionToolCall function) {
        if ("SuggestJokeCategory".equals(function.function().name())) {
            return function.function().arguments(SuggestJokeCategory.class).execute();
        }
        throw new IllegalArgumentException("Unknown tool requested: " + function.function().name());
    }
}
