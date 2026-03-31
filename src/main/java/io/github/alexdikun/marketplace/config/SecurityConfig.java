package io.github.alexdikun.marketplace.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/v1/adverts/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/adverts/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()

                .requestMatchers("/api/v1/users/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/roles").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/auth").authenticated()

                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
            
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.get("roles") == null) {
                return List.of();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(role -> "ROLE_" + role)   
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }

}
    
