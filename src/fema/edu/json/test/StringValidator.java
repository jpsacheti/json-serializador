package fema.edu.json.test;

import fema.edu.json.validation.JsonValidator;

public class StringValidator implements JsonValidator<String> {

  @Override
  public boolean validate(String object) {
    return true;
  }

}
