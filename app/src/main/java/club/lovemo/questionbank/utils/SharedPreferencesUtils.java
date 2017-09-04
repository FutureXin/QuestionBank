package club.lovemo.questionbank.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils{
	private SharedPreferences mPref;
	private SharedPreferences.Editor editor;
	private static final String PREF_NAME = "config";

	public SharedPreferencesUtils(Context context){
		mPref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}
	public void setCategoryID(int id){
		editor = mPref.edit();
		editor.putInt("CategoryID", id);
		editor.commit();
	}
	public int getCategoryID(){
		int id=mPref.getInt("CategoryID", 0);
		return id;
	}
	public void setUserName(String userName){
		editor = mPref.edit();
		editor.putString("UserName", userName);
		editor.commit();
	}
	public String getUserName(){
		String userName=mPref.getString("UserName", null);
		return userName;
	}
	public void setOldVersionCode(int old_VersionCode){
		editor = mPref.edit();
		editor.putInt("OldVersionCode", old_VersionCode);
		editor.commit();
	}
	public int getOldVersionCode(){
		int OldVersionCode=mPref.getInt("OldVersionCode", 0);
		return OldVersionCode;
	}
	public void setCount(){
		editor = mPref.edit();
		int count=getCount();
		count++;
		editor.putInt("Count", count);
		editor.commit();
	}
	public int getCount(){
		int count=mPref.getInt("Count", 0);
		return count;
	}
	public void setShowAbout(boolean b){
		editor = mPref.edit();
		editor.putBoolean("about", b);
		editor.commit();
	}
	public Boolean getShowAbout(){
		boolean about=mPref.getBoolean("about", true);
		return about;
	}
	public static boolean getBoolean(Context ctx, String key,
									 boolean defaultValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
}
