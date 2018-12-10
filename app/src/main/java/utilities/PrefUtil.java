package utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtil {

    private Activity activity;
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    /*public static final String ID_FAK = "ID_FAK";
    public static final String NAMA_FAK = "NAMA_FAK";
    public static final String ID_JUR = "ID_JUR";
    public static final String NAMA_JUR = "NAMA_JUR";*/
    public static final String TELP = "TELP";
    public static final String ALAMAT = "ALAMAT";
    public static final String EMAIL = "EMAIL";
    public static final String STATUS = "STATUS";// M/D/A

    public PrefUtil(Activity activity) {
        this.activity = activity;
    }

    public void clear() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveUserInfo(String id, String name, String telp, String alamat, String email, String status){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ID, id);
        editor.putString(NAME, name);
        /*editor.putInt(ID_FAK, id_fak);
        editor.putString(NAMA_FAK, nameFak);
        editor.putInt(ID_JUR, id_jur);
        editor.putString(NAMA_JUR, nameJur);*/
        editor.putString(TELP, telp);
        editor.putString(ALAMAT, alamat);
        editor.putString(EMAIL, email);
        editor.putString(STATUS, status);
        editor.apply();
    }

    public SharedPreferences getUserInfo(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        //Log.d("MyApp", "Name : "+prefs.getString("fb_name",null)+"\nEmail : "+prefs.getString("fb_email",null));
        return prefs;
    }
}
