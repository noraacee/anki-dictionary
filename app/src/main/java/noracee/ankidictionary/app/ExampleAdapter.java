package noracee.ankidictionary.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.dictionary.ExampleParser;
import noracee.ankidictionary.entity.Example;
import noracee.ankidictionary.entity.Vocabulary;

/**
 * An adapter for a list of {@link Example Example} for a ListView
 */

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> {
    /**
     * Class for holding a view for a {@link Example Example}
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView japaneseView;
        final TextView englishView;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            japaneseView = itemView.findViewById(R.id.japanese);
            englishView = itemView.findViewById(R.id.english);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                vocabulary.setExample(getAdapterPosition());
                listener.onExampleSelect(vocabulary);
            }
        }
    }

    private ExampleParser parser;
    private ExampleFragment.OnExampleSelectListener listener;
    private Vocabulary vocabulary;

    ExampleAdapter(ExampleParser parser) {
        this.parser = parser;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_example, parent,
                false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Example example = parser.get(position);
        holder.japaneseView.setText(example.getJapanese());
        holder.englishView.setText(example.getEnglish());
    }

    @Override
    public int getItemCount() {
        return parser.size();
    }

    /**
     * Sets the listener for when an example is selected
     * @param listener listener to listen for example selection
     */
    void setListener(ExampleFragment.OnExampleSelectListener listener) {
        this.listener = listener;
    }

    /**
     * Removes the listener associated with this adapter on Activity recreation
     */
    void removeListener() {
        listener = null;
    }

    /**
     * Sets the {@link Vocabulary Vocabulary} used for the current example sentences
     * @param vocabulary Vocabulary
     */
    void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }
}
