/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.lenovo.parts.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import java.util.List;

import com.lenovo.parts.ProperSeekBarPreference;
import com.lenovo.parts.Utils;

public class HeadphoneGainPreference extends ProperSeekBarPreference {

    private static int mMinVal = -84;
    private static int mMaxVal = 20;
    private static int mDefVal = 0;

    private static final String HEADPHONE_GAIN_PATH = "/sys/kernel/sound_control/headphone_gain";

    public HeadphoneGainPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = 1;
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        mMinValue = mMinVal;
        mMaxValue = mMaxVal;
        mDefaultValueExists = true;
        mDefaultValue = mDefVal;
        mValue = Integer.parseInt(loadValue());

        setPersistent(false);
    }

    public static boolean isSupported() {
        return Utils.fileWritable(HEADPHONE_GAIN_PATH);
    }

    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(SoundControlSettings.KEY_HEADPHONE_GAIN, String.valueOf(mDefVal));
        Utils.writeValue(HEADPHONE_GAIN_PATH, storedValue + " " + storedValue);
    }

    public static String loadValue() {
        String storedValue = Utils.getFileValue(HEADPHONE_GAIN_PATH, String.valueOf(mDefVal));
        int value = Integer.valueOf(storedValue.substring(0, storedValue.indexOf(' ')));
        if (value >= 0 && value <= 20) {
            return String.valueOf(value);
        } else if (value >= 172 && value <= 255) {
            return String.valueOf(value - 256);
        }
        return "";
    }

    private void saveValue(String newValue) {
        Utils.writeValue(HEADPHONE_GAIN_PATH, newValue + " " + newValue);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(SoundControlSettings.KEY_HEADPHONE_GAIN, newValue);
        editor.apply();
    }

    @Override
    protected void changeValue(int newValue) {
        saveValue(String.valueOf(newValue));
    }
}

