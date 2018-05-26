package noracee.ankidictionary.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import noracee.ankidictionary.R;
import noracee.ankidictionary.util.AnkiManager;
import noracee.ankidictionary.util.HttpManager;
import noracee.ankidictionary.util.ResourceManager;
import noracee.ankidictionary.util.UtilProvider;
import noracee.ankidictionary.util.SharedPreferencesManager;
import noracee.ankidictionary.util.StatusManager;
import noracee.ankidictionary.widget.NonSwipeableViewPager;

/**
 * Main activity for this application
 */

public class AnkiDictionaryActivity extends AppCompatActivity implements UtilProvider,
        UtilProvider.GetUtilProvider, View.OnClickListener {
    private AnkiDictionaryAdapter adapter;
    private AnkiManager ankiManager;
    private HttpManager httpManager;
    private ResourceManager resourceManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private StatusManager statusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_anki_dictionary);

        // initializes all the managers used in the application
        resourceManager = new ResourceManager(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        ImageView statusButton = findViewById(R.id.statusButton);
        TextView statusView = findViewById(R.id.status);
        TextView titleView = findViewById(R.id.title);
        statusManager = new StatusManager(getResourceManager(), titleView, statusButton, statusView);

        ankiManager = new AnkiManager(this, getSharedPreferencesManager(), getStatusManager());
        httpManager = new HttpManager(this, getStatusManager());

        final NonSwipeableViewPager pager = findViewById(R.id.pager);
        adapter = new AnkiDictionaryAdapter(getSupportFragmentManager(), pager, getStatusManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);

        pager.post(new Runnable() {
            @Override
            public void run() {
                adapter.onPageSelected(pager.getCurrentItem());
            }
        });

        statusManager.setStatusButtonListener(this);
    }

    @Override
    public void onBackPressed() {
        if (!adapter.onBackPressed())
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
            case AnkiManager.CODE_PERMISSION:
                ankiManager.onRequestPermissionResult(grantResults);
                break;
        }
    }

    @Override
    public AnkiManager getAnkiManager() {
        return ankiManager;
    }

    @Override
    public HttpManager getHttpManager() {
        return httpManager;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public SharedPreferencesManager getSharedPreferencesManager() {
        return sharedPreferencesManager;
    }

    @Override
    public StatusManager getStatusManager() {
        return statusManager;
    }

    @Override
    public UtilProvider getUtilProvider() {
        return this;
    }

    @Override
    public void onClick(View view) {
        adapter.onBackPressed();
    }

    /**
     * Returns the adapter used to manage all the fragments, which also serves as a listener for all
     * callbacks
     * @return adapter used to manage all the fragments
     */
    public AnkiDictionaryAdapter getListener() {
        return adapter;
    }
}
