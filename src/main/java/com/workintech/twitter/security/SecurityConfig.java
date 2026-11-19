package com.workintech.twitter.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JsonAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JsonAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS'u Security filtre zincirinin EN BASINDA ele al (asagidaki corsConfigurationSource bean'ini kullanir).
                // Neden burada? Tarayicinin preflight (OPTIONS) istegi token TASIMAZ. CORS'u sadece
                // Spring MVC'de (WebMvcConfigurer) tanimlasaydik, preflight MVC'ye ulasmadan
                // Security filtresine takilip 401 alirdi ve CORS hatasi devam ederdi.
                .cors(Customizer.withDefaults())

                // CSRF, tarayicinin cookie'yi OTOMATIK gonderdigi session sistemlerinde gerekir.
                // Biz token'i Authorization header'inda ELLE gonderiyoruz -> CSRF riski yok.
                .csrf(AbstractHttpConfigurer::disable)

                // Sunucu oturum (session) tutmaz; her istek kendi token'iyla gelir.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/error").permitAll()
                        .anyRequest().authenticated())

                // "Authorization: Bearer <token>" header'ini okuyup JwtDecoder ile dogrulayan filtre.
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint))

                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

    // Hangi baska origin'ler bu API'yi tarayicidan cagirabilir?
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // "*" DEGIL: sadece kendi React uygulamamiza izin veriyoruz.
        config.setAllowedOrigins(List.of("http://localhost:3200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Content-Type: JSON govde icin; Authorization: Bearer token icin.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Tarayici preflight cevabini 1 saat cache'lesin; her istekte tekrar OPTIONS atmasin.
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
