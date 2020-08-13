package fr.stemprado.apps.mocktar.beans;

public class HeaderParam {

    public String name;
    public String value;
   
    public HeaderParam() {}
  
    public HeaderParam(String name, String value) {
      this.name = name;
      this.value = value;
    }
  
    @Override
    public String toString() {
      return String.format(
          "[name='%s' value='%s']", name, value);
    }    
}