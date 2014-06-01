package zz.yy.xx.webservices.aspects;

import org.apache.commons.logging.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
public class LoggingAspects {

    private Log logger;

    public void setLogger(Log logger) {
        this.logger = logger;
    }

    @Before("execution(* zz.yy.xx..*set*(..))")
    public void loggingControllerSetters(JoinPoint joinPoint) {
        String classMethod = this.getClassMethod(joinPoint);
        this.debugLevelLogging(joinPoint, classMethod);
    }


    @AfterReturning(value = "execution(* zz.yy.xx..*.*(..)) && !execution(* zz.yy.xx..*.set*(..))", returning = "returnValue")
    public void loggingControllerMethods(JoinPoint joinPoint, Object returnValue) {
        String classMethod = this.getClassMethod(joinPoint);

        if (returnValue != null) {
            String stringReturnValue = returnValue.toString();
            this.infoLevelLogging(joinPoint, classMethod, stringReturnValue);
        } else {
            this.infoLevelLogging(joinPoint, classMethod);
        }
    }


    //Generic Exceptions
    @AfterThrowing(value = "execution(* zz.yy.xx..*(..)) throws Exception", throwing = "exception")
    public void loggingGenericException(JoinPoint joinPoint, Exception exception) {
        String classMethod = this.getClassMethod(joinPoint);

        String stackTrace = "";
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace += element.toString() + "\n";
        }

        String exceptionMessageAndStackTrace = exception.getMessage() + "\n" + stackTrace;


        this.errorLevelLogging(joinPoint, classMethod, exceptionMessageAndStackTrace);

    }


    private String getClassMethod(JoinPoint joinPoint) {
        String classMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "(";

        try {
            classMethod += joinPoint.getSourceLocation().getLine();
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // /*Oh dear, we're in trouble now!
            classMethod += "no line number available";
        }

        classMethod += ")";

        return classMethod;
    }


    private void errorLevelLogging(JoinPoint joinPoint, String classMethod, String exceptionMessageAndStackTrace) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isErrorEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.error(argumentsString + exceptionMessageAndStackTrace);
            } else {
                logger.error(classMethod + "\n" + exceptionMessageAndStackTrace);
            }
        }
    }


    private void errorLevelLogging(JoinPoint joinPoint, String classMethod) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isErrorEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.error(argumentsString);
            } else {
                logger.error(classMethod + "\n");
            }
        }
    }


    private void infoLevelLogging(JoinPoint joinPoint, String classMethod) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isInfoEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.info(argumentsString);
            } else {
                logger.info(classMethod + "\n");
            }
        }
    }


    private void infoLevelLogging(JoinPoint joinPoint, String classMethod, String returnValue) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isInfoEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.info(argumentsString);

            } else {
                logger.info(classMethod + "\n");
            }
            if (returnValue.length() > 0) {
                logger.info("return value = " + returnValue);
            }
        }
    }


    private void debugLevelLogging(JoinPoint joinPoint, String classMethod, String exceptionMessageAndStackTrace) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isDebugEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.debug(argumentsString + exceptionMessageAndStackTrace);
            } else {
                logger.debug(classMethod + "\n" + exceptionMessageAndStackTrace);
            }
        }
    }


    private void debugLevelLogging(JoinPoint joinPoint, String classMethod) {
        int argLength = joinPoint.getArgs().length;

        if (logger.isDebugEnabled()) {
            if (argLength > 0) {
                String argumentsString = classMethod + outputArgs(joinPoint);
                logger.debug(argumentsString);
            } else {
                logger.debug(classMethod + "\n");
            }
        }
    }


    private String outputArgs(JoinPoint joinPoint) {
        String argumentsString = "\n" + "args[";
        int counter = 0;

        for (Object args : joinPoint.getArgs()) {
            try {
                argumentsString += "(arg" + counter + " = " + args.getClass().getName() + ") " + args.toString() + ", ";
            } catch (NullPointerException nullPointerException) {
                //possibility that calling getClass and/or getString will throw a nullPointer
                argumentsString += "(arg" + counter + " = null) null, ";
            }

            counter++;
        }

        //this just trims off last 2 characters to remove unwanted comma and space at end of argumentsString
        if (argumentsString.length() > 5) {
            argumentsString = argumentsString.substring(0, (argumentsString.length() - 2));
        }

        argumentsString += "]\n";

        return argumentsString;
    }


}
