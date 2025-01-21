package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {
    public static final char[] INVALID_CHARS = new char[]{
            '\\', '/', ':', '*', '?', '"', '<', '>', '|', '[',
            ']', '\'', ';', '=', ','};

    public static String sanitizeFileName(String filename) {
        for (char invalidChar : INVALID_CHARS) {
            if (filename.indexOf(invalidChar) != -1)
                filename = filename.replace(invalidChar, '_');
        }
        return filename;
    }

    public static boolean isValidJson(String json) {
        return isValidJson(json, JsonObject.class);
    }

    public static boolean isValidJson(String json, Class<?> clazz) {
        try {
            Reference.GSON.fromJson(json, clazz);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
        URLConnection conn = source.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestProperty("User-Agent", "Minecraft/Crystal Client-v1.1.16-projectassfucker");
        InputStream input = conn.getInputStream();
        org.apache.commons.io.FileUtils.copyInputStreamToFile(input, destination);
    }
}
