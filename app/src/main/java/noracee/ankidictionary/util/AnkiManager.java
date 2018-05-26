package noracee.ankidictionary.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.ichi2.anki.api.AddContentApi;

import java.util.Map;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Vocabulary;

/**
 * Provides interaction with Anki
 */

public class AnkiManager {
    // Code used to request permission for Anki
    public static final int CODE_PERMISSION = 0;

    private static final int ERROR_DECK       = R.string.anki_error_deck;
    private static final int ERROR_MODEL      = R.string.anki_error_model;
    private static final int ERROR_PERMISSION = R.string.anki_error_permission;

    private static final String DECK_KANJI  = "漢字";
    private static final String MODEL_KANJI = "漢字";

    // Key for SharedPreferences
    private static final String KEY_CATEGORY = "category";

    // Required permission to use Anki
    private static final String PERMISSION = com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION;

    private long deckKanji;
    private long modelKanji;

    private String category;

    private AddContentApi api;
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private StatusManager statusManager;

    public AnkiManager(Activity context, SharedPreferencesManager sharedPreferencesManager,
                       StatusManager statusManager) {
        this.context = context;
        this.sharedPreferencesManager = sharedPreferencesManager;
        this.statusManager = statusManager;

        api = null;
        initCategory();
        getPermission(context);
    }


    /**
     * Adds a vocabulary to Anki only if permissions are granted
     * @param vocabulary {@link Vocabulary Vocabulary}
     */
    public void add(Vocabulary vocabulary) {
        setCategory(vocabulary.getCategory());
        if (api != null)
            api.addNote(modelKanji, deckKanji, vocabulary.getFields(), vocabulary.getTags());
    }

    /**
     * Retrieves the current category used when adding Anki notes
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Checks permission results and initializes this manager if permission was granted
     * @param grantResults results from the permission request
     */
    public void onRequestPermissionResult(int[] grantResults) {
        if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init();
        else
            statusManager.error(ERROR_PERMISSION);
    }

    /**
     * Requests relevant permissions to use Anki API
     * @param context context of the app
     */
    private void getPermission(Activity context) {
        ActivityCompat.requestPermissions(context, new String[] {PERMISSION}, CODE_PERMISSION);
    }

    /**
     * Initializes this managers to be used
     */
    private void init() {
        api = new AddContentApi(context.getApplicationContext());
        initDeck();
        initModel();
    }

    /**
     * Sets the current category as saved in SharedPreferences, or null if none is saved yet
     */
    private void initCategory() {
        category = sharedPreferencesManager.getString(KEY_CATEGORY);
    }

    /**
     * Retrieves the deck list from Anki and sets the current deck as saved in SharedPreferences,
     * or, if it doesn't exist or none is saved yet, sets it as the first deck in the deck list
     */
    private void initDeck() {
        Map<Long, String> map = api.getDeckList();
        deckKanji = -1;
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            if (entry.getValue().equals(DECK_KANJI)) {
                deckKanji = entry.getKey();
                break;
            }
        }

        if (deckKanji == -1)
            statusManager.error(ERROR_DECK, null, DECK_KANJI);
    }

    /**
     * Sets the model ids used in this app
     */
    private void initModel() {
        Map<Long, String> map = api.getModelList();
        modelKanji = -1;
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            if (entry.getValue().equals(MODEL_KANJI)) {
                modelKanji = entry.getKey();
                break;
            }
        }

        if (modelKanji == -1)
            statusManager.error(ERROR_MODEL, null, MODEL_KANJI);
    }

    /**
     * Sets the current category to be used when adding Anki notes
     * @param category category
     */
    private void setCategory(String category) {
        this.category = category;
        sharedPreferencesManager.write(KEY_CATEGORY, category);
    }
}
