package com.example.nutrieye;


public class Item {
    private int imageResource;
    private String foodName;
    private String category;
    private String mealTime;
    private String servingSize;
    private double calories;
    private double carbs;
    private double fat;
    private double protein;
    private double sugar;
    private double water;
    private double vitaminA;
    private double vitaminB1;
    private double vitaminB2;
    private double vitaminC;
    private double calcium;
    private double sodium;
    private double iron;

    public Item(int imageResource, String foodName, String category, String mealTime, String servingSize, double calories, double carbs, double fat, double protein, double sugar, double water, double vitaminA, double vitaminB1, double vitaminB2, double vitaminC, double calcium, double sodium, double iron) {
        this.imageResource = imageResource;
        this.foodName = foodName;
        this.category = category;
        this.mealTime = mealTime;
        this.servingSize = servingSize;
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.sugar = sugar;
        this.water = water;
        this.vitaminA = vitaminA;
        this.vitaminB1 = vitaminB1;
        this.vitaminB2 = vitaminB2;
        this.vitaminC = vitaminC;
        this.calcium = calcium;
        this.sodium = sodium;
        this.iron = iron;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getCategory() {
        return category;
    }

    public String getMealTime() {
        return mealTime;
    }

    public String getServingSize() {
        return servingSize;
    }

    public double getCalories() {
        return calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getSugar() {
        return sugar;
    }

    public double getWater() {
        return water;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminB1() {
        return vitaminB1;
    }

    public double getVitaminB2() {
        return vitaminB2;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getSodium() {
        return sodium;
    }

    public double getIron() {
        return iron;
    }
}
