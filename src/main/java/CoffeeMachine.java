package main.java;

import main.java.beverages.Beverages;
import main.java.exceptions.InvalidQuantityException;
import main.java.exceptions.MachineConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static main.java.Constants.*;

/**
 * CoffeeMachine contains all required feature
 * Ex. outletCount, available Ingredients, Beverages, threadPoolExecutor
 */
public class CoffeeMachine {

    private int outletCount;
    private HashMap<String, Ingredients> ingredients;
    private HashMap<String, Beverages> beverages;
    private final ThreadPoolExecutor threadPoolExecutor;

    private final static JSONParser parser = new JSONParser();

    private CoffeeMachine(int outletCount,
                          HashMap<String, Ingredients> ingredients,
                          HashMap<String, Beverages> beverages) {
        this.outletCount = outletCount;
        this.ingredients = ingredients;
        this.beverages = beverages;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.outletCount);
    }

    public int getOutletCount() {
        return outletCount;
    }

    public void setOutletCount(int outletCount) throws MachineConfigurationException {
        if (outletCount <= 0) {
            throw new MachineConfigurationException("Outlet count must be positive: " + outletCount);
        }
        this.outletCount = outletCount;
    }

    public HashMap<String, Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public HashMap<String, Beverages> getBeverages() {
        return beverages;
    }

    public void setBeverages(HashMap<String, Beverages> beverages) {
        this.beverages = beverages;
    }

    /**
     * Create CoffeeMachine based on given correct configuration file.
     *
     * @param conf                            : CoffeeMachine configuration file
     * @return                                : CoffeeMachine
     * @throws IOException                    : Thrown when conf file read error comes
     * @throws ParseException                 : Thrown when given json file is not in correct format
     * @throws MachineConfigurationException  : Thrown when data is inValid
     * @throws InvalidQuantityException       : Thrown when any quantity is invalid
     */
    public static CoffeeMachine getCoffeeMachine(File conf) throws IOException, ParseException, MachineConfigurationException, InvalidQuantityException {

        Object confObj = parser.parse(new FileReader(conf));

        JSONObject confJson = (JSONObject)confObj;

        final int outletCount = Math.toIntExact(getOutlets(confJson));
        final HashMap<String, Ingredients> ingredients = getIngredients(confJson);
        final HashMap<String, Beverages> beverages = getBeverages(confJson);

        return new CoffeeMachine(outletCount, ingredients, beverages);
    }

    private static Long getOutlets(JSONObject confJson) throws MachineConfigurationException {
        final Long outletCount = (Long) confJson.get(OUTLET_COUNT);
        if (outletCount <= 0) {
            throw new MachineConfigurationException("Outlet count must be positive: " + outletCount);
        }
        return outletCount;
    }

    private static HashMap<String, Ingredients> getIngredients(JSONObject confJson) throws InvalidQuantityException {
        HashMap<String, Ingredients> ingredients = new HashMap<>();

        JSONObject totalItemQuantity = (JSONObject) confJson.get(TOTAL_ITEM_QUANTITY);

        for (Object obj : totalItemQuantity.keySet()) {
            String key = (String) obj;
            Long value = (Long) totalItemQuantity.get(key);
            ingredients.put(key, createIngredients(key, value));
        }

        return ingredients;
    }

    private static Ingredients createIngredients(final String name,
                                                 final Long value) throws InvalidQuantityException {
        if (value <= 0) {
            throw new InvalidQuantityException("Quantity must be positive: " + value);
        }
        return new Ingredients(value, name);
    }

    private static HashMap<String, Beverages> getBeverages(JSONObject confJson) throws InvalidQuantityException {
        HashMap<String, Beverages> beverages = new HashMap<>();

        JSONObject beveragesJson = (JSONObject) confJson.get(BEVERAGES);

        int counter = 1;
        for (Object beverageName : beveragesJson.keySet()) {
            String key = (String) beverageName;
            JSONObject requiredIngredientsJson = (JSONObject) beveragesJson.get(key);

            HashMap<String, Long> requiredIngredients = new HashMap<>();

            for (Object ingredientsName : requiredIngredientsJson.keySet()) {
                String ingredientsKey = (String) ingredientsName;
                final Long quantity = (Long) requiredIngredientsJson.get(ingredientsKey);

                if (quantity <= 0) {
                    throw new InvalidQuantityException("Quantity must be positive: " + quantity);
                }

                requiredIngredients.put(ingredientsKey, quantity);
            }

            beverages.put(key, createBeverage(counter, key, requiredIngredients));
            counter++;
        }

        return beverages;
    }

    /**
     * Creates drink by checking the availability of all required ingredients for current beverage.
     *
     * @param beverage                  : Current beverage
     * @return Response                 : Response with status and error if occur
     * @throws InvalidQuantityException : Throws if some invalid quantity updates occur
     */
    public synchronized Response prepareDrinks(Beverages beverage) throws InvalidQuantityException, InterruptedException {

        System.out.println("Preparing " + beverage.getName() +"...");

        HashMap<String, String> status = new HashMap<>();

        // Checks whether enough quantity is available for all required Ingredients
        for (Map.Entry<String, Long> entry : beverage.getIngredients().entrySet()) {
            Ingredients ingredient = this.getIngredients().get(entry.getKey());
            if (ingredient == null) {
                status.put(entry.getKey(), "Not Available");
            }
            else if (ingredient.getQuantity() < entry.getValue()) {
                status.put(ingredient.getName(), " => Required: " + entry.getValue() + ", Available: " + ingredient.getQuantity());
            }
        }

        Thread.sleep(beverage.getTm());

        if (status.size() > 0) {
            return new Response(status, "Failed: " + beverage.getName());
        }

        // Make Beverage
        for (Map.Entry<String, Long> entry : beverage.getIngredients().entrySet()) {
            Ingredients ingredient = this.getIngredients().get(entry.getKey());
            ingredient.updateQuantity((int) (ingredient.getQuantity() - entry.getValue()));
        }

        return new Response(status, "Successful: " + beverage.getName());
    }

    private static Beverages createBeverage(int counter, String key,
                                            HashMap<String, Long> requiredIngredients) {
        return new Beverages((long) counter, key, requiredIngredients, BEVERAGE_TIME);
    }

    public Future<Response> makeBeverage(String str) {
        return threadPoolExecutor.submit(new BeverageMaker(str, this));
    }

    public void shutUp() {
        this.threadPoolExecutor.shutdown();
    }
}
