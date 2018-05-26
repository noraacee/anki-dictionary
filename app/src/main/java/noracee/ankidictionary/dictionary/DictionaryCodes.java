package noracee.ankidictionary.dictionary;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import noracee.ankidictionary.R;

/**
 * Parses the dictionary codes file and provides helper function in interacting with the codes
 */

class DictionaryCodes {
    // archaic definition that will be ignored
    static final String ARCHAISM = "arch";

    /// indexes of sets of codes
    // codes that will be used in the note
    private static final int INDEX_CODE_USED    = 0;
    // all codes available in the response
    private static final int INDEX_CODE_ALL     = 1;
    // codes used for kanjis or readings that are used in the note
    private static final int INDEX_LITERAL_USED = 2;
    // all codes available for kanjis and readings
    private static final int INDEX_LITERAL_ALL  = 3;

    // delimiter of codes
    private static final String DELIMITER = ";";
    // separates codes from their definitions or names
    private static final String SEPARATOR = ":";

    private Map<String, String> codes;
    private Map<String, String> codesUsed;
    private Map<String, String> literals;
    private Map<String, String> literalsUsed;

    DictionaryCodes(Context context) throws IOException {
        codes = new HashMap<>();
        codesUsed = new HashMap<>();
        literals = new HashMap<>();
        literalsUsed = new HashMap<>();

        InputStream is = context.getResources().openRawResource(R.raw.dictionary_codes);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        int count = 0;
        // parse the line from the file according to its index
        while ((line = br.readLine()) != null) {
            switch (count) {
                case INDEX_CODE_USED:
                    parse(codesUsed, line);
                    break;
                case INDEX_CODE_ALL:
                    parse(codes, line);
                    break;
                case INDEX_LITERAL_USED:
                    parse(literalsUsed, line);
                    break;
                case INDEX_LITERAL_ALL:
                    parse(literals, line);
                    break;
            }

            count++;
        }
    }

    /**
     * Determines whether or not the given code is a valid code
     * @param code code to verify
     * @return true if the code is valid, or false otherwise
     */
    boolean getCode(String code) {
        return codes.get(code) != null;
    }

    /**
     * Determines whether or not the given code will be in the note, and retrieves its name
     * @param code code to retrieve
     * @return the name of the code, or null if it doesn't exist
     */
    String getCodeUsed(String code) {
        return codesUsed.get(code);
    }

    /**
     * Determines whether or not the given code is a valid code for kanjis and readings
     * @param code code to verify
     * @return true if the code is valid, or false otherwise
     */
    boolean getLiteral(String code) { return literals.get(code) != null; }

    /**
     * Determines whether or not the given code will be used in the note for kanjis and readings
     * @param code code to verify
     * @return true if the code is valid, or falise otherwise
     */
    boolean getLiteralUsed(String code) { return literalsUsed.get(code) != null; }

    /**
     * Parses a line of code from the file into its respective map
     * @param codes map that will contain the codes
     * @param raw line of code from the file
     */
    private void parse(Map<String, String> codes, String raw) {
        String[] defs = raw.split(DELIMITER);

        for (String def : defs) {
            String[] parts = def.split(SEPARATOR);

            codes.put(parts[0], parts[1]);
        }
    }
}
