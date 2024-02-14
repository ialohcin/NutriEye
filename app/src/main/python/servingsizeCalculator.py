import pandas as pd
import numpy as np
import os
from drniCalculator import calculateDRNI
from servingsizeRF import loadModel

foodname_dict = {'Green Apple': 24,
                 'Carabao Mango': 9,
                 'Lakatan Banana': 26,
                 'Guyabano': 25,
                 'Red Grapes': 39,
                 'Mangosteen': 28,
                 'Mandarin Orange': 27,
                 'Pomelo': 32,
                 'Rambutan': 37,
                 'Strawberry': 44,
                 'Broccoli': 6,
                 'Winged Bean': 49,
                 'Roma Tomato': 41,
                 'Red Bell Pepper': 38,
                 'Okra': 30,
                 'Eggplant': 23,
                 'Cucumber': 22,
                 'Cauliflower': 11,
                 'Carrot': 10,
                 'Bitter Gourd': 4,
                 'White Rice': 48,
                 'Brown Rice': 7,
                 'Sweet Corn': 45,
                 'White Bread': 47,
                 'Rolled Oats': 40,
                 'Chicken Eggs': 16,
                 'Chicken Thigh': 18,
                 'Chicken Wings': 20,
                 'Chicken Breast': 14,
                 'Chicken Drumstick': 15,
                 'Beef Ground': 0,
                 'Pork Belly': 34,
                 'Pork Chop': 35,
                 'Blue Crab': 5,
                 'Milkfish': 29,
                 'Squid': 43,
                 'Tilapia': 46,
                 'Beef Kaldereta': 1,
                 'Beef Kare-Kare': 2,
                 'Beef Ribs Sinigang': 3,
                 'Bulalo': 8,
                 'Chicken Adobo': 12,
                 'Chicken Afritada': 13,
                 'Chicken Inasal': 17,
                 'Chicken Tinola': 19,
                 'Crispy Pata': 21,
                 'Pork Barbeque': 33,
                 'Pancit Canton': 31,
                 'Pork Menudo': 36,
                 'Shrimp Sinigang': 42}


# Construct the absolute path to the CSV file
filename = os.path.join(os.path.dirname(__file__), 'data/philfctdata.csv')

nutrients_data = pd.read_csv(filename)

def calculateServingSize(foodName, age, sex, height, weight, is_pregnant, is_lactating, actlevel):
    food = nutrients_data[nutrients_data['FoodName'] == foodName]
    foodCal = float(food.iloc[0, 3])
    
    foodItem = foodname_dict[foodName]
    dcn, water, carbll, carbul, protll, protul, fatll, fatul, fibr, vitA, vitB1, vitB2, vitC, ca, na, fe = calculateDRNI(age, sex, height, weight, is_pregnant, is_lactating, actlevel)
    carb_ave = (carbll+carbul)/2
    prot_ave = (protll+protul)/2
    fat_ave = (fatll+fatul)/2
    
    inputdata = pd.DataFrame(columns = ['Calories', 'Carbohydrates', 'TotalFat', 'Protein','Fiber', 'Water', 'VitaminA', 'VitaminB1', 'VitaminB2', 'VitaminC', 'Calcium', 'Sodium', 'Iron', 'FoodName'])
    inputdata.loc[len(inputdata)] = [dcn/3, carb_ave/3, fat_ave/3, prot_ave/3, fibr/3, water/3, vitA/3, vitB1/3, vitB2/3, vitC/3, ca/3, na/3, fe/3, foodItem]
    rf = loadModel()
    rf_res = rf.predict(inputdata)
    
    servingRec = str(np.round(((dcn / 3) / foodCal) * 100, 2)) + 'g'
    return servingRec

# trial
print(str(calculateServingSize('Okra', 21, 'F', 144.78, 63.05, False, False, 'Sedentary')))