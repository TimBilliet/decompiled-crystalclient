package org.json;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public class Property {
  public static JSONObject toJSONObject(Properties properties) throws JSONException {
    JSONObject jo = new JSONObject((properties == null) ? 0 : properties.size());
    if (properties != null && !properties.isEmpty()) {
      Enumeration<?> enumProperties = properties.propertyNames();
      while (enumProperties.hasMoreElements()) {
        String name = (String)enumProperties.nextElement();
        jo.put(name, properties.getProperty(name));
      } 
    } 
    return jo;
  }
  
  public static Properties toProperties(JSONObject jo) throws JSONException {
    Properties properties = new Properties();
    if (jo != null)
      for (Map.Entry<String, ?> entry : jo.entrySet()) {
        Object value = entry.getValue();
        if (!JSONObject.NULL.equals(value))
          properties.put(entry.getKey(), value.toString()); 
      }  
    return properties;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\Property.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */