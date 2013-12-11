package customactions;


import java.lang.annotation.*;

import play.mvc.With;

@With(TokenAuthenticatorImpl.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAuthenticator {
    String value() default "application";

    LogLevel logLevel() default LogLevel.TRACE;
}