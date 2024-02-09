package com.example.nutrieye;

import java.util.HashMap;

public class FoodDataRepository {
    private static HashMap<String, FoodData> foodMap = new HashMap<>();

    static {
        initFoodMap();
    }

    private static void initFoodMap() {
        //Fruits
        foodMap.put("Green Apple", new FoodData("Fruits", 100, 63, 14.9, 0.1, 0.5, 10.4, 3.0, 84.3, 0, 0.01, 0.02, 2, 10, 4, 0.1));
        foodMap.put("Carabao Mango", new FoodData("Fruits", 100, 70, 16.4, 0.2, 0.6, 13.8, 1.7, 82.4, 0, 0.09, 0.07, 46, 10, 4, 0.6));
        foodMap.put("Lakatan Banana", new FoodData("Fruits", 100, 126, 29.6, 0.2, 1.4, 15.6, 3.3, 68, 0, 0.03, 0.05, 25, 21, 2, 0.8));
        foodMap.put("Guyabano", new FoodData("Fruits", 100, 70, 16.2, 0.1, 1.1, 12.9, 3.2, 82, 0, 0.09, 0.07, 27, 16, 4, 0.6));
        foodMap.put("Red Grapes", new FoodData("Fruits", 100, 83, 19.7, 0.3, 0.4, 18.1, 1.0, 79.2, 0, 0.06, 0.02, 3, 6, 16, 0.4));
        foodMap.put("Mandarin Orange", new FoodData("Fruits", 100, 31, 6.9, 0.2, 0.3, 5.5, 0.9, 92.3, 0, 0.03, 0.03, 31, 31, 1, 0.3));
        foodMap.put("Pomelo", new FoodData("Fruits", 100, 51, 10.6, 0.6, 0.7, 8.5, 1.1, 87.8, 0, 0.04, 0.03, 46, 30, 2, 0.7));
        foodMap.put("Rambutan", new FoodData("Fruits", 100, 87, 18.6, 0.9, 1.2, 0, 0.9, 77.6, 0, 0.02, 0.1, 91, 32, 18, 0.4));
        foodMap.put("Strawberry", new FoodData("Fruits", 100, 34, 7.2, 0.2, 0.8, 4.7, 1.9, 91.3, 0, 0.03, 0.03, 97, 34, 1, 1.2));

        //Vegetables
        foodMap.put("Broccoli", new FoodData("Vegetables", 100, 34, 6.64, 0.37, 2.82, 1.7, 2.6, 89.3, 0, 0.07, 0.12, 89.2, 47, 33, 0.73));
        foodMap.put("Okra", new FoodData("Vegetables", 100, 34, 5.9, 0.3, 1.8, 1.2, 3.6, 91.3, 0, 0.06, 0.07, 1, 51, 11, 0.4));
        foodMap.put("Eggplant", new FoodData("Vegetables", 100, 29, 5.5, 0.2, 1.2, 2.7, 1.6, 92.7, 0, 0.1, 0.05, 3, 11, 3, 0.3));
        foodMap.put("Cucumber", new FoodData("Vegetables", 100, 16, 3.2, 0.1, 0.5, 2.1, 0.7, 95.9, 0, 0.02, 0.02, 2, 12, 13, 0.1));
        foodMap.put("Cauliflower", new FoodData("Vegetables", 100, 32, 5.2, 0.3, 2.1, 2, 2.1, 91.7, 0, 0.05, 0.12, 82, 41, 14, 0.8));
        foodMap.put("Carrots", new FoodData("Vegetables", 100, 42, 8.6, 0.3, 1.1, 4.8, 2.6, 89.1, 0, 0.03, 0.03, 0, 25, 26, 0.2));
        foodMap.put("Bitter Gourd", new FoodData("Vegetables", 100, 23, 4.2, 0.2, 1, 0.8, 1.3, 93.9, 0, 0.05, 0.04, 2, 22, 15, 0.4));

        //Grains
        foodMap.put("White Rice", new FoodData("Grains", 100, 129, 29.7, 0.2, 2.1, 0.1, 0.4, 67.6, 0, 0.02, 0.02, 0, 11, 3, 0.6));
        foodMap.put("Brown Rice", new FoodData("Grains", 100, 363, 76.3, 2.8, 8.1, 3.6, 3.0, 11.6, 0, 0.04, 0.00, 4, 6, 9, 1.8));
        foodMap.put("White Bread", new FoodData("Grains", 100, 329, 61.1, 5.1, 9.7, 6.8, 3.3, 23.2, 0, 0.20, 0.16, 0, 77, 592, 3.9));

        //Meat and Poultry
        foodMap.put("Chicken Eggs", new FoodData("Meat and Poultry", 100,139, 1.4, 9.4, 12.3, 0.4, 0.0, 76.0, 201, 0.07, 0.39, 0, 32, 1.30, 128));
        foodMap.put("Chicken Breast", new FoodData("Meat and Poultry", 100,131, 0.0, 5.0, 21.6, 0.0, 0.0, 73.1, 30, 0.06, 0.06, 0, 24, 66, 1.0));
        foodMap.put("Chicken Drumstick", new FoodData("Meat and Poultry", 100,204, 0.0, 15.1, 17.1, 0.0, 0.0, 67.7, 15, 0.10, 0.15, 4, 12, 127, 1.0));
        foodMap.put("Beef Ground", new FoodData("Meat and Poultry", 100,322, 0, 30, 14.4, 0.0, 0.0, 54.3, 4, 0.04, 0.15, 0, 24, 66, 1.64));
        foodMap.put("Pork Chop", new FoodData("Meat and Poultry", 100,390, 0.0, 36.9, 14.5, 0.0, 0.0, 47.8, 5, 0.56, 0.11, 1, 15, 39, 0.8));

        //Seafood Products
        foodMap.put("Blue Crab", new FoodData("Seafood", 100, 98, 3.1, 0.9, 19.4, 0.0, 0.0, 74.6, 1, 0.05, 0.24, 0, 281, 494, 2.0));
        foodMap.put("Milkfish", new FoodData("Seafood", 100, 137, 0.0, 6.4, 19.8, 0.0, 0.0, 72.8, 135, 0.02, 0.10, 0, 44, 67, 1.2));
        foodMap.put("Squid", new FoodData("Seafood", 100, 71, 0.0, 1.0, 15.6, 0.0, 0.0, 82.2, 210, 0.00, 0.04, 0, 55, 143, 1.2));
        foodMap.put("Tilapia", new FoodData("Seafood", 100, 107, 0.0, 3.8, 18.1, 0.0, 0.0, 77.2, 65, 0.06, 0.20, 0, 74, 52, 0.8));

        //Cooked Dishes
        foodMap.put("Beef Kare-kare", new FoodData("Filipino Cooked Dishes", 100,220, 3.8, 15, 20, 2.5, 2, 65, 15, 0.2, 0.4, 20, 30, 600, 3.0));
        foodMap.put("Beef Ribs Sinigang", new FoodData("Filipino Cooked Dishes", 100,180, 2.5, 12, 18, 2, 1, 70, 10, 0.2, 0.3, 25, 20, 600, 2.5));
        foodMap.put("Bulalo", new FoodData("Filipino Cooked Dishes", 100,250, 4, 16, 20, 1.5, 0, 65, 10, 0.2, 0.4, 15, 15, 700, 3.0));
        foodMap.put("Chicken Adobo", new FoodData("Filipino Cooked Dishes", 100,180, 1, 12, 20, 1, 0, 65, 5, 0.1, 0.2, 10, 10, 600, 2.0));
        foodMap.put("Chicken Afritada", new FoodData("Filipino Cooked Dishes", 100,200, 2.5, 14, 18, 2, 1, 70, 10, 0.2, 0.3, 15, 15, 650, 2.5));
        foodMap.put("Pork Barbeque", new FoodData("Filipino Cooked Dishes", 100, 280, 10, 20, 18, 8, 0, 50, 4, 0.2, 0.3, 6, 8, 650, 3.0));
        foodMap.put("Pork Menudo", new FoodData("Filipino Cooked Dishes", 100, 200, 4, 12, 16, 2, 2, 65, 6, 0.2, 0.3, 10, 15, 600, 2.5));

    }

    public static FoodData getFoodData(String foodName) {
        return foodMap.get(foodName);
    }
}
