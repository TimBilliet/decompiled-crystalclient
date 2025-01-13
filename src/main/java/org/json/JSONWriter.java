package org.json;

import java.io.IOException;

public class JSONWriter {
    private static final int maxdepth = 200;

    private boolean comma;

    protected char mode;

    private final JSONObject[] stack;

    private int top;

    protected Appendable writer;

    public JSONWriter(Appendable w) {
        this.comma = false;
        this.mode = 'i';
        this.stack = new JSONObject[200];
        this.top = 0;
        this.writer = w;
    }

    private JSONWriter append(String string) throws JSONException {
        if (string == null)
            throw new JSONException("Null pointer");
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a')
                    this.writer.append(',');
                this.writer.append(string);
            } catch (IOException e) {
                throw new JSONException(e);
            }
            if (this.mode == 'o')
                this.mode = 'k';
            this.comma = true;
            return this;
        }
        throw new JSONException("Value out of sequence.");
    }

    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            push(null);
            append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    private JSONWriter end(char m, char c) throws JSONException {
        if (this.mode != m)
            throw new JSONException((m == 'a') ? "Misplaced endArray." : "Misplaced endObject.");
        pop(m);
        try {
            this.writer.append(c);
        } catch (IOException e) {
            throw new JSONException(e);
        }
        this.comma = true;
        return this;
    }

    public JSONWriter endArray() throws JSONException {
        return end('a', ']');
    }

    public JSONWriter endObject() throws JSONException {
        return end('k', '}');
    }

    public JSONWriter key(String string) throws JSONException {
        if (string == null)
            throw new JSONException("Null key.");
        if (this.mode == 'k')
            try {
                this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
                if (this.comma)
                    this.writer.append(',');
                this.writer.append(JSONObject.quote(string));
                this.writer.append(':');
                this.comma = false;
                this.mode = 'o';
                return this;
            } catch (IOException e) {
                throw new JSONException(e);
            }
        throw new JSONException("Misplaced key.");
    }

    public JSONWriter object() throws JSONException {
        if (this.mode == 'i')
            this.mode = 'o';
        if (this.mode == 'o' || this.mode == 'a') {
            append("{");
            push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    private void pop(char c) throws JSONException {
        if (this.top <= 0)
            throw new JSONException("Nesting error.");
        char m = (this.stack[this.top - 1] == null) ? 'a' : 'k';
        if (m != c)
            throw new JSONException("Nesting error.");
        this.top--;
        this.mode = (this.top == 0) ? 'd' : ((this.stack[this.top - 1] == null) ? 'a' : 'k');
    }

    private void push(JSONObject jo) throws JSONException {
        if (this.top >= 200)
            throw new JSONException("Nesting too deep.");
        this.stack[this.top] = jo;
        this.mode = (jo == null) ? 'a' : 'k';
        this.top++;
    }

    public JSONWriter value(boolean b) throws JSONException {
        return append(b ? "true" : "false");
    }

    public JSONWriter value(double d) throws JSONException {
        return value(new Double(d));
    }

    public JSONWriter value(long l) throws JSONException {
        return append(Long.toString(l));
    }

    public JSONWriter value(Object object) throws JSONException {
        return append(JSONObject.valueToString(object));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\JSONWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */