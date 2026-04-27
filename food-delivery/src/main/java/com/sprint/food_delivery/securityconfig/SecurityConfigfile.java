package com.sprint.food_delivery.securityconfig;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
public class SecurityConfigfile {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Users with specific roles
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
                .roles("ADMIN")             // full access via rules below
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
                // PUBLIC
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // KISOL (Driver & Delivery Assignment)
                .requestMatchers("/drivers/**").hasAnyRole("KISOL", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/orders/*/assign-driver/*").hasAnyRole("KISOL", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/drivers/*/orders").hasAnyRole("KISOL", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/orders/*/delivery-status").hasAnyRole("KISOL", "ADMIN")

                // JEEVITHA (Coupons, Ratings, Order-Coupon)
                .requestMatchers("/coupons/**").hasAnyRole("JEEVITHA", "ADMIN")
                .requestMatchers("/ratings/**").hasAnyRole("JEEVITHA", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/orders/*/ratings").hasAnyRole("JEEVITHA", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/restaurants/*/ratings").hasAnyRole("JEEVITHA", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/orders/*").hasAnyRole("JEEVITHA", "ADMIN")    // fetch order for rating
                .requestMatchers("/orders/*/coupons/*").hasAnyRole("JEEVITHA", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/orders/*/coupons").hasAnyRole("JEEVITHA", "ADMIN")

                // THENMOZLI (Orders & Order Items)
                .requestMatchers("/orders/**", "/order-items/**").hasAnyRole("THENMOZLI", "ADMIN")

                // HEMANTH (Restaurant & Menu)
                .requestMatchers("/restaurants/**", "/menu-items/**").hasAnyRole("HEMANTH", "ADMIN")

                // ANNIE (Customer & Address)
                .requestMatchers("/customers/**", "/addresses/**").hasAnyRole("ANNIE", "ADMIN")

                
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