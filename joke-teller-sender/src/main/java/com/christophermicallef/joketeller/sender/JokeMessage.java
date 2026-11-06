package com.christophermicallef.joketeller.sender;

/** Message published for a subscriber by the joke sender. */
public record JokeMessage(String emailAddress, String text) {
}
