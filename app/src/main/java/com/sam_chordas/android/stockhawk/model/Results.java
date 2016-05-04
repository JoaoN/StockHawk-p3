package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joao on 04/05/2016.
 */
public class Results {

    @SerializedName("quote")
    @Expose
    private ArrayList<Quote> quote;


    public ArrayList<Quote> getQuote() {
        return quote;
    }
}
