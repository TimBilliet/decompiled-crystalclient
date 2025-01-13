package org.json;

public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\json\JSONException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */