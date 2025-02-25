package org.json;

import java.util.Locale;
import java.util.Map;

public class HTTP {
    public static final String CRLF = "\r\n";

    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        HTTPTokener x = new HTTPTokener(string);
        String token = x.nextToken();
        if (token.toUpperCase(Locale.ROOT).startsWith("HTTP")) {
            jo.put("HTTP-Version", token);
            jo.put("Status-Code", x.nextToken());
//      jo.put("Reason-Phrase", x.nextTo(false));
            jo.put("Reason-Phrase", x.nextTo('0'));
            x.next();
        } else {
            jo.put("Method", token);
            jo.put("Request-URI", x.nextToken());
            jo.put("HTTP-Version", x.nextToken());
        }
        while (x.more()) {
            String name = x.nextTo(':');
            x.next(':');
//      jo.put(name, x.nextTo(false));
            jo.put(name, x.nextTo('0'));
            x.next();
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (jo.has("Status-Code") && jo.has("Reason-Phrase")) {
            sb.append(jo.getString("HTTP-Version"));
            sb.append(' ');
            sb.append(jo.getString("Status-Code"));
            sb.append(' ');
            sb.append(jo.getString("Reason-Phrase"));
        } else if (jo.has("Method") && jo.has("Request-URI")) {
            sb.append(jo.getString("Method"));
            sb.append(' ');
            sb.append('"');
            sb.append(jo.getString("Request-URI"));
            sb.append('"');
            sb.append(' ');
            sb.append(jo.getString("HTTP-Version"));
        } else {
            throw new JSONException("Not enough material for an HTTP header.");
        }
        sb.append("\r\n");
        for (Map.Entry<String, ?> entry : jo.entrySet()) {
            String key = entry.getKey();
            if (!"HTTP-Version".equals(key) && !"Status-Code".equals(key) &&
                    !"Reason-Phrase".equals(key) && !"Method".equals(key) &&
                    !"Request-URI".equals(key) && !JSONObject.NULL.equals(entry.getValue())) {
                sb.append(key);
                sb.append(": ");
                sb.append(jo.optString(key));
                sb.append("\r\n");
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\HTTP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */