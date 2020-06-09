package fr.stemprado.apps.mocktar.beans;

import org.springframework.data.annotation.Id;

public class Mock {

    @Id
    public Long id;
  
    public String name;
    public String request;
    public String response;
  
    public Mock() {}
  
    public Mock(String name, String request, String response) {
      this.name = name;
      this.request = request;
      this.response = response;
    }
  
    @Override
    public String toString() {
      return String.format(
          "Mock[id=%d, name='%s' request='%s', response='%s']", id, name, request, response);
    }    
}