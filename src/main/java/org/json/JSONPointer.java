package org.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONPointer {
    private static final String ENCODING = "utf-8";

    private final List<String> refTokens;

    public static class Builder {
        private final List<String> refTokens = new ArrayList<String>();

        public JSONPointer build() {
            return new JSONPointer(this.refTokens);
        }

        public Builder append(String token) {
            if (token == null)
                throw new NullPointerException("token cannot be null");
            this.refTokens.add(token);
            return this;
        }

        public Builder append(int arrayIndex) {
            this.refTokens.add(String.valueOf(arrayIndex));
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public JSONPointer(String pointer) {
        String refs;
        if (pointer == null)
            throw new NullPointerException("pointer cannot be null");
        if (pointer.isEmpty() || pointer.equals("#")) {
            this.refTokens = Collections.emptyList();
            return;
        }
        if (pointer.startsWith("#/")) {
            refs = pointer.substring(2);
            try {
                refs = URLDecoder.decode(refs, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (pointer.startsWith("/")) {
            refs = pointer.substring(1);
        } else {
            throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
        }
        this.refTokens = new ArrayList<String>();
        for (String token : refs.split("/"))
            this.refTokens.add(unescape(token));
    }

    public JSONPointer(List<String> refTokens) {
        this.refTokens = new ArrayList<String>(refTokens);
    }

    private String unescape(String token) {
        return token.replace("~1", "/").replace("~0", "~")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public Object queryFrom(Object document) {
        if (this.refTokens.isEmpty())
            return document;
        Object current = document;
        for (String token : this.refTokens) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).opt(unescape(token));
                continue;
            }
            if (current instanceof JSONArray) {
                current = readByIndexToken(current, token);
                continue;
            }
            throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", new Object[]{current, token}));
        }
        return current;
    }

    private Object readByIndexToken(Object current, String indexToken) {
        try {
            int index = Integer.parseInt(indexToken);
            JSONArray currentArr = (JSONArray) current;
            if (index >= currentArr.length())
                throw new JSONPointerException(String.format("index %d is out of bounds - the array has %d elements", new Object[]{Integer.valueOf(index),
                        Integer.valueOf(currentArr.length())}));
            return currentArr.get(index);
        } catch (NumberFormatException e) {
            throw new JSONPointerException(String.format("%s is not an array index", new Object[]{indexToken}), e);
        }
    }

    public String toString() {
        StringBuilder rval = new StringBuilder("");
        for (String token : this.refTokens)
            rval.append('/').append(escape(token));
        return rval.toString();
    }

    private String escape(String token) {
        return token.replace("~", "~0")
                .replace("/", "~1")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    public String toURIFragment() {
        try {
            StringBuilder rval = new StringBuilder("#");
            for (String token : this.refTokens)
                rval.append('/').append(URLEncoder.encode(token, "utf-8"));
            return rval.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\JSONPointer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */