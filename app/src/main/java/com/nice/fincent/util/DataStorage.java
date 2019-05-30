package com.nice.fincent.util;

import android.app.Activity;
import android.content.SharedPreferences;


/**
 * Data 저장 관리
 *
 */
public class DataStorage {
	private static Activity activity;

	public DataStorage(Activity activity) {
		DataStorage.activity = activity;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
    public static boolean setString(String name, String value){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(name, value);
    	return editor.commit();
    }
    public static boolean setInt(String name, int value){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(name, value);
    	return editor.commit();
    }
    public static boolean setBoolean(String name, boolean value){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(name, value);
    	return editor.commit();
    }

    public static String getString(String name){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		return pref.getString(name, null);
	}
	public static int getInt(String name){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		return pref.getInt(name, 0);
	}
	public static boolean getBoolean(String name){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		return pref.getBoolean(name, false);
	}

	public static boolean clearData(String name){
		SharedPreferences pref = activity.getSharedPreferences("pref", activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(name);
		return editor.commit();
	}
}
