package noracee.ankidictionary.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import noracee.ankidictionary.entity.Vocabulary;
import noracee.ankidictionary.util.StatusManager;

/**
 * An adapter for the main ViewPager for AnkiDictionaryActivity with a static number of pages to manage
 * all the fragments
 */

public class AnkiDictionaryAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener,
        SearchFragment.OnSearchListener, VocabularyFragment.OnVocabularySelectListener,
        ExampleFragment.OnExampleSelectListener, AddFragment.OnAddListener {
    private static final int COUNT_FRAGMENTS = 4;

    private static final int POSITION_SEARCH     = 0;
    private static final int POSITION_VOCABULARY = 1;
    private static final int POSITION_EXAMPLE    = 2;
    private static final int POSITION_ADD        = 3;

    private ViewPager pager;

    private AddFragment addFragment;
    private ExampleFragment exampleFragment;
    private SearchFragment searchFragment;
    private StatusManager statusManager;
    private VocabularyFragment vocabularyFragment;

    AnkiDictionaryAdapter(FragmentManager fm, ViewPager pager, StatusManager statusManager) {
        super(fm);
        this.pager = pager;
        this.statusManager = statusManager;

        pager.addOnPageChangeListener(this);
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        switch(position) {
            case POSITION_SEARCH:
                searchFragment = (SearchFragment) fragment;
                break;
            case POSITION_VOCABULARY:
                vocabularyFragment = (VocabularyFragment) fragment;
                break;
            case POSITION_EXAMPLE:
                exampleFragment = (ExampleFragment) fragment;
                break;
            case POSITION_ADD:
                addFragment = (AddFragment) fragment;
                break;
        }

        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case POSITION_SEARCH:
                return new SearchFragment();
            case POSITION_VOCABULARY:
                return new VocabularyFragment();
            case POSITION_EXAMPLE:
                return new ExampleFragment();
            case POSITION_ADD:
                return new AddFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return COUNT_FRAGMENTS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == POSITION_SEARCH)
            statusManager.hideStatusButton();
        else
            statusManager.showStatusButton();

        setTitle(position);
    }

    @Override
    public void onSearch(String query) {
        vocabularyFragment.search(query);
        pager.setCurrentItem(POSITION_VOCABULARY);
    }

    @Override
    public void onVocabularySelect(Vocabulary vocabulary) {
        exampleFragment.query(vocabulary);
        pager.setCurrentItem(POSITION_EXAMPLE);
    }

    @Override
    public void onExampleSelect(Vocabulary vocabulary) {
        addFragment.confirm(vocabulary);
        pager.setCurrentItem(POSITION_ADD);
    }

    @Override
    public void onAdd() {
        clear();
        pager.setCurrentItem(POSITION_SEARCH);
    }

    /**
     * Notifies the adapter that the back button has been pressed
     * @return true if this action has been consumed, or false otherwise
     */
     boolean onBackPressed() {
        int position = pager.getCurrentItem();
        if (position != POSITION_SEARCH) {
            pager.setCurrentItem(--position);
            return true;
        }

        return false;
    }

    /**
     * Clears all inputs and results
     */
    private void clear() {
        exampleFragment.clear();
        searchFragment.clear();
        vocabularyFragment.clear();
    }

    /**
     * Sets the title of the status bar in accordance to the current fragment
     * @param position position of the current fragment shown
     */
    private void setTitle(int position) {
        switch(position) {
            case POSITION_SEARCH:
                if (searchFragment != null)
                    searchFragment.setTitle();
                break;
            case POSITION_VOCABULARY:
                if (vocabularyFragment != null)
                    vocabularyFragment.setTitle();
                break;
            case POSITION_EXAMPLE:
                if (exampleFragment != null)
                    exampleFragment.setTitle();
                break;
            case POSITION_ADD:
                if (addFragment != null)
                    addFragment.setTitle();
                break;
        }
    }
}
