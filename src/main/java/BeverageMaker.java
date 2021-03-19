package main.java;

import java.util.concurrent.Callable;

public class BeverageMaker implements Callable<Response> {

    private final String name;
    private final CoffeeMachine coffeeMachine;

    public BeverageMaker(String str, CoffeeMachine coffeeMachine) {
        this.name = str;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public Response call() throws Exception {
        return coffeeMachine.prepareDrinks(coffeeMachine.getBeverages().get(name));
    }
}
