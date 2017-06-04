package fema.edu.json.validation;

/**
 * Created by joao on 04/06/17.
 */
@FunctionalInterface
public interface JsonValidator<E> {
    public boolean validate(E object);
}
