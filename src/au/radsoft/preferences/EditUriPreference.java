package au.radsoft.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;

public class EditUriPreference extends EditTextPreference implements View.OnClickListener, OnActivityResultCallback {
    private LinearLayout mGroup;
    private Button mButton;
    private int mRequestCode = -1;

    private String mMimeType = "*/*";
    private String mPrompt = "Select file";

	public EditUriPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        
        // set android:inputType="numberUri" as default
        int inputType = attrs.getAttributeIntValue(PreferenceActivity.SCHEMA_ANDROID, PreferenceActivity.ATTR_INPUT_TYPE, -1);
        if (inputType == -1)
            getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        
        String buttonText = "...";
        
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditUriPreference, defStyle, 0);
        
        if (a.hasValue(R.styleable.EditUriPreference_buttonText))
            buttonText = a.getString(R.styleable.EditUriPreference_buttonText);
        if (a.hasValue(R.styleable.EditUriPreference_mimeType))
            mMimeType = a.getString(R.styleable.EditUriPreference_mimeType);
        if (a.hasValue(R.styleable.EditUriPreference_prompt))
            mPrompt = a.getString(R.styleable.EditUriPreference_prompt);
        
        mGroup = new LinearLayout(context, attrs);
        mGroup.setOrientation(LinearLayout.HORIZONTAL);
        
        mButton = new Button(context, attrs);
        mButton.setText(buttonText);
        mButton.setOnClickListener(this);
        mButton.setPadding(0, 0, 5, 0);
        
        PreferenceActivity activity = (PreferenceActivity) context;
        if (activity != null)
            mRequestCode = activity.setOnActivityResultCallback(this);
	}
    
	public EditUriPreference(Context context) {
		this(context, null);	
	}	

	public EditUriPreference(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextPreferenceStyle);
	}
    
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        ViewParent oldParent = mGroup.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(mGroup);
            }
            onAddGroupToDialogView(view, mGroup);
        }
    }
    
    @Override
    protected void onAddEditTextToDialogView(View dialogView, android.widget.EditText editText) {
        // Cant stop EditTextPreference from removing the editText from the parent
        // so we have to re-add it here
        mGroup.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        mGroup.addView(editText, params);
        mGroup.addView(mButton, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    
    protected void onAddGroupToDialogView(View dialogView, android.view.ViewGroup group) {
        // android.R.id.edittext_container
        int id = getContext().getResources().getSystem().getIdentifier("edittext_container", "id", "android");
        ViewGroup container = (ViewGroup) dialogView.findViewById(id);
        if (container != null) {
            container.addView(group,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override // from View.OnClickListener
    public void onClick(View view) {
        try
        {
            PreferenceActivity activity = (PreferenceActivity) getContext();
            if (activity != null)
            {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType(mMimeType);
                Intent intent = Intent.createChooser(chooseFile, mPrompt);
                activity.startActivityForResult(intent, mRequestCode);
            }
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            android.widget.Toast.makeText(getContext(), R.string.no_activity_found, android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    @Override // from OnActivityResultCallback
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PreferenceActivity.RESULT_OK) {
            Uri uri = data.getData();
            getEditText().setText(uri.toString());
        }
    }
}
