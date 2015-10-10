package au.radsoft.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import au.radsoft.preferences.PreferenceActivity;

public class Example1 extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PreferenceActivity.init(this, R.xml.preferences);
    }
    
    public void onPreferences(View v)
    {
        PreferenceActivity.show(this, R.xml.preferences);
    }
}
