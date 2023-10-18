package com.example.nutrieye;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BottomMarginPercentageDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private float marginBottomPercentage; // The percentage of the screen height

    public BottomMarginPercentageDecoration(Context context, float marginBottomPercentage) {
        this.context = context;
        this.marginBottomPercentage = marginBottomPercentage;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        if (position == itemCount - 1) {
            int screenHeight = getScreenHeight();
            int marginBottom = (int) (screenHeight * marginBottomPercentage);
            outRect.bottom = marginBottom;
        }
    }

    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowManager.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        return size.y;
    }
}
