package fema.edu.json.test;

import fema.edu.json.engine.Engine;
import fema.edu.json.engine.JsonEngine;

public class Main {
  public static void main(String[] args) {
    Pessoa p = new Pessoa();
    p.setNome("joao pedro");
    p.setIdade(12);
    Engine e = new JsonEngine();
    String json = e.toJson(p);
    System.out.println(json);
  }
}
