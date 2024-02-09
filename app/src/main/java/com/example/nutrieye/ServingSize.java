package com.example.nutrieye;

public class ServingSize {

    public static String calculateServingSize(String foodName, String foodCategory) {
        String servingSizeText;

        switch (foodCategory) {
            case "Fruits":
                servingSizeText = calculateFruitsServingSize(foodName);
                break;
            case "Vegetables":
                servingSizeText = calculateVegetablesServingSize(foodName);
                break;
            case "Grains":
                servingSizeText = calculateGrainsServingSize(foodName);
                break;
            case "Meat and Poultry":
                servingSizeText = calculateMeatAndPoultryServingSize(foodName);
                break;
            case "Seafood Products":
                servingSizeText = calculateSeafoodProductsServingSize(foodName);
                break;
            case "Filipino Cooked Dishes":
                servingSizeText = calculateFilipinoCookedDishesServingSize(foodName);
                break;
            default:
                servingSizeText = "Unknown serving size";
                break;
        }

        return servingSizeText;
    }

    private static String calculateFruitsServingSize(String foodName) {
        switch (foodName) {
            case "Green Apple":
                return "1 Medium Apple (182g)";
            case "Lakatan Banana":
                return "1 Medium Banana (118g)";
            case "Mandarin Orange":
                return "1 Medium Orange (131g)";
            case "Red Grapes":
                return "1 Cup (92g)";
            case "Strawberry":
                return "1 Cup (152g)";
            case "Carabao Mango":
                return "1 Medium Mango (336g)";
            case "Rambutan":
                return "1 Cup (143g)";
            case "Guyabano":
                return "1 Cup (225g)";
            case "Pomelo":
                return "1 Cup (190g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateVegetablesServingSize(String foodName) {
        switch (foodName) {
            case "Bitter Gourd":
                return "1 Cup (94g)";
            case "Eggplant":
                return "1 Cup (82g)";
            case "Okra":
            case "Cauliflower":
                return "1 Cup (100g)";
            case "Broccoli":
                return "1 Cup (91g)";
            case "Carrots":
                return "1 Cup, Chopped (128g)";
            case "Cucumber":
                return "1 Cup, Sliced (119g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateGrainsServingSize(String foodName) {
        switch (foodName) {
            case "Brown Rice":
                return "1 Cup, Cooked (195g)";
            case "White Rice":
                return "1 Cup, Cooked (186g)";
            case "White Bread":
                return "1 Slice (25g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateMeatAndPoultryServingSize(String foodName) {
        switch (foodName) {
            case "Pork Chops":
                return "1 Chop, Boneless (136g)";
            case "Chicken Breast":
                return "1 Breast, Boneless, Skinless (172g)";
            case "Beef Ground":
                return "1 Cup, Cooked (220g)";
            case "Chicken Drumstick":
                return "1 Drumstick, Boneless, Skinless (44g)";
            case "Chicken Eggs":
                return "1 Large Egg (50g approximately)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateSeafoodProductsServingSize(String foodName) {
        switch (foodName) {
            case "Blue Crab":
                return "1 Crab (134g)";
            case "Milkfish":
                return "1 Fillet (180g)";
            case "Tilapia":
                return "1 Fillet (87g)";
            case "Squid":
                return "1 Cup, Sliced (85g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateFilipinoCookedDishesServingSize(String foodName) {
        switch (foodName) {
            case "Pork Barbeque":
                return "1 Skewer (80g)";
            case "Beef Ribs Sinigang":
            case "Chicken Adobo":
            case "Bulalo":
            case "Chicken Afritada":
            case "Pork Menudo":
                return "1 Cup (240g)";
            case "Beef Kare-Kare":
                return "1 Cup (250g)";
            default:
                return "Unknown serving size";
        }
    }
}
