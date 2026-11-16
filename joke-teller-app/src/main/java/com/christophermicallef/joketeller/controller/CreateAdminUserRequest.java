package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminUserRequest(@NotBlank @Size(min = 3, max = 100) String username,
                                     @NotBlank @Size(min = 12, max = 200) String password) { }
