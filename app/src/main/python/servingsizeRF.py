import pandas as pd
import os

# Construct the absolute path to the CSV file
filename = os.path.join(os.path.dirname(__file__), 'data/nutrientsdata.csv')

nutrients_data = pd.read_csv(filename)
nutrients_data = nutrients_data.drop(['FoodGroup'], axis=1)

from sklearn.preprocessing import LabelEncoder
le = LabelEncoder()

ft_Food_Name = le.fit_transform(nutrients_data['FoodName'])
nutrients_data.drop('FoodName', axis=1, inplace=True)
nutrients_data['FoodName'] = ft_Food_Name

X = nutrients_data.drop('ServingSize', axis=1)
y = nutrients_data['ServingSize']

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.2, random_state = 42)

import numpy as np
y_train = np.log10(y_train)
y_test = np.log10(y_test)

from sklearn.ensemble import RandomForestRegressor
reg = RandomForestRegressor(n_estimators=100, random_state=42, n_jobs=-1)
reg.fit(X_train,y_train)

training_accuracy = reg.score(X_train, y_train)
test_accuracy = reg.score(X_test, y_test)
print("Training R2 Score = %0.4f\nTest R2 Score = %0.4f" % (training_accuracy, test_accuracy))

from sklearn.metrics import mean_squared_error
rmse_train = np.sqrt(mean_squared_error(reg.predict(X_train),y_train))
rmse_test = np.sqrt(mean_squared_error(reg.predict(X_test),y_test))
print("Training RMSE = %0.4f\nTest RMSE = %0.4f" % (rmse_train, rmse_test))

y_pred = reg.predict(X_test)
mae = abs(y_pred-y_test)
mape = 100*(mae/y_test)
print("MAE = %0.4f\nMAPE = %0.4f" % (round(np.mean(mae), 4), round(np.mean(mape), 4)))

import joblib

# Construct the absolute path to save the model
model_path = os.path.join(os.path.dirname(__file__), 'model/rf_servingsize.joblib')
joblib.dump(reg, model_path)

def loadModel():
    # Construct the absolute path to load the model
    model_path = os.path.join(os.path.dirname(__file__), 'model/rf_servingsize.joblib')
    rf = joblib.load(model_path)
    return rf
