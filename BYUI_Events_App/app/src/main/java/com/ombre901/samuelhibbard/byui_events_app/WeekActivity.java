package com.ombre901.samuelhibbard.byui_events_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeekActivity extends TemplateActivity implements ActivityObserver {
    private static ExpandableListViewAdapter listAdapter;
    private static ExpandableListView expListView;
    private static TextView textView;
    private static Date date = new Date();
    private String stringStartDate;
    private String stringEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        expListView = (ExpandableListView) findViewById(R.id.week_list);
        textView = (TextView) findViewById(R.id.week_view);

        setAdapter();
    }

    @Override
    protected void grabFromDatabase() {
        setUpStartAndEndDateStrings();
        database.selectEvents(stringStartDate, stringEndDate, headerList, childList, imageList, dateList);
    }

    private void setUpStartAndEndDateStrings() {
        // Grab the current date!
        stringDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String[] dateParts = stringDate.split("-");

        Calendar calendar = new GregorianCalendar(
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1])-1,
                Integer.parseInt(dateParts[2]));

        // Find start of the week of current day
        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DAY_OF_WEEK)-1));
        Date startDate = calendar.getTime();
        stringStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);

        // Find end of the week of current day
        calendar.add(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_WEEK)+5);
        Date endDate = calendar.getTime();
        stringEndDate = new SimpleDateFormat("yyy-MM-dd").format(endDate);
    }

    @Override
    protected void setTitle() {
        // Set the start and end dates as the title
        stringStartDate = dateFormat(stringStartDate);
        stringEndDate = dateFormat(stringEndDate);
        textView.setText(stringStartDate + " to " + stringEndDate);
    }

    @Override
    protected void setUpExpandableListViewAdapter() {
        // Now to put it on the screen!
        if (listAdapter == null) {
            listAdapter = new ExpandableListViewAdapter(this, headerList, childList, imageList, dateList, "WEEK", null);
        } else {
            listAdapter.setLists(headerList, childList, imageList, dateList);
        }

        // Now set it to the screen!
        expListView.setAdapter(listAdapter);
    }

    /**
     * ONRESUME
     */
    @Override
    protected void onResume() {
        super.onResume();

        //now create a listener for the list!
        //this will only allow one thing to be selected!
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (previousItem != groupPosition) {
                    expListView.collapseGroup(previousItem);
                    previousItem = groupPosition;
                }
            }
        });

        //grab the image buttons!
        ImageButton back = (ImageButton) findViewById(R.id.week_back_button);
        ImageButton forward = (ImageButton) findViewById(R.id.week_for_button);

        //now create the listeners!
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("WEEK: ", "Onclick event for back!");
                //grab the date!
                String [] splitDate = stringDate.split("-");

                Calendar calendar = new GregorianCalendar(Integer.parseInt(splitDate[0]),
                        Integer.parseInt(splitDate[1]) - 1,
                        Integer.parseInt(splitDate[2]));

                //move back seven days!
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                date = calendar.getTime();

                setAdapter();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("WEEK: ", "Onclick event for forward!");
                //grab the date!
                String[] splitDate = stringDate.split("-");

                Calendar calendar = new GregorianCalendar(Integer.parseInt(splitDate[0]),
                        Integer.parseInt(splitDate[1]) - 1,
                        Integer.parseInt(splitDate[2]));

                //move forward seven days!
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                date = calendar.getTime();

                setAdapter();
            }
        });
    }

    /**
     * UPDATE
     */
    @Override
    public void update() {
        setAdapter();
    }
}

