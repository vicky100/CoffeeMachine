package main.java.beverages;

import java.util.HashMap;

public class Beverages {

    private Long sn;
    private String name;
    private HashMap<String, Long> ingredients;
    private int tm;

    public Beverages(final Long sn, final String name, HashMap<String, Long> ingredients, int tm) {
        this.sn = sn;
        this.name = name;
        this.ingredients = ingredients;
        this.tm = tm;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Long> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, Long> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTm() {
        return tm;
    }

    public void setTm(int tm) {
        this.tm = tm;
    }
}
