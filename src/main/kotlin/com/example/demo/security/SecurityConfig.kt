package com.example.demo.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component

@Component
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/")
            .securityContext { securityContext -> securityContext.requireExplicitSave(false) }
            .csrf { it.disable() }
            .cors { it.disable() }
            .addFilterAfter(FirebaseAuthenticationFilter(), BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/login", "/users").permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}
