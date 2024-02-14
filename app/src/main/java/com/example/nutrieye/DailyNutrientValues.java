package com.example.nutrieye;

public class DailyNutrientValues {
    private double dcn;
    private double water;
    private double carbohydrates_ll;
    private double carbohydrates_ul;
    private double protein_ll;
    private double protein_ul;
    private double fats_ll;
    private double fats_ul;
    private double fiber;
    private double vitaminA;
    private double vitaminB1;
    private double vitaminB2;
    private double vitaminC;
    private double calcium;
    private double sodium;
    private double iron;

    public DailyNutrientValues(double dcn, double water, double carbohydrates_ll, double carbohydrates_ul,
                               double protein_ll, double protein_ul, double fats_ll, double fats_ul,
                               double fiber, double vitaminA, double vitaminB1, double vitaminB2,
                               double vitaminC, double calcium, double sodium, double iron) {
        this.dcn = dcn;
        this.water = water;
        this.carbohydrates_ll = carbohydrates_ll;
        this.carbohydrates_ul = carbohydrates_ul;
        this.protein_ll = protein_ll;
        this.protein_ul = protein_ul;
        this.fats_ll = fats_ll;
        this.fats_ul = fats_ul;
        this.fiber = fiber;
        this.vitaminA = vitaminA;
        this.vitaminB1 = vitaminB1;
        this.vitaminB2 = vitaminB2;
        this.vitaminC = vitaminC;
        this.calcium = calcium;
        this.sodium = sodium;
        this.iron = iron;
    }

    public double getDcn() {
        return dcn;
    }

    public void setDcn(double dcn) {
        this.dcn = dcn;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getCarbohydrates_ll() {
        return carbohydrates_ll;
    }

    public void setCarbohydrates_ll(double carbohydrates_ll) {
        this.carbohydrates_ll = carbohydrates_ll;
    }

    public double getCarbohydrates_ul() {
        return carbohydrates_ul;
    }

    public void setCarbohydrates_ul(double carbohydrates_ul) {
        this.carbohydrates_ul = carbohydrates_ul;
    }

    public double getProtein_ll() {
        return protein_ll;
    }

    public void setProtein_ll(double protein_ll) {
        this.protein_ll = protein_ll;
    }

    public double getProtein_ul() {
        return protein_ul;
    }

    public void setProtein_ul(double protein_ul) {
        this.protein_ul = protein_ul;
    }

    public double getFats_ll() {
        return fats_ll;
    }

    public void setFats_ll(double fats_ll) {
        this.fats_ll = fats_ll;
    }

    public double getFats_ul() {
        return fats_ul;
    }

    public void setFats_ul(double fats_ul) {
        this.fats_ul = fats_ul;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminB1() {
        return vitaminB1;
    }

    public void setVitaminB1(double vitaminB1) {
        this.vitaminB1 = vitaminB1;
    }

    public double getVitaminB2() {
        return vitaminB2;
    }

    public void setVitaminB2(double vitaminB2) {
        this.vitaminB2 = vitaminB2;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }
}
