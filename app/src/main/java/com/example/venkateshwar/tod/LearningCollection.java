package com.example.venkateshwar.tod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Aravind on 06/11/16.
 */
public class LearningCollection{
    ArrayList<Learning> arrayList;
    HashMap<String, Integer> hashMap;


    public LearningCollection(){
        arrayList = new ArrayList<>();
        hashMap = new HashMap<>();
    }

    void add(Learning le){
        if(!contains(le)) {
            arrayList.add(le);
            hashMap.put(le.getData(), arrayList.indexOf(le));
        }
    }

    void remove(Learning le) {
        arrayList.remove(le);
        hashMap.remove(le.getData());
    }

    boolean contains(Learning le) {
        if(hashMap.containsKey(le.getData()))
            return true;
        else
            return false;
    }


    Learning get(int x){

        return arrayList.get(x);
    }

    int size(){
        return arrayList.size();
    }

    void sort(){
        Collections.sort(arrayList, new Comparator<Learning>() {
            @Override
            public int compare(Learning learning, Learning t1) {
                Date l1 = null;
                Date l2 = null;
                try {
                    l1 = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss").parse(learning.getCreated());
                    l2 = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss").parse(t1.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return l1.compareTo(l2);
            }
        });
    }

    @Override
    public String toString() {
        return arrayList.toString();
    }

    ArrayList<Learning> getArrayList(){
        return arrayList;
    }
}
