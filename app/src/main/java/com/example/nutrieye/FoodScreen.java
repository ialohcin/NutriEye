package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FoodScreen extends AppCompatActivity {

    private boolean allGoods = false;
    private AlertDialog alertDialog;
    ProgressDialog progressDialog;
    String currentDate;
    String servingSizeText = "";
    String userUID;
    FoodData foodData;
    TextInputLayout mealPlanLayout, servingSizeLayout, quantityServingSizeLayout;
    Uri imageUri;
    ConstraintLayout recognizedLayout, standardLayout, nutrientsLayout;
    MaterialButton addMeal, editMeal;
    TextView foodNameText, foodCategory, servingSizeRecommend;
    TextView rCaloriesVal, rCarbsVal, rFatVal, rProteinVal, rSugarVal, rFiberVal, rWaterVal, rVitAVal, rVitB1Val, rVitB2Val, rVitCVal, rCalciumVal, rSodiumVal, rIronVal, rServingSize;
    TextView sCaloriesVal, sCarbsVal, sFatVal, sProteinVal, sSugarVal, sFiberVal, sWaterVal, sVitAVal, sVitB1Val, sVitB2Val, sVitCVal, sCalciumVal, sSodiumVal, sIronVal, sServingSize;
    ColorStateList def;
    TextView showRecognized, showStandard, selectNutrientsTab;
    AutoCompleteTextView mealPlanSelection;
    AutoCompleteTextView servingSizeSelection;
    TextInputEditText quantityServingSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(null);

        initComponents();

        SharedPreferences preferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);

        // Retrieve the captured image URI from the Intent extras
        String imageUriString = getIntent().getStringExtra("captured_image_uri");

        if (imageUriString != null) {
            // Convert the image URI string back to a URI
            imageUri = Uri.parse(imageUriString);

            // Find the ImageView in the layout
            ImageView imageViewFood = findViewById(R.id.foodPreview);

            // Load and display the image using the Picasso library (or another image loading library)
            Picasso.get().load(imageUri).into(imageViewFood);
        }

        editMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
                disableMealOptions();
                setupServingSizeAdapter(alertDialog,foodCategory.getText().toString(), foodNameText.getText().toString());
                servingSizeText = ServingSize.calculateServingSize(foodNameText.getText().toString(), foodCategory.getText().toString());
                servingSizeRecommend.setText(servingSizeText);

            }
        });

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allGoods){
                    String imageUriString = imageUri.toString();
                    String foodName = foodNameText.getText().toString();
                    String category = foodCategory.getText().toString();
                    String mealTime = mealPlanSelection.getText().toString();
                    String servingSize = sServingSize.getText().toString();
                    double calories = extractNumericValue(sCaloriesVal.getText().toString());

                    // Extract the numeric part from the text and then parse to double
                    double carbs = extractNumericValue(sCarbsVal.getText().toString());
                    double fats = extractNumericValue(sFatVal.getText().toString());
                    double protein = extractNumericValue(sProteinVal.getText().toString());
                    double fiber = extractNumericValue(sFiberVal.getText().toString());
                    double sugar = extractNumericValue(sSugarVal.getText().toString());
                    double water = extractNumericValue(sWaterVal.getText().toString());
                    double vitA = extractNumericValue(sVitAVal.getText().toString());
                    double vitB1 = extractNumericValue(sVitB1Val.getText().toString());
                    double vitB2 = extractNumericValue(sVitB2Val.getText().toString());
                    double vitC = extractNumericValue(sVitCVal.getText().toString());
                    double calcium = extractNumericValue(sCalciumVal.getText().toString());
                    double sodium = extractNumericValue(sSodiumVal.getText().toString());
                    double iron = extractNumericValue(sIronVal.getText().toString());

                    if (userUID != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy", Locale.US);
                        currentDate = dateFormat.format(calendar.getTime());

                        DatabaseReference mealTimeRef = userRef.child("MealPlans").child(currentDate).child(mealTime);

                        mealTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long itemCount = dataSnapshot.getChildrenCount();

                                String uniqueID = getMealItemID(mealTime, itemCount);

                                DatabaseReference mealItemRef = mealTimeRef.child(uniqueID);

                                HashMap<String, Object> mealItemMap = new HashMap<>();
                                mealItemMap.put("foodImage", imageUriString);
                                mealItemMap.put("foodName", foodName);
                                mealItemMap.put("foodCategory", category);
                                mealItemMap.put("foodMealTime", mealTime);
                                mealItemMap.put("foodServingSize", servingSize);
                                mealItemMap.put("foodCalories", calories);
                                mealItemMap.put("foodCarbs", carbs);
                                mealItemMap.put("foodFats", fats);
                                mealItemMap.put("foodProtein", protein);
                                mealItemMap.put("foodSugar", sugar);
                                mealItemMap.put("foodFiber", fiber);
                                mealItemMap.put("foodWater", water);
                                mealItemMap.put("foodVitA", vitA);
                                mealItemMap.put("foodVitB1", vitB1);
                                mealItemMap.put("foodVitB2", vitB2);
                                mealItemMap.put("foodVitC", vitC);
                                mealItemMap.put("foodCalcium", calcium);
                                mealItemMap.put("foodSodium", sodium);
                                mealItemMap.put("foodIron", iron);
                                mealItemMap.put("isDone", false);

                                // Call method to upload image to Firebase Storage
                                uploadImageToFirebaseStorage(imageUri, mealItemMap, mealItemRef);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle cancellation
                            }
                        });
                    }
                }
                else {
                    // Show an AlertDialog indicating that there are validation errors
                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodScreen.this);
                    builder.setTitle("Edit Meal Plan")
                            .setMessage("Please verify that all fields are valid and not empty.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        foodNameText.setText("Chicken Thigh");

        setNutrientValues(foodNameText.getText().toString());

        showRecognized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNutrientsTab.animate().x(0).setDuration(100);
                showRecognized.setTextColor(Color.WHITE);
                showStandard.setTextColor(def);

                recognizedLayout.setVisibility(View.VISIBLE);
                standardLayout.setVisibility(View.GONE);
            }
        });
        showStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecognized.setTextColor(def);
                showStandard.setTextColor(Color.WHITE);
                int size = showStandard.getWidth();
                selectNutrientsTab.animate().x(size).setDuration(100);

                recognizedLayout.setVisibility(View.GONE);
                standardLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    public void initComponents(){
        foodNameText = findViewById(R.id.foodName);
        foodCategory = findViewById(R.id.foodCategory);
        addMeal = findViewById(R.id.addMealPlanButton);
        editMeal = findViewById(R.id.editMealPlanButton);
        recognizedLayout = findViewById(R.id.recognizedLayout);
        standardLayout = findViewById(R.id.standardLayout);
        nutrientsLayout = findViewById(R.id.nutrientsLayout);

        selectNutrientsTab = findViewById(R.id.selectNutrientTab);
        showRecognized = findViewById(R.id.showRecognized);
        showStandard = findViewById(R.id.showStandard);

        def = (ColorStateList) showStandard.getTextColors();

        //Recognized
        rServingSize = findViewById(R.id.recognizedServingSize);
        rCaloriesVal = findViewById(R.id.recognizedCalories);
        rCarbsVal = findViewById(R.id.recognizedCarbs);
        rFatVal = findViewById(R.id.recognizedTotalFat);
        rProteinVal = findViewById(R.id.recognizedProtein);
        rSugarVal = findViewById(R.id.recognizedSugar);
        rFiberVal = findViewById(R.id.recognizedFiber);
        rWaterVal = findViewById(R.id.recognizedWater);
        rVitAVal = findViewById(R.id.recognizedVitA);
        rVitB1Val = findViewById(R.id.recognizedVitB1);
        rVitB2Val = findViewById(R.id.recognizedVitB2);
        rVitCVal = findViewById(R.id.recognizedVitC);
        rCalciumVal = findViewById(R.id.recognizedCalcium);
        rSodiumVal = findViewById(R.id.recognizedSodium);
        rIronVal = findViewById(R.id.recognizedIron);

        //Standard
        sServingSize = findViewById(R.id.standardServingSize);
        sCaloriesVal = findViewById(R.id.standardCalories);
        sCarbsVal = findViewById(R.id.standardCarbs);
        sFatVal = findViewById(R.id.standardTotalFat);
        sProteinVal = findViewById(R.id.standardProtein);
        sSugarVal = findViewById(R.id.standardSugar);
        sFiberVal = findViewById(R.id.standardFiber);
        sWaterVal = findViewById(R.id.standardWater);
        sVitAVal = findViewById(R.id.standardVitA);
        sVitB1Val = findViewById(R.id.standardVitB1);
        sVitB2Val = findViewById(R.id.standardVitB2);
        sVitCVal = findViewById(R.id.standardVitC);
        sCalciumVal = findViewById(R.id.standardCalcium);
        sSodiumVal = findViewById(R.id.standardSodium);
        sIronVal = findViewById(R.id.standardIron);

        servingSizeRecommend = findViewById(R.id.recommendedServingSize);

    }

    // Method to generate unique meal item IDs
    public static String getMealItemID(String mealTime, long itemCount) {
        String prefix = mealTime.substring(0, 1); // Get the first letter of the meal time (e.g., B, L, D)
        String suffix = String.format("%02d", itemCount); // Format the count as a two-digit number (e.g., 00, 01, ..., 99)
        return prefix + suffix;
    }

    private double extractNumericValue(String input) {
        // Check if the input string is not empty and contains a letter
        if (input != null && input.matches(".*[a-zA-Z].*")) {
            // Split the string into numeric and non-numeric parts
            String[] parts = input.split("[^0-9.]+");

            // Loop through the parts and find the numeric value
            for (String part : parts) {
                // Attempt to parse the part as a double
                try {
                    return Double.parseDouble(part);
                } catch (NumberFormatException e) {
                    // If parsing fails, continue to the next part
                    continue;
                }
            }
        }
        return 0.0; // Default value if extraction fails
    }

