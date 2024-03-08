package edu.cbsystematics.com.libraryprojectcbs.aop;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {


    ActionType value();

    //If this attribute is true, then logger will log the method parameter values
    boolean isLogArgs() default true;

    // If this attribute is true, then logger will log the method execution time
    boolean isLogExecutionTime() default true;

}