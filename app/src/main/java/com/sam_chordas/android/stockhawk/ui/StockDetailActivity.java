package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.HistoricalStockAPI;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.Utility;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.Stocks;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sam_chordas.android.stockhawk.Utility.gcd2;

/**
 * Created by Joao on 02/05/2016.
 */
public class StockDetailActivity extends AppCompatActivity {

    //Retrofit variables
    private HistoricalStockAPI stockAPI;
    private Stocks resultsList;
    private Call<Stocks> callStock;

    //Stock variables
    public static List<Quote> mStocks ;
    private FrameLayout mGraphLayout;

    //Date variables
    public String currentDate;
    public String finalDate;

    public static final String SELECTED_SYMBOL = "SELECTED_SYMBOL";
    private LineChartView lineChartView;
    String sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        sb = getIntent().getExtras().getString(SELECTED_SYMBOL);
        mGraphLayout = (FrameLayout) findViewById(R.id.frameLayout);
        getSupportActionBar().setTitle(sb);

        currentDate = Utility.getFormattedDate(System.currentTimeMillis());

        finalDate = Utility.oneWeekDate(new Date());
        lineChartView = (LineChartView) findViewById(R.id.linechart);

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        // set your desired log level
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        // add your other interceptors …
//
//        // add logging as last interceptor
//        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        stockAPI = retrofit.create(HistoricalStockAPI.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stockHistorical, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                Date date = new Date();
                switch (item)
                {
                    case "One Week":
                        if (mStocks != null){
                            lineChartView.dismiss();
                            mStocks.clear();
                        }
                        getStocks(sb, Utility.oneWeekDate(date));
                        break;
                    case "One Month":
                        lineChartView.dismiss();
                        mStocks.clear();
                        getStocks(sb, Utility.oneMonthDate(date));
                        break;
                    case "Three Months":
                        lineChartView.dismiss();
                        mStocks.clear();
                        getStocks(sb, Utility.threeMonthDate(date));
                        break;
                    case "Six Months":
                        lineChartView.dismiss();
                        mStocks.clear();
                        getStocks(sb, Utility.sixMonthDate(date));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        return true;
    }

    public void getStocks(final String sb, String finalDate){
        String diagnostics = "false";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";
        String query = "select * from yahoo.finance.historicaldata where symbol = \""+sb+"\" and" +
                " startDate = \""+finalDate+"\" and endDate = \""+currentDate+"\"";
        callStock = stockAPI.getStocks(query, diagnostics, env, format );

        callStock.enqueue(new Callback<Stocks>() {
            @Override
            public void onResponse(Call<Stocks> call, Response<Stocks> response) {
                resultsList = response.body();
                mStocks = resultsList.getQuery().getResults().getQuote();
                getGraphValues();
            }

            @Override
            public void onFailure(Call<Stocks> call, Throwable t) {

            }
        });

    }
    private void getGraphValues(){
        int count = mStocks.size();
        float highValues[] = new float[count];
        float lowValues[] = new float[count];
        String labels[] = new String[count];
        float min = 99999;
        float max = 0;
        for(int i = 0; i < count; i++){
            highValues[i] = Float.parseFloat(mStocks.get(i).getmHigh());
            lowValues[i] = Float.parseFloat(mStocks.get(i).getmLow());
            labels[i] = String.valueOf(i);
            if (lowValues[i] < min)
                min = lowValues[i];
            if (highValues[i] > max)
                max = highValues[i];
        }
        int maxValue = (int)Math.ceil(max) + 1;
        int minValue = (int)Math.floor(min) - 1;
        int step[] = gcd2(maxValue, minValue);
        maxValue = step[1];
        LineSet dataset = new LineSet(labels, highValues);
        mGraphLayout.removeAllViews();
        lineChartView.setAxisBorderValues(minValue, maxValue, step[0]);

        createGraph(dataset);
    }



    private void createGraph( LineSet dataset){

        lineChartView.addData(dataset);
        dataset.setDotsColor(getResources().getColor(R.color.white));
        dataset.setColor(getResources().getColor(R.color.white));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        gridPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        lineChartView.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXAxis(true)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setAxisColor(ContextCompat.getColor(getBaseContext(), R.color.white))
                .setLabelsColor(ContextCompat.getColor(getBaseContext(), R.color.white))
                .setGrid(ChartView.GridType.FULL, gridPaint);

        mGraphLayout.addView(lineChartView);
        lineChartView.show();
    }
}
