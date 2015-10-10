package au.radsoft.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class EditIntegerPreference extends EditTextPreference
{
    private NumberPicker picker;
    private int min = 0;
    private int max = Integer.MAX_VALUE;
    
    public EditIntegerPreference(Context context) {
        super(context);
        init(null, 0);
    }

    public EditIntegerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EditIntegerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    
    private void init(AttributeSet attrs, int defStyle) {
        setDialogLayoutResource(R.layout.integer_preference);
        
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditIntegerPreference, defStyle, 0);
        
        if (a.hasValue(R.styleable.EditIntegerPreference_minValue))
            min = a.getInt(R.styleable.EditIntegerPreference_minValue, min);
        if (a.hasValue(R.styleable.EditIntegerPreference_maxValue))
            max = a.getInt(R.styleable.EditIntegerPreference_maxValue, max);
        
        a.recycle();
    }

    @Override
    public String getText() {
        return String.valueOf(getValue());
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        if (getSharedPreferences().contains(getKey()))
            return String.valueOf(getValue());
        else
            return super.getPersistedString(defaultReturnValue);
    }

    @Override
    public void setText(String text) {
        getSharedPreferences().edit().putInt(getKey(), Integer.parseInt(text)).commit();
    }
    
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker = (NumberPicker) view.findViewById(R.id.value);
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setValue(getValue());
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        if (positiveResult) {
            persistInt(picker.getValue());
            picker = null;
        }
    }
    
    int getValue() {
        return getSharedPreferences().getInt(getKey(), 0);
    }
}
