package edu.cbsystematics.com.libraryprojectcbs.config.aspect;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;


@Aspect
@Component
public class LoggableAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

    private final LogsService logsService;

    private final UserService userService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public LoggableAspect(@Lazy LogsService logsService, @Lazy UserService userService, @Lazy UserAuthenticationUtils userAuthenticationUtils) {
        this.logsService = logsService;
        this.userService = userService;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    @Around(value = "@annotation(edu.cbsystematics.com.libraryprojectcbs.config.aspect.Loggable)")
    public Object autoLoggableHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method;
        Object[] methodArguments;
        long start;
        long executionTime;

        // Authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the signature of the method
        Signature methodSignature = joinPoint.getSignature();

        // Extract the actual method from the signature
        method = ((MethodSignature) methodSignature).getMethod();

        // Get the @Loggable annotation
        Loggable loggableAnnotation = method.getAnnotation(Loggable.class);

        // ActionType specified in the annotation
        ActionType actionType = loggableAnnotation.value();

        // Start measuring the execution time of the method
        start = System.currentTimeMillis();
        // Get the arguments passed to the method
        methodArguments = joinPoint.getArgs();

        // Proceed with the execution of the method
        Object proceed = joinPoint.proceed();

        // Log method execution exit
        if (loggableAnnotation.isLogArgs()) {
            logger.info("Loggable: Exiting from method [{}] with parameters [{}]", methodSignature.getName(), methodArguments);
        } else {
            logger.info("Loggable: Exiting from method [{}]", methodSignature.getName());
        }

        // Calculate method execution time
        executionTime = System.currentTimeMillis() - start;
        if (loggableAnnotation.isLogExecutionTime()) {
            logger.info("Loggable: Execution Time [{}] ms", executionTime);
        }

        // Log the action
        logAction(authentication, actionType, methodSignature, methodArguments, executionTime);

        return proceed;
    }

    private void logAction(Authentication authentication, ActionType actionType, Signature methodSignature, Object[] methodArguments, long executionTime) {
        // Get the current user's Email from the authentication object
        String email = userAuthenticationUtils.getCurrentUserUsername(authentication);

        // Find the user by email
        User userCreator = userService.findByEmail(email);

        // Set fullName to "ANONYMOUS" if userCreator is null
        String fullName = (userCreator != null) ? userCreator.getFirstName() + " " + userCreator.getLastName() : "ANONYMOUS";

        // Get the current user's Role from the authentication object
        String role = userAuthenticationUtils.getCurrentUserRole(authentication);

        // Save the log
        logsService.saveLog(fullName, role, actionType, methodSignature.getName(), Arrays.toString(methodArguments), executionTime, userCreator);
    }


}
