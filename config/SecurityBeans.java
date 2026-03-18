package com.matchme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// BCrypt PasswordEncoder bean
// This gives us a PasswordEncoder we can inject into the auth service.

@Configuration
public class SecurityBeans
{
	@Bean
	public PasswordEncoder passwordEncoder()
	{
        return new BCryptPasswordEncoder();
    }
}
