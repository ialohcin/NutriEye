import pandas as pd
import numpy as np
import os

np.set_printoptions(precision = 3, suppress = True)

# Construct the absolute path to the CSV file
filename = os.path.join(os.path.dirname(__file__), 'data/drimicro.csv')

# Read the CSV file
micronutrients_data = pd.read_csv(filename)

# calculate Daily Recommended Nutrient Intake
def calculateDRNI(age, sex, height, weight, is_pregnant, is_lactating, actlevel):
    
    # Activity Level Rating Multiplier
    if(actlevel == 'Sedentary'):
        arm = 1.2
    elif(actlevel == 'Inactive'):
        arm = 1.375
    elif(actlevel == 'Low Active'):
        arm = 1.55
    elif(actlevel == 'Active'):
        arm = 1.725
    elif(actlevel == 'Very Active'):
        arm = 1.9
    elif(actlevel == 'Athlete'):
        arm = 2.3
    else:
        return 'Error in encoding physical activity level'
    
    # DRI Category
    if(is_pregnant):
        cat = 'Pregnant'
    elif(is_lactating):
        cat = 'Lactating'
    elif(age > 69):
        cat = '> 69 y.o.'
    elif(age > 59):
        cat = '60 – 69 y.o.'
    elif(age > 49):
        cat = '50 – 59 y.o.'
    elif(age > 29):
        cat = '30 – 49 y.o.'
    elif(age > 18):
        cat = '19 – 29 y.o.'
    else:
        cat = 'minor'
        return 'Age not supported'
    
    cat_record = micronutrients_data[micronutrients_data['DRI'] == cat]
    
    if(sex == 'M'):
        bmr = (10*weight)+(6.25*height)+(5*age)+5
        vitaminA = float(cat_record.iloc[0, 1])
        vitaminB1 = float(cat_record.iloc[0, 3])
        vitaminB2 = float(cat_record.iloc[0, 5])
        vitaminC = float(cat_record.iloc[0, 7])
        calcium = float(cat_record.iloc[0, 9])
        sodium = float(cat_record.iloc[0, 11])
        iron = float(cat_record.iloc[0, 13])
    elif(sex == 'F'):
        bmr = ((10*weight)+(6.25*height)+(5*age)-161)
        vitaminA = float(cat_record.iloc[0, 2])
        vitaminB1 = float(cat_record.iloc[0, 4])
        vitaminB2 = float(cat_record.iloc[0, 6])
        vitaminC = float(cat_record.iloc[0, 8])
        calcium = float(cat_record.iloc[0, 10])
        sodium = float(cat_record.iloc[0, 12])
        iron = float(cat_record.iloc[0, 14])
    else:
        return 'Error in encoding sex'
    
    # Daily Caloric Needs
    dcn = bmr*arm

    # ll = lower limit
    # ul = upper limit
    water = dcn
    carbohydrates_ll = np.round((dcn*0.45)/4, 2)
    carbohydrates_ul = np.round((dcn*0.65)/4, 2)
    protein_ll = np.round((dcn*0.10)/4, 2)
    protein_ul = np.round((dcn*0.30)/4, 2)
    fats_ll = np.round((dcn*0.25)/9, 2)
    fats_ul = np.round((dcn*0.35)/9, 2)
    fiber = np.round((dcn/1000)*14, 2)
    
    # DCN = in kcal
    # Water = in mL
    # Carbohydrates, Protein, Fats, Fiber = in g
    # Vitamin A = in mcg
    # Vitamin B1, Vitamin B2, Vitamin C, Calcium, Sodium, Iron = in mg
    drni = [dcn, water, carbohydrates_ll, carbohydrates_ul, protein_ll, protein_ul, fats_ll, fats_ul, fiber, vitaminA, vitaminB1, vitaminB2, vitaminC, calcium, sodium, iron]
    
    return drni

# trial
print(calculateDRNI(21, 'F', 144.78, 63.05, False, False, 'Sedentary'))
print(calculateDRNI(21, 'F', 144.78, 63.05, False, False, 'Athlete'))
