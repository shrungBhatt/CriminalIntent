package com.example.andorid.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bhatt on 26-10-2016.
 */

public class TimePickerFragment extends DialogFragment {
    private TimePicker mTimePicker;
    private static final String TIME = "time";
    public static final String EXTRA_TIME = "TIME_EXTRA";
    private Date time;

    public static TimePickerFragment newInstance (Date time) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TIME, time);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }


    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {

        time = (Date) getArguments().getSerializable(TIME);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Time picker")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {

                        int hour = mTimePicker.getCurrentHour();
                        int min =  mTimePicker.getCurrentMinute();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(time);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);

                        Date time = calendar.getTime();
                        sendIntent(Activity.RESULT_OK, time);
                    }
                })
                .create();
    }

    private void sendIntent(int resultCode,Date time) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,time);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);

    }
}
