package com.example.nutrieye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String[] mOptions;
    private final String[] mDescriptors;

    public CustomAdapter(Context context, String[] options, String[] descriptors) {
        super(context, R.layout.custom_phyactivity_item_xml, options);
        mContext = context;
        mOptions = options;
        mDescriptors = descriptors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_phyactivity_item_xml, parent, false);
        TextView textView = view.findViewById(R.id.dropdown_text);
        textView.setText(mOptions[position] + "\n" + mDescriptors[position]);

        return view;
    }
}
