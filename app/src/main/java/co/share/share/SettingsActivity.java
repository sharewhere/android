package co.share.share;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;

import co.share.share.net.NetworkService;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return ProfilePrefs.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar bar = getActionBar();
        //bar.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ProfilePrefs())
                .commit();

        setTheme(R.style.AppTheme_DarkText);

    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class ProfilePrefs extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            final PreferenceScreen screen = getPreferenceScreen();

            final Activity activity = getActivity();
            Preference licenses = (Preference) screen.findPreference("licenses_preference");
            Preference clearCache = (Preference) screen.findPreference("clear_cache");
            clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImageLoader.getInstance().clearDiskCache();
                    ImageLoader.getInstance().clearMemoryCache();
                    return false;
                }
            });

            EditTextPreference endpoint = (EditTextPreference) screen.findPreference("endpoint");
            endpoint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    NetworkService.setEndpoint(newValue.toString());
                    return false;
                }
            });


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}
