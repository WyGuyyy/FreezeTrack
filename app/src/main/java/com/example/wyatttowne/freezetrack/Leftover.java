package com.example.wyatttowne.freezetrack;

import java.util.ArrayList;
import java.util.Date;

public class Leftover {

    public static ArrayList<Leftover> leftovers = new ArrayList<Leftover>();

    String name;
    int days;
    String startDate;
    String endDate;
    String photoName;

    public Leftover(){}

    public Leftover(String name, int days, String startDate, String endDate, String photoName){
        this.name = name;
        this.days = days;
        this.startDate = startDate;
        this.endDate = endDate;
        this.photoName = photoName;
    }

    public static void addLeftover(String name, int days, String startDate, String endDate, String photoName){

        Leftover tempL = new Leftover(name, days, startDate, endDate, photoName);
        leftovers.add(tempL);

    }

    public static void removeLeftover(String name){

        for(int i = 0; i < leftovers.size(); i++){
            if(name.equals(leftovers.get(i).name)){
                leftovers.remove(i);
                break;
            }
        }

    }


}
