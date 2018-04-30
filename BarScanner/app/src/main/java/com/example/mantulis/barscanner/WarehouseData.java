package com.example.mantulis.barscanner;



public class WarehouseData {
    private String name;
    private String id;
    private int amount;
    public WarehouseData(){
        name = "";
        id = "";
        amount = 0;
    }
    public WarehouseData(String wname, String wid, int wmount){
        name = wname;
        id = wid;
        amount = wmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }


    public void setAmount(int amount) {
        this.amount = amount;
    }
}
