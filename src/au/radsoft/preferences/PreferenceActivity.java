package au.radsoft.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity {
    static String SCHEMA_ANDROID = "http://schemas.android.com/apk/res/android";
    static String ATTR_INPUT_TYPE = "inputType";

    private static String EXTRA_RESOURCE = "resource";
    
    private java.util.ArrayList<OnActivityResultCallback> mOnActivityResultCallback = new java.util.ArrayList<OnActivityResultCallback>();
    
    public static void show(Context context, int res) {
        Intent intent = new Intent(context, PreferenceActivity.class);
        intent.putExtra(EXTRA_RESOURCE, res);
        context.startActivity(intent);
    }
    
    public static SharedPreferences init(Context context, int res) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, res, false);
        return sharedPref;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_RESOURCE)) {
            int res = intent.getIntExtra(EXTRA_RESOURCE, -1);
            addPreferencesFromResource(res);
        }
    }
    
    int setOnActivityResultCallback(OnActivityResultCallback callback) {
        int i = mOnActivityResultCallback.size();
        mOnActivityResultCallback.add(callback);
        return i;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode < mOnActivityResultCallback.size())
            mOnActivityResultCallback.get(requestCode).onActivityResult(requestCode, resultCode, data);
    }
}
