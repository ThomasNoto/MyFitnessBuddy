package com.myfitnessbuddy.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    private String exerciseName;
    private List<String> muscleGroupsWorked;
    private int sets;
    private int reps;
    private double weight;
    private double durationMinutes;
    private double caloriesBurned; 
    private LocalDate date;

    public Exercise() {}

    public Exercise(User user, String exerciseName, List<String> muscleGroupsWorked, int sets, int reps, double weight, double durationMinutes, double caloriesBurned, LocalDate date) {
        this.user = user;
        this.exerciseName = exerciseName;
        this.muscleGroupsWorked = muscleGroupsWorked;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.date = date;
    }

    // GETTERS
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getExerciseName() { return exerciseName; }
    public List<String> getMuscleGroupsWorked() { return muscleGroupsWorked; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public double getDurationMinutes() { return durationMinutes; }
    public double getCaloriesBurned() { return caloriesBurned; }
    public LocalDate getDate() { return date; }

    // SETTERS
    public void setUser(User user) { this.user = user; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setMuscleGroupsWorked(List<String> muscleGroupsWorked) { this.muscleGroupsWorked = muscleGroupsWorked; }
    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setDurationMinutes(double durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public void setDate(LocalDate date) { this.date = date; }
}

