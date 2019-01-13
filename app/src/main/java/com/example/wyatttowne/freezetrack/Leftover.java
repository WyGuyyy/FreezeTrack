package com.example.wyatttowne.freezetrack;

import java.util.ArrayList;

public class Leftover {

    public static ArrayList<Leftover> leftovers = new ArrayList<Leftover>();

    String name;
    String time;
    String photoName;

    public Leftover(){}

    public Leftover(String n, String t, String pn){
        name = n;
        time = t;
        photoName = pn;
    }

    public static void addLeftover(String name, String time, String photoName){

        Leftover tempL = new Leftover(name, time, photoName);
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
