package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequest(@NotBlank @Size(max = 100) String username, @NotBlank @Size(max = 200) String password) { }
