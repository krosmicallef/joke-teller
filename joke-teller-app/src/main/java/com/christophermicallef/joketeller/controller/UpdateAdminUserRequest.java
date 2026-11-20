package com.christophermicallef.joketeller.controller;

import jakarta.validation.constraints.Size;

public record UpdateAdminUserRequest(@Size(min = 3, max = 100) String username,
                                     @Size(min = 12, max = 200) String password) { }
