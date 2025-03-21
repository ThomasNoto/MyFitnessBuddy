package com.myfitnessbuddy.app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// -============- USER ENTITY LAYER -============-
// defines structure of the users database table
// you can think of it as representing the user within the tables of the db
@Entity
@Table(name = "users")
public class User {
    // this id is a unique identify within the db table for user
    // it is the primary keey to find a specific user in the table
    // 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private double weight;
    private double height;
    private String fitnessGoals;
    private String gender;

    // One-To-Many relationship links this user with all rows in food_item table associated to it
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodItem> historicalFood = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> historicalExercise = new ArrayList<>();

    public User() {}

    // each field in the user object corresponds to a column in the user table
    // each row in this table will represent a new user
    public User(String name, int age, double weight, double height, String fitnessGoals, String gender) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.fitnessGoals = fitnessGoals;
        this.gender = gender;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public String getFitnessGoals() { return fitnessGoals; }
    public String getGender() { return gender; }
    public List<FoodItem> getHistoricalFood() { return historicalFood; }
    public List<Exercise> getHistoricalExercise() { return historicalExercise; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setHeight(double height) { this.height = height; }
    public void setFitnessGoals(String fitnessGoals) { this.fitnessGoals = fitnessGoals; }
    public void setGender(String gender) { this.gender = gender; }
    public void setHistoricalFood(List<FoodItem> historicalFood) { this.historicalFood = historicalFood; }
    public void setHistoricalExercise(List<Exercise> historicalExercise) { this.historicalExercise = historicalExercise; }
}
