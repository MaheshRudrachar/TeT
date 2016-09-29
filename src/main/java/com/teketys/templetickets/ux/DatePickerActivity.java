package com.teketys.templetickets.ux;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DefaultDayViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import com.teketys.templetickets.R;


import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by rudram1 on 9/6/16.
 */
public class DatePickerActivity extends Activity {
    private static final String TAG = "SampleTimesSquareActivi";
    private CalendarPickerView calendar;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    private final Set<Button> modeButtons = new LinkedHashSet<Button>();
    private TextView bookingDateTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 2);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.MONTH, 0);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());

        bookingDateTv = (TextView) findViewById(R.id.product_bookingdate_tv);

        initButtonListeners(nextYear, lastYear);
    }

    private void initButtonListeners(final Calendar nextYear, final Calendar lastYear) {

        /*final Button bookingTicketBtn = (Button) findViewById(R.id.product_bookingdate_btn);

        bookingTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "I'm a dialog!";
                calendar.setCustomDayView(new DefaultDayViewAdapter());
                calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
                showCalendarInDialog(title, R.layout.dialog_datepicker);
                dialogView.init(lastYear.getTime(), nextYear.getTime()) //
                        .withSelectedDate(new Date())
                        .inMode(CalendarPickerView.SelectionMode.SINGLE);

                dialogView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {

                    @Override
                    public void onDateUnselected(Date date) {

                    }

                    @Override
                    public void onDateSelected(Date date) {

                        //Toast.makeText(getContext(), dialogView.getSelectedDate().getTime() + "", Toast.LENGTH_LONG).show();
                        String toast = "Selected: " + SimpleDateFormat.getDateInstance().format(dialogView.getSelectedDate().getTime());

                        Toast.makeText(DatePickerActivity.this, toast, LENGTH_SHORT).show();
                        bookingDateTv.setText("Pickup Date:" + SimpleDateFormat.getDateInstance().format(dialogView.getSelectedDate().getTime()));
                    }
                });
            }
        });*/
    }

    private void showCalendarInDialog(String title, int layoutResId) {
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(layoutResId, null, false);
        theDialog = new AlertDialog.Builder(this) //
                .setTitle(title)
                .setView(dialogView)
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Log.d(TAG, "onShow: fix the dimens!");
                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        boolean applyFixes = theDialog != null && theDialog.isShowing();
        if (applyFixes) {
            Log.d(TAG, "Config change: unfix the dimens so I'll get remeasured!");
            dialogView.unfixDialogDimens();
        }
        super.onConfigurationChanged(newConfig);
        if (applyFixes) {
            dialogView.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Config change done: re-fix the dimens!");
                    dialogView.fixDialogDimens();
                }
            });
        }
    }
}