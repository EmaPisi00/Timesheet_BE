package it.project.timesheet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.requestMatchers("/swagger-ui/**", "/api/v1/**")
                        .permitAll()
                        .requestMatchers("/v3/api-docs*/**")
                        .permitAll());

        return http.build();
    }
}
