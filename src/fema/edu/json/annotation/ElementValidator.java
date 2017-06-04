package fema.edu.json.annotation;

import fema.edu.json.validation.JsonValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementValidator {
    Class<? extends JsonValidator> validator();
}
