package com.example.nutrieye;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MealPlanFragment extends Fragment {

    MaterialButton allFilter;
    MaterialButton breakfastFilter;
    MaterialButton lunchFilter;
    MaterialButton dinnerFilter;

    SearchView searchView;

    RecyclerView recyclerView;
    List<Item> itemList;

    MealAdapter mealAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        breakfastFilter = view.findViewById(R.id.breakfastButton);
        lunchFilter = view.findViewById(R.id.lunchButton);
        dinnerFilter = view.findViewById(R.id.dinnerButton);
        allFilter = view.findViewById(R.id.allButton);

        searchView = view.findViewById(R.id.mealSearchView);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        BottomMarginPercentageDecoration marginDecoration = new BottomMarginPercentageDecoration(requireContext(), 0.06f);
        recyclerView.addItemDecoration(marginDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemList = new ArrayList<>();
        mealAdapter = new MealAdapter(itemList);
        recyclerView.setAdapter(mealAdapter);

        itemList.add(new Item(R.drawable.grapes, "Bitter Gourd", "Vegetable", "Lunch", "1 cup", 250, 2.5, 13, 20, 2, 2, 3, 3, 4, 5, 4, 3, 5));

        // Set the hint text color
        int hintTextColor = ContextCompat.getColor(requireContext(), R.color.grey);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(hintTextColor);

        setupRecyclerView(); // Initialize RecyclerView

        selectMealType("All"); // Set the default selection

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleQueryText(newText);
                return true;
            }

            private void handleQueryText(String query) {
                query = query.toLowerCase();

                List<Item> filteredList = new ArrayList<>();

                for (Item item : itemList) {
                    String foodName = item.getFoodName().toLowerCase();
                    // Check if the foodName starts with the query
                    if (foodName.startsWith(query)) {
                        filteredList.add(item);
                    }
                }

                updateRecyclerView(filteredList);

            }

        });


        allFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMealType("All");
            }
        });

        breakfastFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMealType("Breakfast");
            }
        });

        lunchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMealType("Lunch");
            }
        });

        dinnerFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMealType("Dinner");
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        // Create an adapter for the RecyclerView and set it
        MealAdapter mealAdapter = new MealAdapter(itemList);
        recyclerView.setAdapter(mealAdapter);

        // Add a click listener for each item in the RecyclerView
        mealAdapter.setOnItemClickListener(new MealAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                showBottomSheetDialog(item);
            }
        });
    }

    private void selectMealType(String mealType) {
        allFilter.setTypeface(null, Typeface.NORMAL);
        breakfastFilter.setTypeface(null, Typeface.NORMAL);
        lunchFilter.setTypeface(null, Typeface.NORMAL);
        dinnerFilter.setTypeface(null, Typeface.NORMAL);
        allFilter.setAlpha(0.5F);
        breakfastFilter.setAlpha(0.5F);
        lunchFilter.setAlpha(0.5F);
        dinnerFilter.setAlpha(0.5F);

        List<Item> filteredList = new ArrayList<>();

        if ("All".equals(mealType)) {
            allFilter.setTypeface(null, Typeface.BOLD);
            allFilter.setAlpha(1.0F);
            filteredList = itemList; // Show all items
        } else if ("Breakfast".equals(mealType)) {
            breakfastFilter.setTypeface(null, Typeface.BOLD);
            breakfastFilter.setAlpha(1.0F);
            // Filter items for Breakfast
            for (Item item : itemList) {
                if ("Breakfast".equals(item.getMealTime())) {
                    filteredList.add(item);
                }
            }
        } else if ("Lunch".equals(mealType)) {
            lunchFilter.setTypeface(null, Typeface.BOLD);
            lunchFilter.setAlpha(1.0F);
            // Filter items for Lunch
            for (Item item : itemList) {
                if ("Lunch".equals(item.getMealTime())) {
                    filteredList.add(item);
                }
            }
        } else if ("Dinner".equals(mealType)) {
            dinnerFilter.setTypeface(null, Typeface.BOLD);
            dinnerFilter.setAlpha(1.0F);
            // Filter items for Dinner
            for (Item item : itemList) {
                if ("Dinner".equals(item.getMealTime())) {
                    filteredList.add(item);
                }
            }
        }

        // Update the RecyclerView with the filtered data
        updateRecyclerView(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView(List<Item> filteredList) {
        MealAdapter mealAdapter = new MealAdapter(filteredList);
        recyclerView.setAdapter(mealAdapter);
        mealAdapter.setOnItemClickListener(new MealAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                showBottomSheetDialog(item);
            }
        });

        mealAdapter.notifyDataSetChanged();
    }


    private void showBottomSheetDialog(Item item) {
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.meal_summary, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentAlertDialog);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Populate the BottomSheetDialog with additional details
        TextView caloriesTextView = bottomSheetView.findViewById(R.id.mealCalories);
        TextView carbsTextView = bottomSheetView.findViewById(R.id.mealCarbohydrate);
        TextView proteinTextView = bottomSheetView.findViewById(R.id.mealProtein);
        TextView fatTextView = bottomSheetView.findViewById(R.id.mealFats);

        // Set the additional details
        caloriesTextView.setText(item.getCalories() + "g");
        carbsTextView.setText(item.getCarbs() + "g");
        proteinTextView.setText(item.getProtein() + "g");
        fatTextView.setText(item.getFat() + "g");

        // Show the additional details in the BottomSheetDialog
        caloriesTextView.setVisibility(View.VISIBLE);
        carbsTextView.setVisibility(View.VISIBLE);
        proteinTextView.setVisibility(View.VISIBLE);
        fatTextView.setVisibility(View.VISIBLE);

        bottomSheetDialog.show();
    }
}
