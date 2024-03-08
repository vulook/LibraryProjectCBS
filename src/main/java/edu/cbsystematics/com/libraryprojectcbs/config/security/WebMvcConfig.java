package edu.cbsystematics.com.libraryprojectcbs.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("logging/login");
        registry.addViewController("/registration").setViewName("logging/registration");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, LocalDate.class,
                source -> Optional.of(source)
                        .filter(s -> !s.isEmpty())
                        .map(s -> LocalDate.parse(s, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .orElse(null));
    }


}