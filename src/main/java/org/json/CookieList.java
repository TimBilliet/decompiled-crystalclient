package org.json;

import java.util.Map;

public class CookieList {
  public static JSONObject toJSONObject(String string) throws JSONException {
    JSONObject jo = new JSONObject();
    JSONTokener x = new JSONTokener(string);
    while (x.more()) {
      String name = Cookie.unescape(x.nextTo('='));
      x.next('=');
      jo.put(name, Cookie.unescape(x.nextTo(';')));
      x.next();
    } 
    return jo;
  }
  
  public static String toString(JSONObject jo) throws JSONException {
    boolean b = false;
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, ?> entry : jo.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!JSONObject.NULL.equals(value)) {
        if (b)
          sb.append(';'); 
        sb.append(Cookie.escape(key));
        sb.append("=");
        sb.append(Cookie.escape(value.toString()));
        b = true;
      } 
    } 
    return sb.toString();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\CookieList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */