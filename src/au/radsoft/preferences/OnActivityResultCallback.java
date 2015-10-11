package au.radsoft.preferences;

import android.content.Intent;

public interface OnActivityResultCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
