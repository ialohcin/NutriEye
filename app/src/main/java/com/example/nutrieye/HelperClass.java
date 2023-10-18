package com.example.nutrieye;

public class HelperClass {

    String email, password, confirmPass, firstName, lastName, DOB, Sex, contactNum , height, weight, phyActivity, foodAllergens, healthConditions;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhyActivity() {
        return phyActivity;
    }

    public void setPhyActivity(String phyActivity) {
        this.phyActivity = phyActivity;
    }

    public String getFoodAllergens() {
        return foodAllergens;
    }

    public void setFoodAllergens(String foodAllergens) {
        this.foodAllergens = foodAllergens;
    }

    public String getHealthConditions() {
        return healthConditions;
    }

    public void setHealthConditions(String healthConditions) {
        this.healthConditions = healthConditions;
    }

    public HelperClass(String email, String password, String confirmPass, String firstName, String lastName, String DOB, String sex, String contactNum, String height, String weight, String phyActivity, String foodAllergens, String healthConditions) {
        this.email = email;
        this.password = password;
        this.confirmPass = confirmPass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.Sex = sex;
        this.contactNum = contactNum;
        this.height = height;
        this.weight = weight;
        this.phyActivity = phyActivity;
        this.foodAllergens = foodAllergens;
        this.healthConditions = healthConditions;
    }
}
