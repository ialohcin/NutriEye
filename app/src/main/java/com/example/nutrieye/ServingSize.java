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
                return "1 medium apple (182g)";
            case "Lakatan Banana":
                return "1 medium banana (118g)";
            case "Mandarin Orange":
                return "1 medium orange (131g)";
            case "Red Grapes":
                return "1 cup (92g)";
            case "Strawberry":
                return "1 cup (152g)";
            case "Carabao Mango":
                return "1 medium mango (336g)";
            case "Rambutan":
                return "1 cup (143g)";
            case "Guyabano":
                return "1 cup (225g)";
            case "Mangosteen":
                return "1 cup (196g)";
            case "Pomelo":
                return "1 cup (190g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateVegetablesServingSize(String foodName) {
        switch (foodName) {
            case "Bitter Gourd":
                return "1 cup (94g)";
            case "Eggplant":
                return "1 cup (82g)";
            case "Okra":
                return "1 cup (100g)";
            case "Winged Bean":
                return "1 cup (100g)";
            case "Broccoli":
                return "1 cup (91g)";
            case "Carrot":
                return "1 cup, chopped (128g)";
            case "Roma Tomato":
                return "1 medium tomato (123g)";
            case "Cucumber":
                return "1 cup, sliced (119g)";
            case "Red Bell Pepper":
                return "1 cup, chopped (149g)";
            case "Cauliflower":
                return "1 cup (100g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateGrainsServingSize(String foodName) {
        switch (foodName) {
            case "Brown Rice":
                return "1 cup, cooked (195g)";
            case "White Rice":
                return "1 cup, cooked (186g)";
            case "Rolled Oats":
                return "1 cup, cooked (234g)";
            case "Sweet Corn":
                return "1 cup (154g)";
            case "White Bread":
                return "1 slice (25g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateMeatAndPoultryServingSize(String foodName) {
        switch (foodName) {
            case "Pork Chops":
                return "1 chop, boneless (136g)";
            case "Chicken Breast":
                return "1 breast, boneless, skinless (172g)";
            case "Beef Ground":
                return "1 cup, cooked (220g)";
            case "Chicken Thigh":
                return "1 thigh, boneless, skinless (87g)";
            case "Chicken Wings":
                return "1 wing, boneless, skinless (84g)";
            case "Pork Belly":
                return "1 cup, sliced (81g)";
            case "Chicken Drumstick":
                return "1 drumstick, boneless, skinless (44g)";
            case "Chicken Eggs":
                return "1 large egg (50g approximately)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateSeafoodProductsServingSize(String foodName) {
        switch (foodName) {
            case "Blue Crab":
                return "1 crab (134g)";
            case "Milkfish":
                return "1 fillet (180g)";
            case "Tilapia":
                return "1 fillet (87g)";
            case "Spanish Mackerel":
                return "1 fillet (154g)";
            case "Squid":
                return "1 cup, sliced (85g)";
            default:
                return "Unknown serving size";
        }
    }

    private static String calculateFilipinoCookedDishesServingSize(String foodName) {
        switch (foodName) {
            case "Pancit Canton":
                return "1 cup (160g)";
            case "Pork Barbeque":
                return "1 skewer (80g)";
            case "Chicken Inasal":
                return "1 serving (200g)";
            case "Crispy Pata":
                return "1 serving (150g)";
            case "Beef Ribs Sinigang":
                return "1 cup (240g)";
            case "Beef Kare-Kare":
                return "1 cup (250g)";
            case "Shrimp Sinigang":
                return "1 cup (240g)";
            case "Chicken Adobo":
                return "1 cup (240g)";
            case "Bulalo":
                return "1 cup (240g)";
            case "Chicken Tinola":
                return "1 cup (240g)";
            case "Chicken Afritada":
                return "1 cup (240g)";
            case "Beef Kaldereta":
                return "1 cup (240g)";
            case "Pork Menudo":
                return "1 cup (240g)";
            default:
                return "Unknown serving size";
        }
    }
}
