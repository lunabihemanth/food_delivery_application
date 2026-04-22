package com.sprint.food_delivery.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfigfile {

    // 🔐 Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 👤 USERS
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {

        UserDetails hemanth = User.withUsername("hemanth")
                .password(encoder.encode("hemanth123"))
                .roles("HEMANTH")
                .build();

        UserDetails kisol = User.withUsername("kisol")
                .password(encoder.encode("kisol123"))
                .roles("KISOL")
                .build();

        UserDetails thenmozli = User.withUsername("thenmozli")
                .password(encoder.encode("thenmozli123"))
                .roles("THENMOZLI")
                .build();

        UserDetails annie = User.withUsername("annie")
                .password(encoder.encode("annie123"))
                .roles("ANNIE")
                .build();

        UserDetails jeevitha = User.withUsername("jeevitha")
                .password(encoder.encode("jeevitha123"))
                .roles("JEEVITHA")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                hemanth, kisol, thenmozli, annie, jeevitha, admin
        );
    }

    // 🛡️ SECURITY CONFIG
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 🌐 CORS (Angular)
            .cors(Customizer.withDefaults())

            // ❌ Disable CSRF for REST
            .csrf(csrf -> csrf.disable())

            // ❌ Stateless (no session)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔐 Authorization Rules
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/customers/**", "/addresses/**")
                    .hasAnyRole("HEMANTH", "ADMIN")

                .requestMatchers("/restaurants/**", "/menu-items/**")
                    .hasAnyRole("HEMANTH", "ANNIE", "ADMIN")

                .requestMatchers("/orders/**", "/order-items/**")
                    .hasAnyRole("KISOL", "THENMOZLI", "ADMIN")

                .requestMatchers("/drivers/**")
                    .hasAnyRole("KISOL", "JEEVITHA", "ADMIN")

                .requestMatchers("/coupons/**")
                    .hasRole("JEEVITHA")

                .requestMatchers("/ratings/**")
                    .hasRole("ANNIE")

                // ✅ Swagger allowed
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                .anyRequest().authenticated()
            )

            // 🔥 BASIC AUTH (Swagger friendly)
            .httpBasic(Customizer.withDefaults())

            // ⚡ Force login popup when unauthorized
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, exx) -> {
                    res.setStatus(401);
                    res.addHeader("WWW-Authenticate", "Basic realm=\"FoodDelivery\"");
                })
            );

        return http.build();
    }
}