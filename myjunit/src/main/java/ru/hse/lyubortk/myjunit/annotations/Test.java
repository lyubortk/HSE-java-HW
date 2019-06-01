package ru.hse.lyubortk.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    class DoesNotThrow extends Throwable {
    }

    Class<? extends Throwable> expected() default DoesNotThrow.class;
    String ignore() default "";
}
