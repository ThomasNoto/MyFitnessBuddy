package com.myfitnessbuddy.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // foreign key to map food items to user via the user column
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // foreign key to tie food items to users
    @JsonBackReference
    private User user;

    private String foodName;
    private double servingSize;
    private int calories;
    private double proteinAmount;
    private double carbAmount;
    private double fatAmount;
    private double sugarAmount;
    private LocalDate date; // LocalDate from java.time gives dates without time

    public FoodItem() {}

    public FoodItem(User user, String foodName, double servingSize, int calories, 
                    double proteinAmount, double carbAmount, double fatAmount, 
                    double sugarAmount, LocalDate date) {
        this.user = user;
        this.foodName = foodName;
        this.servingSize = servingSize;
        this.calories = calories;
        this.proteinAmount = proteinAmount;
        this.carbAmount = carbAmount;
        this.fatAmount = fatAmount;
        this.sugarAmount = sugarAmount;
        this.date = date;
    }

    // GETTERS
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getFoodName() { return foodName; }
    public double getServingSize() { return servingSize; }
    public int getCalories() { return calories; }
    public double getProteinAmount() { return proteinAmount; }
    public double getCarbAmount() { return carbAmount; }
    public double getFatAmount() { return fatAmount; }
    public double getSugarAmount() { return sugarAmount; }
    public LocalDate getDate() { return date; }

    // SETTERS
    public void setUser(User user) { this.user = user; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setServingSize(double servingSize) { this.servingSize = servingSize; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setProteinAmount(double proteinAmount) { this.proteinAmount = proteinAmount; }
    public void setCarbAmount(double carbAmount) { this.carbAmount = carbAmount; }
    public void setFatAmount(double fatAmount) { this.fatAmount = fatAmount; }
    public void setSugarAmount(double sugarAmount) { this.sugarAmount = sugarAmount; }
    public void setDate(LocalDate date) { this.date = date; }

    // consider add toString method for testing purposes
}