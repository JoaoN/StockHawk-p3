package com.sam_chordas.android.stockhawk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Joao on 04/05/2016.
 */
public class Quote implements Parcelable{
    @SerializedName("Symbol")
    private String mSymbol;

    @SerializedName("Date")
    private String mDate;

    @SerializedName("Open")
    private String mOpen;

    @SerializedName("High")
    private String mHigh;

    @SerializedName("Close")
    private String mClose;

    @SerializedName("Volume")
    private String mVolume;

    public String getmSymbol() {
        return mSymbol;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmOpen() {
        return mOpen;
    }

    public String getmHigh() {
        return mHigh;
    }

    public String getmClose() {
        return mClose;
    }

    public String getmVolume() {
        return mVolume;
    }

    protected Quote(Parcel in){
        mSymbol = in.readString();
        mDate = in.readString();
        mOpen = in.readString();
        mHigh = in.readString();
        mClose = in.readString();
        mVolume = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSymbol);
        dest.writeString(mDate);
        dest.writeString(mOpen);
        dest.writeString(mHigh);
        dest.writeString(mClose);
        dest.writeString(mVolume);
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel source) {
            return new Quote(source);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}