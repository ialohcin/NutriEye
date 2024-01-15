package com.example.nutrieye;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class MealTimeAdapter extends ArrayAdapter<String> {
    private boolean[] isEnabledArray;
    public MealTimeAdapter(@NonNull Context context, int resource, String[] objects) {
        super(context, resource);
        isEnabledArray = new boolean[objects.length];
        Arrays.fill(isEnabledArray, true); // Initially, all items are enabled
    }
    @Override
    public boolean isEnabled(int position) {
        return isEnabledArray[position];
    }

    public void disableItem(int position) {
        isEnabledArray[position] = false;
        notifyDataSetChanged();
    }
}
