package noracee.ankidictionary.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.model.NoraaceeFragment;
import noracee.ankidictionary.dictionary.DictionaryParser;

/**
 * Fragment that will provide and manage the View that deals with {@link noracee.ankidictionary.entity.Vocabulary Vocabulary}
 */

public class VocabularyFragment extends NoraaceeFragment implements DictionaryParser.OnDataSetChangedListener {
    /**
     * Listener to listen for when a {@link Vocabulary Vocabulary} is selected
     */
    public interface OnVocabularySelectListener {
        void onVocabularySelect(Vocabulary vocabulary);
    }

    private static final int ERROR_DICTIONARY = R.string.anki_error_dictionary;
    private static final int STATUS_SEARCHING = R.string.status_searching;
    private static final int TITLE_VOCABULARY = R.string.anki_title_vocabulary;

    private DictionaryParser parser;
    private OnVocabularySelectListener onVocabularySelectListener;
    private VocabularyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            parser = new DictionaryParser(getContext());
            parser.setOnDataSetChangedListener(this);
            adapter = new VocabularyAdapter(parser);
        } catch (IOException e) {
            utilProvider.getStatusManager().error(ERROR_DICTIONARY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary, parent, false);
        RecyclerView defView = view.findViewById(R.id.def);
        defView.setLayoutManager(new LinearLayoutManager(getContext()));
        defView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parser.setUtilProvider(utilProvider);

        onVocabularySelectListener = ((AnkiDictionaryActivity) getContext()).getListener();
        adapter.setListener(onVocabularySelectListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onVocabularySelectListener = null;
        adapter.removeListener();
        parser.removeUtilProvider();
    }

    @Override
    public void setTitle() {
        utilProvider.getStatusManager().title(TITLE_VOCABULARY, parser.getQuery(), parser.size());
    }

    @Override
    public void onDataSetChanged() {
        adapter.notifyDataSetChanged();
        setTitle();
    }

    /**
     * Clears the parser to prepare for another search query
     */
    public void clear() {
        parser.clear();
    }

    /**
     * Commences query for the specified vocabulary
     * @param query query to search for
     */
    public void search(String query) {
        utilProvider.getStatusManager().status(STATUS_SEARCHING);
        parser.query(query);
    }
}
