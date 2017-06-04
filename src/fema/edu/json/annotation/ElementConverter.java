package fema.edu.json.annotation;

import fema.edu.json.converter.JsonConverter;

/**
 * Created by joao on 04/06/17.
 */
public @interface ElementConverter {
    Class<? extends JsonConverter> converter();
}
