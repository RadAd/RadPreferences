package au.radsoft.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

public class EditIntegerPreference extends EditTextPreference
{
	public EditIntegerPreference(Context context) {
		this(context, null);
	}	

	public EditIntegerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextPreferenceStyle);
	}

	public EditIntegerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        
        // set android:inputType="number" as default
        int inputType = attrs.getAttributeIntValue(PreferenceActivity.SCHEMA_ANDROID, PreferenceActivity.ATTR_INPUT_TYPE, -1);
        if (inputType == -1)
            getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	@Override
	public String getText() {
		return String.valueOf(getSharedPreferences().getInt(getKey(), 0));
	}

	@Override
	public void setText(String text) {
		getSharedPreferences().edit().putInt(getKey(), Integer.parseInt(text)).commit();
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue)
			getEditText().setText(getText());
		else
			super.onSetInitialValue(restoreValue, defaultValue);
	}
}
