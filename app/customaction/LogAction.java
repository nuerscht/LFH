package customaction;

import java.lang.annotation.*;
import play.mvc.With;

@With(LogActionImpl.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {
   String value() default "application";
   LogLevel logLevel() default LogLevel.TRACE;
}

