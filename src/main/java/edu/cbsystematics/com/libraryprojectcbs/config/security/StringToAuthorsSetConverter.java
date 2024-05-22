package edu.cbsystematics.com.libraryprojectcbs.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class StringToAuthorsSetConverter implements Converter<String, Set<String>>, Formatter<Object> {

    @Override
    public Set<String> convert(String source) {
        return new HashSet<>(List.of(source));
    }

    @Override
    public String print(Object object, Locale locale) {
        return object.toString();
    }

    @Override
    public Object parse(String text, Locale locale) throws ParseException {
        return convert(text);
    }

}