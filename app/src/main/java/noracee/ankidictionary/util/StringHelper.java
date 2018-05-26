package noracee.ankidictionary.util;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Helper functions for handling Strings
 */

public class StringHelper {
    /**
     * Resets the StringBuilder to an empty state
     * @param sb StringBuilder to clear
     */
    public static void clear(StringBuilder sb) {
        sb.setLength(0);
    }

    /**
     * Checks if the StringBuilder contains given String
     * @param sb StringBuilder to check
     * @param value String to check for
     * @return true if StringBuilder contains the given String, false otherwise
     */
    public static boolean contains(StringBuilder sb, String value) {
        return sb.indexOf(value) != -1;
    }

    /**
     * Retrieves the trimmed String value from the EditText
     * @param editText EditText to get String from
     * @return String value
     */
    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * Retrieves the trimmed String value from the TextView
     * @param textView textView to get String from
     * @return String value
     */
    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    /**
     * Removes a substring in the StringBuilder, if it exists
     * @param sb StringBuilder to change
     * @param from substring to be removed
     */
    public static void remove(StringBuilder sb, String from) {
        int index = sb.indexOf(from);

        if (index != -1)
            sb.replace(index, index + from.length(), "");
    }
}
