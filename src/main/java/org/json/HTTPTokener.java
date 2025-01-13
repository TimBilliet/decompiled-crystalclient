package org.json;

public class HTTPTokener extends JSONTokener {
    public HTTPTokener(String string) {
        super(string);
    }

    public String nextToken() throws JSONException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = next();
            if (!Character.isWhitespace(c)) {
                if (c == '"' || c == '\'') {
                    char q = c;
                    while (true) {
                        c = next();
                        if (c < ' ')
                            throw syntaxError("Unterminated string.");
                        if (c == q)
                            return sb.toString();
                        sb.append(c);
                    }
                }
                while (true) {
                    if (c == '\000' || Character.isWhitespace(c))
                        return sb.toString();
                    sb.append(c);
                    c = next();
                }
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\HTTPTokener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */