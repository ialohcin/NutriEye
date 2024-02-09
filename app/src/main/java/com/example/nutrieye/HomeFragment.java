package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private String userUID;
    String currentDate;
    private boolean isExpanded = false;
    String formattedDate;
    DayScrollDatePicker dayScrollDatePicker;
    CircularProgressIndicator caloriesProgress;
    LinearProgressIndicator carbsProgress, proteinProgress, fatsProgress, sugarProgress, fiberProgress, waterProgress, vitAProgress, vitB1Progress, vitB2Progress, vitCProgress, calciumProgress, sodiumProgress, ironProgress;
    CircleImageView profilePic;
    ImageView expandLessMore;
    TextView displayName;
    TextView sugarLabel, hSugar, hSugarLimit, fiberLabel, hFiber, hFiberLimit, waterLabel, hWater, hWaterLimit, vitALabel, hVitA, hVitALimit, vitB1Label, hVitB1, hVitb1Limit, vitB2Label, hVitB2, hVitB2Limit, vitCLabel, hVitC, hVitCLimit, calciumLabel, hCalcium, hCalciumLimit, sodiumLabel, hSodium, hSodiumLimit, ironLabel, hIron, hIronLimit;
    TextView hCalories, hCarbs, hProtein, hFats, hCaloriesLimit, hCarbsLimit, hProteinLimit, hFatsLimit;
    TextView bCarbsTotal, bProTotal, bFatTotal, bCalTotal, lCarbsTotal, lProTotal, lFatTotal, lCalTotal, dCarbsTotal, dProTotal, dFatTotal, dCalTotal;
    TextView tCaloriesTotal, tCarbohydratesTotal, tProteinTotal, tFatsTotal, tFiberTotal, tVitATotal, tVitB1Total, tVitB2Total, tVitCTotal, tCalciumTotal, tSodiumTotal, tIronTotal, tSugarTotal, tWaterTotal;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profilePic = view.findViewById(R.id.pictureProfile);
        displayName = view.findViewById(R.id.userText);

        expandLessMore = view.findViewById(R.id.expandLessMoreButton);

        //Nutrients Counter
        hCalories = view.findViewById(R.id.caloriesVal);
        hCarbs = view.findViewById(R.id.carbsVal);
        hProtein = view.findViewById(R.id.proteinVal);
        hFats = view.findViewById(R.id.fatsVal);
        hSugar = view.findViewById(R.id.sugarVal);
        hFiber = view.findViewById(R.id.fiberVal);
        hWater = view.findViewById(R.id.waterVal);
        hVitA = view.findViewById(R.id.vitAVal);
        hVitB1 = view.findViewById(R.id.vitB1Val);
        hVitB2 = view.findViewById(R.id.vitB2Val);
        hVitC = view.findViewById(R.id.vitCVal);
        hCalcium = view.findViewById(R.id.calciumVal);
        hSodium = view.findViewById(R.id.sodiumVal);
        hIron = view.findViewById(R.id.ironVal);

        sugarLabel = view.findViewById(R.id.textView15);
        fiberLabel = view.findViewById(R.id.textView16);
        waterLabel = view.findViewById(R.id.textView17);
        vitALabel = view.findViewById(R.id.textView18);
        vitB1Label = view.findViewById(R.id.textView19);
        vitB2Label = view.findViewById(R.id.textView20);
        vitCLabel = view.findViewById(R.id.textView21);
        calciumLabel = view.findViewById(R.id.textView22);
        sodiumLabel = view.findViewById(R.id.textView23);
        ironLabel = view.findViewById(R.id.textView24);

        hCaloriesLimit = view.findViewById(R.id.caloriesLimitTotal);
        hCarbsLimit = view.findViewById(R.id.carbsLimitTotal);
        hProteinLimit = view.findViewById(R.id.proteinLimitTotal);
        hSugarLimit = view.findViewById(R.id.sugarLimitTotal);
        hFatsLimit = view.findViewById(R.id.fatsLimitTotal);
        hFiberLimit = view.findViewById(R.id.fiberLimitTotal);
        hWaterLimit = view.findViewById(R.id.waterLimitTotal);
        hVitALimit = view.findViewById(R.id.vitALimitTotal);
        hVitb1Limit = view.findViewById(R.id.vitB1LimitTotal);
        hVitB2Limit = view.findViewById(R.id.vitB2LimitTotal);
        hVitCLimit = view.findViewById(R.id.vitCLimitTotal);
        hCalciumLimit = view.findViewById(R.id.calciumLimitTotal);
        hSodiumLimit = view.findViewById(R.id.sodiumLimitTotal);
        hIronLimit = view.findViewById(R.id.ironLimitTotal);

        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        carbsProgress = view.findViewById(R.id.carbsProgress);
        proteinProgress = view.findViewById(R.id.proteinProgress);
        sugarProgress = view.findViewById(R.id.sugarProgress);
        fatsProgress = view.findViewById(R.id.fatsProgress);
        fiberProgress = view.findViewById(R.id.fiberProgress);
        waterProgress = view.findViewById(R.id.waterProgress);
        vitAProgress = view.findViewById(R.id.vitAProgress);
        vitB1Progress = view.findViewById(R.id.vitB1Progress);
        vitB2Progress = view.findViewById(R.id.vitB2Progress);
        vitCProgress = view.findViewById(R.id.vitCProgress);
        calciumProgress = view.findViewById(R.id.calciumProgress);
        sodiumProgress = view.findViewById(R.id.sodiumProgress);
        ironProgress = view.findViewById(R.id.ironProgress);

        //Breakfast
        bCarbsTotal = view.findViewById(R.id.breakfastCarbsTotal);
        bProTotal = view.findViewById(R.id.breakfastProteinTotal);
        bFatTotal = view.findViewById(R.id.breakfastFatsTotal);
        bCalTotal = view.findViewById(R.id.breakfastCal);

        //Lunch
        lCarbsTotal = view.findViewById(R.id.lunchCarbsTotal);
        lProTotal = view.findViewById(R.id.lunchProteinTotal);
        lFatTotal = view.findViewById(R.id.lunchFatsTotal);
        lCalTotal = view.findViewById(R.id.lunchCal);

        //Dinner
        dCarbsTotal = view.findViewById(R.id.dinnerCarbsTotal);
        dProTotal = view.findViewById(R.id.dinnerProteinTotal);
        dFatTotal = view.findViewById(R.id.dinnerFatsTotal);
        dCalTotal = view.findViewById(R.id.dinnerCal);

        // Add a TouchListener to the ScrollView
