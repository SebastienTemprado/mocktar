package fr.stemprado.apps.mocktar.beans;

import java.util.ArrayList;
import java.util.List;

import fr.stemprado.apps.mocktar.beans.QueryParam;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Mock {

    @Transient
    public static final String SEQUENCE_NAME = "mocks_sequence";

    @Id
    public Long id;
  
    public String name;
    public String request;
    public List<QueryParam> queryParams = new ArrayList<>();
    public String response;
  
    public Mock() {}
  
    public Mock(long id, String name, String request, List<QueryParam> queryParams, String response) {
      this.id = id;
      this.name = name;
      this.request = request;
      this.queryParams = queryParams;
      this.response = response;
    }
  
    @Override
    public String toString() {
      return String.format(
          "Mock[id=%d, name='%s' request='%s', queryParams='%s' response='%s']", id, name, request, queryParams == null ? "" : queryParams.toString(), response);
    }    
}