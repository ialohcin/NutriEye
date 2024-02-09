package com.example.nutrieye;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutrientOptimal {
    // Define optimalNutrients map
    private Map<String, Map<String, NutrientInfo>> optimalNutrients = new HashMap<>();

    // Define constructor to initialize optimalNutrients map
    public void NutrientCalculator() {
        initializeNutrients();
    }

    private void initializeNutrients() {

        // Example for 18 years old - Male
        Map<String, NutrientInfo> age18Male = new HashMap<>();
        age18Male.put("N/A", new NutrientInfo(3010, 330, 73, 85, 25, 50, 3010, 800, 1.4, 1.5, 90, 1000, 2000, 14));
        age18Male.put("Diabetes", new NutrientInfo(3010, 245, 65, 77, 25, 45, 3010, 1100, 1.7, 1.6, 110, 1300, 2200, 20));
        age18Male.put("Hypertension", new NutrientInfo(2820, 330, 73, 85, 25, 50, 2820, 1100, 1.7, 1.6, 90, 1000, 2000, 14));
        age18Male.put("Heart Disease", new NutrientInfo(3350, 330, 65, 77, 25, 45, 3350, 1100, 1.7, 1.6, 110, 1300, 2300, 20));
        age18Male.put("Kidney Disease", new NutrientInfo(3200, 300, 70, 80, 30, 60, 3200, 900, 1.5, 1.7, 95, 1100, 1800, 16));
        optimalNutrients.put("18_male", age18Male);

        // Example for 18 years old - Female
        Map<String, NutrientInfo> age18Female = new HashMap<>();
        age18Female.put("N/A", new NutrientInfo(2280, 330, 61, 70, 25, 50, 2280, 600, 1.2, 1.3, 70, 1000, 2000, 14));
        age18Female.put("Diabetes", new NutrientInfo(2580, 245, 60, 70, 25, 45, 2580, 900, 1.4, 1.3, 90, 1300, 2200, 20));
        age18Female.put("Hypertension", new NutrientInfo(2440, 330, 61, 70, 25, 50, 2440, 900, 1.4, 1.3, 70, 1000, 2000, 14));
        age18Female.put("Heart Disease", new NutrientInfo(2890, 330, 60, 70, 25, 45, 2890, 900, 1.4, 1.3, 90, 1300, 2300, 20));
        age18Female.put("Pregnant", new NutrientInfo(2580, 245, 60, 70, 25, 45, 2580, 1200, 1.6, 1.4, 85, 1000, 2200, 27));
        age18Female.put("Kidney Disease", new NutrientInfo(2280, 330, 65, 80, 25, 60, 2280, 800, 1.3, 1.5, 75, 1100, 1800, 16));
        optimalNutrients.put("18_female", age18Female);

        // Age Group (19-29 years old - Male)
        Map<String, NutrientInfo> age19_29Male = new HashMap<>();
        age19_29Male.put("N/A", new NutrientInfo(2530, 330, 71, 85, 25, 50, 2530, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age19_29Male.put("Diabetes", new NutrientInfo(2530, 230, 65, 77, 25, 45, 2530, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age19_29Male.put("Hypertension", new NutrientInfo(2390, 330, 71, 85, 25, 50, 2390, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age19_29Male.put("Heart Disease", new NutrientInfo(2840, 330, 65, 77, 25, 45, 2840, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age19_29Male.put("Kidney Disease", new NutrientInfo(2800, 350, 80, 90, 25, 60, 3200, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("19_29_male", age19_29Male);

        // Example for 19-29 years old - Female
        Map<String, NutrientInfo> age19_29Female = new HashMap<>();
        age19_29Female.put("N/A", new NutrientInfo(1930, 330, 62, 70, 25, 50, 1930, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age19_29Female.put("Diabetes", new NutrientInfo(2230, 245, 60, 70, 25, 45, 2230, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age19_29Female.put("Hypertension", new NutrientInfo(2090, 330, 62, 70, 25, 50, 2090, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age19_29Female.put("Heart Disease", new NutrientInfo(2540, 330, 60, 70, 25, 45, 2540, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age19_29Female.put("Pregnant", new NutrientInfo(2230, 245, 60, 70, 25, 45, 2230, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age19_29Female.put("Kidney Disease", new NutrientInfo(1930, 330, 65, 80, 25, 60, 1930, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("19_29_female", age19_29Female);

        // Age Group (30-49 years old - Male)
        Map<String, NutrientInfo> age30_49Male = new HashMap<>();
        age30_49Male.put("N/A", new NutrientInfo(2420, 330, 71, 85, 27, 50, 2420, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age30_49Male.put("Diabetes", new NutrientInfo(2420, 245, 65, 77, 27, 45, 2420, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age30_49Male.put("Hypertension", new NutrientInfo(2280, 330, 71, 85, 27, 50, 2280, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age30_49Male.put("Heart Disease", new NutrientInfo(2730, 330, 65, 77, 27, 45, 2730, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age30_49Male.put("Kidney Disease", new NutrientInfo(2700, 350, 80, 90, 27, 60, 3100, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("30_49_male", age30_49Male);

        // Example for 30-49 years old - Female
        Map<String, NutrientInfo> age30_49Female = new HashMap<>();
        age30_49Female.put("N/A", new NutrientInfo(1870, 330, 61, 70, 25, 50, 1870, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age30_49Female.put("Diabetes", new NutrientInfo(2170, 245, 60, 70, 25, 45, 2170, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age30_49Female.put("Hypertension", new NutrientInfo(2030, 330, 61, 70, 25, 50, 2030, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age30_49Female.put("Heart Disease", new NutrientInfo(2480, 330, 60, 70, 25, 45, 2480, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age30_49Female.put("Pregnant", new NutrientInfo(2170, 245, 60, 70, 25, 45, 2170, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age30_49Female.put("Kidney Disease", new NutrientInfo(1870, 330, 65, 80, 25, 60, 1870, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("30_49_female", age30_49Female);

        // Age Group (50-59 years old - Male)
        Map<String, NutrientInfo> age50_59Male = new HashMap<>();
        age50_59Male.put("N/A", new NutrientInfo(2420, 330, 71, 85, 28, 50, 2420, 700, 1.3, 1.4, 90, 750, 2000, 12));
        age50_59Male.put("Diabetes", new NutrientInfo(2420, 245, 65, 77, 28, 45, 2420, 1000, 1.6, 1.5, 110, 1000, 2200, 18));
        age50_59Male.put("Hypertension", new NutrientInfo(2280, 330, 71, 85, 28, 50, 2280, 1000, 1.6, 1.5, 90, 750, 2000, 12));
        age50_59Male.put("Heart Disease", new NutrientInfo(2730, 330, 65, 77, 28, 45, 2730, 1000, 1.6, 1.5, 110, 1000, 2300, 18));
        age50_59Male.put("Kidney Disease", new NutrientInfo(2600, 350, 80, 90, 28, 60, 3000, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("50_59_male", age50_59Male);

        // Example for 50-59 years old - Female
        Map<String, NutrientInfo> age50_59Female = new HashMap<>();
        age50_59Female.put("N/A", new NutrientInfo(1870, 330, 61, 70, 30, 50, 1870, 600, 1.2, 1.3, 70, 750, 2000, 14));
        age50_59Female.put("Diabetes", new NutrientInfo(2170, 245, 60, 70, 30, 45, 2170, 900, 1.5, 1.4, 90, 1000, 2200, 18));
        age50_59Female.put("Hypertension", new NutrientInfo(2030, 330, 61, 70, 30, 50, 2030, 900, 1.5, 1.4, 70, 750, 2000, 14));
        age50_59Female.put("Heart Disease", new NutrientInfo(2480, 330, 60, 70, 30, 45, 2480, 900, 1.5, 1.4, 90, 1000, 2300, 18));
        age50_59Female.put("Pregnant", new NutrientInfo(2170, 245, 60, 70, 30, 45, 2170, 1100, 1.7, 1.6, 85, 750, 2200, 27));
        age50_59Female.put("Kidney Disease", new NutrientInfo(1870, 330, 65, 80, 30, 60, 1870, 700, 1.4, 1.5, 75, 900, 1800, 16));
        optimalNutrients.put("50_59_female", age50_59Female);

        // Age Group (60-69 years old - Male)
        Map<String, NutrientInfo> age60_69Male = new HashMap<>();
        age60_69Male.put("N/A", new NutrientInfo(2140, 330, 71, 85, 28, 50, 2140, 700, 1.3, 1.4, 90, 800, 2000, 12));
        age60_69Male.put("Diabetes", new NutrientInfo(2140, 230, 65, 77, 28, 45, 2140, 1000, 1.6, 1.5, 110, 800, 2200, 18));
        age60_69Male.put("Hypertension", new NutrientInfo(2010, 330, 71, 85, 28, 50, 2010, 1000, 1.6, 1.5, 90, 800, 2000, 12));
        age60_69Male.put("Heart Disease", new NutrientInfo(2410, 330, 65, 77, 28, 45, 2410, 1000, 1.6, 1.5, 110, 800, 2300, 18));
        age60_69Male.put("Kidney Disease", new NutrientInfo(2500, 350, 80, 90, 28, 60, 2900, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("60_69_male", age60_69Male);

        // Example for 60-69 years old - Female
        Map<String, NutrientInfo> age60_69Female = new HashMap<>();
        age60_69Female.put("N/A", new NutrientInfo(1610, 330, 61, 70, 30, 50, 1610, 600, 1.2, 1.3, 70, 800, 2000, 14));
        age60_69Female.put("Diabetes", new NutrientInfo(1910, 230, 60, 70, 30, 45, 1910, 900, 1.5, 1.4, 90, 800, 2200, 18));
        age60_69Female.put("Hypertension", new NutrientInfo(1780, 330, 61, 70, 30, 50, 1780, 900, 1.5, 1.4, 70, 800, 2000, 14));
        age60_69Female.put("Heart Disease", new NutrientInfo(2170, 330, 60, 70, 30, 45, 2170, 900, 1.5, 1.4, 90, 800, 2300, 18));
        age60_69Female.put("Pregnant", new NutrientInfo(1910, 230, 60, 70, 30, 45, 1910, 1100, 1.7, 1.6, 85, 800, 2200, 27));
        age60_69Female.put("Kidney Disease", new NutrientInfo(1610, 330, 65, 80, 30, 60, 1610, 700, 1.4, 1.5, 75, 1000, 1800, 16));
        optimalNutrients.put("60_69_female", age60_69Female);

        // Age Group (70 years old and above - Male)
        Map<String, NutrientInfo> age70_aboveMale = new HashMap<>();
        age70_aboveMale.put("N/A", new NutrientInfo(1960, 330, 71, 85, 30, 50, 1960, 700, 1.3, 1.4, 90, 800, 2000, 12));
        age70_aboveMale.put("Diabetes", new NutrientInfo(1960, 230, 65, 77, 30, 45, 1960, 1000, 1.6, 1.5, 110, 800, 2200, 18));
        age70_aboveMale.put("Hypertension", new NutrientInfo(1830, 330, 71, 85, 30, 50, 1830, 1000, 1.6, 1.5, 90, 800, 2000, 12));
        age70_aboveMale.put("Heart Disease", new NutrientInfo(2220, 330, 65, 77, 30, 45, 2220, 1000, 1.6, 1.5, 110, 800, 2300, 18));
        age70_aboveMale.put("Kidney Disease", new NutrientInfo(2400, 350, 80, 90, 30, 60, 2800, 1000, 1.5, 1.6, 100, 1200, 2000, 18));
        optimalNutrients.put("70_above_male", age70_aboveMale);

        // Example for 70 and above years old - Female
        Map<String, NutrientInfo> age70_aboveFemale = new HashMap<>();
        age70_aboveFemale.put("N/A", new NutrientInfo(1540, 330, 61, 70, 27, 50, 1540, 600, 1.2, 1.3, 70, 800, 2000, 14));
        age70_aboveFemale.put("Diabetes", new NutrientInfo(1840, 230, 60, 70, 27, 45, 1840, 900, 1.5, 1.4, 90, 800, 2200, 18));
        age70_aboveFemale.put("Hypertension", new NutrientInfo(1710, 330, 61, 70, 27, 50, 1710, 900, 1.5, 1.4, 70, 800, 2000, 14));
        age70_aboveFemale.put("Heart Disease", new NutrientInfo(2100, 330, 60, 70, 28, 45, 2100, 900, 1.5, 1.4, 90, 800, 2300, 18));
        age70_aboveFemale.put("Pregnant", new NutrientInfo(1840, 230, 60, 70, 28, 45, 1840, 1100, 1.7, 1.6, 85, 800, 2200, 27));
        age70_aboveFemale.put("Kidney Disease", new NutrientInfo(1540, 330, 65, 80, 27, 60, 1540, 700, 1.4, 1.5, 75, 1000, 1800, 16));
        optimalNutrients.put("70_above_female", age70_aboveFemale);
    }

    // Define method to compute optimal nutrients based on input parameters
    public NutrientInfo computeOptimalNutrients(int age, String gender, List<String> healthConditions) {
        // Determine age group based on age
        String ageGroup = determineAgeGroup(age);
        // Determine gender group based on gender
        String genderGroup = determineGenderGroup(gender);

        // Get optimal nutrients for the determined age group and gender group
        Map<String, NutrientInfo> ageGenderGroup = optimalNutrients.get(ageGroup + "_" + genderGroup);

        // If health conditions are provided, compute average nutrient values
        if (healthConditions != null && !healthConditions.isEmpty()) {
            NutrientInfo averageNutrientInfo = new NutrientInfo(0,0,0,0,0,0,0,0,0,0,0,0,0,0);
            for (String condition : healthConditions) {
                NutrientInfo conditionNutrientInfo = ageGenderGroup.get(condition);
                averageNutrientInfo.add(conditionNutrientInfo);
            }
            // Divide by the number of conditions to get the average
            averageNutrientInfo.divide(healthConditions.size());
            return averageNutrientInfo;
        } else {
            // If no health conditions provided, return default nutrient values
            return ageGenderGroup.get("N/A");
        }
    }

    // Define method to determine age group based on age
    private String determineAgeGroup(int age) {
        if (age >= 18 && age <= 29) {
            return "18";
        } else if (age >= 30 && age <= 49) {
            return "30_49";
        } else if (age >= 50 && age <= 59) {
            return "50_59";
        } else if (age >= 60 && age <= 69) {
            return "60_69";
        } else {
            return "70_above";
        }
    }

    // Define method to determine gender group based on gender
    private String determineGenderGroup(String gender) {
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
