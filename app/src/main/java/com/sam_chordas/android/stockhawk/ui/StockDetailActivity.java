package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.HistoricalStockAPI;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utility;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.Results;
import com.sam_chordas.android.stockhawk.model.Quote;

import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.database.DatabaseUtils.dumpCursorToString;

/**
 * Created by Joao on 02/05/2016.
 */
public class StockDetailActivity extends AppCompatActivity {

    //Retrofit variables
    private HistoricalStockAPI stockAPI;
    private Results resultsList;
    private Call<Results> callStock;

    //Stock variables
    public static List<Quote> mStocks;

    //Date variables
    String currentDate;
    String finalDate;

    public static final String SELECTED_SYMBOL = "SELECTED_SYMBOL";
    private Cursor data;
    private Cursor aux;
    private LineChartView lineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        currentDate = Utility.getFormattedDate(System.currentTimeMillis());

        finalDate = Utility.oneWeekDate(new Date());

        String sb = getIntent().getExtras().getString(SELECTED_SYMBOL);
        lineChartView = (LineChartView) findViewById(R.id.linechart);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        stockAPI = retrofit.create(HistoricalStockAPI.class);

        getStocks(sb);

        createGraph(sb);

    }

    public void getStocks(String sb){
        String diagnostics = "false";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";
        String query = "select * from yahoo.finance.historicaldata where symbol = \""+sb+"\" and startDate = \""+finalDate+"\" and endDate = \""+currentDate+"\"";
        callStock = stockAPI.getStocks(query, diagnostics, env, format );

        callStock.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                resultsList = response.body();
                mStocks = resultsList.getQuote();
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {

            }
        });

    }

    private void createGraph( String sb){
        data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{
                        QuoteColumns.BIDPRICE + ", " +
                                QuoteColumns.CREATED },
                QuoteColumns.SYMBOL + " = ?",
                new String[]{sb}, null);
        data.moveToFirst();
        int count = data.getCount();
        float points[] = new float[count];
        String labels[] = new String[count];
        float min = 99999;
        float max = 0;
        int step = 1;
        if (count > 200) {
            step = 20;
            points = new float[count/step];
            labels = new String[count/step];
        }
        aux = data;
        for(int index = 0; !data.isAfterLast(); index++) {
            float bidPrice = data.getFloat(0);
            String created = data.getString(1);
            points[index] = bidPrice;
            labels[index] = String.valueOf(index);
            if (bidPrice < min)
                min = bidPrice;
            if (bidPrice > max)
                max = bidPrice;

            if (index == points.length -1)
                break;
            int nextIndex = index + step;
            int newIndex = index;
            while (newIndex < nextIndex && newIndex < points.length - 1) {
                data.moveToNext();
                newIndex++;
            }
            String teste  =  dumpCursorToString(aux);
            Log.v("Cursor", teste);
        }

        LineSet dataset = new LineSet(labels, points);
        int minInt = (int)Math.floor(min);
        int maxInt = (int)Math.ceil(max);
        lineChartView.setAxisBorderValues(minInt - 1, maxInt + 1 , 1);


        lineChartView.addData(dataset);
        dataset.setDotsColor(getResources().getColor(R.color.white));
        dataset.setColor(getResources().getColor(R.color.white));

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#0079ae"));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        lineChartView.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setAxisColor(ContextCompat.getColor(getBaseContext(), R.color.white))
                .setLabelsColor(ContextCompat.getColor(getBaseContext(), R.color.white))
                .setXAxis(false)
                .setGrid(ChartView.GridType.FULL, gridPaint);


        lineChartView.show();

        data.close();
    }
}
