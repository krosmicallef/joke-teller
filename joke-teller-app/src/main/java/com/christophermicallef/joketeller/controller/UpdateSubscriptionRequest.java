package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSubscriptionRequest(
        @NotBlank @Email String email,
        Boolean verified
) {
}
