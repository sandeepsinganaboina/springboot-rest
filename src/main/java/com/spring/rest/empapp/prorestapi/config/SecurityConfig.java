package com.spring.rest.empapp.prorestapi.config;

import com.spring.rest.empapp.prorestapi.repo.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.util.Optional;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/resources/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.contentSecurityPolicy(
                        csp -> csp.policyDirectives("frame-ancestors 'self'")
                ).disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepo userRepo) {
        return username -> {
            // Hardcoded test users
            if ("testuser".equals(username)) {
                return User.builder()
                        .username("testuser")
                        .password(passwordEncoder().encode("testpassword"))
                        .roles("USER")
                        .build();
            }
            if ("admin".equals(username)) {
                return User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("adminpassword"))
                        .roles("ADMIN")
                        .build();
            }

            // Lookup user from the database
            Optional<com.spring.rest.empapp.prorestapi.model.User> userEntityOptional = userRepo.findByUsername(username);
            if (userEntityOptional.isPresent()) {
                com.spring.rest.empapp.prorestapi.model.User userEntity = userEntityOptional.get();
                return User.builder()
                        .username(userEntity.getUsername())
                        .password(userEntity.getPassword()) // Password should already be encoded
                        .roles(userEntity.getRole()) // Assuming role is stored properly
                        .build();
            }

            throw new UsernameNotFoundException("User not found: " + username);
        };
    }
}
