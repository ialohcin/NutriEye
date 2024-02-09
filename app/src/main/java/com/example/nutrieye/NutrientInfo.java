package com.example.nutrieye;

import java.util.HashMap;
import java.util.Map;

public class NutrientInfo {
    double calories;
    double carbohydrates;
    double protein;
    double fats;
    double fiber;
    double sugar;
    double water;
    double vitaminA;
    double vitaminB1;
    double vitaminB2;
    double vitaminC;
    double calcium;
    double sodium;
    double iron;

    public NutrientInfo(double calories, double carbohydrates, double protein, double fats, double fiber,
                        double sugar, double water, double vitaminA, double vitaminB1, double vitaminB2,
                        double vitaminC, double calcium, double sodium, double iron) {
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fats = fats;
        this.fiber = fiber;
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

    public void add(NutrientInfo other) {
        this.calories += other.calories;
        this.carbohydrates += other.carbohydrates;
        this.protein += other.protein;
        this.fats += other.fats;
        this.fiber += other.fiber;
        this.sugar += other.sugar;
        this.water += other.water;
        this.vitaminA += other.vitaminA;
        this.vitaminB1 += other.vitaminB1;
        this.vitaminB2 += other.vitaminB2;
        this.vitaminC += other.vitaminC;
        this.calcium += other.calcium;
        this.sodium += other.sodium;
        this.iron += other.iron;
    }

    public void divide(double divisor) {
        this.calories /= divisor;
        this.carbohydrates /= divisor;
        this.protein /= divisor;
        this.fats /= divisor;
        this.fiber /= divisor;
        this.sugar /= divisor;
        this.water /= divisor;
        this.vitaminA /= divisor;
        this.vitaminB1 /= divisor;
        this.vitaminB2 /= divisor;
        this.vitaminC /= divisor;
        this.calcium /= divisor;
        this.sodium /= divisor;
        this.iron /= divisor;
    }

    // Map to store Optimal Nutrients for different age groups and health conditions
    private static Map<String, Map<String, NutrientInfo>> optimalNutrients = new HashMap<>();

    // Method to populate Optimal Nutrients
    public static void populateOptimalNutrients() {

        // Example for 18 years old - Male
        Map<String, NutrientInfo> age18Male = new HashMap<>();
        age18Male.put("N/A", new NutrientInfo(3010, 330, 73, 85, 25, 50, 3010, 800, 1.4, 1.5, 90, 1000, 2000, 14));
        age18Male.put("diabetes", new NutrientInfo(3010, 245, 65, 77, 25, 45, 3010, 1100, 1.7, 1.6, 110, 1300, 2200, 20));
        age18Male.put("hypertension", new NutrientInfo(2820, 330, 73, 85, 25, 50, 2820, 1100, 1.7, 1.6, 90, 1000, 2000, 14));
        age18Male.put("heart disease", new NutrientInfo(3350, 330, 65, 77, 25, 45, 3350, 1100, 1.7, 1.6, 110, 1300, 2300, 20));
        age18Male.put("kidney disease", new NutrientInfo(3200, 300, 70, 80, 30, 60, 3200, 900, 1.5, 1.7, 95, 1100, 1800, 16));
        optimalNutrients.put("18_male", age18Male);

        // Example for 18 years old - Female
        Map<String, NutrientInfo> age18Female = new HashMap<>();
        age18Female.put("N/A", new NutrientInfo(2280, 330, 61, 70, 25, 50, 2280, 600, 1.2, 1.3, 70, 1000, 2000, 14));
        age18Female.put("diabetes", new NutrientInfo(2580, 245, 60, 70, 25, 45, 2580, 900, 1.4, 1.3, 90, 1300, 2200, 20));
        age18Female.put("hypertension", new NutrientInfo(2440, 330, 61, 70, 25, 50, 2440, 900, 1.4, 1.3, 70, 1000, 2000, 14));
        age18Female.put("heart disease", new NutrientInfo(2890, 330, 60, 70, 25, 45, 2890, 900, 1.4, 1.3, 90, 1300, 2300, 20));
        age18Female.put("pregnancy", new NutrientInfo(2580, 245, 60, 70, 25, 45, 2580, 1200, 1.6, 1.4, 85, 1000, 2200, 27));
        age18Female.put("kidney disease", new NutrientInfo(2280, 330, 65, 80, 25, 60, 2280, 800, 1.3, 1.5, 75, 1100, 1800, 16));
        optimalNutrients.put("18_female", age18Female);

        // Age Group (19-29 years old - Male)
        Map<String, NutrientInfo> age19_29Male = new HashMap<>();
        age19_29Male.put("N/A", new NutrientInfo(2530, 330, 71, 85, 25, 50, 2530, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age19_29Male.put("diabetes", new NutrientInfo(2530, 230, 65, 77, 25, 45, 2530, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age19_29Male.put("hypertension", new NutrientInfo(2390, 330, 71, 85, 25, 50, 2390, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age19_29Male.put("heart disease", new NutrientInfo(2840, 330, 65, 77, 25, 45, 2840, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age19_29Male.put("kidney disease", new NutrientInfo(2800, 350, 80, 90, 25, 60, 3200, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("19_29_male", age19_29Male);

        // Example for 19-29 years old - Female
        Map<String, NutrientInfo> age19_29Female = new HashMap<>();
        age19_29Female.put("N/A", new NutrientInfo(1930, 330, 62, 70, 25, 50, 1930, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age19_29Female.put("diabetes", new NutrientInfo(2230, 245, 60, 70, 25, 45, 2230, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age19_29Female.put("hypertension", new NutrientInfo(2090, 330, 62, 70, 25, 50, 2090, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age19_29Female.put("heart disease", new NutrientInfo(2540, 330, 60, 70, 25, 45, 2540, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age19_29Female.put("pregnancy", new NutrientInfo(2230, 245, 60, 70, 25, 45, 2230, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age19_29Female.put("kidney disease", new NutrientInfo(1930, 330, 65, 80, 25, 60, 1930, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("19_29_female", age19_29Female);

        // Age Group (30-49 years old - Male)
        Map<String, NutrientInfo> age30_49Male = new HashMap<>();
        age30_49Male.put("N/A", new NutrientInfo(2420, 330, 71, 85, 27, 50, 2420, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age30_49Male.put("diabetes", new NutrientInfo(2420, 245, 65, 77, 27, 45, 2420, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age30_49Male.put("hypertension", new NutrientInfo(2280, 330, 71, 85, 27, 50, 2280, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age30_49Male.put("heart disease", new NutrientInfo(2730, 330, 65, 77, 27, 45, 2730, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age30_49Male.put("kidney disease", new NutrientInfo(2700, 350, 80, 90, 27, 60, 3100, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("30_49_male", age30_49Male);

        // Example for 30-49 years old - Female
        Map<String, NutrientInfo> age30_49Female = new HashMap<>();
        age30_49Female.put("N/A", new NutrientInfo(1870, 330, 61, 70, 25, 50, 1870, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age30_49Female.put("diabetes", new NutrientInfo(2170, 245, 60, 70, 25, 45, 2170, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age30_49Female.put("hypertension", new NutrientInfo(2030, 330, 61, 70, 25, 50, 2030, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age30_49Female.put("heart disease", new NutrientInfo(2480, 330, 60, 70, 25, 45, 2480, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age30_49Female.put("pregnancy", new NutrientInfo(2170, 245, 60, 70, 25, 45, 2170, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age30_49Female.put("kidney disease", new NutrientInfo(1870, 330, 65, 80, 25, 60, 1870, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("30_49_female", age30_49Female);

        // Age Group (50-59 years old - Male)
        Map<String, NutrientInfo> age50_59Male = new HashMap<>();
        age50_59Male.put("N/A", new NutrientInfo(2420, 330, 71, 85, 28, 50, 2420, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age50_59Male.put("diabetes", new NutrientInfo(2420, 245, 65, 77, 28, 45, 2420, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age50_59Male.put("hypertension", new NutrientInfo(2280, 330, 71, 85, 28, 50, 2280, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age50_59Male.put("heart disease", new NutrientInfo(2730, 330, 65, 77, 28, 45, 2730, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age50_59Male.put("kidney disease", new NutrientInfo(2600, 350, 80, 90, 28, 60, 3000, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("50_59_male", age50_59Male);

        // Example for 50-59 years old - Female
        Map<String, NutrientInfo> age50_59Female = new HashMap<>();
        age50_59Female.put("N/A", new NutrientInfo(1870, 330, 61, 70, 30, 50, 1870, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age50_59Female.put("diabetes", new NutrientInfo(2170, 245, 60, 70, 30, 45, 2170, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age50_59Female.put("hypertension", new NutrientInfo(2030, 330, 61, 70, 30, 50, 2030, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age50_59Female.put("heart disease", new NutrientInfo(2480, 330, 60, 70, 30, 45, 2480, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age50_59Female.put("pregnancy", new NutrientInfo(2170, 245, 60, 70, 30, 45, 2170, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age50_59Female.put("kidney disease", new NutrientInfo(1870, 330, 65, 80, 30, 60, 1870, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("50_59_female", age50_59Female);

        // Age Group (60-69 years old - Male)
        Map<String, NutrientInfo> age60_69Male = new HashMap<>();
        age60_69Male.put("N/A", new NutrientInfo(2140, 330, 71, 85, 28, 50, 2140, 700, 1.3, 1.4, 90, 800, 2000, 12));
        age60_69Male.put("diabetes", new NutrientInfo(2140, 230, 65, 77, 28, 45, 2140, 1000, 1.6, 1.5, 110, 800, 2200, 18));
        age60_69Male.put("hypertension", new NutrientInfo(2010, 330, 71, 85, 28, 50, 2010, 1000, 1.6, 1.5, 90, 800, 2000, 12));
        age60_69Male.put("heart disease", new NutrientInfo(2410, 330, 65, 77, 28, 45, 2410, 1000, 1.6, 1.5, 110, 800, 2300, 18));
        age60_69Male.put("kidney disease", new NutrientInfo(2500, 350, 80, 90, 28, 60, 2900, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("60_69_male", age60_69Male);

        // Example for 60-69 years old - Female
        Map<String, NutrientInfo> age60_69Female = new HashMap<>();
        age60_69Female.put("N/A", new NutrientInfo(1610, 330, 61, 70, 30, 50, 1610, 600, 1.2, 1.3, 70, 800, 2000, 14));
        age60_69Female.put("diabetes", new NutrientInfo(1910, 230, 60, 70, 30, 45, 1910, 900, 1.5, 1.4, 90, 800, 2200, 18));
        age60_69Female.put("hypertension", new NutrientInfo(1780, 330, 61, 70, 30, 50, 1780, 900, 1.5, 1.4, 70, 800, 2000, 14));
        age60_69Female.put("heart disease", new NutrientInfo(2170, 330, 60, 70, 30, 45, 2170, 900, 1.5, 1.4, 90, 800, 2300, 18));
        age60_69Female.put("pregnancy", new NutrientInfo(1910, 230, 60, 70, 30, 45, 1910, 1100, 1.7, 1.6, 85, 800, 2200, 27));
        age60_69Female.put("kidney disease", new NutrientInfo(1610, 330, 65, 80, 30, 60, 1610, 700, 1.4, 1.5, 75, 1000, 1800, 16));
        optimalNutrients.put("60_69_female", age60_69Female);

        // Age Group (70 years old and above - Male)
        Map<String, NutrientInfo> age70_aboveMale = new HashMap<>();
        age70_aboveMale.put("N/A", new NutrientInfo(1960, 330, 71, 85, 30, 50, 1960, 700, 1.3, 1.4, 90, 800, 2000, 12));
        age70_aboveMale.put("diabetes", new NutrientInfo(1960, 230, 65, 77, 30, 45, 1960, 1000, 1.6, 1.5, 110, 800, 2200, 18));
        age70_aboveMale.put("hypertension", new NutrientInfo(1830, 330, 71, 85, 30, 50, 1830, 1000, 1.6, 1.5, 90, 800, 2000, 12));
        age70_aboveMale.put("heart disease", new NutrientInfo(2220, 330, 65, 77, 30, 45, 2220, 1000, 1.6, 1.5, 110, 800, 2300, 18));
        age70_aboveMale.put("kidney disease", new NutrientInfo(2400, 350, 80, 90, 30, 60, 2800, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("70_above_male", age70_aboveMale);

        // Example for 70 and above years old - Female
        Map<String, NutrientInfo> age70_aboveFemale = new HashMap<>();
        age70_aboveFemale.put("N/A", new NutrientInfo(1540, 330, 61, 70, 27, 50, 1540, 600, 1.2, 1.3, 70, 800, 2000, 14));
        age70_aboveFemale.put("diabetes", new NutrientInfo(1840, 230, 60, 70, 27, 45, 1840, 900, 1.5, 1.4, 90, 800, 2200, 18));
        age70_aboveFemale.put("hypertension", new NutrientInfo(1710, 330, 61, 70, 27, 50, 1710, 900, 1.5, 1.4, 70, 800, 2000, 14));
        age70_aboveFemale.put("heart disease", new NutrientInfo(2100, 330, 60, 70, 28, 45, 2100, 900, 1.5, 1.4, 90, 800, 2300, 18));
        age70_aboveFemale.put("pregnancy", new NutrientInfo(1840, 230, 60, 70, 28, 45, 1840, 1100, 1.7, 1.6, 85, 800, 2200, 27));
        age70_aboveFemale.put("kidney disease", new NutrientInfo(1540, 330, 65, 80, 27, 60, 1540, 700, 1.4, 1.5, 75, 1000, 1800, 16));
        optimalNutrients.put("70_above_female", age70_aboveFemale);

    }

    public static NutrientInfo computeOptimalNutrients(int age, String gender, String healthConditions) {
        String ageGroupKey = calculateAgeGroupKey(age);
        String genderKey = determineGenderGroup(gender);
        String combinedKey = ageGroupKey + genderKey;

        if (optimalNutrients.containsKey(combinedKey)) {
            Map<String, NutrientInfo> ageGroupMap = optimalNutrients.get(combinedKey);

            if (healthConditions != null && !healthConditions.isEmpty()) {
                String[] conditions = healthConditions.trim().split(", ");
                NutrientInfo sumNutrientInfo = new NutrientInfo(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                int foundConditions = 0;

                for (String condition : conditions) {
                    if (!"N/A".equals(condition)) {
                        condition = condition.toLowerCase();
                    }
                    if (ageGroupMap.containsKey(condition)) {
                        NutrientInfo conditionNutrientInfo = ageGroupMap.get(condition);
                        sumNutrientInfo.add(conditionNutrientInfo);
                        foundConditions++;
                    }
                }

                if (foundConditions > 0) {
                    // Divide by the number of conditions found to get the average
                    sumNutrientInfo.divide(foundConditions);
                    return sumNutrientInfo;
                }
            }
        }

        return null;
    }

    private static String calculateAgeGroupKey(int age) {
        if (age == 18) {
            return "18_";
        } else if (age >= 19 && age <= 29) {
            return "19_29_";
        } else if (age >= 30 && age <= 49) {
            return "30_49_";
        } else if (age >= 50 && age <= 59) {
            return "50_59_";
        } else if (age >= 60 && age <= 69) {
            return "60_69_";
        } else if (age >= 70) {
            return "70_above_";
        } else {
            return null;
        }
    }

    // Define method to determine gender group based on gender
    private static String determineGenderGroup(String gender) {
        if (gender.equalsIgnoreCase("male")) {
            return "male";
        } else if (gender.equalsIgnoreCase("female")) {
            return "female";
        } else {
            // Handle other cases if needed
            return "unknown";
        }
    }

}
