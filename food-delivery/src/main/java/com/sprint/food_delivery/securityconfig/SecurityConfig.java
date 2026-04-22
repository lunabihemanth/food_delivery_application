// package com.sprint.food_delivery.securityconfig;


// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     // 🔐 Password Encoder
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // 👤 Users
//     @Bean
//     public UserDetailsService userDetailsService() {

//         UserDetails admin = User.withUsername("admin")
//                 .password(passwordEncoder().encode("admin123"))
//                 .roles("ADMIN")
//                 .build();

//         UserDetails hemanth = User.withUsername("hemanth")
//                 .password(passwordEncoder().encode("pass1"))
//                 .roles("HEMANTH")
//                 .build();

//         UserDetails kisol = User.withUsername("kisol")
//                 .password(passwordEncoder().encode("pass2"))
//                 .roles("KISOL")
//                 .build();

//         UserDetails thenmozhi = User.withUsername("thenmozhi")
//                 .password(passwordEncoder().encode("pass3"))
//                 .roles("THENMOZHI")
//                 .build();

//         UserDetails annie = User.withUsername("annie")
//                 .password(passwordEncoder().encode("pass4"))
//                 .roles("ANNIE")
//                 .build();

//         UserDetails jeevitha = User.withUsername("jeevitha")
//                 .password(passwordEncoder().encode("pass5"))
//                 .roles("JEEVITHA")
//                 .build();

//         return new InMemoryUserDetailsManager(
//                 admin, hemanth, kisol, thenmozhi, annie, jeevitha
//         );
//     }

//     @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//     http
//         .csrf(csrf -> csrf.disable())
//         .authorizeHttpRequests(auth -> auth
//             .anyRequest().permitAll()   // 🔥 allow everything
//         );

//     return http.build();
// }
// }
