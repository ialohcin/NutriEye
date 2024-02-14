package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealPlanFragment extends Fragment {
    String userUID;
    MaterialButton allFilter;
    MaterialButton breakfastFilter;
    MaterialButton lunchFilter;
    MaterialButton dinnerFilter;
    SearchView searchView;
    RecyclerView recyclerView;
    List<Item> itemList;
    MealAdapter mealAdapter;
    String currentDate;
    SwipeRefreshLayout swipeRefreshLayout;
    NavigationScreen navigationScreen;
    MealPlanFragment.FragmentInteractionListener interactionListener;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        // Initialize navigationScreen
        navigationScreen = (NavigationScreen) requireActivity();
        swipeRefreshLayout = rootView.findViewById(R.id.meal_plan_refresh);

        if (savedInstanceState == null){
            bindViews(rootView);
            refreshContent();
        }

        return rootView;
    }

    public void bindViews (View view){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        currentDate = dateFormat.format(calendar.getTime());

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callbackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set the hint text color
        int hintTextColor = ContextCompat.getColor(requireContext(), R.color.gray);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(hintTextColor);

        setupRecyclerView(); // Initialize RecyclerView

        selectMealType("All");

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

        loadUserData();
    }

    //Fetching userUID
    private void loadUserData() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);
        if (userUID != null) {
            fetchMealPlanDataFromFirebase();
        } else {
            // Handle case where userUID is not available
        }
    }

    private void fetchMealPlanDataFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        DatabaseReference mealPlanRef = userRef.child("MealPlans").child(currentDate);

        final int MAX_SERVING_SIZE_LENGTH = 10; // Maximum length for foodServingSize before truncation

        mealPlanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Clear the existing list
                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    long itemCount = 0;
                    for (DataSnapshot itemSnapshot : mealSnapshot.getChildren()) {
                        // Extract data and create Item objects
                        String foodImage = itemSnapshot.child("foodImage").getValue(String.class);
                        String foodName = itemSnapshot.child("foodName").getValue(String.class);
                        String foodCategory = itemSnapshot.child("foodCategory").getValue(String.class);
                        String foodServingSize = itemSnapshot.child("foodServingSize").getValue(String.class);
//
//                        // Truncate foodServingSize if it exceeds a certain length
//                        if (foodServingSize != null && foodServingSize.length() > MAX_SERVING_SIZE_LENGTH) {
//                            foodServingSize = foodServingSize.substring(0, MAX_SERVING_SIZE_LENGTH - 3) + "...";
//                        }

                        String mealTime = itemSnapshot.child("foodMealTime").getValue(String.class);
                        Double foodCalories = itemSnapshot.child("foodCalories").getValue(Double.class);
                        Double foodCarbs = itemSnapshot.child("foodCarbs").getValue(Double.class);
                        Double foodFats = itemSnapshot.child("foodFats").getValue(Double.class);
                        Double foodProtein = itemSnapshot.child("foodProtein").getValue(Double.class);
                        Double foodFiber = itemSnapshot.child("foodFiber").getValue(Double.class);
                        Double foodWater = itemSnapshot.child("foodWater").getValue(Double.class);
                        Double foodVitA = itemSnapshot.child("foodVitA").getValue(Double.class);
                        Double foodVitB1 = itemSnapshot.child("foodVitB1").getValue(Double.class);
                        Double foodVitB2 = itemSnapshot.child("foodVitB2").getValue(Double.class);
                        Double foodVitC = itemSnapshot.child("foodVitC").getValue(Double.class);
                        Double foodCalcium = itemSnapshot.child("foodCalcium").getValue(Double.class);
                        Double foodSodium = itemSnapshot.child("foodSodium").getValue(Double.class);
                        Double foodIron = itemSnapshot.child("foodIron").getValue(Double.class);
                        boolean foodIsDone = itemSnapshot.child("isDone").getValue(Boolean.class);

                        // Update the Item object with the fetched isDone status
                        Item item = new Item(foodImage, foodName, foodCategory, mealTime, foodServingSize, foodCalories,
                                foodCarbs, foodFats, foodProtein, foodFiber,
                                foodWater, foodVitA, foodVitB1, foodVitB2, foodVitC, foodCalcium, foodSodium, foodIron, foodIsDone, itemCount);

                        itemList.add(item);

                        // Increment itemCount for the next item
                        itemCount++;
                    }
                }
                // Update the RecyclerView with the fetched data
                updateRecyclerView(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    private void updateTotalNutrientsInFirebase(String currentDate, Item item) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        DatabaseReference nutrientsTrackingRef = userRef.child("NutrientsTracking").child(currentDate);
        DatabaseReference mealPlansRef = userRef.child("MealPlans").child(currentDate).child(item.getMealTime());

        // Use a Map to store the nutrient values
        Map<String, Object> nutrientValues = new HashMap<>();
        nutrientValues.put("kcalTotal", ServerValue.increment(item.getCalories()));
        nutrientValues.put("proteinTotal", ServerValue.increment(item.getProtein()));
        nutrientValues.put("fatsTotal", ServerValue.increment(item.getFat()));
        nutrientValues.put("carbohydratesTotal", ServerValue.increment(item.getCarbs()));
        nutrientValues.put("fiberTotal", ServerValue.increment(item.getFiber()));
        nutrientValues.put("waterTotal", ServerValue.increment(item.getWater()));
        nutrientValues.put("vitATotal", ServerValue.increment(item.getVitaminA()));
        nutrientValues.put("vitB1Total", ServerValue.increment(item.getVitaminB1()));
        nutrientValues.put("vitB2Total", ServerValue.increment(item.getVitaminB2()));
        nutrientValues.put("vitCTotal", ServerValue.increment(item.getVitaminC()));
        nutrientValues.put("calciumTotal", ServerValue.increment(item.getCalcium()));
        nutrientValues.put("sodiumTotal", ServerValue.increment(item.getSodium()));
        nutrientValues.put("ironTotal", ServerValue.increment(item.getIron()));

        // Increment NumberOfItems or NumberOfFoods attribute
        nutrientValues.put("NumberOfItems", ServerValue.increment(1));  // Increment by 1 each time a food item is consumed

        // Use updateChildren to create the structure if not exists and update the values atomically
        nutrientsTrackingRef.child("TotalNutrients").child(item.getMealTime()).updateChildren(nutrientValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully updated or created the structure

                        // Fetch the updated data
                        fetchNutrientsForSelectedDate(currentDate, item.getMealTime());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("FirebaseUpdate", "Error updating TotalNutrients", e);
                    }
                });

        // Update isDone for the specific item in MealPlans
        String mealItemID = FoodScreen.getMealItemID(item.getMealTime(), item.getItemCount());
        mealPlansRef.child(mealItemID).child("isDone").setValue(true);
    }

    private void fetchNutrientsForSelectedDate(String currentDate, String mealTime) {
        DatabaseReference nutrientsTrackingRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("NutrientsTracking").child(currentDate).child("TotalNutrients").child(mealTime);

        nutrientsTrackingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch and update total nutrients for the selected date and meal time
                    fetchAndRoundTotalNutrients(dataSnapshot);
                } else {
                    // Handle the case where there are no nutrients data for the selected date and meal time
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    private void fetchAndRoundTotalNutrients(DataSnapshot dataSnapshot) {
        double calories = calculateNutrientValue(getNutrientValue(dataSnapshot, "kcalTotal"));
        double carbs = calculateNutrientValue(getNutrientValue(dataSnapshot, "carbohydratesTotal"));
        double protein = calculateNutrientValue(getNutrientValue(dataSnapshot, "proteinTotal"));
        double fats = calculateNutrientValue(getNutrientValue(dataSnapshot, "fatsTotal"));
        double fiber = calculateNutrientValue(getNutrientValue(dataSnapshot, "fiberTotal"));

        double vitA = calculateNutrientValue(getNutrientValue(dataSnapshot, "vitATotal"));
        double vitB1 = calculateNutrientValue(getNutrientValue(dataSnapshot, "vitB1Total"));
        double vitB2 = calculateNutrientValue(getNutrientValue(dataSnapshot, "vitB2Total"));
        double vitC = calculateNutrientValue(getNutrientValue(dataSnapshot, "vitCTotal"));

        double calcium = calculateNutrientValue(getNutrientValue(dataSnapshot, "calciumTotal"));
        double sodium = calculateNutrientValue(getNutrientValue(dataSnapshot, "sodiumTotal"));
        double iron = calculateNutrientValue(getNutrientValue(dataSnapshot, "ironTotal"));

        double water = calculateNutrientValue(getNutrientValue(dataSnapshot, "waterTotal"));

    }

    private double calculateNutrientValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
    }

    private double getNutrientValue(DataSnapshot dataSnapshot, String nutrientKey) {
        // Get the nutrient value from the DataSnapshot
        // Make sure to handle null or missing values appropriately
        if (dataSnapshot.hasChild(nutrientKey)) {
            return dataSnapshot.child(nutrientKey).getValue(Double.class);
        } else {
            return 0.0; // Default value or handle accordingly
        }
    }

    ItemTouchHelper.SimpleCallback callbackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    if (!itemList.get(position).isDone()) {
                        showDiscardMealDialog(itemList.get(position), position);
                    }
                    break;
                case ItemTouchHelper.RIGHT:
                    if (!itemList.get(position).isDone()) {
                        showConsumeMealDialog(itemList.get(position), position);
                    }
                    break;

            }
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && position != RecyclerView.NO_POSITION) {
                int viewType = recyclerView.getAdapter().getItemViewType(position);

                if (viewType == MealAdapter.VIEW_TYPE_NORMAL) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        int labelMargin = 16;
                        Paint paint = new Paint();

                        if (dX > 0) { // Swiping right (mark as consumed)
                            paint.setColor(Color.GREEN);
                            c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), viewHolder.itemView.getLeft() + dX, viewHolder.itemView.getBottom(), paint);

                            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_check_circle_24);
                            if (icon != null) {
                                int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                                int iconTop = viewHolder.itemView.getTop() + iconMargin;
                                int iconBottom = iconTop + icon.getIntrinsicHeight();
                                int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
                                int iconRight = iconLeft + icon.getIntrinsicWidth();

                                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                                icon.draw(c);

                                paint.setColor(Color.WHITE);
                                paint.setTextSize(40);
                                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                String swipeLabel = "Consume Meal";
                                Rect bounds = new Rect();
                                paint.getTextBounds(swipeLabel, 0, swipeLabel.length(), bounds);
                                float textHeight = bounds.height();
                                float labelX = viewHolder.itemView.getLeft() + iconRight + labelMargin;
                                float labelY = viewHolder.itemView.getTop() + viewHolder.itemView.getHeight() / 2 + textHeight / 2;
                                c.drawText(swipeLabel, labelX, labelY, paint);
                            }
                        } else if (dX < 0) { // Swiping left (delete)
                            paint.setColor(Color.RED);
                            c.drawRect(viewHolder.itemView.getRight() + dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), paint);

                            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24);
                            if (icon != null) {
                                int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                                int iconTop = viewHolder.itemView.getTop() + iconMargin;
                                int iconBottom = iconTop + icon.getIntrinsicHeight();
                                int iconLeft = viewHolder.itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                                int iconRight = viewHolder.itemView.getRight() - iconMargin;

                                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                                icon.draw(c);

                                paint.setColor(Color.WHITE);
                                paint.setTextSize(40);
                                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                String swipeLabel = "Delete Meal";
                                Rect bounds = new Rect();
                                paint.getTextBounds(swipeLabel, 0, swipeLabel.length(), bounds);
                                float textWidth = bounds.width();
                                float textHeight = bounds.height();
                                float labelX = viewHolder.itemView.getRight() - iconMargin - icon.getIntrinsicWidth() - textWidth - labelMargin;
                                float labelY = viewHolder.itemView.getTop() + viewHolder.itemView.getHeight() / 2 + textHeight / 2;
                                c.drawText(swipeLabel, labelX, labelY, paint);
                            }
                        }
                    }
                } else if (viewType == MealAdapter.VIEW_TYPE_EMPTY) {
                    //Do nothing for EmptyViewHolder
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            //working
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                int viewType = recyclerView.getAdapter().getItemViewType(position);
                if (viewType == MealAdapter.VIEW_TYPE_NORMAL) {
                    Item item = itemList.get(position);
                    // Allow swipe only if isDone is false
                    return item.isDone() ? 0 : super.getSwipeDirs(recyclerView, viewHolder);
                }
            }
            // Disable swipe for EmptyView
            return 0;
        }

    };

    private void showDiscardMealDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Discard Meal");
        builder.setMessage("Are you sure you want to discard this meal?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the item from Firebase
                removeFromFirebase(item, position);

                // Remove the item from itemList
                itemList.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);

                // Create ActivityLogs structure
                DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                // Get current time
                String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());

                // Generate a unique ID for the log entry
                String logID = "LogID_" + System.currentTimeMillis();

                // Create the log entry structure
                DatabaseReference logEntryRef = activityLogsRef.child(currentDate).child(logID);
                logEntryRef.child("action").setValue("Discarded " + item.getFoodName());
                logEntryRef.child("category").setValue("Meal Plan");
                logEntryRef.child("timestamp").setValue(currentTime);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No", reset the item's position
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        });
        builder.show();
    }

    private void showConsumeMealDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Consume Meal");
        builder.setMessage("Are you sure you want to consume this meal?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Set the item as consumed
                itemList.get(position).setDone(true);
                recyclerView.getAdapter().notifyItemChanged(position);

                // Call the method to update total nutrients in Firebase
                updateTotalNutrientsInFirebase(currentDate, itemList.get(position));

                // Create ActivityLogs structure
                DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                // Get current time
                String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());

                // Generate a unique ID for the log entry
                String logID = "LogID_" + System.currentTimeMillis();

                // Create the log entry structure
                DatabaseReference logEntryRef = activityLogsRef.child(currentDate).child(logID);
                logEntryRef.child("action").setValue("Consumed " + item.getFoodName());
                logEntryRef.child("category").setValue("Meal Plan");
                logEntryRef.child("timestamp").setValue(currentTime);

                // Check if refresh is needed for any fragment and show/hide the badge accordingly
                navigationScreen.updateBadgeVisibility(true, "HomeFragment");

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No", reset the item's position
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        });
        builder.show();
    }

    private void removeFromFirebase(Item item, int position) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        DatabaseReference mealPlansRef = userRef.child("MealPlans").child(currentDate).child(item.getMealTime());

        String mealItemID = FoodScreen.getMealItemID(item.getMealTime(), item.getItemCount());

        // Remove the item from Firebase
        mealPlansRef.child(mealItemID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully removed from Firebase

                        // Create ActivityLogs structure
                        DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                        DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                        // Get current date and time
                        String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());

                        // Generate a unique ID for the log entry
                        String logID = "LogID_" + System.currentTimeMillis();

                        // Create the log entry structure
                        DatabaseReference logEntryRef = activityLogsRef.child(currentDate).child(logID);
                        logEntryRef.child("action").setValue("Discarded " + item.getFoodName());
                        logEntryRef.child("category").setValue("Meal Plan");
                        logEntryRef.child("timestamp").setValue(currentTime);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("FirebaseRemove", "Error removing item from Firebase", e);
                    }
                });
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

        // Update the UI for buttons regardless of whether itemList is empty or not
        switch (mealType) {
            case "All":
                allFilter.setTypeface(null, Typeface.BOLD);
                allFilter.setAlpha(1.0F);
                break;
            case "Breakfast":
                breakfastFilter.setTypeface(null, Typeface.BOLD);
                breakfastFilter.setAlpha(1.0F);
                break;
            case "Lunch":
                lunchFilter.setTypeface(null, Typeface.BOLD);
                lunchFilter.setAlpha(1.0F);
                break;
            case "Dinner":
                dinnerFilter.setTypeface(null, Typeface.BOLD);
                dinnerFilter.setAlpha(1.0F);
                break;
        }

        if (itemList.isEmpty()) {
            // Handle case where itemList is empty, e.g., show a message or load data
            return;
        }

        List<Item> filteredList = new ArrayList<>();

        for (Item item : itemList) {
            switch (mealType) {
                case "All":
                    filteredList.add(item);
                    break;
                case "Breakfast":
                    if ("Breakfast".equalsIgnoreCase(item.getMealTime())) {
                        filteredList.add(item);
                    }
                    break;
                case "Lunch":
                    if ("Lunch".equalsIgnoreCase(item.getMealTime())) {
                        filteredList.add(item);
                    }
                    break;
                case "Dinner":
                    if ("Dinner".equalsIgnoreCase(item.getMealTime())) {
                        filteredList.add(item);
                    }
                    break;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callbackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void showBottomSheetDialog(Item item) {
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.meal_summary, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentAlertDialog);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Populate the BottomSheetDialog with additional details
        TextView foodServingSize = bottomSheetView.findViewById(R.id.textServingSize);

        TextView caloriesTextView = bottomSheetView.findViewById(R.id.mealCalories);
        TextView carbsTextView = bottomSheetView.findViewById(R.id.mealCarbohydrate);
        TextView proteinTextView = bottomSheetView.findViewById(R.id.mealProtein);
        TextView fatTextView = bottomSheetView.findViewById(R.id.mealFats);
        TextView fiberTextView = bottomSheetView.findViewById(R.id.mealFiber);

        TextView vitATextView = bottomSheetView.findViewById(R.id.mealVitA);
        TextView vitB1TextView = bottomSheetView.findViewById(R.id.mealVitB1);
        TextView vitB2TextView = bottomSheetView.findViewById(R.id.mealVitB2);
        TextView vitCTextView = bottomSheetView.findViewById(R.id.mealVitC);

        TextView calciumTextView = bottomSheetView.findViewById(R.id.mealCalcium);
        TextView sodiumTextView = bottomSheetView.findViewById(R.id.mealSodium);
        TextView ironTextView = bottomSheetView.findViewById(R.id.mealIron);

        TextView waterTextView = bottomSheetView.findViewById(R.id.mealWater);

        // Set the additional details
        foodServingSize.setText(item.getServingSize());
        caloriesTextView.setText(item.getCalories() + "kcal");
        carbsTextView.setText(item.getCarbs() + "g");
        proteinTextView.setText(item.getProtein() + "g");
        fatTextView.setText(item.getFat() + "g");
        fiberTextView.setText(item.getFiber() + "g");

        vitATextView.setText(item.getVitaminA() + "mcg");
        vitB1TextView.setText(item.getVitaminB1() + "mg");
        vitB2TextView.setText(item.getVitaminB2() + "mg");
        vitCTextView.setText(item.getVitaminC() + "mg");

        calciumTextView.setText(item.getCalcium() + "mg");
        sodiumTextView.setText(item.getSodium() + "mg");
        ironTextView.setText(item.getIron() + "mg");

        waterTextView.setText(item.getWater() + "g");

        // Show the additional details in the BottomSheetDialog
        foodServingSize.setVisibility(View.VISIBLE);
        caloriesTextView.setVisibility(View.VISIBLE);
        carbsTextView.setVisibility(View.VISIBLE);
        proteinTextView.setVisibility(View.VISIBLE);
        fatTextView.setVisibility(View.VISIBLE);
        fiberTextView.setVisibility(View.VISIBLE);

        vitATextView.setVisibility(View.VISIBLE);
        vitB1TextView.setVisibility(View.VISIBLE);
        vitB2TextView.setVisibility(View.VISIBLE);
        vitCTextView.setVisibility(View.VISIBLE);

        calciumTextView.setVisibility(View.VISIBLE);
        sodiumTextView.setVisibility(View.VISIBLE);
        ironTextView.setVisibility(View.VISIBLE);

        waterTextView.setVisibility(View.VISIBLE);

        bottomSheetDialog.show();
    }

    public void refreshContent(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interactionListener != null){
                    loadUserData();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        },2000);
    }

    public interface FragmentInteractionListener {
        void replaceOrPopFragment(Fragment fragment, boolean triggeredBySwipeRefresh);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            interactionListener = (MealPlanFragment.FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }
}
