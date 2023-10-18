package com.example.nutrieye;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class FoodScreen extends AppCompatActivity {
    private AlertDialog alertDialog;
    View recognized, standard;
    TextView foodNameText, foodCat, foodImg;
    TextView rCaloriesVal, rCarbsVal, rFatVal, rProteinVal, rSugarVal, rWaterVal, rVitAVal, rVitB1Val, rVitB2Val, rVitCVal, rCalciumVal, rSodiumVal, rIronVal;
    TextView sCaloriesVal, sCarbsVal, sFatVal, sProteinVal, sSugarVal, sWaterVal, sVitAVal, sVitB1Val, sVitB2Val, sVitCVal, sCalciumVal, sSodiumVal, sIronVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(null);

        // Retrieve the captured image URI from the Intent extras
        String imageUriString = getIntent().getStringExtra("captured_image_uri");

        if (imageUriString != null) {
            // Convert the image URI string back to a URI
            Uri imageUri = Uri.parse(imageUriString);

            // Find the ImageView in the layout
            ImageView imageViewFood = findViewById(R.id.foodPreview);

            // Load and display the image using the Picasso library (or another image loading library)
            Picasso.get().load(imageUri).into(imageViewFood);
        }

        recognized = getLayoutInflater().inflate(R.layout.recognized_nutrients, null);

        foodNameText = findViewById(R.id.foodCategory);

        foodCat = findViewById(R.id.foodCategory);


        ViewPager viewPager = findViewById(R.id.viewPager); // Replace with your ViewPager ID
        List<Integer> layoutIds = new ArrayList<>();
        layoutIds.add(R.layout.recognized_nutrients);
        layoutIds.add(R.layout.standard_nutrients);

        rCaloriesVal = recognized.findViewById(R.id.caloriesValue);
        rCarbsVal = recognized.findViewById(R.id.carbsValue);
        rFatVal = recognized.findViewById(R.id.totalFatValue);
        rProteinVal = recognized.findViewById(R.id.proteinValue);
        rSugarVal = recognized.findViewById(R.id.sugarValue);
        rWaterVal = recognized.findViewById(R.id.waterValue);
        rVitAVal = recognized.findViewById(R.id.vitaminA_Value);
        rVitB1Val = recognized.findViewById(R.id.vitaminB1_Value);
        rVitB2Val = recognized.findViewById(R.id.vitaminB2_Value);
        rVitCVal = recognized.findViewById(R.id.vitaminC_Value);
        rCalciumVal = recognized.findViewById(R.id.calcium_Value);
        rSodiumVal = recognized.findViewById(R.id.sodium_Value);
        rIronVal = recognized.findViewById(R.id.iron_Value);

        SwipeView swipeView = new SwipeView(this, layoutIds);
        viewPager.setAdapter(swipeView);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        MaterialButton addMeal = findViewById(R.id.addMealPlanButton);

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

    }

    private void showAlertDialog() {
        // Inflate the bottom sheet view
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_food_screen, null);

        TextInputLayout mealPlanLayout = alertDialogView.findViewById(R.id.mealPlanLayout);
        TextInputLayout servingSizeLayout = alertDialogView.findViewById(R.id.servingSizeLayout);

        // Find views within the bottom sheet layout
        AutoCompleteTextView mealPlanSelection = alertDialogView.findViewById(R.id.mealPlanSelection);
        TextInputEditText servingSizeSelection = alertDialogView.findViewById(R.id.servingSizeSelection);

        TextView servingSizeLabel = alertDialogView.findViewById(R.id.recommendedLabel);
        TextView servingSizeRecommend = alertDialogView.findViewById(R.id.recommServingSize);

        // Set click listeners for buttons within the bottom sheet
        Button cancelButton = alertDialogView.findViewById(R.id.cancelMeal);
        Button saveButton = alertDialogView.findViewById(R.id.saveMeal);
        ImageButton closeDialog = alertDialogView.findViewById(R.id.closeDialog);

        // Create the bottom sheet dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentAlertDialog);
        builder.setView(alertDialogView);

        alertDialog = builder.create();

        ArrayAdapter<String> mealTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Breakfast", "Lunch", "Dinner"});
        mealPlanSelection.setAdapter(mealTimeAdapter);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the bottom sheet dialog
                alertDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = (String) foodNameText.getText();
                String category = (String) foodCat.getText();
                String mealTime = String.valueOf(mealPlanSelection.getText());
                String servingSize = String.valueOf(servingSizeSelection.getText());
                double calories = Double.parseDouble(rCaloriesVal.getText().toString());

                // Extract the numeric part from the text and then parse to double
                double carbs = Double.parseDouble(rCarbsVal.getText().toString());
                double fats = Double.parseDouble(rFatVal.getText().toString());
                double protein = Double.parseDouble(rProteinVal.getText().toString());
                double sugar = Double.parseDouble(rSugarVal.getText().toString());
                double water = Double.parseDouble(rWaterVal.getText().toString());
                double vitA = Double.parseDouble(rVitAVal.getText().toString());
                double vitB1 = Double.parseDouble(rVitB1Val.getText().toString());
                double vitB2 = Double.parseDouble(rVitB2Val.getText().toString());
                double vitC = Double.parseDouble(rVitCVal.getText().toString());
                double calcium = Double.parseDouble(rCalciumVal.getText().toString());
                double sodium = Double.parseDouble(rSodiumVal.getText().toString());
                double iron = Double.parseDouble(rIronVal.getText().toString());
                int image = R.drawable.grapes;

                // Create an Item object with the selected item details
                Item selectedFoodItem = new Item(image, foodName, category, mealTime, servingSize, calories, carbs, fats, protein, sugar, water, vitA, vitB1, vitB2, vitC, calcium, sodium, iron);

            }
        });

        alertDialog.show();

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