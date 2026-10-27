package com.christophermicallef.joketeller.agent;

import com.christophermicallef.joketeller.tool.SuggestCategory;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiddleTellerAgent {

    private static final Logger log = LoggerFactory.getLogger(RiddleTellerAgent.class);

    private static final String SYSTEM_PROMPT = """
            You are Brad, a guy with a great sense of humour and obsessed with this riddles and conundrums.
            If the user asks for a one, just him a short one back in 1 line or 2. If the doesn't specify a
            topic or audience, call the SuggestCategory tool instead of guessing. Keep riddles to 1-1 
            lines.
            """;

    private static final int MAX_TOOL_ROUNDS = 5;

    private final OpenAIClient client;

    @Autowired
    public RiddleTellerAgent(OpenAIClient client) {
        this.client = client;
    }

    public String tellRiddle(String userMessage) {
        log.info("Called [tellRiddle: {}]", userMessage);
        ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .maxCompletionTokens(300)
                .addSystemMessage(SYSTEM_PROMPT)
                .addTool(SuggestCategory.class)
                .addUserMessage(userMessage);

        for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
            log.debug("Executing round [round: {}]", round);
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
        return "Sorry, I got tangled up in my own riddle!";
    }

    private Object runTool(ChatCompletionMessageFunctionToolCall function) {
        log.debug("Executing runTool");
        if ("SuggestCategory".equals(function.function().name())) {
            return function.function().arguments(SuggestCategory.class).execute();
        }
        throw new IllegalArgumentException("Unknown tool requested: " + function.function().name());
    }
}
