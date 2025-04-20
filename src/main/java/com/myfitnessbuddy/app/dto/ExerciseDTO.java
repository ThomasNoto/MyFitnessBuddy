package com.myfitnessbuddy.app.dto;

import java.time.LocalDate;
import java.util.List;

public class ExerciseDTO {
    public Long userId;
    public int metIndex;
    public String exerciseName;
    public List<String> muscleGroupsWorked;
    public int sets;
    public int reps;
    public double weight;
    public double durationMinutes;
    public LocalDate date;

    // You can add getters/setters or use Lombok like:
    // @Getter @Setter @NoArgsConstructor
}
