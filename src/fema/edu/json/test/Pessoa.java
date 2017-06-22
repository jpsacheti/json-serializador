package fema.edu.json.test;

import fema.edu.json.annotation.ElementValidator;
import fema.edu.json.annotation.JsonRoot;
@JsonRoot
public class Pessoa{
  
  @ElementValidator(validator=StringValidator.class)
  private String nome;
  private int idade;
  
  public Pessoa(){
    
  }

  public String getNome() {
    return nome;
  }
  
  public int getIdade() {
    return idade;
  }
  
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  public void setIdade(int idade) {
    this.idade = idade;
  }
}
