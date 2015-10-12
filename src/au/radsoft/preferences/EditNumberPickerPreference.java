/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.radsoft.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * A {@link Preference} that allows for integer
 * input.
 * <p>
 * It is a subclass of {@link DialogPreference} and shows the {@link NumberPicker}
 * in a dialog. This {@link NumberPicker} can be modified either programmatically
 * via {@link #getNumberPicker()}, or through XML by setting any NumberPicker
 * attributes on the EditNumberPickerPreference.
 * <p>
 * This preference will store a integer into the SharedPreferences.
 * <p>
 * See {@link android.R.styleable#NumberPicker NumberPicker Attributes}.
 */
public class EditNumberPickerPreference extends DialogPreference {
    /**
     * The edit value shown in the dialog.
     */
    private NumberPicker mNumberPicker;
    
    private int mValue;

    public EditNumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mNumberPicker = new NumberPicker(context, attrs);
        
        // Give it an ID so it can be saved/restored
        mNumberPicker.setId(android.R.id.edit);
        
        /*
         * The preference framework and view framework both have an 'enabled'
         * attribute. Most likely, the 'enabled' specified in this XML is for
         * the preference framework, but it was also given to the view framework.
         * We reset the enabled state.
         */
        mNumberPicker.setEnabled(true);
        
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditNumberPickerPreference, defStyle, 0);
        
        if (a.hasValue(R.styleable.EditNumberPickerPreference_minValue))
            mNumberPicker.setMinValue(a.getInt(R.styleable.EditNumberPickerPreference_minValue, 0));
        if (a.hasValue(R.styleable.EditNumberPickerPreference_maxValue))
            mNumberPicker.setMaxValue(a.getInt(R.styleable.EditNumberPickerPreference_maxValue, Integer.MAX_VALUE));
        
        a.recycle();
    }

    public EditNumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
    }

    public EditNumberPickerPreference(Context context) {
        this(context, null);
    }
    
    /**
     * Saves the value to the {@link SharedPreferences}.
     * 
     * @param value The value to save
     */
    public void setValue(int value) {
        final boolean wasBlocking = shouldDisableDependents();
        
        mValue = value;
        
        persistInt(value);
        
        final boolean isBlocking = shouldDisableDependents(); 
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }
    
    /**
     * Gets the value from the {@link SharedPreferences}.
     * 
     * @return The current preference value.
     */
    public int getValue() {
        return mValue;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        NumberPicker numberPicker = mNumberPicker;
        numberPicker.setValue(getValue());
        
        ViewParent oldParent = numberPicker.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(numberPicker);
            }
            onAddNumberPickerToDialogView(view, numberPicker);
        }
    }

    /**
     * Adds the NumberPicker widget of this preference to the dialog's view.
     * 
     * @param dialogView The dialog view.
     */
    protected void onAddNumberPickerToDialogView(View dialogView, NumberPicker numberPicker) {
        // android.R.id.edittext_container
        int id = getContext().getResources().getSystem().getIdentifier("edittext_container", "id", "android");
        ViewGroup container = (ViewGroup) dialogView.findViewById(id);
        if (container instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
            container.addView(numberPicker, params);
        }
        else if (container != null) {
            container.addView(numberPicker,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        if (positiveResult) {
            int value = mNumberPicker.getValue();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
    }

    @Override
    public boolean shouldDisableDependents() {
        return super.shouldDisableDependents();
    }

    /**
     * Returns the {@link NumberPicker} widget that will be shown in the dialog.
     * 
     * @return The {@link NumberPicker} widget that will be shown in the dialog.
     */
    public NumberPicker getNumberPicker() {
        return mNumberPicker;
    }

    /** @hide */
    //@Override
    protected boolean needInputMethod() {
        // We want the input method to show, if possible, when dialog is displayed
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }
    
    private static class SavedState extends BaseSavedState {
        int value;
        
        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
}
