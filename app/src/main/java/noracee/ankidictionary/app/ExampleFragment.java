package noracee.ankidictionary.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import noracee.ankidictionary.R;
import noracee.ankidictionary.dictionary.ExampleParser;
import noracee.ankidictionary.entity.Example;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.model.NoraaceeFragment;

/**
 * Fragment that will provide and manage the View that deals with {@link noracee.ankidictionary.entity.Example Example}
 */

public class ExampleFragment extends NoraaceeFragment implements ExampleParser.OnDataSetChangedListener,
        NewExampleDialog.OnExampleCreateListener, View.OnClickListener {
    /**
     * Listener to listen for when an {@link Example Example} is selected
     */
    public interface OnExampleSelectListener {
        void onExampleSelect(Vocabulary vocabulary);
    }

    private static final int ERROR_TRANSLATION = R.string.anki_error_translation;
    private static final int STATUS_SEARCH = R.string.anki_status_searching_examples;
    private static final int TITLE_EXAMPLE = R.string.anki_title_example;

    private static final int REQUEST_CODE_CREATE = 0;

    private static final String TAG_CREATE = "create";

    private Button skipButton;
    private ExampleAdapter adapter;
    private ExampleParser parser;
    private OnExampleSelectListener onExampleSelectListener;
    private Vocabulary vocabulary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parser = new ExampleParser();
        parser.setOnDataSetChangedListener(this);

        adapter = new ExampleAdapter(parser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, parent, false);

        RecyclerView examplesView = view.findViewById(R.id.examples);
        examplesView.setLayoutManager(new LinearLayoutManager(getContext()));
        examplesView.setAdapter(adapter);

        skipButton = view.findViewById(R.id.skip);

        view.findViewById(R.id.custom).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parser.setUtilProvider(utilProvider);

        onExampleSelectListener = ((AnkiDictionaryActivity) getContext()).getListener();
        adapter.setListener(onExampleSelectListener);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExampleSelectListener.onExampleSelect(vocabulary);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onExampleSelectListener = null;
        adapter.removeListener();
        parser.removeUtilProvider();
    }

    @Override
    public void setTitle() {
        utilProvider.getStatusManager().title(TITLE_EXAMPLE, vocabulary.getQuery(), parser.size());
    }

    @Override
    public void onDataSetChanged() {
        adapter.notifyDataSetChanged();
        setTitle();
    }

    @Override
    public void onExampleCreate(String japanese, String english) {
        if (japanese.length() != 0 && english.length() != 0) {
            Example example = new Example(japanese, english);
            vocabulary.setExample(example.toString());
        } else {
            utilProvider.getStatusManager().error(ERROR_TRANSLATION);
            return;
        }

        utilProvider.getStatusManager().closeStatus();
        onExampleSelectListener.onExampleSelect(vocabulary);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(TAG_CREATE);
        if (prev != null)
            ft.remove(prev);

        NewExampleDialog dialog = new NewExampleDialog();
        dialog.setTargetFragment(this, REQUEST_CODE_CREATE);

        dialog.show(ft, TAG_CREATE);
    }

    /**
     * Clears the parser to prepare for another search query
     */
    public void clear() {
        parser.clear();
    }

    /**
     * Sets the {@link Vocabulary Vocabulary} and query for example sentences
     * @param vocabulary vocabulary to query example sentences for
     */
    public void query(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        adapter.setVocabulary(vocabulary);
        utilProvider.getStatusManager().status(STATUS_SEARCH);
        parser.query(vocabulary);
    }
}
