package cbqcf.dim.meditime;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREF_NAME = "MeditimePreferences";
    private static final String KEY_DINER_TIME = "diner_time";
    private static final String KEY_NOON_TIME = "noon_time";
    private static final String KEY_MORNING_TIME = "morning_time";
    private static final String KEY_NIGHT_TIME = "night_time";

    private SharedPreferences sharedPreferences;
    private static SettingsManager instance;

    private SettingsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setDinerTime(long timeMillis) {
        sharedPreferences.edit().putLong(KEY_DINER_TIME, timeMillis).apply();
    }

    public long getDinerTime() {
        return sharedPreferences.getLong(KEY_DINER_TIME, DEFAULT_DINER_TIME);
    }

    public void setNoonTime(long timeMillis) {
        sharedPreferences.edit().putLong(KEY_NOON_TIME, timeMillis).apply();
    }

    public long getNoonTime() {
        return sharedPreferences.getLong(KEY_NOON_TIME, DEFAULT_NOON_TIME);
    }

    public void setMorningTime(long timeMillis) {
        sharedPreferences.edit().putLong(KEY_MORNING_TIME, timeMillis).apply();
    }

    public long getMorningTime() {
        return sharedPreferences.getLong(KEY_MORNING_TIME, DEFAULT_MORNING_TIME);
    }

    public void setNightTime(long timeMillis) {
        sharedPreferences.edit().putLong(KEY_NIGHT_TIME, timeMillis).apply();
    }

    public long getNightTime() {
        return sharedPreferences.getLong(KEY_NIGHT_TIME, DEFAULT_NIGHT_TIME);
    }
}