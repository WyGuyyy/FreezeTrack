package com.example.wyatttowne.freezetrack;

import java.util.ArrayList;

public class Leftover {

    public static ArrayList<Leftover> leftovers = new ArrayList<Leftover>();

    String name;
    int days;
    String photoName;

    public Leftover(){}

    public Leftover(String n, int d, String pn){
        name = n;
        days = d;
        photoName = pn;
    }

    public static void addLeftover(String name, int days, String photoName){

        Leftover tempL = new Leftover(name, days, photoName);
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
