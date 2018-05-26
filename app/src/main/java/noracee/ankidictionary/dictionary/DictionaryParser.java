package noracee.ankidictionary.dictionary;

import android.content.Context;

import com.android.volley.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.util.HttpManager;
import noracee.ankidictionary.util.StatusManager;
import noracee.ankidictionary.util.StringHelper;
import noracee.ankidictionary.util.UtilProvider;

/**
 * Queries the server for vocabulary definitions and parses it
 */

public class DictionaryParser {
    /**
     * An interface that allows the parser to notify any listeners of changes in the data set
     */
    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    // Capacity for the StringBuilder
    private static final int CAPACITY_DEF = 100;
    private static final int CAPACITY_POS = 16;

    // Error message for when there is no response to extract
    private static final int ERROR_RESULTS = R.string.anki_error_results;

    // Delimiter to separate entities
    private static final String DELIMITER_DEF = ", ";
    private static final String DELIMITER_POS = "　";

    /// Regex for parsing
    /// Example response:
    /// kanji1(P);kanji2(oK) [reading1(P);reading2(ok)] /(pos) (1) (See ...) definition/(pos) (2) definition2/(P)/
    // Extracts codes from the response
    private static final String REGEX_CODE       = "\\(([^)]+)\\)";
    // Separates parts of speech within the same definition
    private static final String REGEX_CODE_DEFS  = ",";
    // Separates definitions
    private static final String REGEX_DEFS       = "/";
    // Extracts domains out of definitions
    private static final String REGEX_DOMAINS    = "\\{([^}]+)\\}";
    // Checks if section applies specifically to current vocabulary
    private static final String REGEX_ESPECIALLY = "\\((esp.[^)]+)\\)";
    // Separates kanji from readings
    private static final String REGEX_LITERAL    = "\\s";
    // Checks if section applies to current vocabulary
    private static final String REGEX_ONLY       = "\\(([\\s\\S]+)only\\)";
    // Separates kanji and reading from definitions
    private static final String REGEX_PARTS      = " /";
    // Extracts all the readings
    private static final String REGEX_READINGS   = "\\[([^]]+)\\]";
    // Extracts dictionary vocabularies from HTML DOM
    private static final String REGEX_RESPONSE   = "<pre>\\R([\\s\\S]+)\\R</pre>";
    // Separates dictionary vocabularies
    private static final String REGEX_RESULTS    = "\\R";
    // Extracts "See" tags in the definitions
    private static final String REGEX_SEE        = "\\((See[^)]+)\\)";
    // Separates kanji and reading sets
    private static final String REGEX_SET        = ";";

    //Query URL
    private static final String URL = "http://nihongo.monash.edu/cgi-bin/wwwjdic?1ZUJ";

    private ArrayList<Vocabulary> vocabularies;
    private DictionaryCodes codes;
    private HttpManager httpManager;
    private OnDataSetChangedListener onDataSetChangedListener;
    private StatusManager statusManager;
    private String query;

    public DictionaryParser(Context context)
            throws IOException {
        codes = new DictionaryCodes(context);
        vocabularies = new ArrayList<>();
        query = "";
    }

    /**
     * Clears the list in this adapter
     */
    public void clear() {
        vocabularies.clear();
        notifyDataSetChanged();
    }

    /**
     * Retrieves the {@link Vocabulary Vocabulary} from the vocabulary list at the given index
     * @param index index of the {@link Vocabulary Vocabulary}
     * @return vocabulary at the given index
     */
    public Vocabulary get(int index) {
        return vocabularies.get(index);
    }

