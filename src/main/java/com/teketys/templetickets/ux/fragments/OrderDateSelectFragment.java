package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.AlertDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.teketys.templetickets.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by rudram1 on 8/26/16.
 */
public class OrderDateSelectFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);



    }
}
