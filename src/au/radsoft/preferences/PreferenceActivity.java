package au.radsoft.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity
{
    private static String EXTRA_RESOURCE = "resource";
    
    public static void show(Context context, int res)
    {
        Intent intent = new Intent(context, PreferenceActivity.class);
        intent.putExtra(EXTRA_RESOURCE, res);
        context.startActivity(intent);
    }
    
    public static SharedPreferences init(Context context, int res)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, res, false);
        return sharedPref;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        int res = intent.getIntExtra(EXTRA_RESOURCE, 0);
        addPreferencesFromResource(res);
    }
}
