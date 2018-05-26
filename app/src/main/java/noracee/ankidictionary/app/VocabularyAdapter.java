package noracee.ankidictionary.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.dictionary.DictionaryParser;

/**
 * An adapter for a list of {@link Vocabulary Vocabulary} for a ListView
 */

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
    /**
     * Class for holding a view for a {@link Vocabulary Vocabulary}
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView kanjiView;
        final TextView readingView;
        final TextView defView;
        final TextView posView;
        final TextView tagView;
        final TextView rawView;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            kanjiView = itemView.findViewById(R.id.kanji);
            readingView = itemView.findViewById(R.id.reading);
            defView = itemView.findViewById(R.id.def);
            posView = itemView.findViewById(R.id.pos);
            tagView = itemView.findViewById(R.id.tags);
            rawView = itemView.findViewById(R.id.raw);
        }

        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onVocabularySelect(parser.get(getAdapterPosition()));
        }
    }

    private DictionaryParser parser;
    private VocabularyFragment.OnVocabularySelectListener listener;

    VocabularyAdapter(DictionaryParser parser) {
        this.parser = parser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_vocabulary, parent,
                false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Vocabulary vocabulary = parser.get(position);
        holder.kanjiView.setText(vocabulary.getKanji());
        holder.readingView.setText(vocabulary.getReading());
        holder.defView.setText(vocabulary.getDefinitions());
        holder.posView.setText(vocabulary.getPartsOfSpeech());
        holder.tagView.setText(vocabulary.getTagsString());
        holder.rawView.setText(vocabulary.getRaw());
    }

    @Override
    public int getItemCount() {
        return parser.size();
    }

    /**
     * Sets the listener to listen for when a vocabulary is selected
     * @param listener listener to listen for vocabulary selection
     */
    void setListener(VocabularyFragment.OnVocabularySelectListener listener) {
        this.listener = listener;
    }

    /**
     * Removes the listener associated with the adapter on Activity recreation
     */
    void removeListener() {
        listener = null;
    }
}
