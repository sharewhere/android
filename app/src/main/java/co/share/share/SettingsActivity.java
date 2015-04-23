package co.share.share;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
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
        private Preference prefClearCache;
        private Preference prefLicences;
        private Preference prefClearCookies;
        private CharSequence mOldCacheTitle;

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

            final Activity activity = getActivity();
            final PreferenceScreen screen = getPreferenceScreen();

            prefLicences = (Preference) screen.findPreference("licenses_preference");
            prefClearCache = (Preference) screen.findPreference("clear_cache");
            prefClearCookies = (Preference) screen.findPreference("clear_cookies");

            mOldCacheTitle = prefClearCache.getSummary();
            updateCacheTitle();

            prefClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ImageLoader.getInstance().clearDiskCache();
                    ImageLoader.getInstance().clearMemoryCache();
                    updateCacheTitle();
                    return false;
                }
            });

            prefClearCookies.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    NetworkService.getCookies().clear();
                    return false;
                }
            });
        }

        private void updateCacheTitle()
        {
            int totalSize = 0;
            MemoryCache cache = ImageLoader.getInstance().getMemoryCache();
            DiskCache diskCache = ImageLoader.getInstance().getDiskCache();

            for(String b : cache.keys()) {
                totalSize += getBitmapSize(cache.get(b));
            }

            for(String f : diskCache.getDirectory().list()) {
                totalSize += f.length();
            }

            prefClearCache.setSummary(mOldCacheTitle +
                    String.format(" %s is currently used.", humanReadableByteCount(totalSize, false)));
        }

        private int getBitmapSize(Bitmap bitmap) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return bitmap.getAllocationByteCount();
            }
            else {
                return bitmap.getByteCount();
            }
        }

        /* http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java */
        public static String humanReadableByteCount(long bytes, boolean si) {
            int unit = si ? 1000 : 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
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