    /**
     * Retrieves the most recent query searched for
     * @return most recent query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sends an HTTP GET request to retrieve dictionary vocabularies for given word
     * @param query word to search the dictionary for
     */
    public void query (final String query) {
        vocabularies.clear();
        notifyDataSetChanged();
        this.query = query;

        httpManager.get(URL + query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                extract(query, response);
                statusManager.closeStatus();
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets a listener to listen for data set changes
     * @param onDataSetChangedListener listener
     */
    public void setOnDataSetChangedListener(OnDataSetChangedListener onDataSetChangedListener) {
        this.onDataSetChangedListener = onDataSetChangedListener;
    }

    /**
     * Sets the {@link HttpManager HttpManager} and the {@link StatusManager StatusManager} for this
     * parser
     * @param utilProvider provider for the managers
     */
    public void setUtilProvider(UtilProvider utilProvider) {
        httpManager = utilProvider.getHttpManager();
        statusManager = utilProvider.getStatusManager();
    }

    /**
     * Removes the {@link HttpManager HttpManager} and the {@link StatusManager StatusManager} for
     * this parser on Activity recreation
     */
    public void removeUtilProvider() {
        httpManager = null;
        statusManager = null;
    }

    /**
     * Retrieves the size of the vocabulary list
     * @return size of the vocabulary list
     */
    public int size() {
        return vocabularies.size();
    }

    /**
     * Extracts the relevant result from the HTML DOM response
     * @param response response containing entire HTML DOM
     */
    private void extract (String query, String response) {
        Matcher m = Pattern.compile(REGEX_RESPONSE).matcher(response);
        if (m.find())
            parse(query, m.group(1));
        else
            statusManager.error(ERROR_RESULTS);
    }

    /**
     * Notifies any listeners that the data set has been changed
     */
    private void notifyDataSetChanged() {
        if (onDataSetChangedListener != null)
            onDataSetChangedListener.onDataSetChanged();
    }

    /**
     * Separates the the kanji and readings from the definitions
     * @param result result extracted from the HTML DOM
     */
    private void parse(String query, String result) {
        for (String vocabularyRaw : result.split(REGEX_RESULTS)) {
            Vocabulary vocabulary = new Vocabulary(query, vocabularyRaw);

            String[] parts = vocabularyRaw.split(REGEX_PARTS, 2);

            parseLiteral(vocabulary, parts[0].split(REGEX_LITERAL));
            parseSections(vocabulary, parts[1].split(REGEX_DEFS));

            vocabularies.add(vocabulary);
        }
    }

    /**
     * Adds the section to the definition buffer
     * @param section section to add
     * @param def definition buffer
     */
    private void parseDefinition(StringBuilder section, StringBuilder def) {
        String sectionString = section.toString().trim();
        if (!sectionString.isEmpty()) {
            if (def.length() == 0)
                def.append(sectionString);
            else
                def.append(DELIMITER_DEF).append(sectionString);
        }
    }

    /**
     * Checks if this section contains a domain and parses it
     * @param vocabulary Vocabulary to add tags to
     * @param section section to be parsed
     * @param pos parts of speech buffer to add domain to
     * @return true if this domain is not important and section should be discarded, or false
     * otherwise
     */
    private boolean parseDomain(Vocabulary vocabulary, StringBuilder section, StringBuilder pos) {
        Matcher m = Pattern.compile(REGEX_DOMAINS).matcher(section);
        while(m.find()) {
            String match = m.group(1);
            if (codes.getCode(match)) {
                // only removes this part if it is a domain
                StringHelper.remove(section, "{" + match + "}");

                // checks if domain is important
                match = codes.getCodeUsed(match);
                if (match != null) {
                    vocabulary.addTag(match);

                    if (pos.length() == 0)
                        pos.append(match);
                    else
                        pos.append(DELIMITER_POS).append(match);
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if this section specifically applies to the current {@link Vocabulary Vocabulary}
     * @param vocabulary Vocabulary to check
     * @param section section to be checked
     */
    private void parseEspecially(Vocabulary vocabulary, StringBuilder section) {
        Matcher m = Pattern.compile(REGEX_ESPECIALLY).matcher(section);
        if (m.find()) {
            String match = m.group(1);
            if (match.contains(vocabulary.getQuery()))
                StringHelper.remove(section,"(" + match + ")");
        }
    }

    /**
     * Extracts any codes from the kanji, then adds it into the {@link Vocabulary Vocabulary},
     * ignoring any kanji that doesn't match the query
     * @param vocabulary Vocabulary to be parsed into
     * @param kanji kanji to parse
     */
    private void parseKanji(Vocabulary vocabulary, String kanji) {
        if (kanji.contains(vocabulary.getQuery())) {
            Matcher m = Pattern.compile(REGEX_CODE).matcher(kanji);
            if (m.find()) {
                String match = m.group(1);
                if (codes.getLiteralUsed(match))
                    kanji = kanji.replace("(" + match + ")", "");
            }

            vocabulary.addKanji(kanji);
        }
    }

    /**
     * Parses the kanjis
     * @param vocabulary Vocabulary to be parsed into
     * @param kanjiString kanjis to parse
     */
    private void parseKanjis(Vocabulary vocabulary, String kanjiString) {
        if (kanjiString.contains(REGEX_SET)) {
            String[] kanjis = kanjiString.split(REGEX_SET);
            for (String kanji : kanjis)
                parseKanji(vocabulary, kanji);
        } else {
            parseKanji(vocabulary, kanjiString);
        }
    }

    /**
     * Parses the kanjis and readings
     * @param vocabulary Vocabulary to be parsed into
     * @param literal list of kanjis and readings to be parsed
     */
    private void parseLiteral(Vocabulary vocabulary, String[] literal) {
        parseKanjis(vocabulary, literal[0]);

        if (literal.length > 1)
            parseReadings(vocabulary, literal[1]);
    }

    /**
     * Checks if it is a new definition section, adds the previous section to the
     * {@link Vocabulary Vocabulary} and clears all buffers
     * @param vocabulary Vocabulary to add definitions and parts of speech to
     * @param section section to be parsed
     * @param def definitions buffer
     * @param pos parts of speech buffer
     * @return true if this is a start of a new section, or false otherwise
     */
    private boolean parseNewSection(Vocabulary vocabulary, int index, StringBuilder section,
                                    StringBuilder def, StringBuilder pos) {
        String indexString = "(" + index + ")";
        if (StringHelper.contains(section, indexString)) {
            StringHelper.remove(section, indexString);

            // if there is anything to add to vocabulary
            if (def.length() > 0) {
                vocabulary.addDefinition(def.toString());
                vocabulary.addPartsOfSpeech(pos.toString());
            }

            StringHelper.clear(def);
            StringHelper.clear(pos);

            return true;
        }

        return false;
    }

    /**
     * Checks if this section applies to the current {@link Vocabulary Vocabulary}
     * @param vocabulary Vocabulary to check
     * @param section section to be check
     * @return true if this section does not belong to the {@link Vocabulary Vocabulary} and should
     * be discarded, or false otherwise
     */
    private boolean parseOnly(Vocabulary vocabulary, StringBuilder section) {
        Matcher m = Pattern.compile(REGEX_ONLY).matcher(section);
        if(m.find()) {
            String match = m.group(1);
            if (!match.contains(vocabulary.getQuery()))
                return true;
            else
                StringHelper.remove(section,"(" + match + ")");
        }

        return false;
    }

    /**
     * Parses the parts of speech from the section and into the buffer
     * @param vocabulary {@link Vocabulary Vocabulary} to add tags to
     * @param section section to be parsed
     * @param pos parts of speech buffer
     * @return true if this section is archaic and should be discarded, or false otherwise
     */
    private boolean parsePos(Vocabulary vocabulary, StringBuilder section, StringBuilder pos) {
        Matcher m = Pattern.compile(REGEX_CODE).matcher(section);
        while(m.find()) {
            String match = m.group(1);
            if (match.contains(DictionaryCodes.ARCHAISM))
                return true;

            // checks for multiple pos in the same definition
            String[] codeDefs;
            if (match.contains(REGEX_CODE_DEFS))
                codeDefs = match.split(REGEX_CODE_DEFS);
            else
                codeDefs = new String[] {match};

            boolean deleted = false;
            for (String code : codeDefs){
                if (codes.getCode(code)) {
                    if (!deleted) {
                        // only removes this part if it is a part of speech
                        StringHelper.remove(section,"(" + match + ")");
                        deleted = true;
                    }

                    code = codes.getCodeUsed(code);
                    if (code != null) {
                        vocabulary.addTag(code);

                        if (pos.length() == 0)
                            pos.append(code);
                        else
                            pos.append(DELIMITER_POS).append(code);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Extracts any codes from the reading, then adds it to the {@link Vocabulary vocabulary} if it is
     * not an irregulary or outdated reading
     * @param vocabulary {@link Vocabulary Vocabulary} to add reading to
     * @param reading reading to be parsed
     */
    private void parseReading(Vocabulary vocabulary, String reading) {
        Matcher m = Pattern.compile(REGEX_CODE).matcher(reading);
        if (m.find()) {
            String match = m.group(1);
            // ignores readings with irregular or outdated
            if (codes.getLiteralUsed(match)) {
                reading = reading.replace("(" + match + ")", "");
                vocabulary.addReading(reading);
            } else if (!codes.getLiteral(match)) {
                vocabulary.addReading(reading);
            }
        }
    }

    /**
     * Parses the readings
     * @param vocabulary {@link Vocabulary Vocabulary} to be parsed into
     * @param readingString String containing all the readings
     */
    private void parseReadings(Vocabulary vocabulary, String readingString) {
        Matcher m = Pattern.compile(REGEX_READINGS).matcher(readingString);
        if (m.find()) {
            String match = m.group(1);
            // checks if there are multiple readings
            if (match.contains(REGEX_SET)) {
                String[] readings = match.split(REGEX_SET);
                for (String reading : readings) {
                    parseReading(vocabulary, reading);
                }
            } else {
                // only one reading so just add it to vocabulary
                vocabulary.setReading(match);
            }
        }
    }

    /**
     * Parses the definitions and parts of speech
     * @param vocabulary {@link Vocabulary Vocabulary} to parse into
     * @param sections list of sections to be parsed
     */
    private void parseSections(Vocabulary vocabulary, String[] sections) {
        boolean pass = false; // discard irrelevant sections
        int index = 1; // index starts at 1

        StringBuilder sec;
        StringBuilder def = new StringBuilder(CAPACITY_DEF);
        StringBuilder pos = new StringBuilder(CAPACITY_POS);

        for (String section : sections) {
            sec = new StringBuilder(section);

            if (parseNewSection(vocabulary, index, sec, def, pos)) {
                pass = false;
                index++;
            }

            if (parseOnly(vocabulary, sec)) {
                pass = true;
                continue;
            }

            parseEspecially(vocabulary, sec);

            if (parseDomain(vocabulary, sec, pos)) {
                pass = true;
                continue;
            }

            parseSeeAlso(sec);

            if (pass)
                continue;

            if (parsePos(vocabulary, sec, pos)) {
                pass = true;
                continue;
            }

            parseDefinition(sec, def);
        }

        if (!pass) {
            vocabulary.addDefinition(def.toString());
            vocabulary.addPartsOfSpeech(pos.toString());
        }
    }

    /**
     * Removes the "See also" part from the section if any
     * @param section section to be checked
     */
    private void parseSeeAlso(StringBuilder section) {
        Matcher m = Pattern.compile(REGEX_SEE).matcher(section);
        while  (m.find())
            StringHelper.remove(section,"(" + m.group(1) + ")");
    }
}