//            scrollView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_MOVE:
//                            // Disable SwipeRefreshLayout when scrolling
//                            swipeRefreshLayout.setEnabled(false);
//                            break;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_CANCEL:
//                            // Re-enable SwipeRefreshLayout when scrolling stops
//                            swipeRefreshLayout.setEnabled(true);
//                            break;
//                    }
//                    return false;
//                }
//            });

        if (savedInstanceState == null) {

            swipeRefreshLayout = view.findViewById(R.id.home_refresh);

            ScrollView scrollView = view.findViewById(R.id.home_scroll);
            // Set up refresh listener
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadUserData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            // Set up scroll listener
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    // Check if the ScrollView is at the top
                    if (scrollView.getScrollY() == 0) {
                        // Enable SwipeRefreshLayout
                        swipeRefreshLayout.setEnabled(true);
                    } else {
                        // Disable SwipeRefreshLayout
                        swipeRefreshLayout.setEnabled(false);
                    }
                }
            });

            MaterialCardView cardView = view.findViewById(R.id.homeCardView);

            TextView dateToday = view.findViewById(R.id.dateToday);

            LocalDate today = LocalDate.now();
            formattedDate = today.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            dateToday.setText("Today, " + formattedDate);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            currentDate = dateFormat.format(calendar.getTime());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleCardView();
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showBottomSheetDialog();
                    return true;
                }
            });

            loadUserData();
            shrinkCardView();

        } else {

        }

        return view;

    }

    private void toggleCardView() {
        // Toggle between expand and shrink
        isExpanded = !isExpanded;
        if (isExpanded) {
            expandCardView();
            expandLessMore.setBackgroundResource(R.drawable.baseline_expand_less_24);
        } else {
            shrinkCardView();
            expandLessMore.setBackgroundResource(R.drawable.baseline_expand_more_24);
        }
    }

    private void shrinkCardView() {
        setViewsVisibility(View.GONE,
                sugarLabel, hSugar, hSugarLimit, sugarProgress,
                fiberLabel, hFiber, hFiberLimit, fiberProgress,
                waterLabel, hWater, hWaterLimit, waterProgress,
                vitALabel, hVitA, hVitALimit, vitAProgress,
                vitB1Label, hVitB1, hVitb1Limit, vitB1Progress,
                vitB2Label, hVitB2, hVitB2Limit, vitB2Progress,
                vitCLabel, hVitC, hVitCLimit, vitCProgress,
                calciumLabel, hCalcium, hCalciumLimit, calciumProgress,
                sodiumLabel, hSodium, hSodiumLimit, sodiumProgress,
                ironLabel, hIron, hIronLimit, ironProgress);
    }

    private void expandCardView() {
        setViewsVisibility(View.VISIBLE,
                sugarLabel, hSugar, hSugarLimit, sugarProgress,
                fiberLabel, hFiber, hFiberLimit, fiberProgress,
                waterLabel, hWater, hWaterLimit, waterProgress,
                vitALabel, hVitA, hVitALimit, vitAProgress,
                vitB1Label, hVitB1, hVitb1Limit, vitB1Progress,
                vitB2Label, hVitB2, hVitB2Limit, vitB2Progress,
                vitCLabel, hVitC, hVitCLimit, vitCProgress,
                calciumLabel, hCalcium, hCalciumLimit, calciumProgress,
                sodiumLabel, hSodium, hSodiumLimit, sodiumProgress,
                ironLabel, hIron, hIronLimit, ironProgress);
    }

    private void setViewsVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    private void loadUserData() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);
        if (userUID != null) {
            fetchUserDataFromFirebase();
            fetchUserNutrients(userUID);

            swipeRefreshLayout.setRefreshing(false);
        } else {
            // Handle case where userUID is not available
        }
    }

    // Function to extract numeric values from a string
    private double extractDoubleFromString(String input) {
        // Remove any non-numeric characters and parse the remaining string as a double
        String numericValue = input.replaceAll("[^0-9.]+", "");
        return Double.parseDouble(numericValue);
    }


    private void fetchUserNutrients(String userUID) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("Profile");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String sex = snapshot.child("sex").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    int age = calculateAgeFromDOB(dob);
                    String healthConditions = snapshot.child("healthConditions").getValue(String.class);

                    // Get the optimal nutrients for the user based on age, sex, and health conditions
                    NutrientInfo.populateOptimalNutrients();
                    NutrientInfo optimalNutrients = NutrientInfo.computeOptimalNutrients(age, sex, healthConditions);

                    if (optimalNutrients != null) {
                        // Set nutrient limits in TextViews
                        hCaloriesLimit.setText(String.valueOf("of " + optimalNutrients.calories + " kcal"));
                        hCarbsLimit.setText(String.valueOf("of " + optimalNutrients.carbohydrates));
                        hProteinLimit.setText(String.valueOf("of " + optimalNutrients.protein));
                        hSugarLimit.setText(String.valueOf("of " + optimalNutrients.sugar));
                        hFatsLimit.setText(String.valueOf("of " + optimalNutrients.fats));
                        hFiberLimit.setText(String.valueOf("of " + optimalNutrients.fiber));
                        hWaterLimit.setText(String.valueOf("of " + optimalNutrients.water));
                        hVitALimit.setText(String.valueOf("of " + optimalNutrients.vitaminA));
                        hVitb1Limit.setText(String.valueOf("of " + optimalNutrients.vitaminB1));
                        hVitB2Limit.setText(String.valueOf("of " + optimalNutrients.vitaminB2));
                        hVitCLimit.setText(String.valueOf("of " + optimalNutrients.vitaminC));
                        hCalciumLimit.setText(String.valueOf("of " + optimalNutrients.calcium));
                        hSodiumLimit.setText(String.valueOf("of " + optimalNutrients.sodium));
                        hIronLimit.setText(String.valueOf("of " + optimalNutrients.iron));

                        fetchNutrientsFromFirebase();
                    } else {
                        // Handle case where optimal nutrients are not available for the user
                        Toast.makeText(requireContext(), "Optimal nutrients not available for this user", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event if needed
            }
        });
    }

    private void fetchUserDataFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("Profile");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String image = snapshot.child("profilePhoto").getValue(String.class);
                    String sex = snapshot.child("sex").getValue(String.class);

                    displayName.setText("Hi, " + firstName);

                    // Check if the image is not null and not equal to the string "null"
                    if (image != null && !image.equals("null") && !image.isEmpty()) {
                        Picasso.get().load(image).into(profilePic, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Image loaded successfully, do nothing here
                            }

                            @Override
                            public void onError(Exception e) {
                                // Error occurred while loading image
                                // Log the error or handle it as needed
                            }
                        });
                    } else {
                        // If the image URL is empty or "null", load a default image
                        Picasso.get().load("Male".equalsIgnoreCase(sex) ? R.drawable.malepic : R.drawable.femalepic)
                                .into(profilePic);
                    }
                } else {
                    // Handle the case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    private void fetchNutrientsFromFirebase() {


        DatabaseReference nutrientsTrackingRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("NutrientsTracking").child(currentDate).child("TotalNutrients");

        nutrientsTrackingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch total nutrients for all meal types
                    fetchTotalNutrients(dataSnapshot);

                    // For each meal (Breakfast, Lunch, Dinner)
                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        String mealType = mealSnapshot.getKey();

                        if ("Breakfast".equalsIgnoreCase(mealType)) {
                            // Handle Breakfast data
                            setNutrientValuesToTextViews(mealSnapshot, bCarbsTotal, bProTotal, bFatTotal, bCalTotal);
                        } else if ("Lunch".equalsIgnoreCase(mealType)) {
                            // Handle Lunch data
                            setNutrientValuesToTextViews(mealSnapshot, lCarbsTotal, lProTotal, lFatTotal, lCalTotal);
                        } else if ("Dinner".equalsIgnoreCase(mealType)) {
                            // Handle Dinner data
                            setNutrientValuesToTextViews(mealSnapshot, dCarbsTotal, dProTotal, dFatTotal, dCalTotal);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

    }

    private double calculateNutrientValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
    }

    private void fetchTotalNutrients(DataSnapshot dataSnapshot) {
        // Initialize variables to store total nutrients
        double totalCalories = 0.0;
        double totalCarbs = 0.0;
        double totalProtein = 0.0;
        double totalSugar = 0.0;
        double totalFats = 0.0;
        double totalFiber = 0.0;
        double totalWater = 0.0;
        double totalVitA = 0.0;
        double totalVitB1 = 0.0;
        double totalVitB2 = 0.0;
        double totalVitC = 0.0;
        double totalCalcium = 0.0;
        double totalSodium = 0.0;
        double totalIron = 0.0;

        // Iterate through all meal types
        for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
            // Fetch nutrient values for each meal type
            double calories = calculateNutrientValue(getNutrientValue(mealSnapshot, "kcalTotal"));
            double carbs = calculateNutrientValue(getNutrientValue(mealSnapshot, "carbohydratesTotal"));
            double protein = calculateNutrientValue(getNutrientValue(mealSnapshot, "proteinTotal"));
            double sugar = calculateNutrientValue(getNutrientValue(mealSnapshot, "sugarTotal"));
            double fats = calculateNutrientValue(getNutrientValue(mealSnapshot, "fatsTotal"));
            double fiber = calculateNutrientValue(getNutrientValue(mealSnapshot, "fiberTotal"));
            double water = calculateNutrientValue(getNutrientValue(mealSnapshot, "waterTotal"));
            double vita = calculateNutrientValue(getNutrientValue(mealSnapshot, "vitATotal"));
            double vitb1 = calculateNutrientValue(getNutrientValue(mealSnapshot, "vitB1Total"));
            double vitb2 = calculateNutrientValue(getNutrientValue(mealSnapshot, "vitB2Total"));
            double vitc = calculateNutrientValue(getNutrientValue(mealSnapshot, "vitCTotal"));
            double calcium = calculateNutrientValue(getNutrientValue(mealSnapshot, "calciumTotal"));
            double sodium = calculateNutrientValue(getNutrientValue(mealSnapshot, "sodiumTotal"));
            double iron = calculateNutrientValue(getNutrientValue(mealSnapshot, "ironTotal"));

            // Update total nutrient values
            totalCalories += calories;
            totalCarbs += carbs;
            totalProtein += protein;
            totalFats += fats;
            totalSugar += sugar;
            totalFiber += fiber;
            totalWater += water;
            totalVitA += vita;
            totalVitB1 += vitb1;
            totalVitB2 += vitb2;
            totalVitC += vitc;
            totalCalcium += calcium;
            totalSodium += sodium;
            totalIron += iron;
        }

        // Now you have the total nutrient values, you can use them as needed
        // For example, you can update TextViews or perform other operations
        updateTotalNutrientTextViews(totalCalories, totalCarbs, totalProtein, totalFats, totalSugar, totalFiber, totalWater,
                totalVitA, totalVitB1, totalVitB2, totalVitC, totalCalcium, totalSodium, totalIron);


        // Get nutrient limits
        double caloriesLimit = extractDoubleFromString(hCaloriesLimit.getText().toString());
        double carbsLimit = extractDoubleFromString(hCarbsLimit.getText().toString());
        double proteinLimit = extractDoubleFromString(hProteinLimit.getText().toString());
        double fatsLimit = extractDoubleFromString(hFatsLimit.getText().toString());
        double sugarLimit = extractDoubleFromString(hSugarLimit.getText().toString());
        double fiberLimit = extractDoubleFromString(hFiberLimit.getText().toString());
        double waterLimit = extractDoubleFromString(hWaterLimit.getText().toString());
        double vitALimit = extractDoubleFromString(hVitALimit.getText().toString());
        double vitB1Limit = extractDoubleFromString(hVitb1Limit.getText().toString());
        double vitB2Limit = extractDoubleFromString(hVitB2Limit.getText().toString());
        double vitCLimit = extractDoubleFromString(hVitCLimit.getText().toString());
        double calciumLimit = extractDoubleFromString(hCalciumLimit.getText().toString());
        double sodiumLimit = extractDoubleFromString(hSodiumLimit.getText().toString());
        double ironLimit = extractDoubleFromString(hIronLimit.getText().toString());

        int caloriesPercentage = (int) ((totalCalories / caloriesLimit) * 100);
        int carbsPercentage = (int) ((totalCarbs / carbsLimit) * 100);
        int proteinPercentage = (int) ((totalProtein / proteinLimit) * 100);
        int sugarPercentage = (int) ((totalSugar / sugarLimit) * 100);
        int fatsPercentage = (int) ((totalFats / fatsLimit) * 100);
        int fiberPercentage = (int) ((totalFiber / fiberLimit) * 100);
        int waterPercentage = (int) ((totalWater / waterLimit) * 100);
        int vitAPercentage = (int) ((totalVitA / vitALimit) * 100);
        int vitB1Percentage = (int) ((totalVitB1 / vitB1Limit) * 100);
        int vitB2Percentage = (int) ((totalVitB2 / vitB2Limit) * 100);
        int vitCPercentage = (int) ((totalVitC / vitCLimit) * 100);
        int calciumPercentage = (int) ((totalCalcium / calciumLimit) * 100);
        int sodiumPercentage = (int) ((totalSodium / sodiumLimit) * 100);
        int ironPercentage = (int) ((totalIron / ironLimit) * 100);

        caloriesProgress.setProgress(caloriesPercentage);
        carbsProgress.setProgress(carbsPercentage);
        proteinProgress.setProgress(proteinPercentage);
        sugarProgress.setProgress(sugarPercentage);
        fatsProgress.setProgress(fatsPercentage);
        fiberProgress.setProgress(fiberPercentage);
        waterProgress.setProgress(waterPercentage);
        vitAProgress.setProgress(vitAPercentage);
        vitB1Progress.setProgress(vitB1Percentage);
        vitB2Progress.setProgress(vitB2Percentage);
        vitCProgress.setProgress(vitCPercentage);
        calciumProgress.setProgress(calciumPercentage);
        sodiumProgress.setProgress(sodiumPercentage);
        ironProgress.setProgress(ironPercentage);

    }

    private void updateTotalNutrientTextViews(double totalCalories, double totalCarbs, double totalProtein, double totalFats, double totalSugar,
                                              double totalFiber, double totalWater, double totalVitA, double totalVitB1,
                                              double totalVitB2, double totalVitC, double totalCalcium, double totalSodium,
                                              double totalIron) {
        // Update the TextViews with the total nutrient values
        hCalories.setText(String.valueOf(totalCalories));
        hCarbs.setText(String.valueOf(totalCarbs));
        hProtein.setText(String.valueOf(totalProtein));
        hSugar.setText(String.valueOf(totalSugar));
        hFats.setText(String.valueOf(totalFats));
        hFiber.setText(String.valueOf(totalFiber));
        hWater.setText(String.valueOf(totalWater));
        hVitA.setText(String.valueOf(totalVitA));
        hVitB1.setText(String.valueOf(totalVitB1));
        hVitB2.setText(String.valueOf(totalVitB2));
        hVitC.setText(String.valueOf(totalVitC));
        hCalcium.setText(String.valueOf(totalCalcium));
        hSodium.setText(String.valueOf(totalSodium));
        hIron.setText(String.valueOf(totalIron));
    }


    //Breakfast, Lunch, and Dinner
    private void setNutrientValuesToTextViews(DataSnapshot mealSnapshot, TextView carbsTextView, TextView proteinTextView, TextView fatsTextView, TextView caloriesTextView) {
        // Retrieve nutrient values
        double carbs = calculateNutrientValue(getNutrientValue(mealSnapshot, "carbohydratesTotal"));
        double protein = calculateNutrientValue(getNutrientValue(mealSnapshot, "proteinTotal"));
        double fats = calculateNutrientValue(getNutrientValue(mealSnapshot, "fatsTotal"));
        double calories = calculateNutrientValue(getNutrientValue(mealSnapshot, "kcalTotal"));

        // Set the values to the respective TextViews
        carbsTextView.setText(String.valueOf(carbs) + "g");
        proteinTextView.setText(String.valueOf(protein) + "g");
        fatsTextView.setText(String.valueOf(fats) + "g");
        caloriesTextView.setText(String.valueOf(calories) + " kcal");
    }

    private double getNutrientValue(DataSnapshot mealSnapshot, String nutrientKey) {
        if (mealSnapshot.hasChild(nutrientKey)) {
            return mealSnapshot.child(nutrientKey).getValue(Double.class);
        } else {
            return 0.0; // Return 0 if the nutrient is not present
        }
    }

    private int calculateAgeFromDOB(String dob) {
        // Parse the date string from DOB
        LocalDate dateOfBirth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in years
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    private void showBottomSheetDialog() {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_calendar, null);

        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentAlertDialog);

        // Set the content view of the dialog to your layout
        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize TextViews
        tCaloriesTotal = bottomSheetView.findViewById(R.id.caloriesTotalSummary);
        tCarbohydratesTotal = bottomSheetView.findViewById(R.id.carbohydratesTotalSummary);
        tProteinTotal = bottomSheetView.findViewById(R.id.proteinTotalSummary);
        tFatsTotal = bottomSheetView.findViewById(R.id.fatsTotalSummary);
        tFiberTotal = bottomSheetView.findViewById(R.id.fiberTotalSummary);

        tVitATotal = bottomSheetView.findViewById(R.id.vitATotalSummary);
        tVitB1Total = bottomSheetView.findViewById(R.id.vitB1TotalSummary);
        tVitB2Total = bottomSheetView.findViewById(R.id.vitB2TotalSummary);
        tVitCTotal = bottomSheetView.findViewById(R.id.vitCTotalSummary);

        tCalciumTotal = bottomSheetView.findViewById(R.id.calciumTotalSummary);
        tSodiumTotal = bottomSheetView.findViewById(R.id.sodiumTotalSummary);
        tIronTotal = bottomSheetView.findViewById(R.id.ironTotalSummary);

        tSugarTotal = bottomSheetView.findViewById(R.id.sugarTotalSummary);
        tWaterTotal = bottomSheetView.findViewById(R.id.waterTotalSummary);

        // Show the dialog
        bottomSheetDialog.show();

        // Set up date range
        LocalDate today = LocalDate.now();
        int daysToDisplay = 10; // Number of days to display (5 past, today, 4 future)
        int daysBefore = (daysToDisplay - 1) / 2; // Days before today
        int daysAfter = daysToDisplay - daysBefore - 1; // Days after today (excluding today)

        LocalDate startDate = today.minusDays(daysBefore); // Calculate start date
        LocalDate endDate = today.plusDays(daysAfter); // Calculate end date

        // Set up date picker
        dayScrollDatePicker = bottomSheetView.findViewById(R.id.dayDatePicker);
        dayScrollDatePicker.setStartDate(startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear());
        dayScrollDatePicker.setEndDate(endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());

        // Listen for date selection
        dayScrollDatePicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if (date != null) {
                    // Convert selected date to LocalDate
                    LocalDate selectedDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Format selected date to match Firebase date format (MMM d yyyy)
                    String selectedDateString = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US));

                    // Fetch nutrients for the selected date
                    fetchNutrientsForSelectedDate(selectedDateString);

                    // Fetch the "NumberOfItems" values for all meal types on the selected date
                    DatabaseReference nutrientsTrackingRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(userUID).child("NutrientsTracking").child(selectedDateString).child("TotalNutrients");

                    nutrientsTrackingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                long totalNumberOfItems = 0;

                                for (DataSnapshot mealTypeSnapshot : dataSnapshot.getChildren()) {
                                    // Iterate through all meal types under the selected date
                                    if (mealTypeSnapshot.child("NumberOfItems").exists()) {
                                        // If "NumberOfItems" exists for the current meal type, add it to the total
                                        long numberOfItems = mealTypeSnapshot.child("NumberOfItems").getValue(Long.class);
                                        totalNumberOfItems += numberOfItems;
                                    }
                                }

                                if (totalNumberOfItems > 0) {
                                    // Show a toast with the sum of "NumberOfItems"
                                    Toast.makeText(requireContext(), totalNumberOfItems + " Food item(s) consumed.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle the case where there are no food items for this date
                                    Toast.makeText(requireContext(), "There are no food items for this date", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the case where there are no food items for this date
                                Toast.makeText(requireContext(), "There are no food items for this date", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                            Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    private void fetchNutrientsForSelectedDate(String selectedDate) {
        DatabaseReference nutrientsTrackingRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("NutrientsTracking").child(selectedDate).child("TotalNutrients");

        nutrientsTrackingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch total nutrients for the selected date
                    fetchTotalNutrientsForSelectedDate(dataSnapshot);
                } else {
                    // Handle the case where there are no nutrients data for the selected date
                    // You might want to clear the TextViews or show a message to the user
                    clearNutrientTextViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    private void fetchTotalNutrientsForSelectedDate(DataSnapshot dataSnapshot) {
        // Initialize variables to store total nutrients
        double totalCalories = 0.0;
        double totalCarbs = 0.0;
        double totalProtein = 0.0;
        double totalFats = 0.0;
        double totalFiber = 0.0;

        double totalVitA = 0.0;
        double totalVitB1 = 0.0;
        double totalVitB2 = 0.0;
        double totalVitC = 0.0;

        double totalCalcium = 0.0;
        double totalSodium = 0.0;
        double totalIron = 0.0;

        double totalSugar = 0.0;
        double totalWater = 0.0;

        // Iterate through all meal types
        for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
            // Fetch nutrient values for each meal type
            double calories = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "kcalTotal"));
            double carbs = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "carbohydratesTotal"));
            double protein = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "proteinTotal"));
            double fats = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "fatsTotal"));
            double fiber = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "fiberTotal"));

            double vitA = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "vitATotal"));
            double vitB1 = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "vitB1Total"));
            double vitB2 = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "vitB2Total"));
            double vitC = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "vitCTotal"));

            double calcium = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "calciumTotal"));
            double sodium = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "sodiumTotal"));
            double iron = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "ironTotal"));

            double sugar = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "sugarTotal"));
            double water = calculateNutrientValueForSelectedDate(getNutrientValue(mealSnapshot, "waterTotal"));

            // Update total nutrient values
            totalCalories += calories;
            totalCarbs += carbs;
            totalProtein += protein;
            totalFats += fats;
            totalFiber += fiber;

            totalVitA += vitA;
            totalVitB1 += vitB1;
            totalVitB2 += vitB2;
            totalVitC += vitC;

            totalCalcium += calcium;
            totalSodium += sodium;
            totalIron += iron;

            totalSugar += sugar;
            totalWater += water;
        }

        // Now you have the total nutrient values, you can use them as needed
        // For example, you can update TextViews or perform other operations
        updateTotalNutrientTextViewsForSelectedDate(totalCalories, totalCarbs, totalProtein, totalFats,
                totalFiber, totalVitA, totalVitB1, totalVitB2, totalVitC, totalCalcium, totalSodium, totalIron,
                totalSugar, totalWater);
    }

    private double calculateNutrientValueForSelectedDate(double value) {
        // Use BigDecimal for precise rounding
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void updateTotalNutrientTextViewsForSelectedDate(double totalCalories, double totalCarbs, double totalProtein, double totalFats, double totalFiber, double totalVitA, double totalVitB1, double totalVitB2, double totalVitC, double totalCalcium, double totalSodium, double totalIron, double totalSugar, double totalWater) {
        // Update the TextViews with the total nutrient values for the selected date
        tCaloriesTotal.setText(String.valueOf(totalCalories) + " kcal");
        tCarbohydratesTotal.setText(String.valueOf(totalCarbs) + "g");
        tProteinTotal.setText(String.valueOf(totalProtein) + "g");
        tFatsTotal.setText(String.valueOf(totalFats) + "g");
        tFiberTotal.setText(String.valueOf(totalFiber) + "g");

        tVitATotal.setText(String.valueOf(totalVitA) + "mcg");
        tVitB1Total.setText(String.valueOf(totalVitB1) + "mg");
        tVitB2Total.setText(String.valueOf(totalVitB2) + "mg");
        tVitCTotal.setText(String.valueOf(totalVitC) + "mg");

        tCalciumTotal.setText(String.valueOf(totalCalcium) + "mg");
        tSodiumTotal.setText(String.valueOf(totalSodium) + "mg");
        tIronTotal.setText(String.valueOf(totalIron) + "mg");

        tSugarTotal.setText(String.valueOf(totalSugar) + "g");
        tWaterTotal.setText(String.valueOf(totalWater) + "mL");

        // You can update other nutrient TextViews here as needed
    }

    private void clearNutrientTextViews() {
        // Clear the TextViews or set them to a default state
        tCaloriesTotal.setText("0.0 kcal");
        tCarbohydratesTotal.setText("0.0g");
        tProteinTotal.setText("0.0g");
        tFatsTotal.setText("0.0g");
        tFiberTotal.setText("0.0g");
        tVitATotal.setText("0.0mcg");
        tVitB1Total.setText("0.0mg");
        tVitB2Total.setText("0.0mg");
        tVitCTotal.setText("0.0mg");
        tCalciumTotal.setText("0.0mg");
        tSodiumTotal.setText("0.0mg");
        tIronTotal.setText("0.0mg");
        tSugarTotal.setText("0.0g");
        tWaterTotal.setText("0.0mL");

        // Clear other nutrient TextViews here as needed
    }

    public void refreshContent() {
        loadUserData();
    }
}