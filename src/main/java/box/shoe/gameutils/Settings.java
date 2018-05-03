package box.shoe.gameutils;

import android.content.SharedPreferences;

/**
 * @deprecated unusable, how to get correct sharedpref instance? that isn't accidentally used elsewhere?
 */

public class Settings
{
    private static SharedPreferences sharedPreferences;

    public static boolean getBoolean(String key, boolean defaultValue)
    {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue)
    {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue)
    {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue)
    {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public static String getString(String key, String defaultValue)
    {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putInt(String key, int value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putLong(String key, long value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putFloat(String key, float value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putString(String key, String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