private void uploadImageToFirebaseStorage(Uri imageUri, HashMap<String, Object> mealItemMap, DatabaseReference mealItemRef) {
    // Create and show a ProgressDialog
    progressDialog = new ProgressDialog(FoodScreen.this);
    progressDialog.setTitle("Creating Meal Plan");
    progressDialog.setMessage("Verifying meal information. Please wait patiently..."); // Updated message
    progressDialog.setCancelable(false);
    progressDialog.show();

    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("food_images");
    final String filename = userUID + "_foodImage.jpg";

    // Call the method to upload with incremented filenames
    uploadWithIncrementedFilename(imageRef, userUID, 0, imageUri, mealItemMap, mealItemRef);
}

    private void uploadWithIncrementedFilename(StorageReference imageRef, String userUID, int index, Uri imageUri, HashMap<String, Object> mealItemMap, DatabaseReference mealItemRef) {
        final String filename = userUID + "_foodImage_" + index + ".jpg";

        imageRef.child(filename).getDownloadUrl().addOnSuccessListener(uri -> {
            // File already exists, increment the filename
            uploadWithIncrementedFilename(imageRef, userUID, index + 1, imageUri, mealItemMap, mealItemRef);
        }).addOnFailureListener(e -> {
            // File doesn't exist, upload with current filename
            imageRef.child(filename).putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get download URL
                        imageRef.child(filename).getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            // Update the mealItemMap with the Firebase Storage URL
                            mealItemMap.put("foodImage", imageUrl);

                            // Set the updated mealItemMap to the Firebase Database
                            mealItemRef.setValue(mealItemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss(); // Dismiss the ProgressDialog
                                    if (task.isSuccessful()) {
                                        // Meal item added successfully
                                        Toast.makeText(FoodScreen.this, "A Meal item has been successfully added", Toast.LENGTH_SHORT).show();

                                        // Create ActivityLogs structure
                                        DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                                        DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                                        // Get current date and time
                                        String currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());
                                        String currentDay = new SimpleDateFormat("MMM dd yyyy", Locale.US).format(Calendar.getInstance().getTime());

                                        // Generate a unique ID for the log entry
                                        String logID = "LogID_" + System.currentTimeMillis();

                                        // Create the log entry structure
                                        DatabaseReference logEntryRef = activityLogsRef.child(currentDay).child(logID);
                                        logEntryRef.child("action").setValue("Added " + foodNameText.getText().toString());
                                        logEntryRef.child("category").setValue("Meal Plan");
                                        logEntryRef.child("timestamp").setValue(currentDay + " " + currentTime);

                                        // Navigate back to NavigationScreen
                                        Intent intent = new Intent(FoodScreen.this, NavigationScreen.class);
                                        intent.putExtra("activeFragment", "MealPlanFragment");
                                        startActivity(intent);
                                        finish(); // Close the current activity after navigating back
                                    } else {
                                        Toast.makeText(FoodScreen.this, "Failed to add meal item. Please check information.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        });
                    })
                    .addOnFailureListener(e1 -> {
                        // Handle failure to upload image
                    });
        });
    }

    public void setNutrientValues(String foodName) {
        foodData = FoodDataRepository.getFoodData(foodName);

        if (foodData != null) {
            // Set standard layout values
            foodCategory.setText(String.valueOf(foodData.getCategory()));
            sServingSize.setText(foodData.getServings() + "g");
            sCaloriesVal.setText(foodData.getCalories() + "g");
            sCarbsVal.setText(foodData.getCarbohydrates() + "g");
            sFatVal.setText(foodData.getTotalFat() + "g");
            sProteinVal.setText(foodData.getProtein() + "g");
            sSugarVal.setText(foodData.getSugar() + "g");
            sFiberVal.setText(foodData.getFiber() + "g");
            sWaterVal.setText(foodData.getWater() + "g");
            sVitAVal.setText(foodData.getVitaminA() + "mcg");
            sVitB1Val.setText(foodData.getVitaminB1() + "mg");
            sVitB2Val.setText(foodData.getVitaminB2() + "mg");
            sVitCVal.setText(foodData.getVitaminC() + "mg");
            sCalciumVal.setText(foodData.getCalcium() + "mg");
            sSodiumVal.setText(foodData.getSodium() + "mg");
            sIronVal.setText(foodData.getIron() + "mg");

            // Show recognized layout and hide standard layout initially
            recognizedLayout.setVisibility(View.VISIBLE);
            standardLayout.setVisibility(View.GONE);
        }
    }

