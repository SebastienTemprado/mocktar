package fr.stemprado.apps.mocktar.beans;

public class QueryParam {

    public String name;
    public String value;
   
    public QueryParam() {}
  
    public QueryParam(String name, String value) {
      this.name = name;
      this.value = value;
    }
  
    @Override
    public String toString() {
      return String.format(
          "QueryParam[name='%s' value='%s']", name, value);
    }    
}