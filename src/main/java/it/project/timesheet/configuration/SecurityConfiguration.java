package it.project.timesheet.configuration;

import it.project.timesheet.service.auth.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailService userDetailService;

    // IN QUESTO MODO RENDO LO SWAGGER PUBBLICO E ACCESSIBILE DA TUTTI
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs*/**").permitAll();
                    auth.requestMatchers("/api/v1/user/findAll", "/api/v1/user/login").permitAll();
                    auth.requestMatchers("/api/v1/user/register").hasRole("ADMIN");

                    // 👉 Tutte le altre API necessitano autenticazione
                    auth.requestMatchers("/api/v1/user/**").authenticated();  // Tutte le richieste sotto /user sono protette
                    auth.anyRequest().permitAll();
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // 🔥 Aggiunge il filtro JWT
                .build();
    }

    /**
     * UTILE SE SI VUOLE RENDERE LO SWAGGER PROTETTO DA UN LOGIN,
     * CON IL METODO QUI SOTTO USIAMO UN METODO DI DEFAULT MESSO A DISPOSIZIONE DI SPRING CHE CI FARA' LOGGARE CON LE
     * CREDENZIALI DI UN UTENTE NEL DB.
     *
     * @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     * return http
     * .cors(cors -> cors.configurationSource(corsConfigurationSource()))
     * .csrf(AbstractHttpConfigurer::disable)
     * .authorizeHttpRequests(auth -> {
     * auth.requestMatchers("/swagger-ui/**", "/v3/api-docs").authenticated(); auth.anyRequest().permitAll();
     * })
     * .formLogin(Customizer.withDefaults())
     * .build();
     * }
     */

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
