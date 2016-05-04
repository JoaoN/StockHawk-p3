package com.sam_chordas.android.stockhawk;

import com.sam_chordas.android.stockhawk.model.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Joao on 04/05/2016.
 */
public interface HistoricalStockAPI {

    // To get all the movies and store them as MovieModel objects
    @GET("v1/public/yql")
            Call<Results> getStocks(
            @Query("q") String q, @Query("diagnostics") String diagnostics,
            @Query("env") String env, @Query("format") String format
    );

}
