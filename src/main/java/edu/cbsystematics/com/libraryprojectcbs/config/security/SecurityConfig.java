package edu.cbsystematics.com.libraryprojectcbs.config.security;

import edu.cbsystematics.com.libraryprojectcbs.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private final AccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomLogoutSuccessHandler customLogoutSuccessHandler, AccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Configure password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure authentication provider bean
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        // Output a message when the user is authenticated
        authProvider.setPostAuthenticationChecks((userDetails) -> {
            System.out.print("\033[1;32m");
            System.out.println("User " + userDetails.getUsername() + " logged in with role: " + userDetails.getAuthorities());
            System.out.print("\033[0m");
        });
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules for different URL patterns
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/registration",
                                "/forgot-password",
                                "/reset-password",
                                "/verify").permitAll()
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/webjars/**").permitAll()
                        .requestMatchers("/library/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // Configure form login
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureUrl("/login?error")
                )

                // Configure logout
                .logout((logout) -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // Configure access denied handler
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                );

        // Set authentication provider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }


}
