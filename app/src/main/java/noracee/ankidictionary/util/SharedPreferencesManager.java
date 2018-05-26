package noracee.ankidictionary.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Provides interaction with Android's SharedPreferences
 */

public class SharedPreferencesManager {
    //Name of the SharedPreferences file
    private static final String NAME = "AnkiDictionary";

    private SharedPreferences settings;

    public SharedPreferencesManager(Context context) {
        settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves a String from the SharedPreferences
     * @param key key of the String that will be retrieved
     * @return String value of key or null if none found
     */
    String getString(String key) {
        return settings.getString(key, null);
    }

    /**
     * Writes a String to the SharedPreferences with given key
     * @param key key of the String that will be written
     * @param value String to be written
     */
    void write(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
