package noracee.ankidictionary.dictionary;

import com.android.volley.Response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Example;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.util.HttpManager;
import noracee.ankidictionary.util.StatusManager;
import noracee.ankidictionary.util.UtilProvider;

/**
 * Queries the server for example sentences and parses it
 */

public class ExampleParser {
    /**
     * An interface that allows the parser to notify any listeners of changes in the data set
     */
    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    // Number of parts when an example string is split
    private static final int COUNT_TRANSLATIONS = 3;
    // Positions of the translations when example string is split
    private static final int POSITION_ENGLISH = 2;
    private static final int POSITION_JAPANESE = 0;

    // Error message for when the Japanese and English translations cannot be separated
    private static final int ERROR_PARSE = R.string.anki_error_parse;
    // Error message for when there is no response to extract
    private static final int ERROR_RESULTS = R.string.anki_error_results;
    // Status message to show when no examples are found
    private static final int STATUS_NO_EXAMPLES = R.string.anki_status_no_examples;

    // Separates examples
    private static final String REGEX_EXAMPLES  = "<li>";
    // Extracts example sentences from HTML DOM
    private static final String REGEX_RESPONSE  = "<ul>\\R([\\s\\S]+)\\R</ul>";
    // Separates translations
    private static final String REGEX_TRANSLATION = "\\R";

    // Query url
    private static final String URL = "http://nihongo.monash.edu/cgi-bin/wwwjdic?1ZTU";

    private HttpManager httpManager;
    private OnDataSetChangedListener onDataSetChangedListener;
    private StatusManager statusManager;
    private Vocabulary vocabulary;

    /**
     * Removes the current {@link Vocabulary Vocabulary} associated to the parser
     */
    public void clear() {
        vocabulary = null;
        notifyDataSetChanged();
    }

    /**
     * Retrieves the {@link Example Example} from the example list at the given index
     * @param index index of the {@link Example Example}
     * @return example at the given index
     */
    public Example get(int index) {
        if (vocabulary != null)
            return vocabulary.getExamples().get(index);
        return null;
    }

    /**
     * Sends an HTTP GET request to the server to retrieve example sentences for the given word
     * @param vocabulary Vocabulary to query
     */
    public void query(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;

        List<Example> examples = vocabulary.getExamples();
        if (examples != null) {
            vocabulary.getExamples().clear();
            notifyDataSetChanged();
        }

        if (httpManager != null) {
            httpManager.get(URL + vocabulary.getQuery(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            extract(response);
                            notifyDataSetChanged();

                            List<Example> examples = ExampleParser.this.vocabulary.getExamples();
                            if (examples == null || examples.size() == 0)
                                statusManager.status(STATUS_NO_EXAMPLES);
                            else
                                statusManager.closeStatus();
                        }
                    });
        }
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
        if (vocabulary != null) {
            List<Example> examples = vocabulary.getExamples();
            if (examples != null)
                return examples.size();
        }
        return 0;
    }

    /**
     * Extracts the relevant result from the HTML DOM response
     * @param response response containing the entire HTML DOM
     */
    private void extract(String response) {
        Matcher m = Pattern.compile(REGEX_RESPONSE).matcher(response);
        if (m.find())
            parse(m.group(1));
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
     * Separates the example sentences
     * @param result result extracted from the HTML DOM
     */
    private void parse(String result) {
        String[] examples = result.split(REGEX_EXAMPLES);
        for(String example : examples) {
            if (example.length() > 0)
                parseExample(example.trim());
        }
    }

    /**
     * Parses an example sentence into an {@link Example Example}
     * @param exampleString example sentence as a string
     */
    private void parseExample(String exampleString) {
        // splits Japanese from English translation
        String[] translations = exampleString.split(REGEX_TRANSLATION);
        if (translations.length == COUNT_TRANSLATIONS) {
            Example example = new Example(translations[POSITION_JAPANESE], translations[POSITION_ENGLISH]);
            vocabulary.addExample(example);
        } else {
            statusManager.error(ERROR_PARSE);
        }
    }
}
