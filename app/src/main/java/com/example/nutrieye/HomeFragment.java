package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
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
    LinearProgressIndicator carbsProgress, proteinProgress, fatsProgress, fiberProgress, waterProgress, vitAProgress, vitB1Progress, vitB2Progress, vitCProgress, calciumProgress, sodiumProgress, ironProgress;
    CircleImageView profilePic;
    ImageView expandLessMore;
    TextView displayName;
    TextView fiberLabel, hFiber, hFiberLimit, waterLabel, hWater, hWaterLimit, vitALabel, hVitA, hVitALimit, vitB1Label, hVitB1, hVitb1Limit, vitB2Label, hVitB2, hVitB2Limit, vitCLabel, hVitC, hVitCLimit, calciumLabel, hCalcium, hCalciumLimit, sodiumLabel, hSodium, hSodiumLimit, ironLabel, hIron, hIronLimit;
    TextView hCalories, hCarbs, hProtein, hFats, hCaloriesLimit, hCarbsLimit, hProteinLimit, hFatsLimit;
    TextView bCarbsTotal, bProTotal, bFatTotal, bCalTotal, lCarbsTotal, lProTotal, lFatTotal, lCalTotal, dCarbsTotal, dProTotal, dFatTotal, dCalTotal;
    TextView tCaloriesTotal, tCarbohydratesTotal, tProteinTotal, tFatsTotal, tFiberTotal, tVitATotal, tVitB1Total, tVitB2Total, tVitCTotal, tCalciumTotal, tSodiumTotal, tIronTotal, tWaterTotal;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView;

    HomeFragment.FragmentInteractionListener interactionListener;

    Python python;
    PyObject pyObjectDRNI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.home_refresh);

        if (savedInstanceState == null) {
            bindViews(rootView);
            refreshContent();
        }

        return rootView;

    }

    public void bindViews(View view) {
        profilePic = view.findViewById(R.id.pictureProfile);
        displayName = view.findViewById(R.id.userText);

        expandLessMore = view.findViewById(R.id.expandLessMoreButton);

        //Nutrients Counter
        hCalories = view.findViewById(R.id.caloriesVal);
        hCarbs = view.findViewById(R.id.carbsVal);
        hProtein = view.findViewById(R.id.proteinVal);
        hFats = view.findViewById(R.id.fatsVal);
        hFiber = view.findViewById(R.id.fiberVal);
        hWater = view.findViewById(R.id.waterVal);
        hVitA = view.findViewById(R.id.vitAVal);
        hVitB1 = view.findViewById(R.id.vitB1Val);
        hVitB2 = view.findViewById(R.id.vitB2Val);
        hVitC = view.findViewById(R.id.vitCVal);
        hCalcium = view.findViewById(R.id.calciumVal);
        hSodium = view.findViewById(R.id.sodiumVal);
        hIron = view.findViewById(R.id.ironVal);

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

        ScrollView scrollView = view.findViewById(R.id.home_scroll);

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
                    String dob = snapshot.child("dob").getValue(String.class);
                    int age = calculateAgeFromDOB(dob);
                    String sexString = snapshot.child("sex").getValue(String.class);
                    char sex = sexString.charAt(0); // Extract first letter

                    Double height = snapshot.child("height").getValue(Double.class);
                    Double weight = snapshot.child("weight").getValue(Double.class);
                    boolean isPregnant = false;
                    boolean isLactating = false;

                    String healthConditions = snapshot.child("healthConditions").getValue(String.class);

                    if (healthConditions != null) {
                        isPregnant = healthConditions.contains("Pregnant");
                        isLactating = healthConditions.contains("Lactating");
                    }

                    String phyActivity = snapshot.child("phyActivity").getValue(String.class);

                    // Call the Python script and retrieve the calculated values
                    if (!Python.isStarted()) {
                        Python.start(new AndroidPlatform(requireActivity()));
                    }

                    // Generate Daily Recommended Nutrient Intakes
                    python = Python.getInstance();
                    pyObjectDRNI = python.getModule("drniCalculator");

                    // Assuming age, sex, weight, height, and activityLevel are available variables in your context
                    PyObject resultDRNI = pyObjectDRNI.callAttr("calculateDRNI", age, sex, weight, height, PyObject.fromJava(isPregnant), PyObject.fromJava(isLactating), phyActivity);
                    Object[] resultArray = resultDRNI.toJava(Object[].class);

                    // Convert each element to the appropriate type
                    double dcn = (Double) resultArray[0];
                    double water = (Double) resultArray[1];
                    double carbohydrates_ll = (Double) resultArray[2];
                    double carbohydrates_ul = (Double) resultArray[3];
                    double protein_ll = (Double) resultArray[4];
                    double protein_ul = (Double) resultArray[5];
                    double fats_ll = (Double) resultArray[6];
                    double fats_ul = (Double) resultArray[7];
                    double fiber = (Double) resultArray[8];
                    double vitaminA = (Double) resultArray[9];
                    double vitaminB1 = (Double) resultArray[10];
                    double vitaminB2 = (Double) resultArray[11];
                    double vitaminC = (Double) resultArray[12];
                    double calcium = (Double) resultArray[13];
                    double sodium = (Double) resultArray[14];
                    double iron = (Double) resultArray[15];

                    // Create an instance of DailyNutrientValues with the calculated values
                    DailyNutrientValues dailyNutrientValues = new DailyNutrientValues(dcn, water, carbohydrates_ll, carbohydrates_ul, protein_ll, protein_ul,
                            fats_ll, fats_ul, fiber, vitaminA, vitaminB1, vitaminB2,
                            vitaminC, calcium, sodium, iron);

                    // Set text for TextViews using the retrieved nutrient values
                    hCaloriesLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getDcn()) + " kcal"));
                    hCarbsLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getCarbohydrates_ul()) + "g"));
                    hProteinLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getProtein_ul()) + "g"));
                    hFatsLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getFats_ul()) + "g"));
                    hFiberLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getFiber()) + "g"));
                    hWaterLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getWater()) + "mL"));
                    hVitALimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getVitaminA()) + "mcg"));
                    hVitb1Limit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getVitaminB1()) + "mg"));
                    hVitB2Limit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getVitaminB2()) + "mg"));
                    hVitCLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getVitaminC()) + " mg"));
                    hCalciumLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getCalcium()) + "mg"));
                    hSodiumLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getSodium()) + "mg"));
                    hIronLimit.setText(String.valueOf("of " + calculateNutrientValue(dailyNutrientValues.getIron()) + "mg"));

                    fetchNutrientsFromFirebase();
                } else {
                    // Handle case where optimal nutrients are not available for the user
                    Toast.makeText(requireContext(), "Optimal nutrients not available for this user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                    // Call updateBadgeVisibility method to hide badge
                    if (getActivity() instanceof NavigationScreen) {
                        ((NavigationScreen) getActivity()).updateBadgeVisibility(false, "HomeFragment");
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
        updateTotalNutrientTextViews(totalCalories, totalCarbs, totalProtein, totalFats, totalFiber, totalWater,
                totalVitA, totalVitB1, totalVitB2, totalVitC, totalCalcium, totalSodium, totalIron);


        // Get nutrient limits
        double caloriesLimit = extractDoubleFromString(hCaloriesLimit.getText().toString());
        double carbsLimit = extractDoubleFromString(hCarbsLimit.getText().toString());
        double proteinLimit = extractDoubleFromString(hProteinLimit.getText().toString());
        double fatsLimit = extractDoubleFromString(hFatsLimit.getText().toString());
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

    private void updateTotalNutrientTextViews(double totalCalories, double totalCarbs, double totalProtein, double totalFats,
                                              double totalFiber, double totalWater, double totalVitA, double totalVitB1,
                                              double totalVitB2, double totalVitC, double totalCalcium, double totalSodium,
                                              double totalIron) {
        // Update the TextViews with the total nutrient values
        hCalories.setText(String.valueOf(calculateNutrientValue(totalCalories)));
        hCarbs.setText(String.valueOf(calculateNutrientValue(totalCarbs)));
        hProtein.setText(String.valueOf(calculateNutrientValue(totalProtein)));
        hFats.setText(String.valueOf(calculateNutrientValue(totalFats)));
        hFiber.setText(String.valueOf(calculateNutrientValue(totalFiber)));
        hWater.setText(String.valueOf(calculateNutrientValue(totalWater)));
        hVitA.setText(String.valueOf(calculateNutrientValue(totalVitA)));
        hVitB1.setText(String.valueOf(calculateNutrientValue(totalVitB1)));
        hVitB2.setText(String.valueOf(calculateNutrientValue(totalVitB2)));
        hVitC.setText(String.valueOf(calculateNutrientValue(totalVitC)));
        hCalcium.setText(String.valueOf(calculateNutrientValue(totalCalcium)));
        hSodium.setText(String.valueOf(calculateNutrientValue(totalSodium)));
        hIron.setText(String.valueOf(calculateNutrientValue(totalIron)));
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

            totalWater += water;
        }

        // Now you have the total nutrient values, you can use them as needed
        // For example, you can update TextViews or perform other operations
        updateTotalNutrientTextViewsForSelectedDate(totalCalories, totalCarbs, totalProtein, totalFats,
                totalFiber, totalVitA, totalVitB1, totalVitB2, totalVitC, totalCalcium, totalSodium, totalIron,
                totalWater);
    }

    private double calculateNutrientValueForSelectedDate(double value) {
        // Use BigDecimal for precise rounding
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void updateTotalNutrientTextViewsForSelectedDate(double totalCalories, double totalCarbs, double totalProtein, double totalFats, double totalFiber, double totalVitA, double totalVitB1, double totalVitB2, double totalVitC, double totalCalcium, double totalSodium, double totalIron, double totalWater) {
        // Update the TextViews with the total nutrient values for the selected date
        tCaloriesTotal.setText(String.valueOf(calculateNutrientValue(totalCalories)) + " kcal");
        tCarbohydratesTotal.setText(String.valueOf(calculateNutrientValue(totalCarbs)) + "g");
        tProteinTotal.setText(String.valueOf(calculateNutrientValue(totalProtein)) + "g");
        tFatsTotal.setText(String.valueOf(calculateNutrientValue(totalFats)) + "g");
        tFiberTotal.setText(String.valueOf(calculateNutrientValue(totalFiber)) + "g");

        tVitATotal.setText(String.valueOf(calculateNutrientValue(totalVitA)) + "mcg");
        tVitB1Total.setText(String.valueOf(calculateNutrientValue(totalVitB1)) + "mg");
        tVitB2Total.setText(String.valueOf(calculateNutrientValue(totalVitB2)) + "mg");
        tVitCTotal.setText(String.valueOf(calculateNutrientValue(totalVitC)) + "mg");

        tCalciumTotal.setText(String.valueOf(calculateNutrientValue(totalCalcium)) + "mg");
        tSodiumTotal.setText(String.valueOf(calculateNutrientValue(totalSodium)) + "mg");
        tIronTotal.setText(String.valueOf(calculateNutrientValue(totalIron)) + "mg");

        tWaterTotal.setText(String.valueOf(calculateNutrientValue(totalWater)) + "mL");

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
        tWaterTotal.setText("0.0mL");

        // Clear other nutrient TextViews here as needed
    }

    public void refreshContent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interactionListener != null) {
                            loadUserData();
                        }
                        swipeRefreshLayout.setRefreshing(false);

                        // Call updateBadgeVisibility method to hide badge
                        if (getActivity() instanceof NavigationScreen) {
                            ((NavigationScreen) getActivity()).updateBadgeVisibility(false, "HomeFragment");
                        }
                    }
                }, 2000);
            }
        });
    }

    public interface FragmentInteractionListener {
        void replaceOrPopFragment(Fragment fragment, boolean triggeredBySwipeRefresh);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            interactionListener = (HomeFragment.FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }
}