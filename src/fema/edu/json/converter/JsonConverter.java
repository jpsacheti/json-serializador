package fema.edu.json.converter;

/**
 * Created by joao on 04/06/17.
 */
public interface JsonConverter<V> {
    String convert(V valor);
}
