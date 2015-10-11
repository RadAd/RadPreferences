package au.radsoft.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

public class EditFloatPreference extends EditTextPreference
{
	public EditFloatPreference(Context context) {
		this(context, null);
	}	

	public EditFloatPreference(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextPreferenceStyle);
	}

	public EditFloatPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        
        // set android:inputType="numberDecimal" as default
        int inputType = attrs.getAttributeIntValue(PreferenceActivity.SCHEMA_ANDROID, PreferenceActivity.ATTR_INPUT_TYPE, -1);
        if (inputType == -1)
            getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
	}

	@Override
	public String getText() {
		return String.valueOf(getSharedPreferences().getFloat(getKey(), 0));
	}

	@Override
	public void setText(String text) {
		getSharedPreferences().edit().putFloat(getKey(), Float.parseFloat(text)).commit();
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue)
			getEditText().setText(getText());
		else
			super.onSetInitialValue(restoreValue, defaultValue);
	}
}
