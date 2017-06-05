package fema.edu.json.engine;

import java.io.NotSerializableException;
import java.io.Writer;

/**
 * Created by joao on 04/06/17.
 */
public interface Engine {
    <E> String toJson(E object) throws NotSerializableException;

    <E> Writer toJsonStream(E object) throws NotSerializableException;
}
