package com.tfm.edcuaweb.config;

import com.tfm.edcuaweb.repository.UserRepository;
import com.tfm.edcuaweb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Lazy
	private final JwtAuthenticationFilter jwtAuthenticationFilter; //Seguridad minima para el registro de usuarios
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) //desactiva la CSRF para pruebas
                .cors(withDefaults())
                .headers(headers->headers.contentSecurityPolicy(csp->csp
                        .policyDirectives("frame-ancestors 'self'  http://localhost:4200")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register-teacher",
                                "/auth/register-student",
                                "/auth/logout",
                                "/auth/refresh",
                                "/auth/password/forgot",
                                "/auth/password/reset",
                                "/auth/password/change"
                        ).permitAll() // Deja pasar a / y /auth  /login, /register, /refresh
                        .requestMatchers("/ws/**").permitAll()
                        // NOTIFICATIONS REST (solo GET requiere token)
                        .requestMatchers(HttpMethod.GET, "/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/auth/**").authenticated()
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/documents/**").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/students/**").hasRole("STUDENT")
                        .requestMatchers("/documents/download/**").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/user/me").authenticated()
                        .anyRequest().authenticated() //Para el resto pide autenticacion
                )
                .sessionManagement(
                        // STATELLES  asegura que el back no cree sessiones.authorizeHttpRequests(auth -> auth
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                //addFilterBefore mantiene el filtro -JWT funcionando ok
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
//FUNCIONES
    // 🔹 Servicio que carga usuarios por username
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
