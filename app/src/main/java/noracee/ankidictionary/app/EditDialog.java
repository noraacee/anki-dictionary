package noracee.ankidictionary.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.util.StringHelper;

/**
 * Dialog that provide an interface for editing a specific field
 */

public class EditDialog extends AppCompatDialogFragment implements TextView.OnEditorActionListener,
        View.OnClickListener {
    public interface OnEditListener {
        void onEdit(int function, String value);
    }

    private static final int HINT_DEF     = R.string.anki_hint_definitions;
    private static final int HINT_EXAMPLE = R.string.anki_hint_example;
    private static final int HINT_KANJI   = R.string.anki_hint_kanji;
    private static final int HINT_POS     = R.string.anki_hint_parts_of_speech;
    private static final int HINT_READING = R.string.anki_hint_reading;
    private static final int HINT_TAGS    = R.string.anki_hint_tags;

    private static final String KEY_FUNCTION = "function";
    private static final String KEY_VALUE = "value";

    private int function;

    private EditText fieldView;

    /**
     * Creates an {@link EditDialog EditDialog} with specified function and starting value
     * @param function the field being edited
     * @param value starting value of the field
     * @return EditDialog
     */
    public static EditDialog newInstance(int function, String value) {
        EditDialog editDialog = new EditDialog();

        Bundle args = new Bundle();
        args.putInt(KEY_FUNCTION, function);
        args.putString(KEY_VALUE, value);
        editDialog.setArguments(args);

        return editDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle onSavedInstanceState) {
        Bundle args = getArguments();
        function = args.getInt(KEY_FUNCTION);

        View view = inflater.inflate(R.layout.dialog_edit, parent, false);

        fieldView = view.findViewById(R.id.field);
        fieldView.setText(args.getString(KEY_VALUE));
        switch(function) {
            case AddFragment.FUNCTION_KANJI:
                fieldView.setHint(HINT_KANJI);
                break;
            case AddFragment.FUNCTION_READING:
                fieldView.setHint(HINT_READING);
                break;
            case AddFragment.FUNCTION_DEF:
                fieldView.setHint(HINT_DEF);
                break;
            case AddFragment.FUNCTION_POS:
                fieldView.setHint(HINT_POS);
                break;
            case AddFragment.FUNCTION_EXAMPLE:
                fieldView.setHint(HINT_EXAMPLE);
                break;
            case AddFragment.FUNCTION_TAGS:
                fieldView.setHint(HINT_TAGS);
                break;
        }

        view.findViewById(R.id.edit).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            edit();

            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        edit();
    }

    /**
     * Notifies the listener that the edit is complete
     */
    private void edit() {
        dismiss();

        ((OnEditListener) getTargetFragment()).onEdit(function, StringHelper.getText(fieldView));
    }
}
