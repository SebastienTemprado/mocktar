package fr.stemprado.apps.mocktar.beans;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Mock {

    @Transient
    public static final String SEQUENCE_NAME = "mocks_sequence";

    @Id
    public Long id;
  
    public String apiName;
    public String name;
    public String verb;
    public String request;
    public List<QueryParam> queryParams = new ArrayList<>();
    public List<HeaderParam> headerParams = new ArrayList<>();
    public String body;
    public String response;
  
    public Mock() {}
  
    public Mock(long id, String apiName, String name, String verb, String request, List<QueryParam> queryParams, List<HeaderParam> headerParams, String body, String response) {
      this.id = id;
      this.apiName = apiName;
      this.name = name;
      this.verb = verb;
      this.request = request;
      this.queryParams = queryParams;
      this.headerParams = headerParams;
      this.body = body;
      this.response = response;
    }
  
    @Override
    public String toString() {
      return String.format(
          "Mock[id=%d, apiName='%s', name='%s', verb='%s', request='%s',\nqueryParams='%s',\nheaderParams='%s',\nbody='%s'\nresponse='%s']", id, apiName, name, verb, request, queryParams == null ? "" : queryParams.toString(), headerParams == null ? "" : headerParams.toString(), body, response);
    }    
}