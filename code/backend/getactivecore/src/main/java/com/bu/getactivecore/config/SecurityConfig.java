package com.bu.getactivecore.config;

import com.bu.getactivecore.service.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

import static com.bu.getactivecore.shared.Constants.PASSWORD_ENCODER_STRENGTH;

/**
 * This class configures the security settings for the application, including stateless session management,
 * JWT-based authentication, and custom access controls. CSRF protection is disabled since the application
 * is stateless and uses JWT tokens for authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService m_userDetailsService;

    private final JwtFilter jwtFilter;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Constructs the SecurityConfig.
     *
     * @param userDetailsService        used to load user details for authentication
     * @param jwtFilter                 used to filter requests and validate JWT tokens
     * @param customAccessDeniedHandler used to wrap access denied exceptions in a custom response
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.m_userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;

    }

    /**
     * Configures the HTTP security settings for the application.
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return the {@link SecurityFilterChain} instance for the application
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().disable()) // Allow H2 Console to load in frame
                .exceptionHandling(accessDenied -> accessDenied
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(request -> request
                        
                        // Permit following endpoints without authentication
                        .requestMatchers("/h2-console/**", "/v1/register",
                                "/v1/health",
                                "/v1/login"
                                ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures the authentication provider with a custom user details service and password encoder.
     * This bean is used to authenticate users based on credentials stored in the system.
     *
     * @return the configured {@link AuthenticationProvider} instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH));
        provider.setUserDetailsService(m_userDetailsService);
        return provider;
    }

    /**
     * This bean is responsible for managing authentication processes in the application.
     *
     * @param config the {@link AuthenticationConfiguration} object
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
