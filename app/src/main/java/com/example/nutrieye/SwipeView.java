package com.example.nutrieye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SwipeView extends PagerAdapter {
    private final Context context;
    private final List<Integer> layoutIds;

    TextView rCaloriesVal, rCarbsVal, rFatVal, rProteinVal, rSugarVal, rFiberVal, rWaterVal, rVitAVal, rVitB1Val, rVitB2Val, rVitCVal, rCalciumVal, rSodiumVal, rIronVal, rServingSize;
    TextView sCaloriesVal, sCarbsVal, sFatVal, sProteinVal, sSugarVal, sFiberVal, sWaterVal, sVitAVal, sVitB1Val, sVitB2Val, sVitCVal, sCalciumVal, sSodiumVal, sIronVal, sServingSize;


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


