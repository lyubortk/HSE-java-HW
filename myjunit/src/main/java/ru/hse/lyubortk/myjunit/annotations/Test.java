package ru.hse.lyubortk.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods with this annotation are regular test methods.
 * Those methods must not have any parameters and must not have any parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * Method is supposed to throw an instance of this exception (or any successor). If this
     * parameter is set to DoesNotThrow then the method is not supposed to throw exceptions.
     */
    Class<? extends Throwable> expected() default DoesNotThrow.class;

    /**
     * If this parameter is set to nonempty string then the method is ignored and this string is
     * return as a cause.
     */
    String ignore() default "";

    /**
     * This Throwable is used to distinguish methods
     * which are not supposed to throw an exception.
     */
    class DoesNotThrow extends Throwable {
    }
}
