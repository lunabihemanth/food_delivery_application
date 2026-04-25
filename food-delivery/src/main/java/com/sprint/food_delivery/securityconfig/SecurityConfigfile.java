package com.sprint.food_delivery.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfigfile {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

        return new InMemoryUserDetailsManager(hemanth, kisol, thenmozli, annie, jeevitha, admin);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Annie – Customer & Address APIs
                .requestMatchers("/customers/**", "/addresses/**")
                    .hasAnyRole("ANNIE", "ADMIN")
                // Hemanth – Restaurant & Menu APIs
                .requestMatchers("/restaurants/**", "/menu-items/**")
                    .hasAnyRole("HEMANTH", "ADMIN")
                // Thenmozhi (Orders) & Kisol (Delivery Assignments) share /orders/**
                .requestMatchers("/orders/**", "/order-items/**")
                    .hasAnyRole("THENMOZLI", "KISOL", "ADMIN")
                // Kisol – Driver APIs
                .requestMatchers("/drivers/**")
                    .hasAnyRole("KISOL", "ADMIN")
                // Jeevitha – Coupons & Ratings APIs
                .requestMatchers("/coupons/**", "/ratings/**")
                    .hasAnyRole("JEEVITHA", "ADMIN")
                // Swagger public
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, exx) -> {
                    res.setStatus(401);
                    res.addHeader("WWW-Authenticate", "Basic realm=\"FoodDelivery\"");
                })
            );

        return http.build();
    }
}