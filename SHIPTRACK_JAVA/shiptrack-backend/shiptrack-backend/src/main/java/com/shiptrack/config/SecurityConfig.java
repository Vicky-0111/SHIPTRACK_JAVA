package com.shiptrack.config;

import com.shiptrack.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder googleJwtDecoder(
            @Value("${google.issuer}") String issuer,
            @Value("${google.jwks-uri}") String jwkSetUri,
            @Value("${google.client-id}") String clientId) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withJwkSetUri(jwkSetUri)
                        .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                new JwtClaimValidator<>(
                        JwtClaimNames.ISS,
                        iss ->
                                "https://accounts.google.com".equals(iss)
                                        || "accounts.google.com".equals(iss)
                );

        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>(
                        JwtClaimNames.AUD,
                        aud -> aud != null && aud.contains(clientId)
                );

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        JwtValidators.createDefault(),
                        issuerValidator,
                        audienceValidator
                )
        );

        return decoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Local React development
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:5174");
        configuration.addAllowedOrigin("http://127.0.0.1:5173");
        configuration.addAllowedOrigin("http://127.0.0.1:5174");

        // Allow HTTP methods
        configuration.addAllowedMethod("*");

        // Allow HTTP headers
        configuration.addAllowedHeader("*");

        // Allow credentials such as JWT cookies/auth headers
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}