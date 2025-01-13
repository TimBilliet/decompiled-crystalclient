package org.json;

import java.io.StringWriter;

public class JSONStringer extends JSONWriter {
    public JSONStringer() {
        super(new StringWriter());
    }

    public String toString() {
        return (this.mode == 'd') ? this.writer.toString() : null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\JSONStringer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */