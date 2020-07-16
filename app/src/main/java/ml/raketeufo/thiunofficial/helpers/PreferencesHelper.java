package ml.raketeufo.thiunofficial.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    public static final String PREFERENCES_KEY = "ml.raketeufo.thiunofficial.preferences";
    public static final String P_AUTO_LOGIN_KEY = "pref.autologin";
    public static final String P_POPULATE_CALENDAR_KEY = "pref.populatecalendar";
    private final Context context;
    private final SharedPreferences sharedPreferences;

    private PreferencesHelper(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static PreferencesHelper get(Context context) {
        return new PreferencesHelper(context);
    }

    public boolean useAutoLogin() {
        return this.sharedPreferences.getBoolean(P_AUTO_LOGIN_KEY, true);
    }

    public boolean usePopulateCalendar() {
        return this.sharedPreferences.getBoolean(P_POPULATE_CALENDAR_KEY, false);
    }

    public void setUseAutoLogin(boolean isChecked) {
        this.sharedPreferences.edit().putBoolean(P_AUTO_LOGIN_KEY, isChecked).apply();
    }
}
