package fema.edu.json.test;

import fema.edu.json.converter.JsonConverter;

public class StringConverter implements JsonConverter<String> {

  @Override
  public String convert(String valor) {
    return "Pedro paulo e Alex";
  }

}
