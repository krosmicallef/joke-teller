package com.christophermicallef.joketeller.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A "tool" the agent can call when the user asks for a joke but doesn't say
 * what kind. The model decides on its own whether and when to invoke this -
 * that decision-making is what makes JokeTellerAgent an agent rather than a
 * plain prompt-in/text-out wrapper.
 *
 * The SDK derives the function name from the class name ("SuggestCategory")
 * and its JSON schema from the public fields below.
 */
@JsonClassDescription(
        "Suggests a joke category appropriate for the requested audience. "
                + "Call this when the user asks for a joke without specifying a topic.")
public class SuggestCategory {

    @JsonPropertyDescription("Who the joke is for: \"kids\", \"adults\", or \"general\".")
    public String audience;

    public String execute() {
        String safeAudience = audience == null ? "general" : audience.toLowerCase();
        switch (safeAudience) {
            case "kids":
                return pick("animals", "school", "knock-knock jokes");
            case "adults":
                return pick("work", "marriage", "getting older");
            default:
                return pick("puns", "food", "everyday life", "technology");
        }
    }

    private String pick(String... options) {
        return options[ThreadLocalRandom.current().nextInt(options.length)];
    }
}

