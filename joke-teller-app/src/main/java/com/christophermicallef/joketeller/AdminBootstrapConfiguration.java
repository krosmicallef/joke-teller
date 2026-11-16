package com.christophermicallef.joketeller;

import com.christophermicallef.joketeller.services.service.AdminUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBootstrapConfiguration {
    @Bean
    CommandLineRunner bootstrapAdmin(AdminUserService users,
                                     @Value("${admin.bootstrap.username:}") String username,
                                     @Value("${admin.bootstrap.password:}") String password) {
        return args -> users.createBootstrapUser(username, password);
    }
}
