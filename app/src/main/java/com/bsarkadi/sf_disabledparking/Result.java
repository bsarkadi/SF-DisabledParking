package com.bsarkadi.sf_disabledparking;

import java.io.Serializable;

/**
 * Class for result record objects
 */
public class Result implements Serializable {
    private String record;
    private double distance;

    Result (String record, double distance){
        this.record = record;
        this.distance = distance;

    }


    public String getRecord(){
        return record;
    }

    public double getDistance(){
        return distance;
    }

    @Override
    public String toString() {
        String item ="";

        item = getRecord();

        return item;
    }

    }
