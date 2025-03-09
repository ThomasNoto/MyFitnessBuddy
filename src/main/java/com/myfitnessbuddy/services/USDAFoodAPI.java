package com.myfitnessbuddy.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class USDAFoodAPI {
    
    public static JSONObject searchFood(String foodName, String apiKey) throws Exception {

        String encodedFoodName = URLEncoder.encode(foodName, StandardCharsets.UTF_8.toString());
        
        //Print the encoded food name for debugging purposes
        //System.out.println("Encoded food name: " + encodedFoodName);

         //URL encode the food name to handle spaces and commas
        String urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" 
                            + encodedFoodName + "&api_key=" + apiKey;

        //Print the full URL for debugging purposes
        //System.out.println("Request URL: " + urlString);
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        //print response for debugging
        //System.out.println("JSON Response: " + response.toString());


        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray foods = jsonResponse.optJSONArray("foods");

        if (foods != null && foods.length() > 0) {
            //System.out.println(foods.getJSONObject(0).toString());
            return foods.getJSONObject(0); //return first match
        }
        return null; //No results found
    }

    public static JSONObject getFoodDetails(int fdcId, String apiKey) throws Exception {
        String urlString = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiKey;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return new JSONObject(response.toString());
    }
    
    /* Returns Double value based on Nutrient ID Given i.e. Protein value is 1003. Creates an array of values and iterates through the array
     * until the desired nutrient is found and then returned. Returns null if there is no nutrient data.
     * Returns -1 if there is nutrient data but nothing for the specific nutrient ID.
     * 
     * @Param foodData a JSONObject that is created after the search for a food
     * @Param nutirentId an int corresponding to nutrient value being targeted
     * @return a double of desired nutrient value. Defaults to -1
     */
    public static Double getNutrientValue(JSONObject foodData, int nutrientId) {
        JSONArray foodNutrients = foodData.optJSONArray("foodNutrients");
    
        if (foodNutrients != null) {
            for (int i = 0; i < foodNutrients.length(); i++) {
                JSONObject nutrient = foodNutrients.optJSONObject(i);
                if (nutrient != null && nutrient.optInt("nutrientId") == nutrientId) {
                    return nutrient.optDouble("value", -1.0);  // Return the value, defaulting to -1 if not found
                }
            }
        }
        return null;  // Return null if nutrientId is not found
    }

    /* Retrieves Protein based on foodName and requires API Key (Might Change in the future)
     * This function uses getNutrientValue() and searchFood() to find the food then extract data
     * 
     * @param foodName is the name of the food
     * @param apiKey is the USDAFood API key
     * @return Double value for protein on success. On failure returns -1.0
     */
    public static Double getProtein(String foodName, String apiKey){
        JSONObject foodItem;
        try {
            foodItem = searchFood(foodName, apiKey);
            return getNutrientValue(foodItem, 1003);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return -1.0;
    }

    public static Double getCarbs(String foodName, String apiKey){
        JSONObject foodItem;
        try {
            foodItem = searchFood(foodName, apiKey);
            return getNutrientValue(foodItem, 1005);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return -1.0;
    }

    public static Double getFats(String foodName, String apiKey){
        JSONObject foodItem;
        try {
            foodItem = searchFood(foodName, apiKey);
            return getNutrientValue(foodItem, 1004);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return -1.0;
    }

    public static Double getSugar(String foodName, String apiKey){
        JSONObject foodItem;
        try {
            foodItem = searchFood(foodName, apiKey);
            return getNutrientValue(foodItem, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return -1.0;
    }

    public static Double getCalories(String foodName, String apiKey){
        JSONObject foodItem;
        try {
            foodItem = searchFood(foodName, apiKey);
            return getNutrientValue(foodItem, 1008);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return -1.0;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Get user input for API key
            String apiKey = "1oy4bfB9ZiqzRv89bjspa5isaCb1YfSeDIVNkjtL";
    
            // Get user input for food name
            System.out.print("Enter the food name: ");
            String foodName = scanner.nextLine();
    
            // Fetch food details
            JSONObject foodItem = searchFood(foodName, apiKey);

            // Get nutrient values
            int Protein = 1003;
            int Carbs = 1005;
            int Fats = 1004;
            int Sugar = 2000;
            int Calories = 1008;
            Double ProteinVal = getNutrientValue(foodItem, Protein);
            Double ProteinVal2 = getProtein(foodName, apiKey);
            Double CarbsVal = getNutrientValue(foodItem, Carbs);
            Double FatsVal = getNutrientValue(foodItem, Fats);
            Double SugarVal = getNutrientValue(foodItem, Sugar);
            Double CaloriesVal = getNutrientValue(foodItem, Calories);
    
            // Print nutritional information
            System.out.println("\nNutritional Information for: " + foodName);
            System.out.println("Protein: " + ProteinVal + "g vs " + ProteinVal2 + "g");
            System.out.println("Carbs: " + CarbsVal + "g");
            System.out.println("Fats: " + FatsVal + "g");
            System.out.println("Sugar: " + SugarVal + "g");
            System.out.println("Calories: " + CaloriesVal);
            //System.out.println("Serving size: " + Servingsize + " g");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

