package com.bsarkadi.sf_disabledparking;

import java.util.Comparator;

/**
 * Comparation class for result sorting
 */
public class Compare implements Comparator<Result> {
    @Override
    public int compare(Result o1, Result o2) {
        return o1.getDistance() < o2.getDistance() ? -1
                :o1.getDistance() > o2.getDistance() ? 1
                : 0;


    }
}