//    //Temporary
//    public void setRecValNutrientValues(String foodName) {
//        FoodData foodData = FoodDataRepository.getFoodData(foodName);
//
//        if (foodData != null) {
//            // Set recognized layout values
//            foodCategory.setText(String.valueOf(foodData.getCategory()));
//            rServingSize.setText(getIntent().getDoubleExtra("passedServingSize", 0) + "g");
//            rCaloriesVal.setText(getIntent().getDoubleExtra("passedCalories", 0) + "g");
//            rCarbsVal.setText(getIntent().getDoubleExtra("passedCarbs", 0) + "g");
//            rFatVal.setText(getIntent().getDoubleExtra("passedFat", 0) + "g");
//            rProteinVal.setText(getIntent().getDoubleExtra("passedProtein", 0) + "g");
//            rSugarVal.setText(getIntent().getDoubleExtra("passedSugar", 0) + "g");
//            rFiberVal.setText(getIntent().getDoubleExtra("passedFiber", 0) + "g");
//            rWaterVal.setText(getIntent().getDoubleExtra("passedWater", 0) + "g");
//            rVitAVal.setText(getIntent().getDoubleExtra("passedVitA", 0) + "mcg");
//            rVitB1Val.setText(getIntent().getDoubleExtra("passedVitB1", 0) + "mg");
//            rVitB2Val.setText(getIntent().getDoubleExtra("passedVitB2", 0) + "mg");
//            rVitCVal.setText(getIntent().getDoubleExtra("passedVitC", 0) + "mg");
//            rCalciumVal.setText(getIntent().getDoubleExtra("passedCalcium", 0) + "mg");
//            rSodiumVal.setText(getIntent().getDoubleExtra("passedSodium", 0) + "mg");
//            rIronVal.setText(getIntent().getDoubleExtra("passedIron", 0) + "mg");
//
//            // Show recognized layout and hide standard layout initially
//            recognizedLayout.setVisibility(View.VISIBLE);
//            standardLayout.setVisibility(View.GONE);
//        }
//    }

    // Method to disable meal options based on current time
    private void disableMealOptions() {
        List<String> mealOptions = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner"));

        // Get the current time in hours using Calendar
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format

        // Determine which meal options to disable based on the current time
        if (currentHour >= 20) { // After 8 PM, disable Dinner
            mealOptions.remove("Dinner");
        }
        if (currentHour >= 14) { // After 2 PM, disable Lunch
            mealOptions.remove("Lunch");
        }
        if (currentHour >= 10) { // After 10 AM, disable Breakfast
            mealOptions.remove("Breakfast");
        }

        // Create a new adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> mealTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealOptions);

        mealPlanSelection = alertDialog.findViewById(R.id.mealPlanSelection);
        if (mealPlanSelection != null) {
            mealPlanSelection.setAdapter(mealTimeAdapter);
        }
    }

    public void calculateNutrients(double quantity, String selectedServingSize) {
        int servingSizeValue = extractServingSize(selectedServingSize);

        // Calculate total serving size and update the text
        double totalServingSize = quantity * servingSizeValue;
        String totalServingText = String.format("%.1f", totalServingSize) + "g";

        // Extract the descriptive part of the serving size
        String descriptivePart = selectedServingSize.replaceFirst("^\\d+\\s*", ""); // Remove the initial number and space

        // Remove any numeric part followed by "g" in parentheses
        descriptivePart = descriptivePart.replaceAll("\\(\\d+\\.?\\d*g\\)", "").trim();

        // Construct the complete serving text, including descriptive parts
        String servingText;
        if (isWholeNumber(quantity)) {
            servingText = (int) quantity + " " + descriptivePart + " (" + totalServingText + ")";
        } else {
            servingText = String.format("%.1f", quantity) + " " + descriptivePart + " (" + totalServingText + ")";
        }
        sServingSize.setText(servingText); // Update the standard serving size TextView

        // Update nutrient values based on the calculated serving size
        updateNutrientValue(sCaloriesVal, calculateNutrientValue(quantity * foodData.getCalories()), "g");
        updateNutrientValue(sCarbsVal, calculateNutrientValue(quantity * foodData.getCarbohydrates()), "g");
        updateNutrientValue(sFatVal, calculateNutrientValue(quantity * foodData.getTotalFat()), "g");
        updateNutrientValue(sProteinVal, calculateNutrientValue(quantity * foodData.getProtein()), "g");
        updateNutrientValue(sSugarVal, calculateNutrientValue(quantity * foodData.getSugar()), "g");
        updateNutrientValue(sFiberVal, calculateNutrientValue(quantity * foodData.getFiber()), "g");
        updateNutrientValue(sWaterVal, calculateNutrientValue(quantity * foodData.getWater()), "g");
        updateNutrientValue(sVitAVal, calculateNutrientValue(quantity * foodData.getVitaminA()), "mcg");
        updateNutrientValue(sVitB1Val, calculateNutrientValue(quantity * foodData.getVitaminB1()), "mg");
        updateNutrientValue(sVitB2Val, calculateNutrientValue(quantity * foodData.getVitaminB2()), "mg");
        updateNutrientValue(sVitCVal, calculateNutrientValue(quantity * foodData.getVitaminC()), "mg");
        updateNutrientValue(sCalciumVal, calculateNutrientValue(quantity * foodData.getCalcium()), "mg");
        updateNutrientValue(sSodiumVal, calculateNutrientValue(quantity * foodData.getSodium()), "mg");
        updateNutrientValue(sIronVal, calculateNutrientValue(quantity * foodData.getIron()), "mg");
        // Update other nutrient values similarly
    }

    private boolean isWholeNumber(double number) {
        return number % 1 == 0;
    }

    private double calculateNutrientValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
    }

    private int extractServingSize(String str) {
    String[] parts = str.split("\\D+");
    if (parts.length > 0) {
        return Integer.parseInt(parts[parts.length - 1]); // Extract the last numeric part
    }
    return 0;
    }

    // Helper method to update nutrient values
    private void updateNutrientValue(TextView view, double value, String unit) {
        view.setText(value + unit);
    }


    private void setupServingSizeAdapter(AlertDialog alertDialogView, String foodCategory, String foodName) {
        // Find the AutoCompleteTextView in the alert dialog layout
        servingSizeSelection = alertDialogView.findViewById(R.id.servingSizeSelection);

        // Create an ArrayList to hold the serving sizes
        ArrayList<String> servingSizeList = new ArrayList<>();

        // Add serving sizes based on food category
        switch (foodCategory) {
            case "Fruits":
                servingSizeList.add("1 Piece (150g)");
                servingSizeList.add("1 Handful (100g)");
                servingSizeList.add("1 Medium-sized Fruit (150g)");
                servingSizeList.add("1 Portion (200g)");
                break;
            case "Vegetables":
                servingSizeList.add("1 Bunch (200g)");
                servingSizeList.add("1 Head (500g)");
                servingSizeList.add("1 Stalk (50g)");
                servingSizeList.add("1 Handful (50g)");
                break;
            case "Grains":
                switch (foodName) {
                    case "White Bread":
                    case "Sweet Corn":
                        servingSizeList.add("1 Slice (30g)");
                        break;
                    case "Brown Rice":
                    case "Rolled Oats":
                        servingSizeList.add("1 Cup Cooked (200g)");
                        break;
                    default:
                        servingSizeList.add("1 Bowl (250g)");
                        servingSizeList.add("1 Packet (50g)");
                        break;
                }
                break;
            case "Meat and Poultry":
                switch (foodName) {
                    case "Chicken Eggs":
                        servingSizeList.add("1 Large Egg (50g)");
                        servingSizeList.add("1 Medium Egg (25g)");
                        servingSizeList.add("1 Serving (100g)");
                        break;
                    case "Chicken Thigh":
                        servingSizeList.add("1 Serving (150g)");
                        servingSizeList.add("1 Thigh, Skinless, Big (90g)");
                        break;
                    case "Chicken Drumstick":
                        servingSizeList.add("1 Serving (150g)");
                        break;
                    default:
                        servingSizeList.add("1 Piece (100g)");
                        servingSizeList.add("1 Serving (150g)");
                        break;
                }
                break;
            case "Seafood Products":
                if (foodName.equals("Blue Crab")) {
                    servingSizeList.add("1 Crab (200g)");
                    servingSizeList.add("1 Whole (300g)");
                    servingSizeList.add("1 Portion (200g)");
                } else {
                    servingSizeList.add("1 Whole (300g)");
                    servingSizeList.add("1 Fillet (150g)");
                    servingSizeList.add("1 Portion (200g)");
                }
                break;
            case "Filipino Cooked Dishes":
                if (foodName.equals("Pork Barbeque")) {
                    servingSizeList.add("1 Skewer (100g)");
                } else {
                    servingSizeList.add("1 Plateful (300g)");
                    servingSizeList.add("1 Bowl (200g)");
                    servingSizeList.add("1 Dish (300g)");
                }

                break;
            default:
                servingSizeList.addAll(Arrays.asList("1 Cup", "1 Bowl", "1 Plate", "1 Slice"));
                break;
        }


        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> servingSizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, servingSizeList);
        servingSizeSelection.setAdapter(servingSizeAdapter);
    }

    public void showAlertDialog() {
        // Inflate the bottom sheet view
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_food_screen, null);

        mealPlanLayout = alertDialogView.findViewById(R.id.mealPlanLayout);
        servingSizeLayout = alertDialogView.findViewById(R.id.servingSizeLayout);
        quantityServingSizeLayout = alertDialogView.findViewById(R.id.quantityServingSizeLayout);

        // Find views within the pop up
        mealPlanSelection = alertDialogView.findViewById(R.id.mealPlanSelection);
        servingSizeSelection = alertDialogView.findViewById(R.id.servingSizeSelection);
        quantityServingSize = alertDialogView.findViewById(R.id.quantityServingSize);

        TextView servingSizeLabel = alertDialogView.findViewById(R.id.recommendedLabel);
        servingSizeRecommend = alertDialogView.findViewById(R.id.recommendedServingSize);

        // Set click listeners for buttons within the pop up
        Button cancelButton = alertDialogView.findViewById(R.id.cancelMeal);
        Button saveButton = alertDialogView.findViewById(R.id.saveMeal);
        ImageButton closeDialog = alertDialogView.findViewById(R.id.closeDialog);

        // Create the pop up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentAlertDialog);
        builder.setView(alertDialogView);

        alertDialog = builder.create();

        // Define a TextWatcher for Quantity field
        quantityServingSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    quantityServingSizeLayout.setError(null); // Clear error when text is entered
                }
            }
        });

        // Define a TextWatcher for Serving Size Selection field
        servingSizeSelection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    servingSizeLayout.setError(null); // Clear error when text is entered
                }
            }
        });

        // Define a TextWatcher for Meal Plan (Time) field
        mealPlanSelection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    mealPlanLayout.setError(null); // Clear error when text is entered
                }
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the bottom sheet dialog
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the bottom sheet dialog
                alertDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isQuantityValid = isQuantityValid();
                boolean isServingSizeValid = isServingSizeValid();
                boolean isMealPlanValid = isMealPlanValid();

                // Set the value of allGoods based on conditions
                allGoods = isQuantityValid && isServingSizeValid && isMealPlanValid;

                if (allGoods) {
                    // Fields are valid, proceed with calculations
                    calculateNutrients(Double.parseDouble(quantityServingSize.getText().toString()), servingSizeSelection.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();

    }

    private boolean isQuantityValid() {
        String quantity = quantityServingSize.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            quantityServingSizeLayout.setError("*Required");
            return false;
        }
        return true;
    }

    private boolean isServingSizeValid() {
        String servingSize = servingSizeSelection.getText().toString().trim();
        if (TextUtils.isEmpty(servingSize)) {
            servingSizeLayout.setError("*Required");
            return false;
        }
        return true;
    }

    private boolean isMealPlanValid() {
        String mealPlan = mealPlanSelection.getText().toString().trim();
        if (TextUtils.isEmpty(mealPlan)) {
            mealPlanLayout.setError("*Required");
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle the back button press
            navigateToCameraScreen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        navigateToCameraScreen();
    }

    private void navigateToCameraScreen() {
        Intent intent = new Intent(this, CameraScreen.class);
        startActivity(intent);
        finish();
    }

}