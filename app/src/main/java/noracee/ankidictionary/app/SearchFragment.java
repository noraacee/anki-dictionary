package noracee.ankidictionary.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.model.NoraaceeFragment;
import noracee.ankidictionary.util.StringHelper;

/**
 *
 */

public class SearchFragment extends NoraaceeFragment implements TextView.OnEditorActionListener,
        View.OnClickListener {
    /**
     * Listener to listen for when a search is queried
     */
    public interface OnSearchListener {
        void onSearch(String query);
    }

    private static final int ERROR_SEARCH = R.string.anki_error_search;
    private static final int TITLE_SEARCH = R.string.anki_title_search;

    private EditText queryView;
    private OnSearchListener onSearchListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, parent, false);

        queryView = view.findViewById(R.id.query);
        queryView.setOnEditorActionListener(this);

        view.findViewById(R.id.search).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onSearchListener = ((AnkiDictionaryActivity) getContext()).getListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSearchListener = null;
    }

    @Override
    public void setTitle() {
        utilProvider.getStatusManager().title(TITLE_SEARCH);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        search();
    }

    /**
     * Clears the text in the search field
     */
    public void clear() {
        queryView.setText("");
    }

    /**
     * Commences search for dictionary definitions
     */
    public void search() {
        String query = StringHelper.getText(queryView);

        if (query.length() > 0)
            onSearchListener.onSearch(query);
        else
            utilProvider.getStatusManager().error(ERROR_SEARCH);
    }
}
