package com.myfitnessbuddy.app.controller;

import com.myfitnessbuddy.app.dto.ExerciseDTO;
import com.myfitnessbuddy.app.entity.Exercise;
import com.myfitnessbuddy.app.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;
    // CREATE
    @PostMapping("raw")
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise createdExercise = exerciseService.createExercise(exercise);
        return ResponseEntity.status(201).body(createdExercise);
    }

    @PostMapping
    public ResponseEntity<Exercise> createExerciseFromForm(@RequestBody ExerciseDTO dto) {
        Exercise createdExercise = exerciseService.createExerciseFromFrontend(
            dto.userId,
            dto.metIndex,
            dto.exerciseName,
            dto.muscleGroupsWorked,
            dto.sets,
            dto.reps,
            dto.weight,
            dto.durationMinutes,
            dto.date
        );

        return (createdExercise != null)
            ? ResponseEntity.ok(createdExercise)
            : ResponseEntity.badRequest().build();
    }

    // READ
    // get exercise by id
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return (exercise != null) ? ResponseEntity.ok(exercise) : ResponseEntity.notFound().build();
    }

    // get all exercises
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    // get all exercises associated with a given user by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Exercise>> getExercisesByUserId(@PathVariable Long userId) {
        List<Exercise> exercises = exerciseService.getExercisesByUserId(userId);
        return ResponseEntity.ok(exercises);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exerciseDetails) {
        Exercise updatedExercise = exerciseService.updateExercise(id, exerciseDetails);
        return (updatedExercise != null) ? ResponseEntity.ok(updatedExercise) : ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        boolean deleted = exerciseService.deleteExercise(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
