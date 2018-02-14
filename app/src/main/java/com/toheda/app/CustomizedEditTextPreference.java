package com.toheda.app;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by tobia on 31.01.2018.
 */

public class CustomizedEditTextPreference extends EditTextPreference {

    public CustomizedEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
