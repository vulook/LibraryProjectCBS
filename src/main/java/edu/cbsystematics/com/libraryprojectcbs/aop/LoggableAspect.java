package edu.cbsystematics.com.libraryprojectcbs.aop;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.service.CustomLoggableAction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
public class LoggableAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

    private final CustomLoggableAction customLoggableAction;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public LoggableAspect(CustomLoggableAction customLoggableAction, UserAuthenticationUtils userAuthenticationUtils) {
        this.customLoggableAction = customLoggableAction;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    @Around(value = "@annotation(edu.cbsystematics.com.libraryprojectcbs.aop.Loggable)")
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

        // Retrieves the current user's email and role using an authentication object
        String email = userAuthenticationUtils.getCurrentUsername(authentication);
        String role = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Log the action
        customLoggableAction.logLoggableAction(email, role, actionType, methodSignature, methodArguments, executionTime);

        return proceed;
    }

}
