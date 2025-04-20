package com.myfitnessbuddy.app.service;

import com.myfitnessbuddy.app.entity.FoodItem;
import com.myfitnessbuddy.app.entity.User;
import com.myfitnessbuddy.app.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private UserService userService;

    // Basic CRUD ops below :)
    // CREATE
    // creates a new food item
    public FoodItem createFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    //CREATE USING USDA
    public FoodItem createFoodItemFromUSDA(String foodName, Long userId, double servingSize, LocalDate date){
        try{
            FoodItem foodItem = loadFoodItemFromUSDA(foodName, servingSize, date);
            if(foodItem != null){
                User user = userService.getUserById(userId);
                if (user != null) {
                    foodItem.setUser(user);
                    return foodItemRepository.save(foodItem);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            
        }
        return new FoodItem(null, foodName, 0, -1, -1, -1, -1, -1, null);
    }

    // READ
    // gets fooditem by id
    public FoodItem getFoodItemById(Long id) {
        return foodItemRepository.findById(id).orElse(null);
    }

    // get all fooditems in db table
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    // gets list of food items associated with a given user id
    public List<FoodItem> getFoodItemsByUserId(Long userId) {
        return foodItemRepository.findByUserId(userId);
    }

    // UPDATE
    // update food item's details
    public FoodItem updateFoodItem(Long id, FoodItem foodItemDetails) {
        Optional<FoodItem> optionalFoodItem = foodItemRepository.findById(id);

        if (optionalFoodItem.isPresent()) {
            FoodItem foodItem = optionalFoodItem.get();
            
            foodItem.setFoodName(foodItemDetails.getFoodName());
            foodItem.setServingSize(foodItemDetails.getServingSize());
            foodItem.setCalories(foodItemDetails.getCalories());
            foodItem.setProteinAmount(foodItemDetails.getProteinAmount());
            foodItem.setCarbAmount(foodItemDetails.getCarbAmount());
            foodItem.setFatAmount(foodItemDetails.getFatAmount());
            foodItem.setSugarAmount(foodItemDetails.getSugarAmount());
            foodItem.setDate(foodItemDetails.getDate());
    
            return foodItemRepository.save(foodItem);
        }
        return null;
    }

    // DELETE
    public boolean deleteFoodItem(Long id) {
        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Factory method to create a FoodItem from USDA API
    public FoodItem loadFoodItemFromUSDA(String name, double servingSize, LocalDate date) {
        try {
            // Fetch the nutritional data from USDA API
            USDAFoodAPI api = new USDAFoodAPI();
            String apiKey = api.getApiKey();
            // rounds doubles to nearest 2 decimals places
            Double proteinAmount = Math.round(api.getProtein(name, apiKey) * servingSize * 100.0) / 100.0;
            Double carbAmount = Math.round(api.getCarbs(name, apiKey) * servingSize * 100.0) / 100.0;
            Double fatAmount = Math.round(api.getFats(name, apiKey) * servingSize * 100.0) / 100.0;
            Double sugarAmount = Math.round(api.getSugar(name, apiKey) * servingSize * 100.0) / 100.0;
            int calories = (int) Math.round(api.getCalories(name, apiKey) * servingSize);

            // Create and return a new FoodItem with the fetched data
            FoodItem foodItem = new FoodItem();
            foodItem.setFoodName(name);
            foodItem.setServingSize(servingSize);
            foodItem.setProteinAmount(proteinAmount);
            foodItem.setCarbAmount(carbAmount);
            foodItem.setFatAmount(fatAmount);
            foodItem.setSugarAmount(sugarAmount);
            foodItem.setCalories(calories);
            foodItem.setDate(date);

            return foodItem;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
