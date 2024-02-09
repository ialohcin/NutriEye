package com.example.nutrieye;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private OnItemClickListener onItemClickListener;
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_EMPTY = 1;

    public MealAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void updateData(List<Item> filteredList) {
        itemList.clear();
        itemList.addAll(filteredList);
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_NORMAL) {
            view = inflater.inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_EMPTY) {
            view = inflater.inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Item item = itemList.get(position);
            ((ViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        if (itemList.isEmpty()) {
            return 1; // Show the empty layout
        } else {
            return itemList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.isEmpty()) {
            return VIEW_TYPE_EMPTY; // Empty layout
        } else {
            return VIEW_TYPE_NORMAL; // Normal item
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealPhoto;
        private TextView mealName;
        private TextView mealCategory;
        private TextView mealServingSize;
        private TextView mealTime;
        private SwitchMaterial mealIsDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealPhoto = itemView.findViewById(R.id.mealPhoto);
            mealName = itemView.findViewById(R.id.mealName);
            mealCategory = itemView.findViewById(R.id.mealCategory);
            mealServingSize = itemView.findViewById(R.id.mealServingSize);
            mealTime = itemView.findViewById(R.id.mealTime);
            mealIsDone = itemView.findViewById(R.id.foodIndicator);

            // Disable user interaction with the SwitchMaterial
            mealIsDone.setClickable(false);
            mealIsDone.setFocusable(false);
            mealIsDone.setFocusableInTouchMode(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(itemList.get(position));
                        }
                    }

                }
            });
        }

        public void bind(Item item) {
            Picasso.get().load(item.getImageResource()).into(mealPhoto);
            mealName.setText(item.getFoodName());
            mealCategory.setText(item.getCategory());
            mealServingSize.setText(item.getServingSize());
            mealTime.setText(item.getMealTime());
            mealIsDone.setChecked(item.isDone());
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ImageView emptyImg;
        private TextView emptyText;

        public EmptyViewHolder(View view) {
            super(view);
            emptyImg = view.findViewById(R.id.empty);
            emptyText = view.findViewById(R.id.emptyText);
        }
    }
}
