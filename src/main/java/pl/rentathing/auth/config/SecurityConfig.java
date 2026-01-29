package pl.rentathing.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for setting up security measures for the application.
 * This class customizes various aspects of application security, including
 * request authorization, login/logout mechanisms, session management, CSRF protection,
 * and frame options for embedded resources.
 *
 * This class uses annotations such as:
 * - {@code @Configuration} to indicate that it is a Spring configuration class.
 * - {@code @EnableWebSecurity} to enable Web Security features in the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application, defining authorization rules,
     * login behavior, session management, and other security measures.
     *
     * This method sets up:
     * - Authorization rules for different URL patterns, including distinguishing between static resources,
     *   public pages, and secured areas for authenticated users or specific roles (e.g., ADMIN and USER).
     * - Login and logout configurations with custom URLs and behavior.
     * - Session management constraints, such as limiting concurrent sessions and session expiration handling.
     * - CSRF exclusions for certain endpoints.
     * - Frame options configuration to allow embedding specific resources (e.g., H2 console).
     *
     * @param http the {@link HttpSecurity} object that provides methods to configure web-based security
     *             for specific HTTP requests
     * @return a {@link SecurityFilterChain} that applies the defined security configurations
     * @throws Exception if an error occurs while configuring the {@link HttpSecurity} object
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(auth -> auth

                        // static resources
                        .requestMatchers
                                ("/css/**", "/js/**", "/images/**", "/libs/**" ).permitAll()

                        // Auth
                        .requestMatchers("/logowanie", "/rejestracja", "/register", "/logout").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // Api
                        .requestMatchers("/api/rentals/*/availability").permitAll()

                        // Unauntenticated
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/katalog/**").permitAll()
                        .requestMatchers("/przedmiot/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").permitAll()

                        // Authenticated
                        .requestMatchers("/wypozyczenia/**").hasRole("USER")

                        // Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // H2-CONSOLE
                        .requestMatchers("/h2-console/**").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/logowanie")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/logowanie?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .expiredUrl("/logowanie?expired=true"))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));


        return http.build();
    }

    /**
     * Defines a Bean for the PasswordEncoder used by the application.
     *
     * This method creates and returns an instance of {@link BCryptPasswordEncoder},
     * which is used to securely encode passwords. BCrypt provides a reliable
     * hashing algorithm that automatically handles salting and ensures robust
     * security for stored password hashes.
     *
     * @return a {@link PasswordEncoder} instance of {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
