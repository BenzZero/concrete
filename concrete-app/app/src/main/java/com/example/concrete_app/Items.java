package com.example.concrete_app;

public class Items {
    public int index;
    public String cube;
    public String itemName;
    public float price;
    public float installment;

    public Items(int index, String cube, float price, float installment) {
        this.index = index;
        this.cube = cube;
        this.price = price;
        this.installment = installment;
    }

    public String getInstallment() {
        return Float.toString(installment);
    }
    public String getPrice() {
        return Float.toString(price);
    }
    public String getIndex() {
        return Integer.toString(index);
    }
    public String getCube() {
        return cube;
    }
}
