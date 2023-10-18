package com.example.nutrieye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

public class SwipeView extends PagerAdapter {
    private Context context;
    private List<Integer> layoutIds;

    public SwipeView(Context context, List<Integer> layoutIds) {
        this.context = context;
        this.layoutIds = layoutIds;
    }

    @Override
    public int getCount() {
        return layoutIds.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutIds.get(position), container, false);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    // New method to prevent the ViewPager from destroying the pages when swiping between them
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        for (int i = 0; i < getCount(); i++) {
            View view = container.getChildAt(i);
            view.setVisibility(View.GONE);
        }
        ((View) object).setVisibility(View.VISIBLE);
    }
}
