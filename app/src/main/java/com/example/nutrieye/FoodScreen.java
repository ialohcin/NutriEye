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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodScreen extends AppCompatActivity {

    private boolean allGoods = false;
    private AlertDialog alertDialog;
    ProgressDialog progressDialog;
    String currentDate;
    String servingSizeText = "";
    String rServingSizeTemp = "";
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
                servingSizeText = ServingSize.calculateServingSize(foodNameText.getText().toString(), foodCategory.getText().toString());
                servingSizeRecommend.setText(servingSizeText);
                setupServingSizeAdapter(alertDialog, foodCategory.getText().toString(), foodNameText.getText().toString());

            }
        });

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allGoods) {
                    String imageUriString = imageUri.toString();
                    String foodName = foodNameText.getText().toString();
                    String category = foodCategory.getText().toString();
                    String mealTime = mealPlanSelection.getText().toString();
                    String servingSize = rServingSize.getText().toString();
                    double calories = extractNumericValue(rCaloriesVal.getText().toString());

                    // Extract the numeric part from the text and then parse to double
                    double carbs = extractNumericValue(rCarbsVal.getText().toString());
                    double fats = extractNumericValue(rFatVal.getText().toString());
                    double protein = extractNumericValue(rProteinVal.getText().toString());
                    double fiber = extractNumericValue(rFiberVal.getText().toString());
                    double sugar = extractNumericValue(rSugarVal.getText().toString());
                    double water = extractNumericValue(rWaterVal.getText().toString());
                    double vitA = extractNumericValue(rVitAVal.getText().toString());
                    double vitB1 = extractNumericValue(rVitB1Val.getText().toString());
                    double vitB2 = extractNumericValue(rVitB2Val.getText().toString());
                    double vitC = extractNumericValue(rVitCVal.getText().toString());
                    double calcium = extractNumericValue(rCalciumVal.getText().toString());
                    double sodium = extractNumericValue(rSodiumVal.getText().toString());
                    double iron = extractNumericValue(rIronVal.getText().toString());

                    if (userUID != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
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
                } else {
                    // Show an AlertDialog indicating that there are validation errors
                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodScreen.this);
                    builder.setTitle("Edit Meal Plan").setMessage("Please verify that all fields are valid and not empty.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });

        foodNameText.setText("Mandarin Orange");

        setNutrientValues(foodNameText.getText().toString());

        showAllergensDialog(foodNameText.getText().toString());

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

    public void initComponents() {
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

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        currentDate = dateFormat.format(calendar.getTime());

    }

    private void showAllergensDialog(String foodName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Profile").child("foodAllergens");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userAllergensString = dataSnapshot.getValue(String.class);
                if (userAllergensString != null && !userAllergensString.isEmpty()) {
                    List<String> userAllergens = Arrays.asList(userAllergensString.split(",\\s*"));

                    Map<String, List<String>> foodAllergensMap = getFoodAllergensMap();
                    if (foodAllergensMap.containsKey(foodName)) {
                        List<String> allergensInFood = foodAllergensMap.get(foodName);
                        List<String> matchingAllergens = new ArrayList<>();
                        for (String allergen : allergensInFood) {
                            if (userAllergens.contains(allergen)) {
                                matchingAllergens.add(allergen);
                            }
                        }

                        if (!matchingAllergens.isEmpty()) {
                            StringBuilder message = new StringBuilder("The selected food item may contain the following allergens:\n");
                            for (String allergen : matchingAllergens) {
                                message.append("\n- ").append(allergen);
                                // Additional descriptions for specific allergens (if needed)
                                if (foodName.equals("Chicken Adobo") && allergen.equals("Wheat")) {
                                    message.append("\n  (may be thickened with wheat flour)");
                                } else if (foodName.equals("Milkfish") && allergen.equals("Dairy")) {
                                    message.append("\n  (may contain milk proteins)");
                                } else if (foodName.equals("Bulalo") && allergen.equals("Dairy")) {
                                    message.append("\n  (may contain dairy in the broth)");
                                } else if (foodName.equals("Chicken Eggs") && allergen.equals("Dairy")) {
                                    message.append("\n  (may contain trace amounts of dairy proteins)");
                                } else if (foodName.equals("White Bread") && (allergen.equals("Dairy") || allergen.equals("Wheat"))) {
                                    message.append("\n  (may contain milk powder and/or wheat flour)");
                                } else if (foodName.equals("White Rice") && allergen.equals("Dairy")) {
                                    message.append("\n  (can contain milk components)");
                                } else if (foodName.equals("Brown Rice") && allergen.equals("Wheat")) {
                                    message.append("\n  (may contain wheat flour as a thickener)");
                                }
                            }

                            message.append("\n\nDo you wish to continue?");

                            AlertDialog.Builder builder = new AlertDialog.Builder(FoodScreen.this);
                            builder.setTitle("Food Allergens").setMessage(message.toString()).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    navigateToCameraScreen();
                                }
                            }).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private Map<String, List<String>> getFoodAllergensMap() {
        Map<String, List<String>> foodAllergensMap = new HashMap<>();
        foodAllergensMap.put("Beef Kare-kare", Collections.singletonList("Peanuts"));
        foodAllergensMap.put("Beef Ribs Sinigang", Collections.singletonList("Dairy"));
        foodAllergensMap.put("Bulalo", Collections.singletonList("Dairy"));
        foodAllergensMap.put("Chicken Adobo", Collections.singletonList("Wheat"));
        foodAllergensMap.put("Chicken Afritada", Collections.singletonList("Dairy"));
        foodAllergensMap.put("Pork Menudo", Collections.singletonList("Dairy"));
        foodAllergensMap.put("Chicken Eggs", Collections.singletonList("Dairy"));
        foodAllergensMap.put("White Rice", Collections.singletonList("Dairy"));
        foodAllergensMap.put("Brown Rice", Collections.singletonList("Wheat"));
        foodAllergensMap.put("White Bread", Arrays.asList("Dairy", "Wheat"));
        foodAllergensMap.put("Blue Crab", Collections.singletonList("Shellfish"));
        foodAllergensMap.put("Squid", Collections.singletonList("Shellfish"));
        foodAllergensMap.put("Milkfish", Collections.singletonList("Dairy"));
        return foodAllergensMap;
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
            imageRef.child(filename).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
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
                                Toast.makeText(FoodScreen.this, "A Meal item has been successfully added", Toast.LENGTH_LONG).show();

                                // Create ActivityLogs structure
                                DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                                DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                                // Get current date and time
                                String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());

                                // Generate a unique ID for the log entry
                                String logID = "LogID_" + System.currentTimeMillis();

                                // Create the log entry structure
                                DatabaseReference logEntryRef = activityLogsRef.child(currentDate).child(logID);
                                logEntryRef.child("action").setValue("Added " + foodNameText.getText().toString());
                                logEntryRef.child("category").setValue("Meal Plan");
                                logEntryRef.child("timestamp").setValue(currentTime);

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
            }).addOnFailureListener(e1 -> {
                // Handle failure to upload image
            });
        });
    }

    public void setNutrientValues(String foodName) {
        foodData = FoodDataRepository.getFoodData(foodName);

        if (foodData != null) {

            rServingSize.setText("1 small orange (150g)");
            rServingSizeTemp = rServingSize.getText().toString().trim();

            // Extract serving size from the text
            String servingSizeText = rServingSizeTemp;
            double servingSizeAmount = extractAmount(servingSizeText);

            rCaloriesVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getCalories()) + " kcal");
            rCarbsVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getCarbohydrates()) + "g");
            rFatVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getTotalFat()) + "g");
            rProteinVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getProtein()) + "g");
            rSugarVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getSugar()) + "g");
            rFiberVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getFiber()) + "g");
            rWaterVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getWater()) + "mL");
            rVitAVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getVitaminA()) + "mcg");
            rVitB1Val.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getVitaminB1()) + "mg");
            rVitB2Val.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getVitaminB2()) + "mg");
            rVitCVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getVitaminC()) + "mg");
            rCalciumVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getCalcium()) + "mg");
            rSodiumVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getSodium()) + "mg");
            rIronVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getIron()) + "mg");

            // Set standard layout values
            foodCategory.setText(String.valueOf(foodData.getCategory()));
            sServingSize.setText(foodData.getServings() + "g");
            sCaloriesVal.setText(foodData.getCalories() + " kcal");
            sCarbsVal.setText(foodData.getCarbohydrates() + "g");
            sFatVal.setText(foodData.getTotalFat() + "g");
            sProteinVal.setText(foodData.getProtein() + "g");
            sSugarVal.setText(foodData.getSugar() + "g");
            sFiberVal.setText(foodData.getFiber() + "g");
            sWaterVal.setText(foodData.getWater() + "mL");
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
//            rWaterVal.setText(getIntent().getDoubleExtra("passedWater", 0) + "mL");
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
        if (currentHour >= 24) { // After 8 PM, disable Dinner
            mealOptions.remove("Dinner");
        }
        if (currentHour >= 15) { // After 2 PM, disable Lunch
            mealOptions.remove("Lunch");
        }
        if (currentHour >= 11) { // After 10 AM, disable Breakfast
            mealOptions.remove("Breakfast");
        }

        // Create a new adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> mealTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealOptions);

        mealPlanSelection = alertDialog.findViewById(R.id.mealPlanSelection);
        if (mealPlanSelection != null) {
            mealPlanSelection.setAdapter(mealTimeAdapter);
        }
    }

    public void calculateNutrients(String selectedServingSize) {
        rServingSize.setText(selectedServingSize); // Update the standard serving size TextView

        // Extract serving size value from the selected serving size string
        double totalServingSize = extractAmount(selectedServingSize);

        // Calculate nutrient values based on the total serving size
        double calories = (totalServingSize / 100) * foodData.getCalories();
        double carbs = (totalServingSize / 100) * foodData.getCarbohydrates();
        double fat = (totalServingSize / 100) * foodData.getTotalFat();
        double protein = (totalServingSize / 100) * foodData.getProtein();
        double sugar = (totalServingSize / 100) * foodData.getSugar();
        double fiber = (totalServingSize / 100) * foodData.getFiber();
        double water = (totalServingSize / 100) * foodData.getWater();
        double vitA = (totalServingSize / 100) * foodData.getVitaminA();
        double vitB1 = (totalServingSize / 100) * foodData.getVitaminB1();
        double vitB2 = (totalServingSize / 100) * foodData.getVitaminB2();
        double vitC = (totalServingSize / 100) * foodData.getVitaminC();
        double calcium = (totalServingSize / 100) * foodData.getCalcium();
        double sodium = (totalServingSize / 100) * foodData.getSodium();
        double iron = (totalServingSize / 100) * foodData.getIron();

        // Update nutrient values with appropriate units
        updateNutrientValue(rCaloriesVal, calculateNutrientValue(calories), " kcal");
        updateNutrientValue(rCarbsVal, calculateNutrientValue(carbs), "g");
        updateNutrientValue(rFatVal, calculateNutrientValue(fat), "g");
        updateNutrientValue(rProteinVal, calculateNutrientValue(protein), "g");
        updateNutrientValue(rSugarVal, calculateNutrientValue(sugar), "g");
        updateNutrientValue(rFiberVal, calculateNutrientValue(fiber), "g");
        updateNutrientValue(rWaterVal, calculateNutrientValue(water), "mL");
        updateNutrientValue(rVitAVal, calculateNutrientValue(vitA), "mcg");
        updateNutrientValue(rVitB1Val, calculateNutrientValue(vitB1), "mg");
        updateNutrientValue(rVitB2Val, calculateNutrientValue(vitB2), "mg");
        updateNutrientValue(rVitCVal, calculateNutrientValue(vitC), "mg");
        updateNutrientValue(rCalciumVal, calculateNutrientValue(calcium), "mg");
        updateNutrientValue(rSodiumVal, calculateNutrientValue(sodium), "mg");
        updateNutrientValue(rIronVal, calculateNutrientValue(iron), "mg");
        // Update other nutrient values similarly
    }

    private double calculateNutrientValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
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

        // Calculate and add serving sizes based on recognized serving size
        if (rServingSize != null && !rServingSize.getText().toString().isEmpty()) {
            String recognizedServingSize = rServingSizeTemp;
            double recognizedAmount = extractAmount(recognizedServingSize);
            String recognizedUnit = extractUnit(recognizedServingSize);

            // Extract the size descriptor from the recognized serving size
            String sizeDescriptor = recognizedServingSize.substring(recognizedServingSize.indexOf(" ") + 1, recognizedServingSize.lastIndexOf(" ("));

            // Capitalize each word in the size descriptor
            String[] descriptorWords = sizeDescriptor.split(" ");
            StringBuilder capitalizedDescriptor = new StringBuilder();
            for (String word : descriptorWords) {
                capitalizedDescriptor.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
            }
            sizeDescriptor = capitalizedDescriptor.toString().trim();

            // Format the recognized amount with one decimal place
            String formattedAmount = String.format("%.1f", recognizedAmount);

            // Construct the serving size description with the formatted amount and extracted size descriptor
            String servingSizeDescription = "1 " + sizeDescriptor + " (" + formattedAmount + recognizedUnit + ")";

            // Add serving sizes based on food category
            switch (foodCategory) {
                case "Fruits":
                    servingSizeList.add("1 Handful (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                    servingSizeList.add(servingSizeDescription);
                    servingSizeList.add("1 Portion (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                    servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    break;
                case "Vegetables":
                    servingSizeList.add("1 Stalk (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                    servingSizeList.add(servingSizeDescription);
                    servingSizeList.add("1 Head (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                    servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    break;
                case "Grains":
                    switch (foodName) {
                        case "White Bread":
                            servingSizeList.add("1 Slice (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add("2 Slices (" + recognizedAmount * 2 + recognizedUnit + ")");
                            servingSizeList.add("3 Slices (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        case "White Rice":
                        case "Brown Rice":
                            servingSizeList.add("1 Cup Cooked (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add("1 Bowl (" + recognizedAmount + recognizedUnit + ")");
                            servingSizeList.add("1 Plate (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        default:
                            servingSizeList.add("1 Bowl (" + recognizedAmount + recognizedUnit + ")");
                            servingSizeList.add("1 Packet (" + recognizedAmount + recognizedUnit + ")");
                            break;
                    }
                    break;
                case "Meat and Poultry":
                    switch (foodName) {
                        case "Chicken Eggs":
                            servingSizeList.add("1 Medium Egg (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add("1 Large Egg (" + recognizedAmount + recognizedUnit + ")");
                            servingSizeList.add("1 Serving (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        case "Chicken Breast":
                        case "Beef Ground":
                            servingSizeList.add("Small Portion (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeDescription);
                            servingSizeList.add("Large Portion (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        case "Chicken Drumstick":
                            servingSizeList.add("1 Drumstick, Skinless (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeDescription);
                            servingSizeList.add("2 Drumsticks, Skinless (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        case "Pork Chop":
                            servingSizeList.add("Small Cut (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeDescription);
                            servingSizeList.add("Large Cut (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                            break;
                        default:
                            servingSizeList.add("1 Piece (" + recognizedAmount + recognizedUnit + ")");
                            servingSizeList.add("1 Serving (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                            break;
                    }
                    break;
                case "Seafood Products":
                    if (foodName.equals("Blue Crab")) {
                        servingSizeList.add("1 Portion (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeDescription);
                        servingSizeList.add("1 Whole (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    } else {
                        servingSizeList.add("1 Fillet (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeDescription);
                        servingSizeList.add("1 Whole (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    }
                    break;
                case "Filipino Cooked Dishes":
                    if (foodName.equals("Pork Barbeque")) {
                        servingSizeList.add("1 Skewer (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeDescription);
                        servingSizeList.add("1 Plate (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    } else {
                        servingSizeList.add("1 Serving (" + calculateNutrientValue(recognizedAmount * 0.7) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeDescription);
                        servingSizeList.add("1 Dish (" + calculateNutrientValue(recognizedAmount * 1.3) + recognizedUnit + ")");
                        servingSizeList.add(servingSizeRecommend.getText().toString().trim());
                    }
                    break;
                default:
                    servingSizeList.addAll(Arrays.asList("1 Cup", "1 Bowl", "1 Plate", "1 Slice"));
                    break;
            }
        }

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> servingSizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, servingSizeList);
        servingSizeSelection.setAdapter(servingSizeAdapter);

    }

    // Helper method to extract amount from recognized serving size string
    private double extractAmount(String servingSize) {
        // Use regular expression to find the number within parentheses followed by "g"
        Pattern pattern = Pattern.compile("\\((\\d+\\.?\\d*)\\s*g\\)");
        Matcher matcher = pattern.matcher(servingSize);

        // Check if the pattern is found
        if (matcher.find()) {
            // Extract the matched group (the number within parentheses)
            String numberStr = matcher.group(1);
            try {
                // Try parsing the extracted number to a double
                return Double.parseDouble(numberStr);
            } catch (NumberFormatException ignored) {
                // Handle parsing error (if any)
            }
        }

        // Return a default value if no number is found
        return 0.0;
    }

    // Helper method to extract unit from recognized serving size string
    private String extractUnit(String servingSize) {
        // Unit is assumed to be "g" (grams)
        return "g";
    }

    public void showAlertDialog() {
        // Inflate the bottom sheet view
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_food_screen, null);

        mealPlanLayout = alertDialogView.findViewById(R.id.mealPlanLayout);
        servingSizeLayout = alertDialogView.findViewById(R.id.servingSizeLayout);

        // Find views within the pop up
        mealPlanSelection = alertDialogView.findViewById(R.id.mealPlanSelection);
        servingSizeSelection = alertDialogView.findViewById(R.id.servingSizeSelection);

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
                boolean isServingSizeValid = isServingSizeValid();
                boolean isMealPlanValid = isMealPlanValid();

                // Set the value of allGoods based on conditions
                allGoods = isServingSizeValid && isMealPlanValid;

                if (allGoods) {
                    // Fields are valid, proceed with calculations
                    calculateNutrients(servingSizeSelection.getText().toString());
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog.show();

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