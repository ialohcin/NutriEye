package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.nutrieye.ml.NutriEyeCNN;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    boolean showAlertServingSize = true;
    private AlertDialog alertDialog;
    public boolean hasServingSize = false;
    ProgressDialog progressDialog;
    String currentDate;
    String servingSizeText = "";
    String rServingSizeTemp = "";
    String userUID;
    FoodData foodData;
    TextInputLayout mealPlanLayout, servingSizeLayout, inputServingLayout;
    TextInputEditText inputServingText;
    Uri imageUri;
    ConstraintLayout recognizedLayout, standardLayout, nutrientsLayout;
    MaterialButton addMeal, editMeal;
    TextView foodNameText, foodCategory, servingSizeRecommend;
    TextView rCaloriesVal, rCarbsVal, rFatVal, rProteinVal, rFiberVal, rWaterVal, rVitAVal, rVitB1Val, rVitB2Val, rVitCVal, rCalciumVal, rSodiumVal, rIronVal, rServingSize;
    TextView sCaloriesVal, sCarbsVal, sFatVal, sProteinVal, sFiberVal, sWaterVal, sVitAVal, sVitB1Val, sVitB2Val, sVitCVal, sCalciumVal, sSodiumVal, sIronVal, sServingSize;
    ColorStateList def;
    TextView showRecognized, showStandard, selectNutrientsTab;
    AutoCompleteTextView mealPlanSelection;
    AutoCompleteTextView servingSizeSelection;
    Python python;
    PyObject pyObjectServing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(FoodScreen.this));
        }

        setTitle(null);

        initComponents();

        SharedPreferences preferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);

        // Retrieve the captured image URI from the Intent extras
        String imageUriString = getIntent().getStringExtra("captured_image_uri");
        // Retrieve the selected image URI from the Intent extras
        String selectedImageUriString = getIntent().getStringExtra("selected_image_uri");

        if (imageUriString != null) {
            // Convert the image URI string back to a URI
            imageUri = Uri.parse(imageUriString);
            // Load and display the captured image
            displayImage(imageUri);
        } else if (selectedImageUriString != null) {
            // Convert the selected image URI string back to a URI
            imageUri = Uri.parse(selectedImageUriString);
            // Load and display the selected image
            displayImage(imageUri);
        } else {
            // Handle the case where both image URIs are null
            Toast.makeText(this, "No image URI found.", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no image URI is found
        }

        editMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RF Integration
                python = Python.getInstance();
                pyObjectServing = python.getModule("servingsizeCalculator");

                showAlertDialog();
                disableMealOptions();
                fetchUserDataFromFirebase(foodNameText.getText().toString());
            }
        });

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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
                                    Toast.makeText(FoodScreen.this, "Database operation cancelled", Toast.LENGTH_SHORT).show();
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
                } catch (Exception e) {
                    // Handle any exceptions
                    e.printStackTrace();
                    Toast.makeText(FoodScreen.this, "Error adding meal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    private int calculateAgeFromDOB(String dob) {
        // Parse the date string from DOB
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate dateOfBirth = formatter.parseLocalDate(dob);

        // Get current date
        LocalDate currentDate = new LocalDate();

        // Calculate the difference in years
        Period period = new Period(dateOfBirth, currentDate);
        return period.getYears();
    }

    private void displayImage(Uri imageUri) {
        try {
            // Find the ImageView in the layout
            ImageView imageViewFood = findViewById(R.id.foodPreview);

            // Load and display the image using the Picasso library (or another image loading library)
            Picasso.get().load(imageUri).into(imageViewFood, new Callback() {
                @Override
                public void onSuccess() {
                    // Get the bitmap from the ImageView
                    BitmapDrawable drawable = (BitmapDrawable) imageViewFood.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    // Execute TFLite model inference
                    executeTfliteInference(bitmap);
                }

                @Override
                public void onError(Exception e) {
                    // Handle error loading the image
                    Toast.makeText(FoodScreen.this, "Error loading image.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in displaying image", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeTfliteInference(Bitmap image) {
        try {
            NutriEyeCNN model = NutriEyeCNN.newInstance(getApplicationContext());

            // Resize the input image to the expected input size of the model
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, 224, 224, true);

            // Normalize pixel values and prepare input buffer
            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4); // Float data size is 4 bytes
            inputBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[224 * 224];
            resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

            for (int pixelValue : intValues) {
                inputBuffer.putFloat((((pixelValue >> 16) & 0xFF) * (1.f / 255.f))); // Red channel
                inputBuffer.putFloat((((pixelValue >> 8) & 0xFF) * (1.f / 255.f)));  // Green channel
                inputBuffer.putFloat(((pixelValue & 0xFF) * (1.f / 255.f)));         // Blue channel
            }

            // Run inference
            org.tensorflow.lite.support.tensorbuffer.TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(inputBuffer);

            // Run model inference
            NutriEyeCNN.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Post-processing
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;

            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            // Process the output result (e.g., display or use it)
            String[] classLabels = {
                    "Green Apple", "Lakatan Banana", "Red Grapes", "Strawberry", "Pomelo",
                    "Rambutan", "Mandarin Orange", "Guyabano", "Carabao Mango", "Mangosteen",
                    "Bitter Gourd", "Broccoli", "Carrots", "Cauliflower", "Cucumber",
                    "Eggplant", "Okra", "Red Bell Pepper", "Roma Tomato", "Winged Bean",
                    "Brown Rice", "Rolled Oats", "Sweet Corn", "White Bread", "White Rice",
                    "Beef Ground", "Chicken Breast", "Chicken Drumstick", "Chicken Egg",
                    "Chicken Thigh", "Chicken Wing", "Pork Belly", "Pork Chop", "Blue Crab",
                    "Milkfish", "Spanish Mackerel", "Squid", "Tilapia", "Beef Kaldereta",
                    "Beef Kare-Kare", "Beef Ribs Sinigang", "Bulalo", "Chicken Adobo",
                    "Chicken Afritada", "Chicken Inasal", "Chicken Tinola", "Crispy Pata",
                    "Pork Barbeque", "Pancit Canton", "Pork Menudo", "Shrimp Sinigang"
            };

            if (maxPos < classLabels.length) {
                foodNameText.setText(classLabels[maxPos]);
                foodData = FoodDataRepository.getFoodData(foodNameText.getText().toString());
                foodCategory.setText(String.valueOf(foodData.getCategory()));
            } else {
                // Handle if maxPos is out of bounds
                foodNameText.setText("Unknown");

                // Show an AlertDialog indicating the image is not recognizable as food
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Food Recognition")
                        .setMessage("The image does not contain recognizable food items.")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Navigate back to CameraScreen
                                navigateToCameraScreen();
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Navigate back to NavigationScreen
                                Intent intent = new Intent(FoodScreen.this, NavigationScreen.class);
                                intent.putExtra("activeFragment", "HomeFragment");
                                startActivity(intent);
                                finish(); // Close the current activity after navigating back
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            //showAllergensDialog(foodNameText.getText().toString());

            // Call showAllergensDialog and set showAlertServingSize accordingly
            showAllergensDialog(foodNameText.getText().toString(), new OnAllergensDialogDismissListener() {
                @Override
                public void onDismiss(boolean proceed) {
                    showAlertServingSize = proceed;
                    if (proceed) {
                        showAlertServingSizeDialog();
                    }
                }
            });
            showAlertServingSizeDialog();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in executing ML Model", Toast.LENGTH_SHORT).show();
        }
    }

    // Define interface for handling allergens dialog dismissal
    public interface OnAllergensDialogDismissListener {
        void onDismiss(boolean proceed);
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

        def = showStandard.getTextColors();

        //Recognized
        rServingSize = findViewById(R.id.recognizedServingSize);
        rCaloriesVal = findViewById(R.id.recognizedCalories);
        rCarbsVal = findViewById(R.id.recognizedCarbs);
        rFatVal = findViewById(R.id.recognizedTotalFat);
        rProteinVal = findViewById(R.id.recognizedProtein);
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

    private void fetchUserDataFromFirebase(String foodName) {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Profile");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve user profile data
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

                        // Pass this data to the Python function
                        PyObject resultServing = pyObjectServing.callAttr("calculateServingSize", foodName, age, sex, height, weight, PyObject.fromJava(isPregnant), PyObject.fromJava(isLactating), phyActivity);
                        String recommservingSize = resultServing.toString();

                        // Update UI with the result
                        servingSizeRecommend.setText("1 Serving of (" + recommservingSize + ")");
                    }
                    setupServingSizeAdapter(alertDialog, foodCategory.getText().toString(), foodNameText.getText().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                    Toast.makeText(FoodScreen.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllergensDialog(String foodName, OnAllergensDialogDismissListener listener) {
        try {
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
                                builder.setTitle("Food Allergens").setMessage(message.toString()).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        listener.onDismiss(true);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        navigateToCameraScreen();
                                        listener.onDismiss(false);
                                    }
                                }).show();
                            } else {
                                listener.onDismiss(true);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(FoodScreen.this, "Database operation cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FoodScreen.this, "Error in showing allergens dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        try {
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
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(FoodScreen.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Dismiss the ProgressDialog if it's still showing
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void uploadWithIncrementedFilename(StorageReference imageRef, String userUID, int index, Uri imageUri, HashMap<String, Object> mealItemMap, DatabaseReference mealItemRef) {
        try {
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
                    e1.printStackTrace();
                    Toast.makeText(FoodScreen.this, "Error uploading image: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(FoodScreen.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Dismiss the ProgressDialog if it's still showing
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void setNutrientValues(String foodName) {
        try {
            foodData = FoodDataRepository.getFoodData(foodName);

            if (foodData != null) {

                rServingSize.setText(inputServingText.getText().toString().trim());
                rServingSizeTemp = rServingSize.getText().toString().trim();

                // Extract serving size from the text
                String servingSizeText = rServingSizeTemp;
                double servingSizeAmount = extractAmount(servingSizeText);

                foodCategory.setText(String.valueOf(foodData.getCategory()));
                rCaloriesVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getCalories()) + " kcal");
                rCarbsVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getCarbohydrates()) + "g");
                rFatVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getTotalFat()) + "g");
                rProteinVal.setText(calculateNutrientValue((servingSizeAmount / 100.0) * foodData.getProtein()) + "g");
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
                sServingSize.setText(foodData.getServings() + "g");
                sCaloriesVal.setText(foodData.getCalories() + " kcal");
                sCarbsVal.setText(foodData.getCarbohydrates() + "g");
                sFatVal.setText(foodData.getTotalFat() + "g");
                sProteinVal.setText(foodData.getProtein() + "g");
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
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(FoodScreen.this, "Error setting nutrient values: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to disable meal options based on current time
    private void disableMealOptions() {
        List<String> mealOptions = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner"));

        // Get the current time in hours using Calendar
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format

        // Determine which meal options to disable based on the current time
        if (currentHour >= 23) { // After 11 PM, disable Dinner
            mealOptions.remove("Dinner");
        }
        if (currentHour >= 17) { // After 5 PM, disable Lunch
            mealOptions.remove("Lunch");
        }
        if (currentHour >= 11) { // After 11 AM, disable Breakfast
            mealOptions.remove("Breakfast");
        }

        // Create a new adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> mealTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealOptions);

        mealPlanSelection = alertDialog.findViewById(R.id.inputServingText);
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
        updateNutrientValue(rFiberVal, calculateNutrientValue(fiber), "g");
        updateNutrientValue(rWaterVal, calculateNutrientValue(water), "mL");
        updateNutrientValue(rVitAVal, calculateNutrientValue(vitA), "mcg");
        updateNutrientValue(rVitB1Val, calculateNutrientValue(vitB1), "mg");
        updateNutrientValue(rVitB2Val, calculateNutrientValue(vitB2), "mg");
        updateNutrientValue(rVitCVal, calculateNutrientValue(vitC), "mg");
        updateNutrientValue(rCalciumVal, calculateNutrientValue(calcium), "mg");
        updateNutrientValue(rSodiumVal, calculateNutrientValue(sodium), "mg");
        updateNutrientValue(rIronVal, calculateNutrientValue(iron), "mg");
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
                            servingSizeList.add(servingSizeRecommend.getText().toString());
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
                case "Seafood":
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
                case "Cooked Dishes":
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

        mealPlanLayout = alertDialogView.findViewById(R.id.inputServingLayout);
        servingSizeLayout = alertDialogView.findViewById(R.id.servingSizeLayout);

        // Find views within the pop up
        mealPlanSelection = alertDialogView.findViewById(R.id.inputServingText);
        servingSizeSelection = alertDialogView.findViewById(R.id.servingSizeSelection);

        TextView servingSizeLabel = alertDialogView.findViewById(R.id.recommendedLabel);
        servingSizeRecommend = alertDialogView.findViewById(R.id.recommendedServingSize);

        // Set click listeners for buttons within the pop up
        Button cancelButton = alertDialogView.findViewById(R.id.cancelServing);
        Button saveButton = alertDialogView.findViewById(R.id.saveServing);
        ImageButton closeDialog = alertDialogView.findViewById(R.id.closeServingDialog);

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
                boolean isServingSizeValid = isServingSizeSelectionValid();
                boolean isMealPlanValid = isMealPlanValid();
                hasServingSize = validateServingSize();

                // Set the value of allGoods based on conditions
                allGoods = isServingSizeValid && isMealPlanValid && hasServingSize;

                if (allGoods) {
                    // Fields are valid, proceed with calculations
                    calculateNutrients(servingSizeSelection.getText().toString());
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog.show();

    }

    public void showAlertServingSizeDialog() {
        // Inflate the bottom sheet view
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_food_serving, null);

        inputServingLayout = alertDialogView.findViewById(R.id.inputServingLayout);
        inputServingText = alertDialogView.findViewById(R.id.inputServingText);

        // Set click listeners for buttons within the pop up
        Button confirmButton = alertDialogView.findViewById(R.id.saveServing);
        ImageButton closeDialog = alertDialogView.findViewById(R.id.closeServingDialog);

        // Create the pop up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentAlertDialog);
        builder.setView(alertDialogView);

        alertDialog = builder.create();

        // Set the AlertDialog to be non-cancellable by touching outside the dialog
        alertDialog.setCanceledOnTouchOutside(false);

        // Define a TextWatcher for Serving Size Selection field
        inputServingText.addTextChangedListener(new TextWatcher() {
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
                validateServingSize();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodScreen.this);
                builder.setTitle("Discard Serving Size?");
                builder.setCancelable(false);
                builder.setMessage("Are you sure you want to cancel providing serving size? This value is needed to proceed.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go back to cameraScreen (assuming startActivity method)
                        startActivity(new Intent(FoodScreen.this, CameraScreen.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, simply dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog
                AlertDialog alertDialog = builder.create();

                // Show the AlertDialog
                alertDialog.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String servingSize = inputServingText.getText().toString().trim();
//
//                // Check if serving size is not empty
//                if (TextUtils.isEmpty(servingSize)) {
//                    inputServingLayout.setError("Serving size cannot be empty");
//                    return;
//                }
//
//                // Check if serving size matches the required formats
//                if (!servingSize.matches("^(1-9\\s+)?[\\w\\s]+\\s+\\(\\d+(\\.\\d+)?\\w+\\)$")) {
//                    // If not in the format "1 serving (100g)" or "1 serving (100.9g)",
//                    // try to parse it as "<amount><unit>", e.g., "100g" or "100.9g"
//                    if (!servingSize.matches("^\\d+(\\.\\d+)?\\w+$")) {
//                        // If not in the format "<amount><unit>", check if it's in the format "1 medium apple 100g"
//                        if (servingSize.matches("^\\d+\\s+[\\w\\s]+\\s+\\d+(\\.\\d+)?\\w+$")) {
//                            // Transform the format "1 medium apple 100g" to "1 medium apple (100g)"
//                            int lastSpaceIndex = servingSize.lastIndexOf(' ');
//                            String serving = servingSize.substring(lastSpaceIndex + 1);
//                            servingSize = servingSize.substring(0, lastSpaceIndex) + " (" + serving + ")";
//                            inputServingText.setText(servingSize);
//                        } else {
//                            inputServingLayout.setError("Valid formats: 1 serving (100g), 1 serving (100.9g), or <amount><g>");
//                            return;
//                        }
//                    } else {
//                        // Format the serving size as "1 serving (<amount><unit>)"
//                        servingSize = "1 serving (" + servingSize + ")";
//                        inputServingText.setText(servingSize);
//                    }
//                }
//
//// Clear any previous errors
//                inputServingLayout.setError(null);

                String servingSize = inputServingText.getText().toString().trim();

                // Check if serving size is not empty
                if (TextUtils.isEmpty(servingSize)) {
                    inputServingLayout.setError("Serving size cannot be empty");
                    return;
                }

                // Check if serving size matches the required formats
                if (!servingSize.matches("^(\\d+-\\d+\\s+)?[\\w\\s]+\\s+(\\(\\d+(\\.\\d+)?\\w+\\))?$")) {
                    // If not in the format "1 serving (100g)" or "1 serving (100.9g)",
                    // try to parse it as "<amount><unit>", e.g., "100g" or "100.9g"
                    if (!servingSize.matches("^\\d+(\\.\\d+)?\\w+$")) {
                        // If not in the format "<amount><unit>", check if it's in the format "1 medium apple 100g"
                        if (servingSize.matches("^\\d+\\s+[\\w\\s]+\\s+\\d+(\\.\\d+)?\\w+$")) {
                            // Transform the format "1 medium apple 100g" to "1 medium apple (100g)"
                            int lastSpaceIndex = servingSize.lastIndexOf(' ');
                            String serving = servingSize.substring(lastSpaceIndex + 1);
                            servingSize = servingSize.substring(0, lastSpaceIndex) + " (" + serving + ")";
                            inputServingText.setText(servingSize);
                        } else {
                            inputServingLayout.setError("Valid formats: 1 serving (100g), 1 serving (100.9g), or 100g");
                            return;
                        }
                    } else {
                        // Format the serving size as "1 serving (<amount><unit>)"
                        servingSize = "1 serving (" + servingSize + ")";
                        inputServingText.setText(servingSize);
                    }
                }

                // Clear any previous errors
                inputServingLayout.setError(null);

                // Set the serving size in the recognized serving size field
                rServingSize.setText(servingSize);
                setNutrientValues(foodNameText.getText().toString());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        // Intercept the back button press to prevent the dialog from being canceled
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    showCancelDialog();
                    return true; // Consumes the back key event
                }
                return false; // Continue with normal processing (cancellation)
            }
        });
    }

    private boolean validateServingSize() {
        if (inputServingText != null && inputServingLayout != null) {
            String servingSize = inputServingText.getText().toString().trim();

            // Check if serving size is empty
            if (TextUtils.isEmpty(servingSize)) {
                inputServingLayout.setError("Serving size cannot be empty");
                return false;
            }

            // Check if serving size matches the required format
            if (!servingSize.matches("^\\d+(\\.\\d+)?\\s*g$") && // Matches "100g" or "100.5g"
                    !servingSize.matches("^\\d+(\\.\\d+)?\\s+(.+\\s+)?\\w+\\s+\\d+(\\.\\d+)?\\s*g$") && // Matches "1 medium pork barbeque 100g" or "1 medium pork barbeque 100.5g"
                    !servingSize.matches("^\\d+(\\.\\d+)?\\s+(.+\\s+)?\\(\\d+(\\.\\d+)?\\s*g\\)$")) { // Matches "1 medium (100g)" or "1 medium (100.5g)"
                inputServingLayout.setError("Valid formats: 100g, 1 medium pork barbeque 100g, or 1 medium (100g)");
                return false;
            }

            // Clear any previous errors
            inputServingLayout.setError(null);
            return true;
        }
        return false;
    }

    private boolean isServingSizeSelectionValid() {
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (alertDialog != null && alertDialog.isShowing()) {
            // Handle the back button press here
            showCancelDialog();
        } else {
            showDiscardChangesDialog();
        }
    }

    private void showDiscardChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Changes?");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to discard the changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Navigate back
                navigateToCameraScreen();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog discardChangesDialog = builder.create();
        discardChangesDialog.show();
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Serving Size?");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to cancel providing serving size? This value is needed to proceed.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go back to cameraScreen (assuming startActivity method)
                navigateToCameraScreen();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, simply dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog cancelDialog = builder.create();

        // Show the AlertDialog
        cancelDialog.show();
    }

    private void navigateToCameraScreen() {
        Intent intent = new Intent(this, CameraScreen.class);
        startActivity(intent);
        finish();
    }

}