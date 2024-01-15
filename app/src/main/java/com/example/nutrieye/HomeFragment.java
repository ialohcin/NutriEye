package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private String userUID;
    String formattedDate;
    DayScrollDatePicker dayScrollDatePicker;
    CircleImageView profilePic;
    TextView displayName;
    TextView hCalories, hCarbs, hProtein, hFats, hCaloriesLimit, hCarbsLimit, hProteinLimit, hFatsLimit;
    TextView bCarbsTotal, bProTotal, bFatTotal, bCalTotal, lCarbsTotal, lProTotal, lFatTotal, lCalTotal, dCarbsTotal, dProTotal, dFatTotal, dCalTotal;
    TextView tCaloriesTotal, tCarbohydratesTotal, tProteinTotal, tFatsTotal, tFiberTotal, tVitATotal, tVitB1Total,tVitB2Total, tVitCTotal, tCalciumTotal, tSodiumTotal, tIronTotal, tSugarTotal, tWaterTotal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView cardView = view.findViewById(R.id.homeCardView);

        TextView dateToday = view.findViewById(R.id.dateToday);

        LocalDate today = LocalDate.now();
        formattedDate = today.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        dateToday.setText("Today, " + formattedDate);

        profilePic = view.findViewById(R.id.pictureProfile);
        displayName = view.findViewById(R.id.userText);

        //Nutrients Counter
        hCalories = view.findViewById(R.id.caloriesVal);
        hCarbs = view.findViewById(R.id.carbsVal);
        hProtein = view.findViewById(R.id.proteinVal);
        hFats = view.findViewById(R.id.fatsVal);

        hCaloriesLimit = view.findViewById(R.id.caloriesLimitTotal);
        hCarbsLimit = view.findViewById(R.id.carbsLimitTotal);
        hProteinLimit = view.findViewById(R.id.proteinLimitTotal);
        hFatsLimit = view.findViewById(R.id.fatsLimitTotal);

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

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        loadUserData();

        return view;

    }

    private void loadUserData() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);
        if (userUID != null) {
            fetchUserDataFromFirebase();
        } else {
            // Handle case where userUID is not available
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        fetchNutrientsFromFirebase();
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

                    // Update profilePic with the new image URL
                    if (image != null && !image.isEmpty()) {
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
                        // If the image URL is empty or null, load a default image
                        Picasso.get().load("male".equalsIgnoreCase(sex) ? R.drawable.malepic : R.drawable.femalepic)
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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.US);
        String currentDate = dateFormat.format(calendar.getTime());

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

        // Iterate through all meal types
        for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
            // Fetch nutrient values for each meal type
            double calories = calculateNutrientValue(getNutrientValue(mealSnapshot, "kcalTotal"));
            double carbs = calculateNutrientValue(getNutrientValue(mealSnapshot, "carbohydratesTotal"));
            double protein = calculateNutrientValue(getNutrientValue(mealSnapshot, "proteinTotal"));
            double fats = calculateNutrientValue(getNutrientValue(mealSnapshot, "fatsTotal"));

            // Update total nutrient values
            totalCalories += calories;
            totalCarbs += carbs;
            totalProtein += protein;
            totalFats += fats;
        }

        // Now you have the total nutrient values, you can use them as needed
        // For example, you can update TextViews or perform other operations
        updateTotalNutrientTextViews(totalCalories, totalCarbs, totalProtein, totalFats);
    }

    private void updateTotalNutrientTextViews(double totalCalories, double totalCarbs, double totalProtein, double totalFats) {
        // Update the TextViews with the total nutrient values
        hCalories.setText(String.valueOf(totalCalories));
        hCarbs.setText(String.valueOf(totalCarbs));
        hProtein.setText(String.valueOf(totalProtein));
        hFats.setText(String.valueOf(totalFats));
    }

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
                    String selectedDateString = selectedDate.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.US));

                    // Fetch nutrients for the selected date
                    fetchNutrientsForSelectedDate(selectedDateString);
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
        tCaloriesTotal.setText(String.valueOf(totalCalories));
        tCarbohydratesTotal.setText(String.valueOf(totalCarbs));
        tProteinTotal.setText(String.valueOf(totalProtein));
        tFatsTotal.setText(String.valueOf(totalFats));
        tFiberTotal.setText(String.valueOf(totalFiber));

        tVitATotal.setText(String.valueOf(totalVitA));
        tVitB1Total.setText(String.valueOf(totalVitB1));
        tVitB2Total.setText(String.valueOf(totalVitB2));
        tVitCTotal.setText(String.valueOf(totalVitC));

        tCalciumTotal.setText(String.valueOf(totalCalcium));
        tSodiumTotal.setText(String.valueOf(totalSodium));
        tIronTotal.setText(String.valueOf(totalIron));

        tSugarTotal.setText(String.valueOf(totalSugar));
        tWaterTotal.setText(String.valueOf(totalWater));

        // You can update other nutrient TextViews here as needed
    }

    private void clearNutrientTextViews() {
        // Clear the TextViews or set them to a default state
        tCaloriesTotal.setText("0.0 kcal");
        tCarbohydratesTotal.setText("0.0g");
        tProteinTotal.setText("0.0g");
        tFatsTotal.setText("0.0g");
        tFiberTotal.setText("0.0g");
        tVitATotal.setText("0.0mc");
        tVitB1Total.setText("0.0mg");
        tVitB2Total.setText("0.0mg");
        tVitCTotal.setText("0.0mg");
        tCalciumTotal.setText("0.0mg");
        tSodiumTotal.setText("0.0mg");
        tIronTotal.setText("0.0mg");
        tSugarTotal.setText("0.0g");
        tWaterTotal.setText("0.0g");

        // Clear other nutrient TextViews here as needed
    }
}