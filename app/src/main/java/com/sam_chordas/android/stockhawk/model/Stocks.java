package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Joao on 04/05/2016.
 */
public class Stocks {

    @SerializedName("query")
    @Expose
    private Query query;

    public Query getQuery() {
        return query;
    }


    public void setQuery(Query query) {
        this.query = query;
    }

    public Stocks withQuery(Query query) {
        this.query = query;
        return this;
    }
}