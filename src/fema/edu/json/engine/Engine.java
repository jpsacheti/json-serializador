package fema.edu.json.engine;

import java.io.Writer;

/**
 * Created by joao on 04/06/17.
 */
public interface Engine {
    <E> String toJson(E object);

    <E> Writer toJsonStream(E object);
}
