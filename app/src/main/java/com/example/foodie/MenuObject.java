package com.example.foodie;

public class MenuObject {
    private String name;
    private int[] price;
    public MenuObject(String name, int[] price)
    {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int[] getPrice(){
        return price;
    }
}
