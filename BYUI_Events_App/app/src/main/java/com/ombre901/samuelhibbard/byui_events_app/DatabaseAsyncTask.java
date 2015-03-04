package com.ombre901.samuelhibbard.byui_events_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TabHost;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to set up MainActivity with the tabs, but makes sure the database is filled before setting up the tabs.
 * Created by Grant on 12/9/2014.
 */
public class DatabaseAsyncTask extends AsyncTask<SQLDatabase, Object, Object> {
    MainActivity activity;
    ProgressDialog progressDialog;

    public DatabaseAsyncTask(MainActivity activity) {
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }

    /**
     * Sets up the ProgressDialog
     */
    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Updating Events");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * Writes to the database on another thread
     * @param params
     * @return
     */
    @Override
    protected Object doInBackground(SQLDatabase[] params) {
        params[0] = new SQLDatabase();
        params[0].run();

        return true;
    }

    /**
     * Gets rid of the ProgressDialog and sets up the Tabs with their necessary information
     * @param result
     */
    @Override
    protected void onPostExecute(Object result) {
        progressDialog.dismiss();

        // Create tabs!
        TabHost tabHost = (TabHost) activity.findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("DayTab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("WeekTab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("MonthTab");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("MyEventsTab");


        // Create all the activities!
        DayActivity day = new DayActivity();
        WeekActivity week = new WeekActivity();
        MonthActivity month = new MonthActivity();
        MyEventsActivity myevents = new MyEventsActivity();
        List<ActivityObserver> observers = new ArrayList<ActivityObserver>();

        //now give it back to main!
        observers.add(day);
        observers.add(week);
        observers.add(month);
        observers.add(myevents);
        activity.addObserver(observers);

        Intent intent = new Intent(activity, observers.get(0).getClass());

        //Log.d("ASYNC: ", day.toString());

        // Set up tab name
        tab1.setIndicator("Day");
        tab1.setContent(intent);

        intent = new Intent(activity, observers.get(1).getClass());

        tab2.setIndicator("Week");
        tab2.setContent(intent);
        //Log.d("ASYNC: ", day.toString());

        intent = new Intent(activity, observers.get(2).getClass());

        tab3.setIndicator("Month");
        tab3.setContent(intent);

        intent = new Intent(activity, observers.get(3).getClass());

        tab4.setIndicator("My Events");
        tab4.setContent(intent);

        // Now add to the host!
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        // Now change the indicator color!
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView textView = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(activity.getResources().getColor(R.color.white));
            textView.setTextSize(15);
        }

        // Middle tab (which is week) is the current tab
        tabHost.setCurrentTab(2);
        tabHost.setCurrentTab(1);
    }
}
