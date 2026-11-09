package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifySubscriberRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}") String code
) { }
