package com.example.andorid.criminalintent;


import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;   //Id for crime from the list
    private String mTitle;// Giving title to the crime


    private Date mDate;//To set the date
    private Date mTime; //To set the time
    private boolean mSolved; //To see wether the case is solved or not
    private String mSuspect;

    public Crime(){
        this(UUID.randomUUID());
    }


    public Crime (UUID id) {
        mId = id;
        mDate = new Date();
        mTime = new Date();
        mSuspect = CrimeFragment.getSuspectName();

    }

    public UUID getId () {
        return mId;
    }

    public String getTitle () {
        return mTitle;
    }

    public void setTitle (String title) {
        mTitle = title;
    }

    public Date getDate () {
        return mDate;
    }

    public void setDate (Date date) {
        mDate = date;
    }

    public Date getTime () {
        return mTime;
    }

    public void setTime (Date time) {
        mTime = time;
    }

    public boolean isSolved () {
        return mSolved;
    }

    public void setSolved (boolean solved) {
        mSolved = solved;
    }

    public String getSuspect () {
        return mSuspect;
    }

    public void setSuspect (String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFileName(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
