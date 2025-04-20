package com.myfitnessbuddy.app.service;

import com.myfitnessbuddy.app.entity.Exercise;
import com.myfitnessbuddy.app.entity.User;
import com.myfitnessbuddy.app.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private UserService userService;

    private static final double[] MET_VALUES = {
        8.0,  // calisthenics
        6.0,  // weight training
        7.5,  // biking
        9.8,  // running
        3.5   // walking
    };

    // CREATE
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise createExerciseFromFrontend(Long userId, int metIndex, String exerciseName,
            List<String> muscleGroupsWorked, int sets, int reps,
            double weight, double durationMinutes, LocalDate date) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {

                Exercise exercise = new Exercise();

                exercise.setUser(user);
                exercise.setExerciseName(exerciseName);
                exercise.setMuscleGroupsWorked(muscleGroupsWorked);
                exercise.setSets(sets);
                exercise.setReps(reps);
                exercise.setWeight(weight);
                exercise.setDurationMinutes(durationMinutes);
                exercise.setCaloriesBurned(
                        calculateCaloriesBurned(metIndex, reps, sets, weight, user.getWeight(), durationMinutes));
                exercise.setDate(date);

                return exerciseRepository.save(exercise);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    // READ
    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id).orElse(null);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesByUserId(Long userId) {
        return exerciseRepository.findByUserId(userId);
    }

    // UPDATE
    public Exercise updateExercise(Long id, Exercise exerciseDetails) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);

        if (optionalExercise.isPresent()) {
            Exercise exercise = optionalExercise.get();
    
            exercise.setExerciseName(exerciseDetails.getExerciseName());
            exercise.setMuscleGroupsWorked(exerciseDetails.getMuscleGroupsWorked());
            exercise.setSets(exerciseDetails.getSets());
            exercise.setReps(exerciseDetails.getReps());
            exercise.setWeight(exerciseDetails.getWeight());
            exercise.setDurationMinutes(exerciseDetails.getDurationMinutes());
            exercise.setCaloriesBurned(exerciseDetails.getCaloriesBurned());
            exercise.setDate(exerciseDetails.getDate());
    
            return exerciseRepository.save(exercise);
        }
        return null;
    }

    // DELETE
    public boolean deleteExercise(Long id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private double calculateCaloriesBurned(int metIndex, int reps, int sets, double weight, double userWeight,
            double durationMinutes) {
        double totalLift = 0;
        double metValue = MET_VALUES[metIndex];

        if (metIndex == 1) { // indicates user selected weight training category
            totalLift = weight * reps * sets;
        }

        return Math.round((metValue * userWeight * (durationMinutes / 60.0)) + (totalLift * 0.0015));
    }
}
