package noracee.ankidictionary.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Manages interaction with the Resources of this application
 */

public class ResourceManager {
    private Context context;

    public ResourceManager(Context context) {
        this.context = context;
    }

    /**
     * Retrieves a String from the Resources
     * @param id id of String
     * @return String of id
     */
    public String getString(int id) {
        return context.getResources().getString(id);
    }

    /**
     * Retrieves a color as Integer from the Resources
     * @param id id of the color
     * @return color as an int
     */
    public int getColor(int id) {
        return ContextCompat.getColor(context, id);
    }
}
