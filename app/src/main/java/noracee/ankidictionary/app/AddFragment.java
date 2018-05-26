package noracee.ankidictionary.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.model.NoraaceeFragment;
import noracee.ankidictionary.util.ResourceManager;
import noracee.ankidictionary.util.StatusManager;
import noracee.ankidictionary.util.StringHelper;

/**
 *
 */

public class AddFragment extends NoraaceeFragment implements TextWatcher, View.OnClickListener,
        EditDialog.OnEditListener {
    /**
     * Interface that notifies the listener that a note has been added to Anki
     */
    public interface OnAddListener {
        void onAdd();
    }

    // Code representing fields of the Vocabulary
    public static final int FUNCTION_KANJI    = 0;
    public static final int FUNCTION_READING  = 1;
    public static final int FUNCTION_DEF      = 2;
    public static final int FUNCTION_POS      = 3;
    public static final int FUNCTION_EXAMPLE  = 4;
    public static final int FUNCTION_TAGS     = 5;

    private static final int ERROR_CATEGORY = R.string.anki_error_category;
    private static final int ERROR_FIELD    = R.string.anki_error_field_edit;

    private static final int HINT_DEF     = R.string.anki_hint_definitions;
    private static final int HINT_EXAMPLE = R.string.anki_hint_example;
    private static final int HINT_KANJI   = R.string.anki_hint_kanji;
    private static final int HINT_POS     = R.string.anki_hint_parts_of_speech;
    private static final int HINT_READING = R.string.anki_hint_reading;
    private static final int HINT_TAGS    = R.string.anki_hint_tags;

    private static final int REQUEST_CODE_EDIT = 0;

    private static final int TITLE_ADD = R.string.anki_title_add;

    private static final String TAG_EDIT = "edit";

    private OnAddListener onAddListener;
    private Vocabulary vocabulary;

    private EditText categoryView;
    private TextView kanjiView;
    private TextView readingView;
    private TextView defView;
    private TextView posView;
    private TextView exampleView;
    private TextView tagsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, parent, false);

        categoryView = view.findViewById(R.id.category);
        categoryView.addTextChangedListener(this);

        kanjiView = view.findViewById(R.id.kanji);
        kanjiView.setOnClickListener(createEditListener(FUNCTION_KANJI));

        readingView = view.findViewById(R.id.reading);
        readingView.setOnClickListener(createEditListener(FUNCTION_READING));

        defView = view.findViewById(R.id.def);
        defView.setOnClickListener(createEditListener(FUNCTION_DEF));

        posView = view.findViewById(R.id.pos);
        posView.setOnClickListener(createEditListener(FUNCTION_POS));

        exampleView = view.findViewById(R.id.example);
        exampleView.setOnClickListener(createEditListener(FUNCTION_EXAMPLE));

        tagsView = view.findViewById(R.id.tags);
        tagsView.setOnClickListener(createEditListener(FUNCTION_TAGS));

        view.findViewById(R.id.add).setOnClickListener(this);

        if (vocabulary != null)
            confirm(vocabulary);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onAddListener = ((AnkiDictionaryActivity) getContext()).getListener();
        categoryView.setText(utilProvider.getAnkiManager().getCategory());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onAddListener = null;
    }

    @Override
    public void setTitle() {
        utilProvider.getStatusManager().title(TITLE_ADD);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String category = editable.toString().trim();

        if (vocabulary != null && category.length() != 0)
            vocabulary.setCategory(editable.toString().trim());
    }

    @Override
    public void onClick(View view) {
        if (onAddListener != null && validate()) {
            utilProvider.getAnkiManager().add(vocabulary);
            vocabulary = null;
            onAddListener.onAdd();
        }
    }

    @Override
    public void onEdit(int function, String value) {
        if (value.length() == 0) {
            String field = null;
            ResourceManager resourceManager = utilProvider.getResourceManager();
            switch(function) {
                case FUNCTION_KANJI:
                    field = resourceManager.getString(HINT_KANJI);
                    break;
                case FUNCTION_READING:
                    field = resourceManager.getString(HINT_READING);
                    break;
                case FUNCTION_DEF:
                    field = resourceManager.getString(HINT_DEF);
                    break;
                case FUNCTION_POS:
                    field = resourceManager.getString(HINT_POS);
                    break;
                case FUNCTION_EXAMPLE:
                    field = resourceManager.getString(HINT_EXAMPLE);
                    break;
                case FUNCTION_TAGS:
                    field = resourceManager.getString(HINT_TAGS);
                    break;
            }

            utilProvider.getStatusManager().error(ERROR_FIELD, field, null);
        } else {
            switch(function) {
                case FUNCTION_KANJI:
                    vocabulary.setKanji(value);
                    kanjiView.setText(value);
                    break;
                case FUNCTION_READING:
                    vocabulary.setReading(value);
                    readingView.setText(value);
                    break;
                case FUNCTION_DEF:
                    vocabulary.setDefinitions(value);
                    defView.setText(value);
                    break;
                case FUNCTION_POS:
                    vocabulary.setPartsOfSpeech(value);
                    posView.setText(value);
                    break;
                case FUNCTION_EXAMPLE:
                    vocabulary.setExample(value);
                    exampleView.setText(value);
                    break;
                case FUNCTION_TAGS:
                    vocabulary.setTags(value);
                    tagsView.setText(value);
                    break;
            }
        }
    }

    /**
     * Creates a OnClickListener to listen for clicks on specific fields to edit
     * @param function field to be editted
     * @return OnClickListener that opens an {@link EditDialog EditDialog} to edit a specific field
     */
    public View.OnClickListener createEditListener(final int function) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = null;
                switch(function) {
                    case FUNCTION_KANJI:
                        value = kanjiView.getText().toString().trim();
                        break;
                    case FUNCTION_READING:
                        value = readingView.getText().toString().trim();
                        break;
                    case FUNCTION_DEF:
                        value = defView.getText().toString().trim();
                        break;
                    case FUNCTION_POS:
                        value = posView.getText().toString().trim();
                        break;
                    case FUNCTION_EXAMPLE:
                        value = exampleView.getText().toString().trim();
                        break;
                    case FUNCTION_TAGS:
                        value = tagsView.getText().toString().trim();
                        break;
                }

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(TAG_EDIT);
                if (prev != null)
                    ft.remove(prev);

                EditDialog dialog = EditDialog.newInstance(function, value);
                dialog.setTargetFragment(AddFragment.this, REQUEST_CODE_EDIT);

                dialog.show(ft, TAG_EDIT);
            }
        };
    }

    /**
     * Populates the view with this fragment for confirmation before adding it to Anki
     * @param vocabulary vocabulary to confirm
     */
    public void confirm(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;

        if (utilProvider != null)
            vocabulary.setCategory(utilProvider.getAnkiManager().getCategory());

        kanjiView.setText(vocabulary.getKanji());
        readingView.setText(vocabulary.getReading());
        defView.setText(vocabulary.getDefinitions());
        posView.setText(vocabulary.getPartsOfSpeech());
        exampleView.setText(vocabulary.getExample());
        tagsView.setText(vocabulary.getTagsString());
    }

    /**
     * Validates all the fields before adding it to Anki
     * @return true if all fields are filled in, or false otherwise
     */
    public boolean validate() {
        ResourceManager resourceManager = utilProvider.getResourceManager();
        StatusManager statusManager = utilProvider.getStatusManager();
        if (StringHelper.getText(categoryView).length() == 0) {
            statusManager.error(ERROR_CATEGORY);
            return false;
        } else if (StringHelper.getText(kanjiView).length() == 0) {
            statusManager.error(ERROR_FIELD, resourceManager.getString(HINT_KANJI), null);
            return false;
        } else if (StringHelper.getText(readingView).length() == 0) {
            statusManager.error(ERROR_FIELD, resourceManager.getString(HINT_READING), null);
            return false;
        } else if (StringHelper.getText(defView).length() == 0) {
            statusManager.error(ERROR_FIELD, resourceManager.getString(HINT_DEF), null);
            return false;
        } else {
            return true;
        }
    }
}
