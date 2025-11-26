package app.aspect;

import app.user.model.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

// This class is Aspect
@Aspect
@Component
public class MyCustomAspect {

    // This method is Advice
    // This expression @Before... -> pointcut expression
    @Before("bean(indexController))")
    public void logMessage(JoinPoint method) {

        System.out.println();
        System.out.println("Hello world!");
    }

    //    @Before("within(app.web.UserController))")
    @Before("@annotation(app.aspect.VeryImportant)")
    public void logMessage1(JoinPoint method) {

        System.out.println();
        System.out.println("Hello user service!");
    }

    @AfterReturning(value = "execution(* getById(..))", returning = "user")
    public void checkUserDetails(User user) {

        System.out.println("Hello user service!");
//        user.setUsername("TEST_1234");
    }

    @AfterThrowing(value = "execution(* getById(..))", throwing = "exception")
    public void checkUserDetailsIfExceptionIsThrown(RuntimeException exception) {

        System.out.println(exception.getMessage());
    }
//
//    @Around("execution(* getById(..))")
//    public Object exampleAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//
//        System.out.println("before method invocation");
//        Object result = proceedingJoinPoint.proceed();
//        System.out.println("after method invocation");
//
//        return result;
//    }
}