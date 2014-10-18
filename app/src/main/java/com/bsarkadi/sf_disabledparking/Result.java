package com.bsarkadi.sf_disabledparking;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Class for result record objects
 */
public class Result implements Parcelable {
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

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {

        pc.writeString(record);

        pc.writeDouble(distance);


    }

    /** Static field used to regenerate object, individually or as arrays */
    
    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        
        public Result createFromParcel(Parcel pc) {
            
            return new Result(pc);
            
        }
       
        public Result[] newArray(int size) {
          
            return new Result[size];
          
        }
     
    };
   

/**Ctor from Parcel, reads back fields IN THE ORDER they were written */
          
    public Result(Parcel pc){
       
        record         = pc.readString();
      
        distance       =  pc.readDouble();
     
    }



    }
