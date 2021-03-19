package main.java;

import main.java.beverages.Beverages;
import main.java.exceptions.InvalidQuantityException;
import main.java.exceptions.MachineConfigurationException;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class App {

    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        CoffeeMachine coffeeMachine = null;

        // Configuration file to initialize Coffee Machine.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File configFile = new File(classLoader.getResource("main/resources/configuration.json").getFile());

        try {
            coffeeMachine = CoffeeMachine.getCoffeeMachine(configFile);
        } catch (IOException e) {
            System.out.println("Unable to read file: " + e);
            return;
        } catch (ParseException e) {
            System.out.println("Unable to parse file: " + e);
            return;
        } catch (MachineConfigurationException | InvalidQuantityException e) {
            System.out.println(e.getMessage());
        }


        System.out.println("Available outlets: " + coffeeMachine.getOutletCount());
        System.out.println("\n");

        System.out.println("***** Available Beverages *****");
        for (Map.Entry<String, Beverages> beverages : coffeeMachine.getBeverages().entrySet()) {
            System.out.println(beverages.getValue().getSn() + ". " + beverages.getKey());
        }
        System.out.println("\n");


        while (true) {
            System.out.println("Select operation: ");
            System.out.println("1. Make Beverage");
            System.out.println("2. Update Ingredients");
            System.out.println("3. Get Ingredients status");

            int op = scanner.nextInt();
            switch (op) {
                case 1:
                    // Ex: "hot_tea,hot_coffee,black_tea,green_tea,hot_tea"
                    System.out.println("Enter the beverage name in comma separated string.");
                    String command = scanner.next();
                    makeBeverage(command, coffeeMachine);
                    break;
                case 2:
                    // Ex: "hot_water 500" without double quotes
                    System.out.println("Enter ingredient name and value: ");
                    String iName = scanner.next();
                    int value = scanner.nextInt();
                    updateIngredientValue(iName, value, coffeeMachine);
                    break;
                case 3:
                    showIngredientStatus(coffeeMachine);
                    break;
                default:
                    System.out.println("Operation not supported.");
            }
        }
    }


    private static void makeBeverage(String command, CoffeeMachine coffeeMachine) {
        List<Future<Response>> futures = new ArrayList<>();
        for (String str : command.split(",")) {
            futures.add(coffeeMachine.makeBeverage(str));
        }

        for (Future<Response> future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n");
        for (Map.Entry<String, Ingredients> ingredients : coffeeMachine.getIngredients().entrySet()) {
            System.out.println(ingredients.getKey() + " : " + ingredients.getValue().getQuantity());
        }
    }

    private static void updateIngredientValue(String iName, int value, CoffeeMachine coffeeMachine) {
        if (coffeeMachine.getIngredients() != null && coffeeMachine.getIngredients().containsKey(iName)) {
            try {
                Ingredients ingredients = coffeeMachine.getIngredients().get(iName);
                // You can remove and add ingredients until it is positive
                ingredients.updateQuantity((int) (ingredients.getQuantity() + value));
            } catch (InvalidQuantityException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Something wrong: either key is not present or ingredients is not created yet.");
        }
    }

    private static void showIngredientStatus(CoffeeMachine coffeeMachine) {
        System.out.println("Ingredients : Quantity");
        for (Map.Entry<String, Ingredients> ingredients : coffeeMachine.getIngredients().entrySet()) {
            System.out.println(ingredients.getKey() + " : " + ingredients.getValue().getQuantity());
        }
        System.out.println("\n");
    }
}
