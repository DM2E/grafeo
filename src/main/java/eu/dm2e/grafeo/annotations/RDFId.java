package eu.dm2e.grafeo.annotations;

import java.lang.annotation.*;

/**
 * This field represents the URL of the thing.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RDFId {
    String prefix() default "";


}
