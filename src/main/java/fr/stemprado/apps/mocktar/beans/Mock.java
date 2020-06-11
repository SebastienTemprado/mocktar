package fr.stemprado.apps.mocktar.beans;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Mock {

    @Transient
    public static final String SEQUENCE_NAME = "mocks_sequence";

    @Id
    public Long id;
  
    public String name;
    public String request;
    public String response;
  
    public Mock() {}
  
    public Mock(long id, String name, String request, String response) {
      this.id = id;
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