package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SubscribeRequest(
        @NotBlank @Email String email
) {
}
