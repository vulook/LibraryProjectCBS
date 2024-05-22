package edu.cbsystematics.com.libraryprojectcbs.config.security;

import edu.cbsystematics.com.libraryprojectcbs.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final AccessDeniedHandler accessDeniedHandler;

    private final SpringResourceTemplateResolver templateResolver;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomLogoutSuccessHandler customLogoutSuccessHandler, CustomAuthenticationFailureHandler customAuthenticationFailureHandler, AccessDeniedHandler accessDeniedHandler, SpringResourceTemplateResolver templateResolver, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.templateResolver = templateResolver;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Configure password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Configure authentication provider bean
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        // Output a message when the user is authenticated
        authProvider.setPostAuthenticationChecks(userDetails -> {
            System.out.print("\033[1;32m");
            System.out.println("User " + userDetails.getUsername() + " logged in with role: " + userDetails.getAuthorities());
            System.out.print("\033[0m");
        });
        return authProvider;
    }


    // Configure authentication manager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure Cross-Site Request Forgery (CSRF) protection
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/library",
                                "/access-denied",
                                "/404",
                                "/registration",
                                "/verify",
                                "/forgot-password",
                                "/reset-password").permitAll()
                        .requestMatchers(
                                "/library/anonymous/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/webjars/**").permitAll()

                        .requestMatchers("/library/admin/home/**").hasAuthority(ROLE_ADMIN)
                        .requestMatchers("/library/librarian/home/**").hasAuthority(ROLE_LIBRARIAN)
                        .requestMatchers(
                                "/library/reader/home/**",
                                "/library/cards/**",
                                "/library/books/**",
                                "/library/authors/**").hasAnyAuthority(ROLE_READER, ROLE_LIBRARIAN)
                        .requestMatchers("/library/worker/home/**").hasAuthority(ROLE_WORKER)
                        .anyRequest().authenticated()
                )

                // Configure authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure form login
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .failureHandler(customAuthenticationFailureHandler)
                        //.failureUrl("/login?error")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                )

                // Configure logout
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        //.logoutSuccessUrl("/login?logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .permitAll()
                )

                // Configure handling of access denied exceptions
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }


    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }


}
