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
 * Dialog that provide an interface for user to create their own custom
 * {@link noracee.ankidictionary.entity.Example Example}
 */

public class NewExampleDialog extends AppCompatDialogFragment implements TextView.OnEditorActionListener,
        View.OnClickListener {
    /**
     * Listener to listen for when an {@link noracee.ankidictionary.entity.Example Example} is created
     */
    public interface OnExampleCreateListener {
        void onExampleCreate(String japanese, String english);
    }

    private EditText englishView;
    private EditText japaneseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle onSavedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_example, parent, false);

        japaneseView = view.findViewById(R.id.japanese);
        englishView = view.findViewById(R.id.english);

        view.findViewById(R.id.create).setOnClickListener(this);

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
            create();

            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        create();
    }

    /**
     * Notifies listener that an {@link noracee.ankidictionary.entity.Example Example} is created
     */
    private void create() {
        dismiss();
        ((OnExampleCreateListener) getTargetFragment()).onExampleCreate(StringHelper.getText(japaneseView),
                StringHelper.getText(englishView));
    }
}
