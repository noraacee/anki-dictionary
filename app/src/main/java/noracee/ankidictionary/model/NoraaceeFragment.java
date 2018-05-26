package noracee.ankidictionary.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import noracee.ankidictionary.util.UtilProvider;

/**
 * Model Fragment class modified to work with all noraacee apps
 */

public abstract class NoraaceeFragment extends Fragment {
    protected UtilProvider utilProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        utilProvider = ((UtilProvider.GetUtilProvider) getContext()).getUtilProvider();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        utilProvider = null;
    }

    /**
     * Sets the title of the status bar
     */
    public abstract void setTitle();
}
